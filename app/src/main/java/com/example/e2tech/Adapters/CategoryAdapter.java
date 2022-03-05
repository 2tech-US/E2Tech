package com.example.e2tech.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<CategoryModel> categories;
    int layout;

    public CategoryAdapter(Context context, List<CategoryModel> categories, int layout) {
        this.context = context;
        this.categories = categories;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layout == R.layout.catetory_item_category) {
                    Bundle bundle = new Bundle();
                    bundle.putString("collection", holder.tvCateNameCate.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.shopFragment, bundle,
                            null, null);
                } else {
                    Toast.makeText(context, (String) holder.tvCateNameHome.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (this.layout == R.layout.catetory_item_category) {
            holder.tvCateNameCate.setText(categories.get(position).getName());
            Glide.with(context).load(categories.get(position).getImg_url()).into(holder.imgCategory);
        } else {
            holder.tvCateNameHome.setText(categories.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCateNameHome;
        TextView tvCateNameCate;
        ImageView imgCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCateNameHome = itemView.findViewById(R.id.tv_category_home);
            tvCateNameCate = itemView.findViewById(R.id.tv_category_category);
            imgCategory = itemView.findViewById(R.id.iv_category_category);
        }
    }
}
