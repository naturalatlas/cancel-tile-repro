package com.naturalatlas.canceltilereproapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.CustomGeometrySource;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TOPO_STYLE_URL = "https://naturalatlas-gl.global.ssl.fastly.net/styles/cotrex-topo-v3.json";
    private static final String SATELLITE_STYLE_URL = "https://naturalatlas-gl.global.ssl.fastly.net/styles/cotrex-topo-v3.json";


    private MapView mapView;
    private MyGeometryProvider geometryProvider;
    private CustomGeometrySource customSource;
    private LineLayer customLayer;
    private MapboxMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);

        geometryProvider = new MyGeometryProvider();

        customSource = new CustomGeometrySource("my-custom-source", geometryProvider);

        customLayer = new LineLayer("my-custom-layer", "my-custom-source");
        customLayer.withProperties(
                PropertyFactory.lineWidth(2.0f),
                PropertyFactory.lineColor("#FF0000"),
                PropertyFactory.lineOpacity(0.5f)
        );

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setMinZoomPreference(4);
                map.getUiSettings().setRotateGesturesEnabled(false);

                final Style.Builder styleBuilder = new Style.Builder();
                styleBuilder.fromUri(TOPO_STYLE_URL);

                mapboxMap.setStyle(styleBuilder, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        Log.d(TAG, "Adding Custom Source");
                        style.addSource(customSource);
                        style.addLayer(customLayer);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "Removing Custom Source (" + geometryProvider.getPendingRequestCount() + " pending requests)");
                                        map.getStyle().removeLayer(customLayer);
                                        map.getStyle().removeSource(customSource);
                                    }
                                });
                            }
                        }, 2000);
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
