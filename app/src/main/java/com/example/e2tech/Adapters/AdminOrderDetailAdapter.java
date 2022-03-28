package com.example.e2tech.Adapters;

import android.content.Context;
import android.os.Bundle;
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
import com.example.e2tech.R;

import java.util.ArrayList;


public class AdminOrderDetailAdapter extends RecyclerView.Adapter<AdminOrderDetailAdapter.ViewHolder> {

    ArrayList<ProductModel> productList;
    Context context;

    public AdminOrderDetailAdapter(Context context, ArrayList<ProductModel> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminOrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_in_order_item, parent, false);
        final AdminOrderDetailAdapter.ViewHolder holder = new AdminOrderDetailAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderDetailAdapter.ViewHolder holder, int position) {
        holder.tvProductName.setText(productList.get(position).getName());
        holder.tvPrice.setText(String.valueOf(productList.get(position).getPrice()));
        holder.tvQuantity.setText("x" + String.valueOf(productList.get(position).getQuantityOnOrder()));
        Glide.with(context).load(productList.get(position).getImg_url()).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvProductName;
        TextView tvQuantity;
        TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_admin_order_detail_product_item);
            tvProductName = itemView.findViewById(R.id.tv_admin_order_detail_product_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_admin_order_detail_product_item_quantity);
            tvPrice = itemView.findViewById(R.id.tv_admin_order_detail_product_item_price);
        }
    }
}
