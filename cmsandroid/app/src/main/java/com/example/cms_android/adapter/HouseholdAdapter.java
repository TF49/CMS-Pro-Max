package com.example.cms_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.model.Household;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.ViewHolder> {
    private Context context;
    private List<Household> householdList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Household household);
    }

    public HouseholdAdapter(Context context, List<Household> householdList) {
        this.context = context;
        this.householdList = householdList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_household, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Household household = householdList.get(position);
        
        // 设置数据，添加空值检查
        holder.tvId.setText(household.getId() != null ? String.valueOf(household.getId()) : "");
        holder.tvResidentId.setText(household.getHeadOfHouseholdIdCard() != null ? household.getHeadOfHouseholdIdCard() : "");
        holder.tvHouseholdType.setText("家庭户"); // 暂时硬编码，后续可从模型中获取
        holder.tvHouseholdNumber.setText(household.getHouseholdNumber() != null ? household.getHouseholdNumber() : "");
        
        // 优化日期处理
        if (household.getCreateTime() != null) {
            holder.tvRegisterDate.setText(dateFormat.format(household.getCreateTime()));
        } else {
            holder.tvRegisterDate.setText("");
        }
        
        holder.tvRegisterAddress.setText(household.getAddress() != null ? household.getAddress() : "");
        holder.tvHouseholdHead.setText(household.getHeadOfHouseholdName() != null ? household.getHeadOfHouseholdName() : "");
        holder.tvHouseholdStatus.setText("正常"); // 暂时硬编码，后续可从模型中获取
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(household);
            }
        });
    }

    @Override
    public int getItemCount() {
        return householdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvResidentId, tvHouseholdType, tvHouseholdNumber, tvRegisterDate, tvRegisterAddress, tvHouseholdHead, tvHouseholdStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvResidentId = itemView.findViewById(R.id.tv_resident_id);
            tvHouseholdType = itemView.findViewById(R.id.tv_household_type);
            tvHouseholdNumber = itemView.findViewById(R.id.tv_household_number);
            tvRegisterDate = itemView.findViewById(R.id.tv_register_date);
            tvRegisterAddress = itemView.findViewById(R.id.tv_register_address);
            tvHouseholdHead = itemView.findViewById(R.id.tv_household_head);
            tvHouseholdStatus = itemView.findViewById(R.id.tv_household_status);
        }
    }
}