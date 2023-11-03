package com.example.wisataciayumajakuning.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisataciayumajakuning.databinding.ItemWisataBinding;
import com.example.wisataciayumajakuning.model.Wisata;
import com.example.wisataciayumajakuning.wisata.DetailWisataActivity;

import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.MyViewHolder> {
    private Context context;
    private List<Wisata> wisataList;

    public WisataAdapter(Context context, List<Wisata> wisataList) {
        this.context = context;
        this.wisataList = wisataList;
    }

    @NonNull
    @Override
    public WisataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWisataBinding binding = ItemWisataBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WisataAdapter.MyViewHolder holder, int position) {
        Wisata wisata = wisataList.get(position);
        if (wisata != null){
            holder.bind(wisata);
        }
    }

    @Override
    public int getItemCount() {
        return wisataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemWisataBinding binding;
        public MyViewHolder(ItemWisataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Wisata wisata){
            binding.namaWisata.setText(wisata.getName());
            binding.cityWisata.setText(wisata.getCity());

            Glide.with(this.itemView).load(wisata.getImage()).centerCrop().into(binding.wisataImg);

            int position = getAdapterPosition();
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailWisataActivity.class);
                intent.putExtra("img", wisataList.get(position).getImage());
                intent.putExtra("name", wisataList.get(position).getName());
                intent.putExtra("city", wisataList.get(position).getCity());
                intent.putExtra("description", wisataList.get(position).getDescription());
                intent.putExtra("address", wisataList.get(position).getAddress());
                intent.putExtra("lat", wisataList.get(position).getLat());
                intent.putExtra("lng", wisataList.get(position).getLng());
                intent.putExtra("umum", wisataList.get(position).getTiketUmum());
                intent.putExtra("anak", wisataList.get(position).getTiketAnak());
                intent.putExtra("pelajar", wisataList.get(position).getTiketPelajar());
                intent.putExtra("dewasa", wisataList.get(position).getTiketDewasa());
                intent.putExtra("operasional", wisataList.get(position).getJamOperasional());
                intent.putExtra("headline", wisataList.get(position).getHeadline());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
