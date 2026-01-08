package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Resident;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ViewHolder> {
    private Context context;
    private List<Resident> residentList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Resident resident);
    }

    public ResidentAdapter(Context context, List<Resident> residentList) {
        this.context = context;
        this.residentList = residentList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resident, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resident resident = residentList.get(position);
        
        // 设置数据，添加空值检查，与户籍管理保持一致
        holder.tvName.setText(resident.getName() != null ? resident.getName() : "");
        holder.tvIdCard.setText(resident.getIdCard() != null ? resident.getIdCard() : "");
        // 性别转换：将英文转换为中文
        String genderText = "";
        if (resident.getGender() != null) {
            if ("MALE".equals(resident.getGender())) {
                genderText = "男";
            } else if ("FEMALE".equals(resident.getGender())) {
                genderText = "女";
            } else {
                genderText = resident.getGender();
            }
        }
        holder.tvGender.setText(genderText);
        holder.tvPhone.setText(resident.getPhoneNumber() != null ? resident.getPhoneNumber() : "");
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(resident);
            }
        });
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIdCard, tvGender, tvPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvIdCard = itemView.findViewById(R.id.tv_id_card);
            tvGender = itemView.findViewById(R.id.tv_gender);
            tvPhone = itemView.findViewById(R.id.tv_phone);
        }
    }
}