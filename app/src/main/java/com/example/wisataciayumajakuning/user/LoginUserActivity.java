package com.example.wisataciayumajakuning.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.wisataciayumajakuning.MainActivity;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityLoginUserBinding;

public class LoginUserActivity extends AppCompatActivity {
    private ActivityLoginUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerUser.setOnClickListener(v -> {
            Intent intent = new Intent(LoginUserActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        });
    }
}