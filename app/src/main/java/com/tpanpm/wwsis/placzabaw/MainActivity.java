package com.tpanpm.wwsis.placzabaw;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {



    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    double locLong;
    double locLat;
    LocationManager lm;
    Criteria kr;
    String najlepszyDostawca;
    Location loc = null;


    private void refreshLocationInfo() {
        kr = new Criteria();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        najlepszyDostawca = lm.getBestProvider(kr, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        loc = lm.getLastKnownLocation(najlepszyDostawca);
        if(loc==null){
            locLat = 0.0;
            locLong = 0.0;
        }
        else{
            locLat = loc.getLatitude();
            locLong = loc.getLongitude();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPlayground(); //
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkNetworkAndLocation();
        refreshLocationInfo();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lm.requestLocationUpdates(najlepszyDostawca, 1000, 1, this);

    }

    private void checkNetworkAndLocation() {

        ConnectivityManager managerCon = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = managerCon.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();

        boolean isWifi = managerCon.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        View view = findViewById(R.id.map);

        checkNetwork(is3g, isWifi, view);

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkLocation(locationManager, view);

    }

    private void checkLocation(LocationManager locationManager, View view) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Snackbar.make(view, "GPS is enable", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void checkNetwork(boolean is3g, boolean isWifi, View view) {
        if (!is3g && !isWifi) {
            buildAlertMessageNoNet();
        } else {
            Snackbar.make(view, "Network is enable", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void addNewPlayground() {
        MarkerOptions markers = new MarkerOptions().position(
            new LatLng(locLat, locLong)).title("Twój nowy plac zabaw").snippet("A tu będzie krótki opis placu zabaw");
            markers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        mGoogleMap.addMarker(markers);

    }


    private void buildAlertMessageNoNet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Dostęp do sieci internet jest niemożliwy!" +
                            "\nCzy chcesz przejść do ustawień sieciowych?\n " +
                            "Brak dostępu do sieci internet uniemożliwia\n" +
                            "korzystanie z podstawowych funkcji aplikacji.")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Brak sygnału GPS!" +
                "\nCzy chcesz przejść do ustawień lokalizacji?\n" +
                "Brak informacji o lokalizacji uniemożliwia\n" +
                "korzystanie z podstawowych funkcji aplikacji.")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mGoogleMap.setPadding(0, 0, 5, 130);
            //mGoogleMap.clear();
           // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

          CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(locLat, locLong)).zoom(14).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                  .newCameraPosition(cameraPosition));

            // create markerOptions
          //  MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(
            //        location.getLatitude(), location.getLongitude()));
            // ROSE color icon
           // markerOptions.icon(BitmapDescriptorFactory
             //       .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
           // markerOptions.position(latLng);
            // adding markerOptions
         //  googleMap.addMarker(markerOptions);





      //cameraPosition = new CameraPosition.Builder()
        //        .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(20).build();

      //  mGoogleMap.animateCamera(CameraUpdateFactory
        //        .newCameraPosition(cameraPosition));

    }

    @Override
    public void onPause() {
        super.onPause();

    }



    @Override
    public void onLocationChanged(Location location) {

        //remove previous current location marker and add new one at current position

            refreshLocationInfo();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(locLat, locLong)).zoom(14).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        /*latLng = new LatLng(locLat, locLong);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocation = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.addMarker(markerOptions);
        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(locLat, locLong)).zoom(14).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);*/
       // t2.setText(String.format("long: %s \n", locLong));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}


