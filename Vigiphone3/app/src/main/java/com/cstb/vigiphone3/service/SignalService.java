package com.cstb.vigiphone3.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.cstb.vigiphone3.R;

import java.lang.reflect.Method;
import java.util.List;

public class SignalService extends Service {

    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("SignalService", "Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        initializeData();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("SignalService", "Service stopped");
        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_NONE);
    }

    public void sendMessageToActivity(int CID, int LAC, int MCC, int MNC, String networkName, String networkType, String neighbours, int strength) {
        Intent intent = new Intent("SignalChanged");
        intent.putExtra("cid", CID);
        intent.putExtra("lac", LAC);
        intent.putExtra("mcc", MCC);
        intent.putExtra("mnc", MNC);
        intent.putExtra("name", networkName);
        intent.putExtra("type", networkType);
        intent.putExtra("neighbours", neighbours);
        intent.putExtra("strength", strength);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void initializeData() {
        myPhoneStateListener.setCellInfo();
        sendMessageToActivity(myPhoneStateListener.CID, myPhoneStateListener.LAC, myPhoneStateListener.MCC, myPhoneStateListener.MNC, myPhoneStateListener.networkName, myPhoneStateListener.networkType, myPhoneStateListener.neighbours, myPhoneStateListener.strength);
    }

    public class MyPhoneStateListener extends PhoneStateListener {

        private int CID = 0, LAC = 0, MCC = 0, MNC = 0, strength = 0;
        private String networkName = "", networkType = "", neighbours = "";

        void setCellInfo() {
            if (telephonyManager.getCellLocation() != null) {
                CID = ((GsmCellLocation) telephonyManager.getCellLocation()).getCid();
                LAC = ((GsmCellLocation) telephonyManager.getCellLocation()).getLac();
            }
            if (telephonyManager.getNetworkOperator() != null && !telephonyManager.getNetworkOperator().isEmpty()) {
                MCC = Integer.parseInt(telephonyManager.getNetworkOperator().substring(0, 3));
                MNC = Integer.parseInt(telephonyManager.getNetworkOperator().substring(3, telephonyManager.getNetworkOperator().length()));
            }
            if (telephonyManager.getNetworkOperatorName() != null && !telephonyManager.getNetworkOperatorName().isEmpty()) {
                networkName = telephonyManager.getNetworkOperatorName();
            }
            networkType = getNetworkTypeFromInt(telephonyManager.getNetworkType());

            if (telephonyManager.getNeighboringCellInfo() != null && !telephonyManager.getNeighboringCellInfo().isEmpty()) {

                List<NeighboringCellInfo> neighboringCellInfoList = telephonyManager.getNeighboringCellInfo();
                neighbours = "";
                int j = 1;
                for (NeighboringCellInfo i : neighboringCellInfoList) {
                    if (i.getCid() != -1)
                        neighbours = neighbours + "#" + j + "-" + getNetworkTypeFromInt(i.getNetworkType()) + "/" + i.getCid() + "/" + (i.getRssi() * 2 - 113) + "\n";
                    j++;
                }
            }
        }

        void setStrength(SignalStrength signalStrength) {
            strength = 0;
            try {
                Method[] methods = android.telephony.SignalStrength.class.getMethods();
                for (Method mthd : methods) {
                    if (mthd.getName().equals("getLteRsrp")) {
                        if ((int) mthd.invoke(signalStrength) > 0) {
                            strength = signalStrength.getGsmSignalStrength() * 2 - 113;
                        } else {
                            strength = (int) mthd.invoke(signalStrength);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("getSignalStrength Error", getString(R.string.log_error), e);
            }
        }

        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
            setCellInfo();
            sendMessageToActivity(CID, LAC, MCC, MNC, networkName, networkType, neighbours, strength);
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            setCellInfo();
            setStrength(signalStrength);
            sendMessageToActivity(CID, LAC, MCC, MNC, networkName, networkType, neighbours, strength);
        }

        private String getNetworkTypeFromInt(int type) {

            switch (type) {
                case 0:
                    return "Unknown";

                case 1:
                    return "GPRS";

                case 2:
                    return "EDGE";

                case 3:
                    return "UMTS";

                case 4:
                    return "CDMA";

                case 5:
                    return "EVDO rev. 0";

                case 6:
                    return "EVDO rev. A";

                case 7:
                    return "1xRTT";

                case 8:
                    return "HSDPA";

                case 9:
                    return "HSUPA";

                case 10:
                    return "HSPA";

                case 11:
                    return "iDen";

                case 12:
                    return "EVDO rev. B";

                case 13:
                    return "LTE";

                case 14:
                    return "eHRPD";

                case 15:
                    return "HSPA+";

                default:
                    return "Unknown";
            }
        }

    }

}
