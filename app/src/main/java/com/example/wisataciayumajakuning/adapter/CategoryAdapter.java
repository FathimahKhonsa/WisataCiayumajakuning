package com.example.wisataciayumajakuning.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.databinding.ItemCategoryBinding;
import com.example.wisataciayumajakuning.model.Wisata;
import com.example.wisataciayumajakuning.wisata.DetailWisataActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context context;
    private List<Wisata> list;

    public CategoryAdapter(Context context, List<Wisata> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        Wisata wisata = list.get(position);
        if (wisata != null){
            holder.bind(wisata);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
       private final ItemCategoryBinding binding;
        public MyViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Wisata wisata){
            binding.namaWisata.setText(wisata.getName());
            binding.kotaWisata.setText(wisata.getCity());
            binding.alamatWisata.setText(wisata.getAddress());

            Glide.with(this.itemView).load(wisata.getImage()).centerCrop().into(binding.wisataImg);

            int position = getAdapterPosition();
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailWisataActivity.class);
                intent.putExtra("img", list.get(position).getImage());
                intent.putExtra("name", list.get(position).getName());
                intent.putExtra("city", list.get(position).getCity());
                intent.putExtra("description", list.get(position).getDescription());
                intent.putExtra("address", list.get(position).getAddress());
                intent.putExtra("lat", list.get(position).getLat());
                intent.putExtra("lng", list.get(position).getLng());
                intent.putExtra("umum", list.get(position).getTiketUmum());
                intent.putExtra("anak", list.get(position).getTiketAnak());
                intent.putExtra("pelajar", list.get(position).getTiketPelajar());
                intent.putExtra("dewasa", list.get(position).getTiketDewasa());
                intent.putExtra("operasional", list.get(position).getJamOperasional());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
