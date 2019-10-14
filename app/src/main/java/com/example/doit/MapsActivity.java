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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;

    private static final String TAG="MapsActivity";

    //Widget
    private EditText mSearchText;
    private ImageView mGps;
    private Button mSelect;

    //the data that want to give back
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText=(EditText)findViewById(R.id.input_search);
        mGps=(ImageView)findViewById(R.id.ic_gps);
        mSelect=(Button)findViewById(R.id.btn_select);
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

        createLocationRequest();
    }

    private void init(){
        Log.d(TAG, "init: initialiazing");

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
                Log.d(TAG,"onClick: clicked gps icon");
                setUpMap();
            }
        });
    }

    private void getLocate(){
        Log.d(TAG,"geoLocate: geolocating");

        String searchString =mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> list =new ArrayList<>();
        try {
            list=geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG, "geloLocate: IOException: "+ e.getMessage());
        }
        if (list.size()>0){
            Address address=list.get(0);
            Log.d(TAG, "geoLocate: found a location: "+ address.toString());
            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();
            LatLng newPlace = new LatLng(address.getLatitude(), address.getLongitude());  // this is New York
            mMap.addMarker(new MarkerOptions().position(newPlace).title(address.getAddressLine(0)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace,12));
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
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
    }

    protected void placeMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        //adding location's information
        String titleStr = getAddress(location);
        markerOptions.title(titleStr);

        mMap.addMarker(markerOptions);
    }

    //return a readable address from a given location
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
                    // 4
                    case LocationSettingsStatusCodes.SUCCESS:
                        mLocationUpdateState = true;
                        startLocationUpdates();
                        break;
                    // 5
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    // 6
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    //override the onActivityResult method in fragmentActivity
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

    // 2
    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // 3
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
        //if mLocationUpdateState true, start update user current location
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
}
