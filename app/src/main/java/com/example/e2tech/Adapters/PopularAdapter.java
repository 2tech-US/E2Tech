package com.example.e2tech.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder>  {

    ArrayList<ProductModel> productList;
    Context context;


    public PopularAdapter(Context context, ArrayList<ProductModel> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item_home, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", holder.id);
                bundle.putString("collection", "PopularProducts");
                bundle.putString("img_url",holder.img_url);

                holder.setTransitionId();
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(holder.imgProduct, holder.id)
                        .build();


//                Navigation.findNavController(view).navigate(R.id.detailFragment, bundle,
//                        null, extras);

                Navigation.findNavController(view).navigate(R.id.detailFragment, bundle,
                        null, null);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(productList.get(position).getName());
        holder.tvPrice.setText(Integer.toString(productList.get(position).getPrice()));

        holder.id = productList.get(position).getId();
        holder.category = productList.get(position).getType();
        holder.img_url = productList.get(position).getImg_url();

        Glide.with(context).load(productList.get(position).getImg_url()).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPrice;
        ImageView imgProduct;

        //      below attribute just for reference
        String category;
        String id;
        String img_url;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.pop_name);
            tvPrice = itemView.findViewById(R.id.pop_price);
            imgProduct = itemView.findViewById(R.id.pop_img);
        }

        public void setTransitionId() {
            ViewCompat.setTransitionName(this.imgProduct,this.img_url);
        }

    }

}
