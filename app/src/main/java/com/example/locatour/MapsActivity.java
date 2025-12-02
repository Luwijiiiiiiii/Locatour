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

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView;
    private static final int REQUEST_LOCATION = 100;

    // TARGET POINT IN BAGUIO CITY
    private final GeoPoint targetPoint = new GeoPoint(16.410461, 120.594424);

    private GeoPoint userPoint;
    private MyLocationNewOverlay locationOverlay;

    // GraphHopper API KEY
    private final String GRAPH_HOPPER_KEY = "4d0e85e7-7cca-4063-a954-b89b87a63942";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.getController().setZoom(14.5);
        mapView.getController().setCenter(targetPoint);

        addTargetMarker();
        requestLocationPermission();
    }

    // --------------------------------------------------------------------------
    // PERMISSION HANDLING
    // --------------------------------------------------------------------------

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

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
    // ENABLE USER LOCATION + GRAPH HOPPER ROUTING
    // --------------------------------------------------------------------------

    private void enableUserLocation() {
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        FusedLocationProviderClient fused = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fused.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapView.getController().setCenter(userPoint);

                requestRouteFromGraphHopper(userPoint, targetPoint);
            }
        });
    }

    // --------------------------------------------------------------------------
    // GRAPH HOPPER ROUTING REQUEST
    // --------------------------------------------------------------------------

    private void requestRouteFromGraphHopper(GeoPoint start, GeoPoint end) {

        String url = "https://graphhopper.com/api/1/route?"
                + "point=" + start.getLatitude() + "," + start.getLongitude()
                + "&point=" + end.getLatitude() + "," + end.getLongitude()
                + "&profile=foot"               // WALKING (change to car/bike if needed)
                + "&points_encoded=false"
                + "&key=" + GRAPH_HOPPER_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String body = response.body().string();
                    drawRouteFromJson(body);
                }
            }
        });
    }

    // --------------------------------------------------------------------------
    // DRAW ROUTE ON OSM MAP
    // --------------------------------------------------------------------------

    private void drawRouteFromJson(String json) {

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray coords = obj
                    .getJSONArray("paths")
                    .getJSONObject(0)
                    .getJSONObject("points")
                    .getJSONArray("coordinates");

            Polyline polyline = new Polyline();
            polyline.setWidth(8);

            for (int i = 0; i < coords.length(); i++) {
                JSONArray c = coords.getJSONArray(i);
                double lon = c.getDouble(0);
                double lat = c.getDouble(1);
                polyline.addPoint(new GeoPoint(lat, lon));
            }

            runOnUiThread(() -> {
                mapView.getOverlays().add(polyline);
                mapView.invalidate();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
