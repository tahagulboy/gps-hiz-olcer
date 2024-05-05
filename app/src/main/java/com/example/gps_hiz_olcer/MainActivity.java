package com.example.gps_hiz_olcer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private TextView textSpeed;
    private Location previousLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSpeed = findViewById(R.id.text_speed);

        // GPS izni alınması
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (previousLocation == null) {
                        previousLocation = location;
                        return;
                    }

                    // Hız hesaplaması
                    float distanceInMeters = previousLocation.distanceTo(location);
                    float timeInSeconds = (location.getTime() - previousLocation.getTime()) / 1000f; // milliseconds to seconds
                    float speedInMetersPerSecond = distanceInMeters / timeInSeconds;
                    float speedInKmPerHour = speedInMetersPerSecond * 3.6f; // Meters per second to km/h
                    textSpeed.setText("Hız: " + String.format("%.2f", speedInKmPerHour) + " km/s");

                    previousLocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }
            });
        }
    }
}