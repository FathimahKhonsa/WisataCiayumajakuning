package com.example.wisataciayumajakuning.wisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wisataciayumajakuning.AllConstant;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityDirectionBinding;
import com.example.wisataciayumajakuning.databinding.BottomInformationBinding;
import com.example.wisataciayumajakuning.databinding.LayoutAdressItemBinding;
import com.example.wisataciayumajakuning.model.DirectionModel.DirectionLegModel;
import com.example.wisataciayumajakuning.model.DirectionModel.DirectionResponseModel;
import com.example.wisataciayumajakuning.model.DirectionModel.DirectionRouteModel;
import com.example.wisataciayumajakuning.model.DirectionModel.DirectionStepModel;
import com.example.wisataciayumajakuning.permission.AppPermission;
import com.example.wisataciayumajakuning.webservice.RetrofitAPI;
import com.example.wisataciayumajakuning.webservice.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.util.Assert;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback{
    private ActivityDirectionBinding binding;
    private GoogleMap gMap;
 //   private GeofencingClient geofencingClient;
 //   private float geoFenceRadius = 10000;
 //   private LatLng latLng = new LatLng(-6.7320229, 108.5523164);
    private AppPermission appPermission;
    private Location currentLocation;
    private boolean isLocationPermissionOk;
    private Double endLat, endLng, outLat, outLng;
    private RetrofitAPI retrofitAPI;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomInformationBinding informationBinding;
    private LayoutAdressItemBinding adressItemBinding;
    private KmlLayer layer;
    private boolean contains = false;
    private LatLng latLngTest;
    private List<LatLng> outerBoundary = new ArrayList<>();
    private List<LatLng> areas = new ArrayList<>();
    private double distance;

//    private int currentMode;
//    private Wisata wisata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appPermission = new AppPermission();

        endLat = Double.valueOf(getIntent().getStringExtra("lat"));
        endLng = Double.valueOf(getIntent().getStringExtra("lng"));

        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);

        informationBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(informationBinding.getRoot());

        adressItemBinding = binding.layoutAddress;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        areas.add(new LatLng(-6.24864, 107.92192));
        areas.add(new LatLng(-6.22543, 108.19932));
        areas.add(new LatLng(-6.24727, 108.34626));
        areas.add(new LatLng(-6.48066, 108.53441));
        areas.add(new LatLng(-6.51675, 108.53962));
        areas.add(new LatLng(-6.76302, 108.67391));
        areas.add(new LatLng(-6.73078, 108.83920));
        areas.add(new LatLng(-6.83216, 108.82284));
        areas.add(new LatLng(-7.11180, 108.76882));
        areas.add(new LatLng(-7.19492, 108.56145));
        areas.add(new LatLng(-7.07154, 108.38696));
        areas.add(new LatLng(-7.06745, 108.15487));
        areas.add(new LatLng(-7.03883, 108.13702));
        areas.add(new LatLng(-6.92706, 108.21942));
        areas.add(new LatLng(-6.82208, 108.15762));
        areas.add(new LatLng(-6.75116, 108.14938));
        areas.add(new LatLng(-6.66153, 108.02676));
        areas.add(new LatLng(-6.57149, 107.85098));
//        geofencingClient = LocationServices.getGeofencingClient(this);  //1
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    //    this.gMap = googleMap;
        if (appPermission.isLocationOK(this)){
            isLocationPermissionOk = true;
            setupGoogleMap();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("This app required location permission to access your location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appPermission.requestLocationPermission(DirectionActivity.this);
                            }
                        })
                        .create().show();
            } else {
                appPermission.requestLocationPermission(DirectionActivity.this);
            }
        }
    }


    private void getDirection(Double lat, Double lng){
        if (isLocationPermissionOk){
            binding.progressBar.setVisibility(View.VISIBLE);
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + lat + "," + lng +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=driving" +
                    "&key=" + getResources().getString(R.string.API_KEY);

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    Log.d("TAG", "onResponse: " + res);

                    if (response.errorBody() == null) {
                        if (response.body() != null) {
//                            clearUI();

                            if (response.body().getDirectionRouteModels().size() > 0){
                                DirectionRouteModel ruteModel = response.body().getDirectionRouteModels().get(0);
                                //getSupportActionBar().setTitle(ruteModel.getSummary());

                                DirectionLegModel legModel = ruteModel.getLegs().get(0);
                                adressItemBinding.currentLocation.setText(legModel.getStartAddress());
                                adressItemBinding.endLocation.setText(legModel.getEndAddress());

                                String jarak = legModel.getDistance().getText();

                                try {
                                    distance = Double.parseDouble(jarak);
                                }catch (NumberFormatException e){
                                    Toast.makeText(DirectionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                informationBinding.txtTime.setText("Waktu tempuh: " + legModel.getDuration().getText());
                                informationBinding.txtDistance.setText("Jarak tempuh: " + jarak);


                                gMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng())))
                                        .setTitle("Start Location");

                                gMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng())))
                                        .setTitle("End Location");

                                List<LatLng> stepList = new ArrayList<>();

                                PolylineOptions options = new PolylineOptions().width(15)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);

                                List<PatternItem> pattern;
                                pattern = Arrays.asList(new Dash(30));

                                options.pattern(pattern);

                                for (DirectionStepModel stepModel : legModel.getSteps()){
                                    List<com.google.android.gms.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                    for (com.google.android.gms.maps.model.LatLng latLng : decodedLatLng){
                                        stepList.add(new LatLng(latLng.latitude, latLng.longitude));
                                    }
                                }

                                options.addAll(stepList);

                                Polyline polyline = gMap.addPolyline(options);

                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                LatLng endLocation = new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng());

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(startLocation).include(endLocation);

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 17);
                                gMap.animateCamera(cameraUpdate);

                                //gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
                            } else {
                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("TAG", "onResponse: " + response);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {

                }
            });
        }
    }

    private void retrieveFromResource(){
        try {
            layer = new KmlLayer(gMap, R.raw.ciayumajakuning, this);
    //        layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //    try {

    //        GeoJsonLayer layer = new GeoJsonLayer(gMap, R.raw.area_boundaries, this);
    //        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
    //        polygonStyle.setStrokeColor(Color.RED);
    //        polygonStyle.setStrokeWidth(4);
    //        layer.addLayerToMap();
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    } catch (XmlPullParserException e) {
    //        e.printStackTrace();
    //    }
    }



    private void clearUI() {
        gMap.clear();
        retrieveFromResource();

        adressItemBinding.currentLocation.setText("");
        adressItemBinding.endLocation.setText("");
        informationBinding.txtTime.setText("");
        informationBinding.txtDistance.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isLocationPermissionOk = true;
                setupGoogleMap();
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupGoogleMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        gMap.setMyLocationEnabled(true);
        getCurrentLocation();
//        addMarker(latLng);
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED){
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
//
                    clearUI();

                    boolean isInside = ifUserInside(new LatLng(location.getLatitude(), location.getLongitude()));

                    if (isInside){
                        Toast.makeText(DirectionActivity.this, "You are in the area of Ciayumajakuning", Toast.LENGTH_SHORT).show();
                        getDirection(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(DirectionActivity.this, "You are outside the area of Ciayumajakuning", Toast.LENGTH_SHORT).show();

                        LatLng nearestPoint = findPoint(new LatLng(location.getLatitude(), location.getLongitude()), areas);
//                        LatLng nearestPoint = nearest_point(new LatLng(location.getLatitude(), location.getLongitude()), areas);
                        Toast.makeText(DirectionActivity.this, "Nearest Point : " + nearestPoint, Toast.LENGTH_SHORT).show();
                        getDirection(nearestPoint.latitude, nearestPoint.longitude);
//                        getDirection(nearestPoint.latitude, nearestPoint.longitude);
//                        Toast.makeText(DirectionActivity.this, "Nearest point: " + nearestPoint, Toast.LENGTH_SHORT).show();
                        }
                } else {
                    Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean ifUserInside(LatLng latLngTest) {
        if (layer.getContainers() != null){
            for (KmlContainer container : layer.getContainers()){
                if (container.getPlacemarks() != null){
                    for (KmlPlacemark placemark : container.getPlacemarks()){
                        contains = false;

                        if(placemark.getGeometry() instanceof KmlPolygon){
                            KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();

                            outerBoundary = polygon.getOuterBoundaryCoordinates();

                            contains = PolyUtil.containsLocation(latLngTest, outerBoundary, true);
//                            listOut.addAll(outerBoundary);
//                            Log.e("Tag", String.valueOf(listOut));

                            if (contains){
                                List<List<LatLng>> innerBoundaries = polygon.getInnerBoundaryCoordinates();
                                if (innerBoundaries != null){
                                    for (List<LatLng> innerBoundary : innerBoundaries){
                                        if (PolyUtil.containsLocation(latLngTest, innerBoundary, true)){
                                            contains = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return contains;
    }


//    private void addMarker(LatLng latLng){                                          //2
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
//        gMap.addMarker(markerOptions);
//    }

//    private void addCircle(LatLng latLng, float radius){                            //3
//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(latLng);
//        circleOptions.radius(radius);
//        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
//        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
//        circleOptions.strokeWidth(4);
//        gMap.addCircle(circleOptions);
//    }

//    public static double haversine(double lat1, double lng1, double lat2, double lng2 ){
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);

//        lat1 = Math.toRadians(lat1);
//        lat2 = Math.toRadians(lat2);

//        double a = Math.pow(Math.sin(dLat/2), 2) + Math.pow(Math.sin(dLng/2), 2) * Math.cos(lat1) * Math.cos(lat2);
//        double rad = 6371;
//        double c = 2 * Math.asin(Math.sqrt(a));
//        return rad * c;
//    }

//    public LatLng nearest_point(LatLng test, List<LatLng> target){
//        double minDistance = distance;
//        LatLng minDistancePoint = test;

//        for (int i = 0; i < target.size(); i++){
//            LatLng point = target.get(i);
//            double distance = haversine(test.latitude, test.longitude, point.latitude, point.longitude);

//            if (distance < minDistance){
//                minDistance = distance;
//                minDistancePoint = point;
//            }
//        }
//        return minDistancePoint;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    private LatLng findPoint(LatLng test, List<LatLng> target){
        double distance = 100000;
        LatLng minDistancePoint = test;

        if (test == null || target == null){
            return minDistancePoint;
        }

        for (int i = 0; i < target.size(); i++){
            LatLng point = target.get(i);

            int segmentPoint = i + 1;
            if (segmentPoint >= target.size()){
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(test, point, target.get(segmentPoint));
            if (distance == 100000 || currentDistance < distance){
                distance = currentDistance;
                minDistancePoint = findNearestPoint(test, point, target.get(segmentPoint));
            }
        }
        return minDistancePoint;
    }

    private LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end){
        if (start.equals(end)){
            return start;
        }

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));
    }


    private List<com.google.android.gms.maps.model.LatLng> decode(String points) {
        int len = points.length();

        final List<com.google.android.gms.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.android.gms.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }
        return path;
    }
}

