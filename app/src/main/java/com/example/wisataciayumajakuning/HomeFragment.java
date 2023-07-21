package com.example.wisataciayumajakuning;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wisataciayumajakuning.adapter.WisataAdapter;
import com.example.wisataciayumajakuning.databinding.FragmentHomeBinding;
import com.example.wisataciayumajakuning.model.Wisata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private RecyclerView rvWisata;
    private ImageView alam, pantai, sejarah, kuliner, market;
    private List<Wisata> wisataList = new ArrayList<>();
    private WisataAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        rvWisata = binding.rvWisata;
        getDataWisata();
        rvWisata.setHasFixedSize(true);
        rvWisata.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return root;
    }

    private void getDataWisata(){
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("wisata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        Wisata wisata = documentSnapshot.toObject(Wisata.class);
                        wisataList.add(wisata);
                    }
                    adapter = new WisataAdapter(getContext(), wisataList);
                    adapter.notifyDataSetChanged();
                    rvWisata.setAdapter(adapter);
                } else {
                    Log.w("HomeFragment", "loadPost:OnCancelled", task.getException());
                }
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}