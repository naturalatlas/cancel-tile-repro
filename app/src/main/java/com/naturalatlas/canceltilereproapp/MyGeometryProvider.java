package com.naturalatlas.canceltilereproapp;


import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.style.sources.GeometryTileProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyGeometryProvider implements GeometryTileProvider {
    private static final String TAG = "MyGeometryProvider";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int pendingRequestCount = 0;

    @Override
    public FeatureCollection getFeaturesForBounds(final LatLngBounds bounds, final int zoomLevel) {
        final List<Feature> features = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        pendingRequestCount++;

        // Imitate a slow asynchronous fetch from a database
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}

                List<Point> pts = new ArrayList<>();
                pts.add(Point.fromLngLat(bounds.getNorthWest().getLongitude(), bounds.getNorthWest().getLatitude()));
                pts.add(Point.fromLngLat(bounds.getNorthEast().getLongitude(), bounds.getNorthEast().getLatitude()));
                pts.add(Point.fromLngLat(bounds.getSouthEast().getLongitude(), bounds.getSouthEast().getLatitude()));
                pts.add(Point.fromLngLat(bounds.getSouthWest().getLongitude(), bounds.getSouthWest().getLatitude()));
                pts.add(Point.fromLngLat(bounds.getNorthWest().getLongitude(), bounds.getNorthWest().getLatitude()));
                Polygon polygon = Polygon.fromLngLats(Collections.singletonList(pts));
                features.add(Feature.fromGeometry(polygon));
                latch.countDown();
            }
        });

        // Block until fetch from database thread finishes
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return FeatureCollection.fromFeatures(Collections.<Feature>emptyList());
        }

        pendingRequestCount--;
        return FeatureCollection.fromFeatures(features);
    }

    public int getPendingRequestCount() {
        return pendingRequestCount;
    }
}



