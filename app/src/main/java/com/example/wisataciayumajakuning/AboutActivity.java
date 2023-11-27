package com.example.wisataciayumajakuning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.wisataciayumajakuning.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}