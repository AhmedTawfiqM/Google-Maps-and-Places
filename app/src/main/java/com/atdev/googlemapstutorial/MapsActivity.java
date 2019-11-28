package com.atdev.googlemapstutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atdev.googlemapstutorial.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    //
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUESTED_CODE = 1234;
    private boolean isLocationPermitionsGrnated = false;
    private static final float DEFAULT_ZOOM = 15f;

    //
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
    }

    private void init() {

        etSearch = findViewById(R.id.input_search);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {

                if (actionID == EditorInfo.IME_ACTION_DONE
                        || actionID == EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //EXecute Our Method for Search
                    geoLocate();
                }
                return false;
            }
        });

    }

    private void geoLocate() {

        String searhTxt = etSearch.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocationName(searhTxt, 1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            Toast.makeText(this, "Found Location  " + address, Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (isLocationPermitionsGrnated) {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Successful");

                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: FAiled Tasl Location Current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException s) {
            Log.d(TAG, "getDeviceLocation: ERROR : " + s.getMessage());
        }

    }


    private void moveCamera(LatLng latLng, float zoom) {

        Log.d(TAG, "moveCamera: Moving Camera to lat:" + latLng.latitude + "  lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {

        Log.d(TAG, "initMap: intialize MaP");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission() {

        Log.d(TAG, "getLocationPermission: getting location permisttion");
        String[] permitions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                isLocationPermitionsGrnated = true;

                //
                initMap();

            } else {

                ActivityCompat.requestPermissions(this,
                        permitions, LOCATION_PERMISSION_REQUESTED_CODE);
            }

        } else {

            ActivityCompat.requestPermissions(this,
                    permitions, LOCATION_PERMISSION_REQUESTED_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //isLocationPermitionsGrnated = false;
        Log.d(TAG, "onRequestPermissionsResult: ReuestPermission");

        if (requestCode == LOCATION_PERMISSION_REQUESTED_CODE) {

            if (grantResults.length > 0) {

                //Loop on All permissions
                for (int i = 0; i < grantResults.length; i++) {

                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isLocationPermitionsGrnated = false;
                        Log.d(TAG, "onRequestPermissionsResult: Failed to permission");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: Success Granted Permissions");
                isLocationPermitionsGrnated = true;
                //init Our Map Here
                initMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(MapsActivity.this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (isLocationPermitionsGrnated) {
            getDeviceLocation();

            //Add Marker Blue and Add top Button for Retrieve to my location after navigation in map
            mMap.setMyLocationEnabled(true);
            //but we must remove that button on the top to make search abr for all places
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

}
