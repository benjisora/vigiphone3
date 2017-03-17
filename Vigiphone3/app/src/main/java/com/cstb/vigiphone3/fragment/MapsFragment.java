package com.cstb.vigiphone3.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.data.database.MyApplication;
import com.cstb.vigiphone3.data.database.Utils;
import com.cstb.vigiphone3.data.model.Emitter;
import com.cstb.vigiphone3.data.model.Emitters;
import com.cstb.vigiphone3.data.model.HeapSort;
import com.cstb.vigiphone3.data.model.Lambert;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.network.NetworkService;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MapsFragment class, used to display a map and to show emitters nearby
 */
public class MapsFragment extends Fragment {

    @BindView(R.id.mapView)
    MapView mMapView;

    private MapboxMap googleMap;
    private NetworkService networkService;
    private Call<Emitters> getAntennas = null;
    private List<Emitter> emitters;
    private RecordingRow rec;
    private LatLng center;
    private Double distanceLatitude, distanceLongitude;
    private double[] boundaries;

    /**
     * Updates the content and/or refreshes the map whenever a message is received
     */
    private BroadcastReceiver mUpdateRowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MapsFragment", "received row from broadcast");
            rec = (RecordingRow) intent.getExtras().get("row");
            requestAntennas();

            /*if (rec == null) {
                requestAntennas();
            } else {
                refreshMap();
            }*/

        }
    };

    /**
     * {@inheritDoc}
     * Initializes the values, registers the LocalBroadcast listener, and loads the map
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        center = new LatLng();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateRowReceiver, new IntentFilter(MyApplication.updateMarkerFromServiceManager));
        boundaries = new double[4];

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mMap) {
                googleMap = mMap;
                initializeMap();
                requestAntennas();
            }
        });

        return rootView;
    }

    /**
     * Refreshes the content of the map
     */
    private void refreshMap() {
        if (emitters != null && !emitters.isEmpty()) {
            applyPassLoss();
            addRank();
            addMarkers();
            googleMap.invalidate();
        }
    }

    /**
     * Applies pass loss to each emitter
     */
    private void applyPassLoss() {
        Lambert.Lambert93 lambert;
        lambert = new Lambert.Lambert93();
        Double[] xy_phone = new Double[]{null, null};
        Double[] xy_emitter = new Double[]{null, null};

        for (Emitter e : emitters) {

            double lat = e.getLatitude();
            double lng = e.getLongitude();
            float azimut = e.getAzimut();

            if (lat != 0 && lng != 0) {
                lambert.XY(rec.getLongitude() * 3.14159265358979323846 / 180.0, rec.getLatitude() * 3.14159265358979323846 / 180.0, xy_phone);
                lambert.XY(lng * 3.14159265358979323846 / 180.0, lat * 3.14159265358979323846 / 180.0, xy_emitter);
                e.setPassloss(CalculatePassLoss(xy_emitter, Math.round(azimut), xy_phone));
            }

            HeapSort.sort(emitters);

        }
    }

    /**
     * Adds a rank to display the correct icon
     * depending on the probability that the emitter is the one we're connected to
     */
    private void addRank() {

        int rankValue = 1;

        for (int i = 0; i < emitters.size(); i++) {
            if (i == 0) {
                emitters.get(i).setColorIndex(rankValue + 4);
                continue;
            } else if (emitters.get(i).getCID() == rec.getCID()) {
                emitters.get(i).setColorIndex(4);
                continue;
            }

            if (emitters.get(i).getPassloss() != emitters.get(i - 1).getPassloss()) {
                rankValue++;
            }

            if (rankValue <= 3) {
                emitters.get(i).setColorIndex(rankValue + 4);
            } else {
                emitters.get(i).setColorIndex(-1);
            }
        }
    }

    /**
     * Adds a marker onto the map for each emitters
     */
    private void addMarkers() {
        List<MarkerOptions> markers = new ArrayList<>();

        for (Emitter e : emitters) {

            double lat = e.getLatitude();
            double lng = e.getLongitude();
            float azimut = e.getAzimut();

            if (lat != 0 && lng != 0 && MyApplication.getIcons() != null) {
                markers.add(
                        new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .icon((MyApplication.getIcons().get(e.getColorIndex()).get(Math.round(azimut))))
                );
            }
        }
        googleMap.addMarkers(markers);
    }

    /**
     * Calculates the pass loss
     *
     * @param XYemitter      The emitter's coordinates
     * @param azimut_emitter The emitter's azimuth
     * @param XYphone        The phone's coordinates
     * @return The pass loss value
     */
    private Double CalculatePassLoss(Double[] XYemitter, int azimut_emitter, Double[] XYphone) {

        Double Ld = Math.max(20 * Math.log10(Math.sqrt(Math.pow((XYphone[0] - XYemitter[0]), 2) + Math.pow((XYphone[1] - XYemitter[1]), 2))), 0);
        Double azimuth_phone = Math.atan2(XYphone[0] - XYemitter[0], XYphone[1] - XYemitter[1]);
        Double varAzimut = Math.acos(Math.cos(azimuth_phone - Math.toRadians(azimut_emitter)));
        Double Lh = Math.min((Math.toDegrees(varAzimut) / 5), 30);

        return Ld + Lh;
    }

    /**
     * Makes a call to the server to receive the visible emitters
     */
    private void requestAntennas() {

        if (rec != null) {

            Log.d("MapsFragment", "RequestAntenna called, rec not null");

            calculateScreenEdgesCoordinates();
            networkService = MyApplication.getNetworkServiceInstance();
            getAntennas = networkService.getEmitters(MyApplication.getEmittersTableName(), rec.getCID(), rec.getLAC(), rec.getMCC(), rec.getMNC(), rec.getType(), boundaries[0], boundaries[1], boundaries[2], boundaries[3]);
            getAntennas.enqueue(new Callback<Emitters>() {
                @Override
                public void onResponse(Call<Emitters> call, Response<Emitters> response) {

                    if (response.code() == HttpURLConnection.HTTP_OK) {

                        Emitters emitterList = response.body();

                        if (emitterList.list != null && !emitterList.list.isEmpty()) {
                            for (Emitter e : emitterList.list) {
                                e.save();
                            }
                        } else {
                            Log.e("RecordService", "Server returned no emitters");
                        }
                        emitters = Utils.getinstance().getAllEmitters();
                    } else {
                        Log.e("RecordService", "Error code " + String.valueOf(response.code()) + ", address : " + call.request().url());
                    }
                    refreshMap();
                }

                @Override
                public void onFailure(Call<Emitters> call, Throwable t) {
                    Log.e("MapsFragment", "Error during fetch", t);
                    emitters = Utils.getinstance().getAllEmitters();
                    refreshMap();
                }
            });
        }
    }

    /**
     * Initializes the map with custom settings
     */
    private void initializeMap() {
        googleMap.getUiSettings().setAttributionEnabled(false);
        googleMap.getUiSettings().setLogoEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setStyleUrl(Style.MAPBOX_STREETS);
        googleMap.setMyLocationEnabled(true);

        Location userLocation = googleMap.getMyLocation();

        if (userLocation != null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(userLocation.getLatitude(), userLocation.getLatitude()))
                    .zoom(14)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    /**
     * Calculates the visible edges of the map in coordinates
     */
    private void calculateScreenEdgesCoordinates() {
        double centerLatitude = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter().getLatitude();
        double centerLongitude = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter().getLongitude();

        double latitudeNorth = googleMap.getProjection().getVisibleRegion().latLngBounds.getLatNorth();
        double longitudeEast = googleMap.getProjection().getVisibleRegion().latLngBounds.getLonEast();

        distanceLatitude = Math.abs(center.getLatitude() - latitudeNorth) * 2;
        distanceLongitude = Math.abs(center.getLongitude() - longitudeEast) * 2;


        boundaries[0] = centerLatitude + distanceLatitude;
        boundaries[1] = centerLatitude - distanceLatitude;
        boundaries[2] = centerLongitude + distanceLongitude;
        boundaries[3] = centerLongitude - distanceLongitude;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateRowReceiver, new IntentFilter(MyApplication.updateMarkerFromServiceManager));
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateRowReceiver);
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateRowReceiver);
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateRowReceiver);
        mMapView.onLowMemory();
    }
}
