package com.example.wisataciayumajakuning.wisata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityDetailWisataBinding;

public class DetailWisataActivity extends AppCompatActivity {
    private ActivityDetailWisataBinding binding;
    String wisataImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailWisataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.namaWisata.setText(getIntent().getStringExtra("name"));
        binding.kotaWisata.setText(getIntent().getStringExtra("city"));
        binding.deskripsiWisata.setText(getIntent().getStringExtra("description"));
        binding.tempatWisata.setText(getIntent().getStringExtra("address"));
        wisataImg = getIntent().getStringExtra("img");

        Glide.with(this).load(wisataImg).centerCrop().into(binding.wisataImg);
    }
}