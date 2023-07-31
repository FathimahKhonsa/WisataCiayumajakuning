package com.example.wisataciayumajakuning.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wisataciayumajakuning.AllConstant;

public class AppPermission {
    public boolean isLocationOK(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AllConstant.LOCATION_REQUEST_CODE);
    }
}
