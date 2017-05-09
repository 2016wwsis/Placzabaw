package com.tpanpm.wwsis.placzabaw;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.sip.SipAudioCall;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnKeyListener;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {


    Animation fabOpen, fabClose, fabClockwise, fabAnticlockwise;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    double locLong;
    double locLat;
    LocationManager lm;
    Criteria kr;
    String najlepszyDostawca;
    Location loc = null;
    FloatingActionButton fab, fab_playground, fabMyLocation;
    boolean isFabOpen;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Playground[] playgroundList = new Playground[10];
    InputMethodManager imm;
    EditText textSearch;
    ImageButton buttonSearch;
    int testValue = 0;
    PopupMenu pp;

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
        if (loc == null) {
            locLat = 0.0;
            locLong = 0.0;
        } else {
            locLat = loc.getLatitude();
            locLong = loc.getLongitude();
        }
    }


    private void getPlaygroundFromDatabase() {

        //tu bedzie kod do czytania danych

        Random random = new Random();
        float minX = 1.0f;
        float maxX = 5.0f;

        Random rand = new Random();


        for (int i = 0; i < playgroundList.length; i++)
            playgroundList[i] = new Playground();
        StringBuilder sb = new StringBuilder("m");

        for (int i = 0; i <= 9; i++) {
            playgroundList[i].name = "Plac " + i + " ";
            playgroundList[i].description = "Plac zabaw Nivea w parku miejskim. Dużo ciekawych zabaw. Ogrodzony dużo koszy na smieci. Czysto i zadbanie. Polecam.";
            playgroundList[i].rate = rand.nextFloat() * (maxX - minX) + minX;
            playgroundList[i].playgroundLat = locLat + (random.nextDouble() / 100);
            playgroundList[i].playgroundLong = locLong + (random.nextDouble() / 100);
            playgroundList[i].markerId = sb.append(String.valueOf(i)).toString();

            addNewPlayground(new LatLng(playgroundList[i].playgroundLat,
                            playgroundList[i].playgroundLong),
                    playgroundList[i].name,
                    playgroundList[i].description,
                    playgroundList[i].rate,
                    playgroundList[i].markerId);
        }

        // mGoogleMap.setInfoWindowAdapter(new MarkerAdapter(getLayoutInflater()));
        //mGoogleMap.addMarker(new MarkerOptions().position(LOS_ANGELES).title("Los Angeles")).setSnippet("tets");
        //mGoogleMap.addMarker(new MarkerOptions().position(SAN_FRANCISCO).title("San Francisco").snippet("test")
        //       .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_half_orange_500_36dp)));
        // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(LOS_ANGELES));

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       textSearch = (EditText) findViewById(R.id.search_edit_text);
        buttonSearch = (ImageButton) findViewById(R.id.search_button);

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_playground = (FloatingActionButton) findViewById(R.id.fab_playground);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        textSearch.setCursorVisible(false);
        textSearch.setFocusable(false);
        textSearch.setFocusableInTouchMode(true);

        pp = new PopupMenu(MainActivity.this, textSearch);

        pp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Toast.makeText(MainActivity.this, String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();
                pp.getMenu().clear();
                String location = String.valueOf(item.getTitle());
                Geocoder gc = new Geocoder(MainActivity.this);
                List<Address> list = null;//, locLat-1,locLong-10,locLat+1, locLong+10);
                try {
                    list = gc.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                goToLocationZoom(lat, lng, 15);

                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addNewPlayground(new LatLng(locLat, locLong),
                        "Twój nowy plac zabaw",
                        "A tu będzie krótki opis placu zabaw. dla przykładu co zawiera plac lub jakie są mankamenty",
                        1.5f, "m99"); //

                DatabaseReference myRef = database.getReference("message");
                testValue += 1;
                myRef.setValue(testValue);


               /* if (isFabOpen) {

                    fab_playground.startAnimation(fabClose);
                    fab.startAnimation(fabAnticlockwise);
                    fab_playground.setClickable(false);
                    isFabOpen = false;

                } else {
                    fab_playground.startAnimation(fabOpen);
                    fab.startAnimation(fabClockwise);
                    fab_playground.setClickable(true);
                    isFabOpen = true;
                }*/


            }


        });

        fabMyLocation = (FloatingActionButton) findViewById(R.id.fab_my_location);

        fabMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLocationZoom(locLat, locLong, 15);
            }


        });

        //final EditText textSearch = (EditText)findViewById(R.id.search_edit_text);
        //final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rr);
       // relativeLayout.setBackgroundColor(Color.argb(150, 255, 255, 255));
        //textSearch.setTextColor(Color.LTGRAY);


       textSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                textSearch.setCursorVisible(true);
            }
        });
/*
        textSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                   // relativeLayout.setBackgroundColor(Color.rgb(255, 255, 255));
                  // textSearch.setTextColor(Color.GRAY);
                    Toast.makeText(MainActivity.this, "true", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(imm.isActive()){
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                       }
                   // relativeLayout.setBackgroundColor(Color.argb(150, 255, 255, 255));
                   // textSearch.setTextColor(Color.LTGRAY);
                    textSearch.setText("");
                    Toast.makeText(MainActivity.this, "false", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        textSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        geoLocate(textSearch);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }

        });

        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                buttonSearch.setBackgroundResource(R.drawable.ic_search_grey_500_36dp);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if (value.length() > 0) {
                    buttonSearch.setBackgroundResource(R.drawable.ic_search_light_green_600_36dp);
                }else{
                    buttonSearch.setBackgroundResource(R.drawable.ic_search_grey_500_36dp);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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
        }
    }

    private void checkNetwork(boolean is3g, boolean isWifi, View view) {
        if (!is3g && !isWifi) {
            buildAlertMessageNoNet();
        }
    }

    private void addNewPlayground(LatLng latlang, String playgroundTitle, String playgroundDescription, float playgroundRate, String markerId) {

        MarkerAdapter markerAdapter = new MarkerAdapter(getLayoutInflater());
        markerAdapter.setRate(playgroundRate);

        mGoogleMap.setInfoWindowAdapter(markerAdapter);

        MarkerOptions markers = new MarkerOptions()
                .position(latlang)
                .title(String.format(playgroundTitle))
                .snippet(playgroundDescription);

        if (playgroundRate <= 1.5) {
            markers.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_border_red_600_36dp));
        } else if (playgroundRate <=2.5) {
            markers.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_half_orange_500_36dp));
        } else if (playgroundRate <= 3.5) {
            markers.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_half_yellow_500_36dp));
        } else if (playgroundRate <= 4.5) {
            markers.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_half_light_green_300_36dp));
        } else if (playgroundRate > 4.5) {
            markers.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_light_green_500_36dp));
        } else {
            markers.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star_border_red_600_36dp));
        }

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

        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
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
        switch (item.getItemId()) {
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Toast.makeText(MainActivity.this ,"1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_comments) {
            Toast.makeText(MainActivity.this ,"2", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this ,"3", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(MainActivity.this ,"About", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_map) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.nav_sattelite) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }else if (id == R.id.nav_hybrid) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

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

        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);

        View locationCompass = ((View)findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationCompass.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 160,0, 0);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(locLat, locLong)).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        getPlaygroundFromDatabase();
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
    }



    @Override
    protected void onPause(){
        super.onPause();
        lm.removeUpdates(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
       refreshLocationInfo();
    }

    @Override
    protected void onStart(){
        super.onStart();
        refreshLocationInfo();
    }



    @Override
    public void onLocationChanged(Location location) {
            mGoogleMap.clear();
            refreshLocationInfo();
            getPlaygroundFromDatabase();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        refreshLocationInfo();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ll)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void geoLocate(View view) throws IOException {

        if(pp.getMenu().size()>0){
            pp.getMenu().clear();
        }

        if
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(!Objects.equals(textSearch.getText().toString(), "")) {

                String location = textSearch.getText().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                Geocoder gc = new Geocoder(this);
                List<Address> list = gc.getFromLocationName(location, 20);//, locLat-1,locLong-10,locLat+1, locLong+10);



              for(int i=0; i<list.size(); i++) {
                   pp.getMenu().add(String.valueOf(list.get(i).getAddressLine(0))+", "+
                                            String.valueOf(list.get(i).getAdminArea()));
               }

                pp.getMenuInflater().inflate(R.menu.tip_menu, pp.getMenu());

                pp.show();

                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
               // goToLocationZoom(lat, lng, 15);

                textSearch.setText("");
                textSearch.clearFocus();
                textSearch.setCursorVisible(false);
                textSearch.setFocusable(false);
                textSearch.setFocusableInTouchMode(true);

            }
            else{
                Toast.makeText(this, "Wpisz lokalizację", Toast.LENGTH_LONG).show();
            }
        }
    }

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        static final String PACKAGE_NAME =
                "com.google.android.gms.location.sample.locationaddress";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }

    public void hideNavigationDrawer() {
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onMarkerClick(Marker marker){
        String markerPosition, playgroundPosition;
        MarkerAdapter markerAdapter = new MarkerAdapter(getLayoutInflater());
        markerPosition = String.valueOf(marker.getPosition());

        for(int i=0;i<10;i++){
            playgroundPosition = String.valueOf(new LatLng(playgroundList[i].playgroundLat, playgroundList[i].playgroundLong));
            if(Objects.equals(markerPosition, playgroundPosition)){
                           markerAdapter.setRate(playgroundList[i].rate);
            }
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition()).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        mGoogleMap.setInfoWindowAdapter(markerAdapter);
        marker.showInfoWindow();
        return true;

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }


}

