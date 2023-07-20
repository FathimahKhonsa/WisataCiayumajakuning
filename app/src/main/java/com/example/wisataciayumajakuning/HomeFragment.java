package com.example.wisataciayumajakuning;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wisataciayumajakuning.databinding.FragmentHomeBinding;
import com.example.wisataciayumajakuning.model.Wisata;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private RecyclerView rvWisata;
    private ImageView alam, pantai, sejarah, kuliner, market;
    private List<Wisata> wisataList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}