package com.example.e2tech.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.R;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    Context context;
    List<CategoryModel> categories;

    public AdminCategoryAdapter(Context context, List<CategoryModel> categories, int admin_category_item) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override

    public AdminCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_category_item, parent, false);
        final AdminCategoryAdapter.ViewHolder holder = new AdminCategoryAdapter.ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", holder.tvCategoryName.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.adminProductListFragment, bundle);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryAdapter.ViewHolder holder, int position) {
        holder.tvCategoryName.setText(categories.get(position).getName());
        Glide.with(context).load(categories.get(position).getImg_url()).into(holder.imgCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategoryName;
        ImageView imgCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.admin_category_name_product_screen);
            imgCategory = itemView.findViewById(R.id.admin_category_img_category_screen);
        }
    }
}
