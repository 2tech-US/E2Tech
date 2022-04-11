package com.example.e2tech.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.CartModel;
import com.example.e2tech.R;

import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryDetailAdapter extends RecyclerView.Adapter<OrderHistoryDetailAdapter.ViewHolder> {
    List<CartModel> cartModelList;
    Context context;

    public OrderHistoryDetailAdapter(Context context,  List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHistoryDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHistoryDetailAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.dialog_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryDetailAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(cartModelList.get(position).getProductImageURL()).into(holder.productImage);

        holder.name.setText(cartModelList.get(position).getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        String totalAmount = decimalFormat.format(cartModelList.get(position).getProductPrice());
        holder.price.setText(totalAmount);
        holder.quantity.setText(String.valueOf(cartModelList.get(position).getTotalQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, quantity;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);

            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
