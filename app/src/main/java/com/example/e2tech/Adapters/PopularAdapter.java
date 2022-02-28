package com.example.e2tech.Adapters;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    ArrayList<ProductModel> productList;
    Context context;

    public PopularAdapter(Context context, ArrayList<ProductModel> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(productList.get(position).getName());
        holder.tvPrice.setText(Integer.toString(productList.get(position).getPrice()));

        Glide.with(context).load(productList.get(position).getImg_url()).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPrice;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.pop_name);
            tvPrice = itemView.findViewById(R.id.pop_price);
            imgProduct = itemView.findViewById(R.id.pop_img);
        }
    }
}
