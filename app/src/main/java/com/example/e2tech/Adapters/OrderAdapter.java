package com.example.e2tech.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.Models.OrderModel;
import com.example.e2tech.R;
import com.example.e2tech.orderhistory.OrderDetailDialog;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    List<OrderModel> orders;

    public OrderAdapter(Context context, List<OrderModel> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_admin_item, parent, false);
        final OrderAdapter.ViewHolder holder = new OrderAdapter.ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvID.setText(orders.get(position).getId());
        holder.tvProductNames.setText(orders.get(position).getNameProducts());
        holder.tvTotal.setText(Integer.toString(orders.get(position).getTotal()));
        holder.tvQuantity.setText(Integer.toString(orders.get(position).getQuantity()));
        holder.tvDate.setText(orders.get(position).getCreateAt());
        holder.imgProducts.setImageResource(R.drawable.iphone_12_pro_max);
        //Glide.with(context).load(orders.get(position).getImg_url()).into(holder.imgProduct);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle productBundle = new Bundle();
                productBundle.putString("orderId", orders.get(position).getId());
                OrderDetailDialog orderDetailDialog = new OrderDetailDialog();
                orderDetailDialog.setArguments(productBundle);
                orderDetailDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "orderDetailDialog");

            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductNames, tvTotal, tvDate, tvAddress, tvQuantity, tvID;
        ImageView imgProducts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductNames = itemView.findViewById(R.id.tv_admin_order_item_names_list);
            tvTotal = itemView.findViewById(R.id.tv_admin_order_item_total_list);
            tvDate = itemView.findViewById(R.id.tv_admin_order_item_date_list);
            tvAddress = itemView.findViewById(R.id.tv_admin_order_item_address_list);
            tvQuantity = itemView.findViewById(R.id.tv_admin_order_item_quantity_list);
            tvID = itemView.findViewById(R.id.tv_admin_order_item_id_list);
            imgProducts = itemView.findViewById(R.id.img_admin_order_item_image_products_list);
        }
    }
}
