package com.example.wisataciayumajakuning.wisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wisataciayumajakuning.AllConstant;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.adapter.DirectionStepAdapter;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback{
    private ActivityDirectionBinding binding;
    private GoogleMap gMap;
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
    private double distance;
    private DirectionStepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //menghubungkan objek kelas appPemission ke AppPermission
        appPermission = new AppPermission();

        endLat = Double.valueOf(getIntent().getStringExtra("lat"));
        endLng = Double.valueOf(getIntent().getStringExtra("lng"));

        //membuat instance retrofit client
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);

        informationBinding = binding.bottomSheet;
        bottomSheetBehavior = (BottomSheetBehavior.from(informationBinding.getRoot()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //menghubungkan objek kelas adapter ke adapter
        adapter = new DirectionStepAdapter();
        //menampilkan recycler view dengan layout linear
        informationBinding.rvSteps.setLayoutManager(new LinearLayoutManager(this));
        //menghubungkan kelas adapter ke recycler view
        informationBinding.rvSteps.setAdapter(adapter);

        adressItemBinding = binding.layoutAddress;

        //mengelola siklus proses peta dan merupakan elemen induk dari UI aplikasi
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        //mengecek apakah user sudah memberikan izin untuk akses lokasi
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
            //mendapatkan
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + lat + "," + lng +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=driving" + "&language=id" +
                    "&key=" + getResources().getString(R.string.API_KEY);

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    Log.d("TAG", "onResponse: " + res);

                    //untuk memetakan kelas model ke respons -> response.body( )
                    if (response.errorBody() == null) {
                        if (response.body() != null) {
                            if (response.body().getDirectionRouteModels().size() > 0){
                                DirectionRouteModel ruteModel = response.body().getDirectionRouteModels().get(0);

                                DirectionLegModel legModel = ruteModel.getLegs().get(0);
                                adressItemBinding.currentLocation.setText(legModel.getStartAddress());
                                adressItemBinding.endLocation.setText(legModel.getEndAddress());
                                //mendapatkan jarak
                                String jarak = legModel.getDistance().getText();

                                try {
                                    distance = Double.parseDouble(jarak);
                                }catch (NumberFormatException e){
                                    Toast.makeText(DirectionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                                                                      //mendapatkan waktuh tempuh
                                informationBinding.txtTime.setText("Waktu tempuh: " + legModel.getDuration().getText());
                                informationBinding.txtDistance.setText("Jarak tempuh: " + jarak);
                                //menambahkan penanda berwarna biru pada peta untuk menunjukkan lokasi awal
                                gMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                                        .setTitle("Start Location");
                                //menambahkan penanda berwarna merah pada peta untuk menunjukkan lokasi tujuan
                                gMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng())))
                                        .setTitle("End Location");

                                //menampilkan langkah - langkah menuju lokasi objek wisata
                                adapter.setDirectionStepModelList(legModel.getSteps());

                                List<LatLng> stepList = new ArrayList<>();

                                PolylineOptions options = new PolylineOptions().width(15)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);

                                for (DirectionStepModel stepModel : legModel.getSteps()){
                                    List<LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                    for (LatLng latLng : decodedLatLng){
                                        //menambahkan titik - titik koordinat yang diperoleh dari proses decode ke dalam stepList
                                        stepList.add(new LatLng(latLng.latitude, latLng.longitude));
                                    }
                                }

                                //menambahkan simpul / titik - titik ke ujung polyline yang sedang dibangun
                                options.addAll(stepList);

                                //menambahkan polyline pada peta untuk menggambarkan rute
                                Polyline polyline = gMap.addPolyline(options);
                                //mendapatkan posisi awal user
                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                //mendapatkan posisi akhir atau lokasi tujuan
                                LatLng endLocation = new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng());

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(startLocation).include(endLocation);

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 17);
                                gMap.animateCamera(cameraUpdate);

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
            layer = new KmlLayer(gMap, R.raw.ciayumajakuning_area, this);
            //menampilkan batas wilayah Ciayumajakuning pada peta
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        //memanggil method getCurrentLocation( ) untuk mendapatkan lokasi user
        getCurrentLocation();
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
                    clearUI();

                //    double lat = -6.7141875;
                //    double lng = 108.5391875;
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    latLngTest = new LatLng(lat, lng);

                    boolean isInside = ifUserInside();
                    //mengecek apakah user ada di wilayah ciayumajakuning atau tidak
                    if (isInside){
                        Toast.makeText(DirectionActivity.this, "You are in the area of Ciayumajakuning", Toast.LENGTH_SHORT).show();
                        //mengambil rute ketika user ada di wilayah Ciayumajakuning
                        getDirection(lat, lng);
                    } else {
                        Toast.makeText(DirectionActivity.this, "You are outside the area of Ciayumajakuning", Toast.LENGTH_SHORT).show();
                        //mendapatkan titik batas wilayah Ciayumajakuning yang terdekat dengan user
                        LatLng nearestPoint = findPoint(latLngTest, outerBoundary);
                        Toast.makeText(DirectionActivity.this, "Nearest Point : " + nearestPoint, Toast.LENGTH_SHORT).show();
                        //mengambil rute ketika user di luar wilayah Ciayumajakuning
                        getDirection(nearestPoint.latitude, nearestPoint.longitude);
                        }
                } else {
                    Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean ifUserInside() {
        if (layer.getContainers() != null){
            //mendapatkan container
            for (KmlContainer container : layer.getContainers()){
                if (container.getPlacemarks() != null){
                    for (KmlPlacemark placemark : container.getPlacemarks()){
                        contains = false;

                        if(placemark.getGeometry() instanceof KmlPolygon){
                            KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();

                            //mendapatkan batas luar polygon
                            outerBoundary = polygon.getOuterBoundaryCoordinates();
                            //memeriksa apakah titik uji terletak di dalam polygon
                            contains = containsLocation(latLngTest, outerBoundary);

                            if (contains){
                                //mendapatkan batas dalam polygon dan memeriksa apakah titik uji terletak di dalam polygon
                                List<List<LatLng>> innerBoundaries = polygon.getInnerBoundaryCoordinates();
                                if (innerBoundaries != null){
                                    //jika titik uji terletak di lubang, maka polygon tidak memuat lokasi
                                    for (List<LatLng> innerBoundary : innerBoundaries){
                                        if (containsLocation(latLngTest, innerBoundary)){
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

    public static boolean containsLocation(LatLng point, List<LatLng> polygon){
        int intersectCount = 0;
        for (int j = 0; j < polygon.size() - 1; j++){
            if (rayCastIntersect(point, polygon.get(j), polygon.get(j + 1))){
                intersectCount++;
            }
        }
        return ((intersectCount % 2) == 1); //odd is inside, even is outside
    }

    private static boolean rayCastIntersect(LatLng point, LatLng vertA, LatLng vertB){
        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = point.latitude;
        double pX = point.longitude;

        if ((aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX)){
            return false;
        }
        double m = (aY - bY) / (aX - bX);
        double bee = (-aX)*m + aY;
        double x = (pY - bee) / m;

        return  x > pX;
    }
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
        double distance = -1;
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
            if (distance == -1 || currentDistance < distance){
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

    //mengurai atau mendekode string titik - titik koordinat menjadi barisan lat lng
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

