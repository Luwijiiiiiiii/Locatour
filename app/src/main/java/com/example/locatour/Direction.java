package com.example.locatour;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class Direction extends AppCompatActivity {

    private MapView mapView;
    private static final int REQUEST_LOCATION = 100;

    // TARGET POINT IN BAGUIO CITY
    private final GeoPoint targetPoint = new GeoPoint(16.411914, 120.597213);

    private GeoPoint userPoint;         // Store user location
    private Polyline routeLine;         // Line from user → target
    private MyLocationNewOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_maps);
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Default zoom to Baguio area
        mapView.getController().setZoom(14.5);
        mapView.getController().setCenter(targetPoint);

        addTargetMarker();
        requestLocationPermission();
    }

    // --------------------------------------------------------------------------
    // PERMISSION HANDLING
    // --------------------------------------------------------------------------

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

        } else {
            enableUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            enableUserLocation();
        }
    }

    // --------------------------------------------------------------------------
    // ENABLE USER LOCATION + DRAW ROUTE LINE
    // --------------------------------------------------------------------------

    private void enableUserLocation() {

        // Blue location dot overlay
        locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(this), mapView);

        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        // Use Fused Provider for fast, accurate location
        FusedLocationProviderClient fused =
                LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fused.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                // Auto center on user
                mapView.getController().setCenter(userPoint);

                // Draw connecting line
                drawLineToTarget();

                // Print distance
                double distance = calculateDistance(
                        userPoint.getLatitude(),
                        userPoint.getLongitude(),
                        targetPoint.getLatitude(),
                        targetPoint.getLongitude()
                );

                System.out.println("Distance to target: " + distance + " meters");
            }
        });
    }

    // --------------------------------------------------------------------------
    // DRAW LINE FROM USER → TARGET
    // --------------------------------------------------------------------------

    private void drawLineToTarget() {

        if (routeLine != null) {
            mapView.getOverlays().remove(routeLine);
        }

        routeLine = new Polyline();
        routeLine.setWidth(8f);
        routeLine.setGeodesic(true); // Makes it more accurate

        routeLine.addPoint(userPoint);
        routeLine.addPoint(targetPoint);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();
    }

    // --------------------------------------------------------------------------
    // TARGET MARKER
    // --------------------------------------------------------------------------

    private void addTargetMarker() {
        Marker marker = new Marker(mapView);
        marker.setPosition(targetPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Target Location (Baguio)");
        mapView.getOverlays().add(marker);
    }

    // --------------------------------------------------------------------------
    // DISTANCE CALCULATION
    // --------------------------------------------------------------------------

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
