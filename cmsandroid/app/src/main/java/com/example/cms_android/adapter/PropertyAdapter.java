package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Property;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {
    private Context context;
    private List<Property> propertyList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Property property);
    }

    public PropertyAdapter(Context context, List<Property> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = propertyList.get(position);

        holder.tvId.setText(String.valueOf(property.getId() != null ? property.getId() : ""));
        holder.tvResidentName.setText(property.getResidentName() != null ? property.getResidentName() : "未知姓名");
        holder.tvPropertyType.setText(property.getPropertyType() != null ? property.getPropertyType() : "未知类型");
        holder.tvAddress.setText(property.getAddress() != null ? property.getAddress() : "未知地址");
        holder.tvArea.setText(property.getArea() != null ? property.getArea() + " m²" : "");

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(property);
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvResidentName, tvPropertyType, tvAddress, tvArea, tvPropertyStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvPropertyType = itemView.findViewById(R.id.tv_property_type);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvArea = itemView.findViewById(R.id.tv_area);
        }
    }
}