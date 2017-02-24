package com.cstb.vigiphone3.ui;

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
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.fragment.MapsFragment;
import com.cstb.vigiphone3.fragment.RecordingFragment;
import com.cstb.vigiphone3.fragment.SensorsFragment;
import com.cstb.vigiphone3.service.ServiceManager;

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

    private ServiceManager serviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (!isPermissionsGranted(MainActivity.this, new String[]{PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_READ_PHONE_STATE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE})) {
            askForNeededPermissions(getString(R.string.permissions_needed_title), getString(R.string.permissions_needed_text));
        } else {
            createServiceManager();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_record);
        toolbar.setTitle(R.string.recording_fragment_title);
        Class fragmentClass = RecordingFragment.class;
        loadFragment(fragmentClass);

    }

    private void createServiceManager() {
        serviceManager = new ServiceManager(MainActivity.this);
        serviceManager.registerReceivers();
        serviceManager.startServices();
    }

    private void stopServiceManager() {
        serviceManager.unregisterReceivers();
        serviceManager.stopServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServiceManager();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class fragmentClass = null;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_record:
                toolbar.setTitle(R.string.recording_fragment_title);
                fragmentClass = RecordingFragment.class;
                break;
            case R.id.nav_sensors:
                toolbar.setTitle(R.string.sensors_fragment_title);
                fragmentClass = SensorsFragment.class;
                break;
            case R.id.nav_map:
                toolbar.setTitle(R.string.map_fragment_title);
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
                toolbar.setTitle(R.string.recording_fragment_title);
                fragmentClass = RecordingFragment.class;
                break;
        }

        loadFragment(fragmentClass);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Class c) {
        try {
            Fragment fragment = null;
            if (c != null) {
                fragment = (Fragment) c.newInstance();
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, fragment.getTag()).commit();
                Log.d("loadFragment", "Fragment loaded, class name :" + c.getName());
            }
        } catch (Exception e) {
            Log.e("MainActivity", getString(R.string.log_error), e);
        }
    }

    private void showPermissionDialogDenied(String title, String text) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(text)
                .positiveText(R.string.settings_button_text)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        openSettingsApp(MainActivity.this);
                    }
                })
                .show();
    }

    private void askForNeededPermissions(String title, String text) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(text)
                .positiveText(android.R.string.ok)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_READ_PHONE_STATE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
                            @Override
                            public void permissionGranted() {
                                createServiceManager();
                            }

                            @Override
                            public void permissionDenied() {
                                showPermissionDialogDenied(getString(R.string.permissions_denied), getString(R.string.permissions_denied_text));
                            }

                            @Override
                            public void permissionForeverDenied() {
                                showPermissionDialogDenied(getString(R.string.permissions_permanently_denied), getString(R.string.permissions_permanently_denied_text));
                            }
                        });
                    }
                })
                .show();
    }

}
