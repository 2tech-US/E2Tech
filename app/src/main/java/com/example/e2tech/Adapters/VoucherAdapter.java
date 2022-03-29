package com.example.e2tech.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    ArrayList<VoucherModel> voucherList;
    Context context;

    public VoucherAdapter(Context context, ArrayList<VoucherModel> voucherList) {
        this.voucherList = voucherList;
        this.context = context;
    }

    @NonNull
    @Override
    public VoucherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_vouchers_line, parent, false);
        VoucherAdapter.ViewHolder viewHolder = new VoucherAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.voucherCode.setText(voucherList.get(position).getCode());
        holder.voucherDiscount.setText("Discount value: " + Double.toString(voucherList.get(position).getDiscount()));
        holder.voucherDescription.setText("Description: " + voucherList.get(position).getDescription());
        Glide.with(context).load(voucherList.get(position).getImg_url()).into(holder.voucherImg);
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
