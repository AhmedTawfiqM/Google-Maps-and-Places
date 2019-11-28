package com.atdev.googlemapstutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.atdev.googlemapstutorial.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int ERROR_KEY = 1209;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (isServicesOk()) {
            init();
        }
    }

    private void init() {

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

    public boolean isServicesOk() {

        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (availability == ConnectionResult.SUCCESS) {

            Log.d(TAG, "isServicesOk: Avaialable OK");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(availability)) {

            Log.d(TAG, "isServicesOk: Error occured but we Can fix It");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, availability, ERROR_KEY);
            dialog.show();

        } else {
            Toast.makeText(this, "you Cant make Map Requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
