package com.cstb.vigiphone3.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.fragment.MapsFragment;
import com.cstb.vigiphone3.fragment.RecordingFragment;
import com.cstb.vigiphone3.fragment.SensorsFragment;
import com.cstb.vigiphone3.service.LocationService;
import com.cstb.vigiphone3.service.SensorService;
import com.cstb.vigiphone3.service.ServiceManager;
import com.cstb.vigiphone3.service.SignalService;

import butterknife.BindView;
import butterknife.ButterKnife;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class MainActivity extends ActivityManagePermission
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;


    ServiceManager serviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_READ_PHONE_STATE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                serviceManager = new ServiceManager(MainActivity.this);
                serviceManager.registerReceivers();
                serviceManager.startServices();
            }

            @Override
            public void permissionDenied() {
                showPermissionDialog("Permissions Denied", "Location permission is needed to display position. Please allow the permission in the settings");
            }

            @Override
            public void permissionForeverDenied() {
                showPermissionDialog("Permissions Denied", "Location permission is needed to display position. Please allow the permission in the settings");
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_record);
        toolbar.setTitle("Recording");
        Class fragmentClass = RecordingFragment.class;
        try {
            loadFragment(fragmentClass);
        } catch (Exception e) {
            Log.e("loadFragment Error", getString(R.string.log_error), e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceManager.stopServices();
        serviceManager.unregisterReceivers();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class fragmentClass = null;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_record:
                toolbar.setTitle("Recording");
                fragmentClass = RecordingFragment.class;
                break;
            case R.id.nav_sensors:
                toolbar.setTitle("Sensors");
                fragmentClass = SensorsFragment.class;
                break;
            case R.id.nav_map:
                toolbar.setTitle("Map");
                fragmentClass = MapsFragment.class;
                break;
            //case R.id.nav_camera:
                //toolbar.setTitle("Camera");
                //fragmentClass = RecordingFragment.class;
                //break;
            case R.id.nav_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                toolbar.setTitle("Recording");
                fragmentClass = RecordingFragment.class;
                break;
        }

        try {
            loadFragment(fragmentClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        drawer.closeDrawer(GravityCompat.START);
        //navigationView.setCheckedItem(item.getItemId());
        return true;
    }

    public void loadFragment(Class c) throws Exception {
        Fragment fragment = null;
        if (c != null) {
            fragment = (Fragment) c.newInstance();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, fragment.getTag()).commit();
            Log.d("loadFragment", "Fragment loaded, class name :" + c.getName());
        }
    }

    private void showPermissionDialog(String title, String text){
        new MaterialDialog.Builder(this)
                .title(title)
                .content(text)
                .positiveText("Settings")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        openSettingsApp(MainActivity.this);
                    }
                })
                .show();
    }


}
