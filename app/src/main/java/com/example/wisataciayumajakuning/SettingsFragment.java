package com.example.wisataciayumajakuning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.databinding.FragmentSettingsBinding;
import com.example.wisataciayumajakuning.model.User;
import com.example.wisataciayumajakuning.user.EditProfileActivity;
import com.example.wisataciayumajakuning.user.LoginUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String username, email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //menginialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //mendapatkan current user
        currentUser = mAuth.getCurrentUser();

        //mengecek apakah user telah melakukan login atau belum
        if (currentUser == null){
            Toast.makeText(getActivity(), "Terjadi kesalahan, tidak dapat memuat data user", Toast.LENGTH_SHORT).show();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            //menampilkan data user
            showUserData();
        }
        //mengambil url foto user
        Uri uri = currentUser.getPhotoUrl();
        //memuat foto profil user
        Glide.with(this).load(uri).centerCrop().into(binding.imgProfile);

        binding.logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mengeluarkan user dari aplikasi
                FirebaseAuth.getInstance().signOut();
                //mengarahkan user ke halaman login
                Intent intent = new Intent(getContext(), LoginUserActivity.class);
                startActivity(intent);
            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mengarahkan user ke halaman edit profile
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.aboutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mengarahkan user ke halaman about
                Intent intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }



    private void showUserData(){
        //mendapatkan id user
        String uId = currentUser.getUid();
        //mendapatkan referensi database users
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //mengambil data user berdasarkan id user di database
        reference.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mendapatkan objek user dan menggunakan nilainya untuk update UI
                User user = snapshot.getValue(User.class);
                if (user != null){
                    username = user.getUsername();
                    email = currentUser.getEmail();
                    //memuat data user di halaman settings
                    binding.username.setText(username);
                    binding.emailTv.setText(email);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Terjadi kesalahan, tidak dapat memuat data user", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility((View.GONE));
            }
        });
    }
}