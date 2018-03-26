package com.example.shailesh.bloodbankapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shailesh.bloodbankapp.Facts.FactsActivity;
import com.example.shailesh.bloodbankapp.FirebaseDatabase.DonorDatabase;
import com.example.shailesh.bloodbankapp.FirebaseDatabase.Donor;
import com.example.shailesh.bloodbankapp.Login.LoginActivity;
import com.example.shailesh.bloodbankapp.Login.Register;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener {

    public static final int REQUEST_LOCATION_CODE = 99;
    private static final String TAG = "ViewDatabase";
    private static final String ALL_BLOOD_GROUPS = "ALL";
    //NavigationView Variable
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    NavigationView navigationView;
    //Floating action buttons
    FloatingActionButton fabRoot, fab1, fab2, fab3, fab4,fab5,fab6,fab7,fab8;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;
    Marker marker;
    //MAp Activities variables
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;

    //retrieving from database
    private Location lastlocation;
    private Marker currentLocationmMarker;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    //private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);


        ChildEventListener mChildEventListener;
        //floating buttons actions
        fabRoot = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);
        fab6 = (FloatingActionButton) findViewById(R.id.fab6);
        fab7 = (FloatingActionButton) findViewById(R.id.fab7);
        fab8 = (FloatingActionButton) findViewById(R.id.fab8);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        fab1.setOnClickListener(getEventListenerForBloodType("O+"));
        fab2.setOnClickListener(getEventListenerForBloodType("B+"));
        fab3.setOnClickListener(getEventListenerForBloodType("A+"));
        fab4.setOnClickListener(getEventListenerForBloodType("AB+"));
        fab5.setOnClickListener(getEventListenerForBloodType("O-"));
        fab6.setOnClickListener(getEventListenerForBloodType("B-"));
        fab7.setOnClickListener(getEventListenerForBloodType("A-"));
        fab8.setOnClickListener(getEventListenerForBloodType("AB-"));



        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Bank");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Firebase databaseReference
        mUsers = FirebaseDatabase.getInstance().getReference("users");
        mUsers.push().setValue(marker);

    }

    @NonNull
    private View.OnClickListener getEventListenerForBloodType(final String bloodTypeValue) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDonorLocationsFor(bloodTypeValue);
            }
        };
    }

    private void animateFab() {
        FloatingActionButton arrayOfFabs[] = {fab1, fab2, fab3, fab4,fab5,fab6,fab7,fab8};
        for (FloatingActionButton fButton : arrayOfFabs) {
            if (isOpen) {
                fabRoot.startAnimation(rotateForward);
                fButton.startAnimation(fabClose);
                fButton.setClickable(false);

            } else {
                fabRoot.startAnimation(rotateBackward);
                fButton.startAnimation(fabOpen);
                fButton.setClickable(true);
            }
        }
        isOpen = !isOpen;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMarkerClickListener(this);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);//
        // last seelcted bloood group type?
        showDonorLocationsFor(ALL_BLOOD_GROUPS);
    }

    public void showDonorLocationsFor(String bloodType) {
        Query query = this.mUsers.orderByChild("bloodType");
        if (bloodType != ALL_BLOOD_GROUPS) {
            query = query.equalTo(bloodType);
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // add element to list of elements
                mMap.clear();
                for (DataSnapshot singleDataRow : dataSnapshot.getChildren()) {
                    Donor donor = singleDataRow.getValue(Donor.class);
                    Log.d(TAG, "Value is: " + donor);
                    Log.d(TAG, "bloodType: " + donor.getBloodType());
                    addMarkerForUser(donor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addMarkerForUser(Donor donor) {
        Geocoder gc = new Geocoder(MainActivity.this);
        List<Address> list = null;
        try {
            list = gc.getFromLocationName(donor.donorAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Some error occurred in reading donor address for:" + donor.name );
            // toaster - network error try again
            return ;
        }
        if (list.size() == 0) {
            System.out.println("No address returned for given donor:" + donor.name );
            return;
        }
        Address address = list.get(0);
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        MarkerOptions options = new MarkerOptions()
                .title(donor.name)
                .snippet(donor.phoneNo)
                .position(new LatLng(lat, lng));
        mMap.addMarker(options);
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if (currentLocationmMarker != null) {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ", "" + latitude);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(13));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    public void onClick(View v) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch (v.getId()) {
            case R.id.button:
                EditText tf_location = (EditText) findViewById(R.id.searchAddress);
                String location = tf_location.getText().toString();
                List<Address> addressList;


                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if (addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.hospital:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                break;


            case R.id.bloodbank:
                mMap.clear();
                String school = "Blood Bank";
                url = getUrl(latitude, longitude, school);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainActivity.this, "Showing Nearby BloodBanks", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyCo_N8XuUYknH4qZJnirDz4wosUitQnIOw");

        Log.d("MainActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_login:
                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.nav_register:
                Toast.makeText(MainActivity.this, "Register", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Register.class));
                break;
            case R.id.nav_usrProfile:
                Toast.makeText(MainActivity.this, "User Profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), DonorDatabase.class));
                break;
            case R.id.nav_facts:
                Toast.makeText(MainActivity.this, "Facts", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), FactsActivity.class));
                break;


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
