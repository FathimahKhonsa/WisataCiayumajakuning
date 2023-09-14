package com.example.wisataciayumajakuning.wisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.example.wisataciayumajakuning.model.Wisata;
import com.example.wisataciayumajakuning.permission.AppPermission;
import com.example.wisataciayumajakuning.webservice.RetrofitAPI;
import com.example.wisataciayumajakuning.webservice.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.protobuf.DescriptorProtos;

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
    private Double endLat, endLng;
    private RetrofitAPI retrofitAPI;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomInformationBinding informationBinding;
    private LayoutAdressItemBinding adressItemBinding;
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

//        geofencingClient = LocationServices.getGeofencingClient(this);  //1
    }

    private void getDirection(String mode){
        if (isLocationPermissionOk){
            binding.progressBar.setVisibility(View.VISIBLE);
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=" + mode +
                    "&key=" + getResources().getString(R.string.API_KEY);

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    Log.d("TAG", "onResponse: " + res);

                    if (response.errorBody() == null) {
                        if (response.body() != null) {
                            clearUI();

                            if (response.body().getDirectionRouteModels().size() > 0){
                                DirectionRouteModel ruteModel = response.body().getDirectionRouteModels().get(0);
                                //getSupportActionBar().setTitle(ruteModel.getSummary());

                                DirectionLegModel legModel = ruteModel.getLegs().get(0);
                                adressItemBinding.currentLocation.setText(legModel.getStartAddress());
                                adressItemBinding.endLocation.setText(legModel.getEndAddress());

                                informationBinding.txtTime.setText("Waktu tempuh: " + legModel.getDuration().getText());
                                informationBinding.txtDistance.setText("Jarak tempuh: " + legModel.getDistance().getText());

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

    private void clearUI() {
        gMap.clear();
        adressItemBinding.currentLocation.setText("");
        adressItemBinding.endLocation.setText("");
        informationBinding.txtTime.setText("");
        informationBinding.txtDistance.setText("");
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
        showArea();
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
                    getDirection("driving");
                } else {
                    Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   private void showArea(){
       Polygon polygon = gMap.addPolygon(new PolygonOptions()
               .add(new LatLng(-6.69143, 108.56110),
                       new LatLng(-6.69635, 108.54265),
                       new LatLng(-6.72808, 108.54485),
                       new LatLng(-6.73656, 108.51868),
                       new LatLng(-6.78197, 108.53108),
                       new LatLng(-6.79091, 108.54302),
                       new LatLng(-6.74386, 108.56568),
                       new LatLng(-6.74865, 108.58802)));
       polygon.setStrokeColor(Color.RED);
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

