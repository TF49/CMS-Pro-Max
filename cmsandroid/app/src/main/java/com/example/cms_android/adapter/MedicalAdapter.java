package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Medical;

import java.util.List;

public class MedicalAdapter extends RecyclerView.Adapter<MedicalAdapter.MedicalViewHolder> {
    private Context context;
    private List<Medical> medicalList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Medical medical);
    }

    public MedicalAdapter(Context context, List<Medical> medicalList) {
        this.context = context;
        this.medicalList = medicalList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MedicalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medical, parent, false);
        return new MedicalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalViewHolder holder, int position) {
        Medical medical = medicalList.get(position);

        holder.tvId.setText(String.valueOf(medical.getId() != null ? medical.getId() : ""));
        holder.tvResidentId.setText(medical.getDiagnosis() != null ? medical.getDiagnosis() : "");
        holder.tvMedicalCardNumber
                .setText(medical.getIdCard() != null ? medical.getIdCard() : "");
        holder.tvMedicalInsuranceType
                .setText(medical.getResidentName() != null ? medical.getResidentName() : "");
        holder.tvMedicalStatus.setText(medical.getHealthStatus() != null ? medical.getHealthStatus() : "正常");
        
        // 设置就诊类型显示
        String medicalType = medical.getMedicalType();
        if ("HOSPITALIZATION".equals(medicalType)) {
            holder.tvMedicalStatus.setText("住院");
            holder.tvMedicalStatus.setBackgroundColor(context.getResources().getColor(R.color.error));
        } else {
            holder.tvMedicalStatus.setText("门诊");
            holder.tvMedicalStatus.setBackgroundColor(context.getResources().getColor(R.color.menu_medical));
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(medical);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicalList.size();
    }

    public static class MedicalViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvResidentId, tvMedicalCardNumber, tvMedicalInsuranceType, tvMedicalStatus;
        // Keep potential unused views if id is needed, or just remove them from
        // ViewHolder if xml doesn't use them.
        // For safety, I'll bind what is in the new XML.

        public MedicalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvResidentId = itemView.findViewById(R.id.tvResidentId);
            tvMedicalCardNumber = itemView.findViewById(R.id.tvMedicalCardNumber);
            tvMedicalInsuranceType = itemView.findViewById(R.id.tvMedicalInsuranceType);
            tvMedicalStatus = itemView.findViewById(R.id.tvMedicalStatus);
        }
    }
}