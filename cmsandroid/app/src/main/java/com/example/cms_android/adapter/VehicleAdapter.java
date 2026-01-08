package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Vehicle;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    private Context context;
    private List<Vehicle> vehicleList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Vehicle vehicle);
    }

    public VehicleAdapter(Context context, List<Vehicle> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.tvId.setText(String.valueOf(vehicle.getId() != null ? vehicle.getId() : ""));
        holder.tvResidentId.setText(String.valueOf(vehicle.getResidentId() != null ? vehicle.getResidentId() : ""));
         holder.tvResidentName.setText(vehicle.getResidentName() != null ? vehicle.getResidentName() : "");
        holder.tvLicensePlate.setText(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "");
        holder.tvVehicleType.setText(vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "");
        holder.tvBrand.setText(vehicle.getBrand() != null ? vehicle.getBrand() : "");
        holder.tvModel.setText(vehicle.getModel() != null ? vehicle.getModel() : "");
        holder.tvColor.setText(vehicle.getColor() != null ? vehicle.getColor() : "");

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(vehicle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvResidentId, tvResidentName, tvLicensePlate, tvVehicleType, tvBrand, tvModel, tvColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvResidentId = itemView.findViewById(R.id.tv_resident_id);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvLicensePlate = itemView.findViewById(R.id.tv_license_plate);
            tvVehicleType = itemView.findViewById(R.id.tv_vehicle_type);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvModel = itemView.findViewById(R.id.tv_model);
            tvColor = itemView.findViewById(R.id.tv_color);
        }
    }
}