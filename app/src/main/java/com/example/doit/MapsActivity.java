package com.example.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.LocationListener;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.compat.Place;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** A map class which enable users get into the GoogleMap service to get locations.
 * Using MapFragment to demonstrate a simple map model.
 * *Using googleMapApi to get user's location and move camera to destination.
 *
 * -Lue Cai, 14/10/2019
 * **/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    //the static variable that get the requirements
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;

    //Widget
    private EditText mSearchText;
    private ImageView mGps;
    private Button mSelect;

    //the location data that want to give back
    private String location;

    private LatLng destination;
    private LatLng current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //get elements
        mSearchText=(EditText)findViewById(R.id.input_search);
        mGps=(ImageView)findViewById(R.id.ic_gps);
        mSelect=(Button)findViewById(R.id.btn_select);
        //if choosing a location, sent the location back to EventActivity
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location=(String)mSearchText.getText().toString();
                Intent intent=new Intent();
                intent.putExtra("location",location);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Code adapted from Google Map platform: https://developers.google.com/maps/documentation/android-sdk/map-with-marker
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //if mGoogleApiClient is null, initiate one
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //require for authority
        createLocationRequest();
    }

    //initialize the current location and find the destination
    //This was adapted from a video from Codingwithmitch on 20171010 to Youtube here:
    //https://www.youtube.com/watch?v=MWowf5SkiOE&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=6
    private void init(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH
                        || i==EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        || keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){
                    getLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpMap();
            }
        });
    }
    //get desination's location
    //This was adapted from a video from Codingwithmitch on 20171010 to Youtube here:
    //https://www.youtube.com/watch?v=MWowf5SkiOE&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=6
    private void getLocate(){
        String searchString =mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> list =new ArrayList<>();
        try {
            list=geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
        }
        if (list.size()>0){
            Address address=list.get(0);
            LatLng newPlace = new LatLng(address.getLatitude(), address.getLongitude());  // this is destination
            destination=newPlace;
            mMap.addMarker(new MarkerOptions().position(newPlace).title(address.getAddressLine(0)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace,12));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //map ui setting
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //connect the server when start
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }
    //if user have't have authority, require or set the currenlocation
    //This was adapted from a post from YiHeYuan on 20170304 to CSDN forum here:
    //https://blog.csdn.net/kmyhy/article/details/60344699
    private void setUpMap() {
        //get permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        //check if location valuable
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            //get the newest location
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //set the camera focus on that location
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                current=currentLocation;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
    }
    //put a red marker on the map
    protected void placeMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        //adding location's information
        String titleStr = getAddress(location);
        markerOptions.title(titleStr);

        mMap.addMarker(markerOptions);
    }

    //return a readable address from a given location
    //This was adapted from a post from YiHeYuan on 20170304 to CSDN forum here:
    //https://blog.csdn.net/kmyhy/article/details/60344699
    private String getAddress( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( this );
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {
        }
        return addressText;
    }
    //require permission
    //This was adapted from a post from YiHeYuan on 20170304 to CSDN forum here:
    //https://blog.csdn.net/kmyhy/article/details/60344699
    protected void startLocationUpdates() {
        //require permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        //get the location and require update
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    //a basic setting about location, allow phone detecting moving
    //This was adapted from a post from YiHeYuan on 20170304 to CSDN forum here:
    //https://blog.csdn.net/kmyhy/article/details/60344699
    protected void createLocationRequest() {
        //build a request
        mLocationRequest = new LocationRequest();
        //a basic request setting
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        mLocationUpdateState = true;
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    //override the onActivityResult method in fragmentActivity and get location information
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocationUpdateState = true;
                startLocationUpdates();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mLocationUpdateState) {
            startLocationUpdates();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        if (mLocationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //make sure the marker is in the scene.
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (null != mLastLocation) {
            placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

/***
 * A method that calculate the distance between destination and current location
 * **/
    public float distance ()
    {
        if (current==null){
            setUpMap();
        }
        if (destination==null){
            init();
        }
        double lat_a=current.latitude;
        double lng_a=current.longitude;
        double lat_b=destination.latitude;
        double lng_b=destination.longitude;
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }
}
