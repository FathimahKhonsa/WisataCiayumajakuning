package com.example.wisataciayumajakuning.adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wisataciayumajakuning.databinding.StepItemLayoutBinding;
import com.example.wisataciayumajakuning.model.DirectionModel.DirectionStepModel;

import java.util.List;

public class DirectionStepAdapter extends RecyclerView.Adapter<DirectionStepAdapter.MyViewHolder> {
    private List<DirectionStepModel> directionStepModelList;

    @NonNull
    @Override
    public DirectionStepAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StepItemLayoutBinding binding = StepItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectionStepAdapter.MyViewHolder holder, int position) {
        if (directionStepModelList != null){
            DirectionStepModel stepModel = directionStepModelList.get(position);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                holder.binding.textStep.setText(Html.fromHtml(stepModel.getHtmlInstructions(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.binding.textStep.setText(Html.fromHtml(stepModel.getHtmlInstructions()));
            }

            holder.binding.txtStepTime.setText(stepModel.getDuration().getText());
            holder.binding.txtStepDistance.setText(stepModel.getDistance().getText());
        }
    }

    @Override
    public int getItemCount() {
        if (directionStepModelList != null){
            return directionStepModelList.size();
        } else {
            return 0;
        }
    }

    public void setDirectionStepModelList(List<DirectionStepModel> directionStepModelList){
        this.directionStepModelList = directionStepModelList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private StepItemLayoutBinding binding;

        public MyViewHolder(@NonNull StepItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
