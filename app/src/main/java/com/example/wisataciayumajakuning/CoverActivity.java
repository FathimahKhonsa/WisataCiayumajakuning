package com.example.wisataciayumajakuning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.wisataciayumajakuning.databinding.ActivityCoverBinding;
import com.example.wisataciayumajakuning.user.LoginUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CoverActivity extends AppCompatActivity {
    private ActivityCoverBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //menginialisasi firebase authentication
        mAuth = FirebaseAuth.getInstance();

        binding.btnMulai.setOnClickListener(v -> {
            //mengantarkan user ke halaman login
            Intent intent = new Intent(CoverActivity.this, LoginUserActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mendapatkan current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //mengecek user sudah login atau belum, jika sudah maka user langsung berada di halaman utama
        if (currentUser != null){
            reload();
        } else {
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void reload(){
        Intent intent = new Intent(CoverActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}