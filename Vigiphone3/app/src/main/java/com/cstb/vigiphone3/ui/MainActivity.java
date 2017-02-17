package com.cstb.vigiphone3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cstb.vigiphone3.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Class fragmentClass = SchedulesFragment.class;
        try {
            loadFragment(fragmentClass);
        } catch (Exception e) {
            Log.e("RetrofitError", getString(R.string.log_error), e);
        }

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

        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class fragmentClass = null;
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.nav_schedules:
                fragmentClass = SchedulesFragment.class;
                break;
            case R.id.nav_map:
                fragmentClass = MapsFragment.class;
                break;
            case R.id.nav_favorites:
                fragmentClass = FavoritesFragment.class;
                break;
            case R.id.nav_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                fragmentClass = SchedulesFragment.class;
                break;
        }

        try {
            loadFragment(fragmentClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        drawer.closeDrawer(GravityCompat.START);
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

}
