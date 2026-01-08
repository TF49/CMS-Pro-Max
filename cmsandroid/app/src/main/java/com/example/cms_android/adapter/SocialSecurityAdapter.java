package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.SocialSecurity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SocialSecurityAdapter extends RecyclerView.Adapter<SocialSecurityAdapter.SocialSecurityViewHolder> {
    private Context context;
    private List<SocialSecurity> socialSecurityList;
    private OnItemClickListener listener;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public interface OnItemClickListener {
        void onItemClick(SocialSecurity socialSecurity);
    }

    public SocialSecurityAdapter(Context context, List<SocialSecurity> socialSecurityList) {
        this.context = context;
        this.socialSecurityList = socialSecurityList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SocialSecurityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_socialsecurity, parent, false);
        return new SocialSecurityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialSecurityViewHolder holder, int position) {
        SocialSecurity socialSecurity = socialSecurityList.get(position);

        holder.tvResidentName.setText(socialSecurity.getResidentName() != null ? socialSecurity.getResidentName() : "");
        holder.tvInsuranceStatus.setText(getInsuranceStatusDisplay(socialSecurity.getInsuranceStatus()));
        holder.tvMonthlyContribution.setText(socialSecurity.getPaymentAmount() != null ? socialSecurity.getPaymentAmount().toString() : "");
        // 直接使用准确的startDate字段
        holder.tvStartDate.setText(socialSecurity.getStartDate() != null ? socialSecurity.getStartDate().format(dateFormatter) : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(socialSecurity);
            }
        });
    }
    
    /**
     * 将英文参保状态转换为中文显示
     */
    private String getInsuranceStatusDisplay(String status) {
        if (status == null)
            return "参保";
        // 先转换为大写，再进行比较
        String upperStatus = status.toUpperCase();
        switch (upperStatus) {
            case "NORMAL":
                return "参保";
            case "PAUSED":
                return "暂停";
            case "TERMINATED":
                return "终止";
            default:
                return status;
        }
    }

    @Override
    public int getItemCount() {
        return socialSecurityList.size();
    }

    public static class SocialSecurityViewHolder extends RecyclerView.ViewHolder {
        TextView tvResidentName, tvInsuranceStatus, tvMonthlyContribution, tvStartDate;

        public SocialSecurityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvInsuranceStatus = itemView.findViewById(R.id.tv_insurance_status);
            tvMonthlyContribution = itemView.findViewById(R.id.tv_monthly_contribution);
            tvStartDate = itemView.findViewById(R.id.tv_start_date);
        }
    }
}