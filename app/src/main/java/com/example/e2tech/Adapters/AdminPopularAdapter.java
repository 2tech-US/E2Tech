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

public class AdminPopularAdapter extends RecyclerView.Adapter<AdminPopularAdapter.ViewHolder> {

    ArrayList<ProductModel> productList;
    Context context;


    public AdminPopularAdapter(Context context, ArrayList<ProductModel> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminPopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_popular_item, parent, false);
        final AdminPopularAdapter.ViewHolder holder = new AdminPopularAdapter.ViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putString("id", productList.get(position).getId());
                bundle.putString("collection", "Products");
                bundle.putString("img_url", productList.get(position).getImg_url());


                // animation zoom in

//                ViewCompat.setTransitionName(this.imgProduct,productList.get(position).getImg_url());
//                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
//                        .addSharedElement(holder.imgProduct, holder.id)
//                        .build();


//                Navigation.findNavController(view).navigate(R.id.detailFragment, bundle,
//                        null, extras);

                Navigation.findNavController(view).navigate(R.id.detailFragment, bundle,
                        null, null);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPopularAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(productList.get(position).getName());
        holder.tvPrice.setText(Integer.toString(productList.get(position).getPrice()));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.admin_pop_name);
            tvPrice = itemView.findViewById(R.id.admin_pop_price);
            imgProduct = itemView.findViewById(R.id.admin_pop_img);
        }

    }

}
