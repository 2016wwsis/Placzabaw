package com.tpanpm.wwsis.placzabaw;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.google.android.gms.maps.GoogleMap.*;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, OnInfoWindowClickListener, OnMarkerClickListener, OnMapClickListener,OnInfoWindowCloseListener {


    Animation fabOpen, fabClose, fabClockwise, fabAnticlockwise;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    double locLong;
    double locLat;
    LocationManager lm;
    Criteria kr;
    String najlepszyDostawca;
    Location loc = null;
    FloatingActionButton fab, fab_playground, fabMyLocation, fab_navigation;
    boolean isFabOpen;
    InputMethodManager imm;
    EditText textSearch;
    ImageButton buttonSearch;
    PopupMenu pp;
    ListView listOfLocation;
    String positionForNavigate;
    RelativeLayout relativeLayout;
    Playground playground;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String EXTRA_MESSAGE1 = "MESSAGE1";
    public final static String EXTRA_MESSAGE2 = "MESSAGE2";
    public float rate;
    public String markerId;
    HashMap<String,Marker> hashMapMarker = new HashMap<>();
    HashMap<String,Playground> hashMapPlayground = new HashMap<>();

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

        if (mGoogleMap!=null){
            MarkerOptions markers = new MarkerOptions()
                    .position(new LatLng(locLat,locLong))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_indigo_700_48dp));

            Marker marker = mGoogleMap.addMarker(markers);
            markerId = marker.getId();
            hashMapMarker.put(markerId,marker);
        }
    }

    private void getPlaygroundFromDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("playgrounds");

        final ArrayList<Playground> playgroundArrayList = new ArrayList<>();

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,  playgroundArrayList);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Playground playground = new Playground(dataSnapshot.getValue(Playground.class).getDESC(),
                        dataSnapshot.getValue(Playground.class).getIMAGE(),
                        dataSnapshot.getValue(Playground.class).getLAT(),
                        dataSnapshot.getValue(Playground.class).getLON(),
                        dataSnapshot.getValue(Playground.class).getNAME(),
                        dataSnapshot.getValue(Playground.class).getRATE(),
                        "",
                        MainActivity.this
                );
                playgroundArrayList.add(playground);
                for (Playground data:playgroundArrayList) {
                    Playground playGround = new Playground(data.getDESC(), "", data.getLAT(),
                            data.getLON(),
                            data.getNAME(),
                            data.getRATE(),
                            "",
                            MainActivity.this
                    );

                    hashMapPlayground.put(String.valueOf(new LatLng(playGround.getLAT(), playGround.getLON())),
                            playGround);

                    addNewPlayground(new LatLng(data.getLAT(),
                                    data.getLON()),
                            data.getNAME(),
                            data.getDESC(),
                            data.getRATE());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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
        listOfLocation = (ListView)findViewById(R.id.list_view);
        relativeLayout = (RelativeLayout) findViewById(R.id.rr);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_playground = (FloatingActionButton) findViewById(R.id.fab_playground);
        fab_navigation = (FloatingActionButton) findViewById(R.id.fab_navigation);
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
                String locationName = String.valueOf(item.getTitle());
                goToCheckLocation(locationName);
                hideKeyboard();
                clearEditText();
                return true;
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddPlayground.class);
                intent.putExtra(EXTRA_MESSAGE2, locLong);
                intent.putExtra(EXTRA_MESSAGE1, locLat);
                startActivity(intent);

            }


        });

        fabMyLocation = (FloatingActionButton) findViewById(R.id.fab_my_location);

        fabMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hashMapMarker.isEmpty()){
                    refreshLocationInfo();
                    goToLocationZoom(locLat, locLong, 15);
                }else{
                    Marker marker = hashMapMarker.get(markerId);
                    marker.remove();
                    hashMapMarker.remove(markerId);
                    refreshLocationInfo();
                    goToLocationZoom(locLat, locLong, 15);
                }

            }


        });

        fab_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+positionForNavigate);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }


        });

        textSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                textSearch.setCursorVisible(true);
            }
        });

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
                }
                else
                {
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

    private void goToCheckLocation(String locationName) {
        Toast.makeText(MainActivity.this, locationName, Toast.LENGTH_SHORT).show();

        Address address;
        List<Address> list;
        pp.getMenu().clear();

        Geocoder gc = new Geocoder(MainActivity.this);

        try {
            list = gc.getFromLocationName(locationName, 1);
            address = list.get(0);
            double lat = address.getLatitude();
            double lng = address.getLongitude();
            goToLocationZoom(lat, lng, 15);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void addNewPlayground(LatLng latlang, String playgroundTitle, String playgroundDescription, float playgroundRate) {

        MarkerAdapter markerAdapter = new MarkerAdapter(getLayoutInflater());
        mGoogleMap.setInfoWindowAdapter(markerAdapter);

        MarkerOptions markers = new MarkerOptions()
                .position(latlang)
                .title(playgroundTitle)
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

        // Marker marker =
        mGoogleMap.addMarker(markers);
        // hashMapMarker.put(String.valueOf(marker.getPosition()),marker);

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
                mGoogleMap.setMapType(MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(MAP_TYPE_HYBRID);
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

        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent intent = new Intent(this, ListOfPlaygroundActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_comments) {

            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan a barcode");
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this ,"3", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(MainActivity.this ,"About", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_photo) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }else if (id == R.id.nav_map) {
            mGoogleMap.setMapType(MAP_TYPE_NORMAL);
        } else if (id == R.id.nav_sattelite) {
            mGoogleMap.setMapType(MAP_TYPE_SATELLITE);
        }else if (id == R.id.nav_hybrid) {
            mGoogleMap.setMapType(MAP_TYPE_HYBRID);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
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
        mGoogleMap.setOnInfoWindowCloseListener(this);
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
                .target(ll)
                .zoom(zoom)
                .bearing(0)
                .tilt(0)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void geoLocate(View view) throws IOException {

        clearPopupListOfLocation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(!Objects.equals(textSearch.getText().toString(), "")) {

                String location = textSearch.getText().toString();

                Geocoder gc = new Geocoder(this);
                List<Address> list = gc.getFromLocationName(location, 20);

                if(list.size()==1){
                    Address address = list.get(0);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();
                    goToLocationZoom(lat, lng, 15);
                    hideKeyboard();
                    clearEditText();
                }
                else{
                    createPopupListOfLocation(list);
                }
            }
            else{
                Toast.makeText(this, "Wpisz lokalizację", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void clearEditText(){
        textSearch.setText("");
        textSearch.clearFocus();
        textSearch.setCursorVisible(false);
        textSearch.setFocusable(false);
        textSearch.setFocusableInTouchMode(true);
    }

    public void createPopupListOfLocation(List<Address> list){
        for(int i=0; i<list.size(); i++) {
            pp.getMenu().add(String.valueOf(list.get(i).getAddressLine(0))+", "+
                    String.valueOf(list.get(i).getAdminArea()));
        }
        pp.getMenuInflater().inflate(R.menu.tip_menu, pp.getMenu());
        pp.show();
    }

    private void clearPopupListOfLocation() {
        if(pp.getMenu().size()>0){
            pp.getMenu().clear();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onMarkerClick(Marker marker){

        if(Objects.equals(String.valueOf(marker.getId()), markerId)){
            if(marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            }
            return true;
        }

        positionForNavigate = String.valueOf(marker.getPosition().latitude)+","+String.valueOf(marker.getPosition().longitude);
        rate = hashMapPlayground.get(String.valueOf(marker.getPosition())).getRATE();
        final MarkerAdapter markerAdapter = new MarkerAdapter(getLayoutInflater());
        markerAdapter.setRate(rate);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition()).zoom(20).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        mGoogleMap.setInfoWindowAdapter(markerAdapter);
        marker.showInfoWindow();
        return true;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, InfoPlaygroundActivity.class);
        startActivity(intent);
        Toast.makeText(this, "This is on click method"+"\n"+String.valueOf(marker.getPosition().latitude)+"\n"+String.valueOf(marker.getPosition().longitude), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }


    @Override
    public void onInfoWindowClose(Marker marker) {

    }

}

