package com.example.e2tech.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.Models.VoucherModel;
import com.example.e2tech.R;

import java.util.ArrayList;

public class AdminVoucherAdapter extends RecyclerView.Adapter<AdminVoucherAdapter.ViewHolder> {

    ArrayList<VoucherModel> voucherList;
    Context context;

    public AdminVoucherAdapter(Context context, ArrayList<VoucherModel> voucherList) {
        this.voucherList = voucherList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminVoucherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_vouchers_line, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminVoucherAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.voucherCode.setText(voucherList.get(position).getCode());
        holder.voucherDiscount.setText("Discount value: " + Integer.toString(voucherList.get(position).getDiscount()));
        holder.voucherDescription.setText("Description: " + voucherList.get(position).getDescription());
        Glide.with(context).load(voucherList.get(position).getImg_url()).into(holder.voucherImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle voucherBundle = new Bundle();

                voucherBundle.putSerializable("voucher", voucherList.get(position));
                Navigation.findNavController(view).navigate(R.id.adminVoucherDetail, voucherBundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView voucherImg;
        TextView voucherCode, voucherDiscount, voucherDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            voucherImg = itemView.findViewById(R.id.voucherImg);
            voucherCode = itemView.findViewById(R.id.voucherCode);
            voucherDiscount = itemView.findViewById(R.id.voucherDiscount);
            voucherDescription = itemView.findViewById(R.id.voucherDescription);
        }
    }
}
