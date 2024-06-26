package com.example.wisataciayumajakuning.wisata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.R;
import com.example.wisataciayumajakuning.databinding.ActivityDetailWisataBinding;

public class DetailWisataActivity extends AppCompatActivity {
    private ActivityDetailWisataBinding binding;

    String wisataImg, lat, lng, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailWisataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        name = getIntent().getStringExtra("name");
        binding.namaWisata.setText(name);
        binding.headlineWisata.setText(getIntent().getStringExtra("headline"));
        binding.kotaWisata.setText(getIntent().getStringExtra("city"));
        binding.deskripsiWisata.setText(getIntent().getStringExtra("description"));
        binding.tempatWisata.setText(getIntent().getStringExtra("address"));
        binding.tiketAnak.setText(getIntent().getStringExtra("anak"));
        binding.tiketDewasa.setText(getIntent().getStringExtra("dewasa"));
        binding.tiketUmum.setText(getIntent().getStringExtra("umum"));
        binding.tiketPelajar.setText(getIntent().getStringExtra("pelajar"));
        binding.jamOperasional.setText(getIntent().getStringExtra("operasional"));
        wisataImg = getIntent().getStringExtra("img");
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");

        Glide.with(this).load(wisataImg).centerCrop().into(binding.wisataImg);

        binding.fabRoute.setOnClickListener(v -> {
            Intent intent = new Intent(DetailWisataActivity.this, DirectionActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("name", name);
            startActivity(intent);
        });
    }
}