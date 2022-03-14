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
import com.example.e2tech.R;

import java.util.ArrayList;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    ArrayList<ProductModel> productList;
    Context context;

    public AdminProductAdapter(Context context, ArrayList<ProductModel> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(productList.get(position).getName());
        holder.tvPrice.setText(Integer.toString(productList.get(position).getPrice()));
        holder.tvRemain.setText("Remain: " + Integer.toString(productList.get(position).getRemain()));
        Glide.with(context).load(productList.get(position).getImg_url()).into(holder.imgProduct);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle productBundle = new Bundle();

                productBundle.putSerializable("product", productList.get(position));
                Log.v("PREPARE", productList.get(position).getName());
                Navigation.findNavController(view).navigate(R.id.adminProductDetail, productBundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName, tvPrice, tvSold, tvRemain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.img_admin_product_item_list_screen);
            tvName = itemView.findViewById(R.id.tv_admin_product_name_list_screen);
            tvPrice = itemView.findViewById(R.id.tv_admin_product_price_list_screen);
            tvSold = itemView.findViewById(R.id.tv_admin_product_sold_list_screen);
            tvRemain = itemView.findViewById(R.id.tv_admin_product_remain_list_screen);

        }
    }
}
