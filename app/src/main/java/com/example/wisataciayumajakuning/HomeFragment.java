package com.example.wisataciayumajakuning;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.adapter.InfoWindowAdapter;
import com.example.wisataciayumajakuning.adapter.WisataAdapter;
import com.example.wisataciayumajakuning.databinding.FragmentHomeBinding;
import com.example.wisataciayumajakuning.model.Marker;
import com.example.wisataciayumajakuning.model.User;
import com.example.wisataciayumajakuning.model.Wisata;
import com.example.wisataciayumajakuning.permission.AppPermission;
import com.example.wisataciayumajakuning.wisata.CategoryWisataActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleMap gMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView rvWisata;
    private ImageView alam, pantai, sejarah, kuliner, market;
    private List<Wisata> wisataList = new ArrayList<>();
    private List<LatLng> latLngList = new ArrayList<>();
    private WisataAdapter adapter;
    private MarkerOptions markerOptions;
    private String username, profileImg;
    private AppPermission permission;
    private boolean isPermissionLocationOk;
    private DatabaseReference markersReference;
    private KmlLayer layer;


    public HomeFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //menginialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //mendapatkan current user
        currentUser = mAuth.getCurrentUser();
        //mendapatkan referensi database markers
        markersReference = FirebaseDatabase.getInstance().getReference("Markers");

        //membuat objek kelas permission ke AppPermission
        permission = new AppPermission();

        //mengecek apakah user sudah melalui proses login
        if (currentUser != null){
            userProfile();
            //mendapatkan url foto profil user
            Uri uri = currentUser.getPhotoUrl();
            //menampilkan foto profile user di halaman utama (home)
            Glide.with(this).load(uri).centerCrop().into(binding.profileImg);
        }else {
            Toast.makeText(getActivity(), "Terjadi kesalahan, tidak dapat memuat data profile", Toast.LENGTH_SHORT).show();
        }

        //menginisialisasi firebase firestore
        db = FirebaseFirestore.getInstance();

        binding.categoryAlam.setOnClickListener(this);
        binding.categoryPantai.setOnClickListener(this);
        binding.categoryLainnya.setOnClickListener(this);
        binding.categoryOlehOleh.setOnClickListener(this);
        binding.categorySejarah.setOnClickListener(this);

        //mengelola siklus proses peta dan merupakan elemen induk dari UI aplikasi
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        rvWisata = binding.rvWisata;
        //memanggil method getDataWisata( ) untuk menampilkan data wisata
        getDataWisata();
        //supaya recycler view dapat melakukan optimasi ukuran lebar dan tinggi secara otomatis
        rvWisata.setHasFixedSize(true);

        LinearLayoutManager linear = new LinearLayoutManager(getContext());
        linear.setOrientation(LinearLayoutManager.HORIZONTAL);
        //menampilkan recyclerview dengan view linear secara horizontal untuk memposisikan item
        rvWisata.setLayoutManager(linear);
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        //mengecek apakah user sudah memberikan izin untuk akses lokasi
        if (permission.isLocationOK(requireContext())){
            isPermissionLocationOk = true;
            setUpGoogleMap();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission")
                    .setMessage("This app required location permission to show you the tourist attraction")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocation();
                        }
                    }).create().show();
        }else {
            requestLocation();
        }
        retrieveFromResource();
        //memanggil method showMarker( ) untuk menampilkan penanda - penanda berwarna merah
        //yang melambangkan posisi lokasi objek wisata
        showMarker();
    }

    private void retrieveFromResource(){
        try {
            layer = new KmlLayer(gMap, R.raw.ciayumajakuning_area, getContext());
            //menampilkan batas wilayah Ciayumajakuning pada peta
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void userProfile() {
        //mengambil user id
        String uId = currentUser.getUid();

        binding.progressBar.setVisibility(View.VISIBLE);
        //mengambil referensi database user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mengambil data user berdasarkan id
                User user = snapshot.getValue(User.class);
                if (user != null){
                    //mendapatkan username user
                    username = user.getUsername();
                    binding.usernameTv.setText("Welcome " + username);
                }
                binding.progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Terjadi kesalahan, tidak dapat memuat username", Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDataWisata(){
        binding.progressBar.setVisibility(View.VISIBLE);
        //mengambil semua koleksi data wisata di firestore
        db.collection("wisata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        //mengambil semua dokumen data wisata
                        Wisata wisata = documentSnapshot.toObject(Wisata.class);
                        //menambahkan data wisata yang telah diperoleh ke dalam list
                        wisataList.add(wisata);
                    }
                    //menghubungkan objek kelas adapter ke adapter
                    adapter = new WisataAdapter(getContext(), wisataList);
                    //memberitahu bahwa dataset telah berubah
                    adapter.notifyDataSetChanged();
                    //menghubungkan kelas adapter ke recyclerview
                    rvWisata.setAdapter(adapter);
                } else {
                    Log.w("HomeFragment", "loadPost:OnCancelled", task.getException());
                }
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        //mengambil id view berdasarkan katagori
        int id = v.getId();
        if (id == R.id.categoryAlam){
            //memindahkan user ke halaman katagori wisata
            Intent intent = new Intent(getActivity(), CategoryWisataActivity.class);
            //mengirimkan type data wisata berdasarkan key (alam)
            intent.putExtra(CategoryWisataActivity.EXTRA_TYPE, "alam");
            startActivity(intent);
        } else if (id == R.id.categoryLainnya) {
            Intent intent = new Intent(getActivity(), CategoryWisataActivity.class);
            //mengirimkan type data wisata berdasarkan key (lainnya)
            intent.putExtra(CategoryWisataActivity.EXTRA_TYPE, "lainnya");
            startActivity(intent);
        } else if (id == R.id.categoryPantai) {
            Intent intent = new Intent(getActivity(), CategoryWisataActivity.class);
            //mengirimkan type data wisata berdasarkan key (pantai)
            intent.putExtra(CategoryWisataActivity.EXTRA_TYPE, "pantai");
            startActivity(intent);
        } else if (id == R.id.categorySejarah) {
            Intent intent = new Intent(getActivity(), CategoryWisataActivity.class);
            //mengirimkan type data wisata berdasarkan key (sejarah)
            intent.putExtra(CategoryWisataActivity.EXTRA_TYPE, "sejarah");
            startActivity(intent);
        } else if (id == R.id.categoryOlehOleh) {
            Intent intent = new Intent(getActivity(), CategoryWisataActivity.class);
            //mengirimkan type data wisata berdasarkan key (oleh - oleh)
            intent.putExtra(CategoryWisataActivity.EXTRA_TYPE, "oleh-oleh");
            startActivity(intent);
        }
    }

    private void requestLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AllConstant.LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //mengecek apakah user mengizinkan aplikasi mengakses lokasi user
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionLocationOk = true;
                setUpGoogleMap();
            } else {
                isPermissionLocationOk = false;
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            isPermissionLocationOk = false;
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setTiltGesturesEnabled(true);
        //memanggil method setUpLocationUpdate( ) untuk mengatur pembaruan lokasi user
        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null){
                    for (Location location : locationResult.getLocations()){
                        Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            isPermissionLocationOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(requireContext(), "Location Update Started", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //memanggil method getCurrentLocation( ) untuk mengambil lokasi user
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            isPermissionLocationOk = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //mendapatkan lokasi user
                currentLocation = location;
            }
        });
    }

    private void showMarker(){
        //mendapatkan referensi database markers di firebase real time database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Markers");
        //membaca data dan memproses perubahan
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //mendapatkan objek marker dan menggunakan nilainya untuk update UI
                    Marker mark = dataSnapshot.getValue(Marker.class);
                    //mendapatkan latitude dan longtitude dari data yang telah disimpan pada objek
                    LatLng latLng = new LatLng(mark.getLat(), mark.getLng());
                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng).title(mark.getName())
                            .snippet(mark.getCity())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //menambahkan penanda lokasi objek wisata pada peta
                    gMap.addMarker(markerOptions);
                    //mengatur jendela info
                    gMap.setInfoWindowAdapter(new InfoWindowAdapter(getContext()));

                    gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull com.google.android.gms.maps.model.Marker marker) {
                            //menampilkan info window ketika penanda lokasi wisata di klik
                            marker.showInfoWindow();
                            return true;
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}