package com.example.wisataciayumajakuning.wisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.HomeFragment;
import com.example.wisataciayumajakuning.ProfileFragment;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.SettingsFragment;
import com.example.wisataciayumajakuning.adapter.CategoryAdapter;
import com.example.wisataciayumajakuning.databinding.ActivityCategoryWisataBinding;
import com.example.wisataciayumajakuning.model.Wisata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryWisataActivity extends AppCompatActivity {
    private ActivityCategoryWisataBinding binding;
    private FirebaseFirestore db;
    public static final String EXTRA_TYPE = "extra_type";
    private RecyclerView rvWisata;
    private List<Wisata> list = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Toolbar toolbar;
    private CategoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryWisataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //menginialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //mendapatkan currentUser
        currentUser = mAuth.getCurrentUser();

        binding.backBtn.setOnClickListener(v -> {
            //kembali ke halaman utama aplikasi
            Fragment fragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
        });

        //menampilkan foto profile user
        Glide.with(this).load(currentUser.getPhotoUrl()).centerCrop().into(binding.profileImg);

        rvWisata = binding.rvWisata;
        //menginialisasi firebase cloud firestore
        db = FirebaseFirestore.getInstance();

        //supaya recycler view dapat melakukan optimasi ukuran lebar dan tinggi secara otomatis
        rvWisata.setHasFixedSize(true);
        //menampilkan recyclerview dengan view linear untuk memposisikan item
        rvWisata.setLayoutManager(new LinearLayoutManager(this));

        //mengambil type (katagori) berdasarkan key
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        if (type != null){
            if (Objects.equals(type, "alam")){
                getData(type);
                binding.categoryTv.setText("Wisata Alam");
            } else if (Objects.equals(type, "pantai")) {
                getData(type);
                binding.categoryTv.setText("Wisata Pantai");
            } else if (Objects.equals(type, "sejarah")) {
                getData(type);
                binding.categoryTv.setText("Wisata Sejarah");
            } else if (Objects.equals(type, "lainnya")) {
                getData(type);
                binding.categoryTv.setText("Wisata Lainnya");
            } else if (Objects.equals(type, "oleh-oleh")) {
                getData(type);
                binding.categoryTv.setText("Oleh - Oleh");
            }
        }

    }

    private void getData(String type){
        binding.progressBar.setVisibility(View.VISIBLE);
        //mengambil koleksi data wisata di firestore berdasarkan type (katagori)
        db.collection("wisata").whereEqualTo("type", type).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                //mengambil dokumen data wisata yang sesuai dengan katagori
                                Wisata wisata = documentSnapshot.toObject(Wisata.class);
                                //menambahkan data wisata ke dalam list
                                list.add(wisata);
                            }
                            //menghubungkan objek kelas adapter ke adapter
                            adapter = new CategoryAdapter(CategoryWisataActivity.this, list);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.menu_profile);
//        View view = MenuItemCompat.getActionView(menuItem);

//        CircleImageView profileImg = (CircleImageView) view.findViewById(R.id.profile_layout);

    //    ImageView profileImg = view.findViewById(R.id.profileImg_layout);

//        Glide.with(this).load(currentUser.getPhotoUrl()).centerCrop().into(profileImg);

//        profileImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new ProfileFragment();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
//            }
//        });

     //   profileImg.setOnClickListener(new View.OnClickListener() {
     //       @Override
     //       public void onClick(View v) {
     //           Fragment fragment = new ProfileFragment();
     //           FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
     //           fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
     //       }
     //   });

//        return super.onCreateOptionsMenu(menu);
 //   }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.menu_home){
//            Fragment fragment = new HomeFragment();
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
//        }  else if (id == R.id.menu_settings) {
//            Fragment fragment = new SettingsFragment();
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment).commit();
//        }
//        return super.onOptionsItemSelected(item);
//    }


}