package com.example.wisataciayumajakuning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wisataciayumajakuning.databinding.CustomInfoWindowBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public InfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        CustomInfoWindowBinding binding = CustomInfoWindowBinding.inflate(LayoutInflater.from(context), null, false);
        binding.name.setText(marker.getTitle());
        binding.city.setText(marker.getSnippet());
        return binding.getRoot();
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
