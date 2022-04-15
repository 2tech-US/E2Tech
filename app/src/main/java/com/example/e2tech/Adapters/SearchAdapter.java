package com.example.e2tech.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    Context context;
    ArrayList<ProductModel> productList;
    ArrayList<ProductModel> listFilter;

    public SearchAdapter(Context context, ArrayList<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
        this.listFilter = new ArrayList<>(this.productList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        final SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvNameSearchItem.setText(listFilter.get(position).getName());
        holder.tvPrice.setText(Integer.toString(listFilter.get(position).getPrice()));
        Glide.with(context).load(listFilter.get(position).getImg_url()).into(holder.imgView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putString("id", listFilter.get(position).getId());
                bundle.putString("collection", "Products");
                bundle.putString("img_url", listFilter.get(position).getImg_url());
                Navigation.findNavController(view).navigate(R.id.detailFragment, bundle,
                        null, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private final Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<ProductModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length()==0) {
                filteredList.addAll(productList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ProductModel product: productList) {
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listFilter.clear();
            listFilter.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameSearchItem;
        TextView tvPrice;
        ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameSearchItem = itemView.findViewById(R.id.search_item_name);
            tvPrice = itemView.findViewById(R.id.search_item_price);
            imgView = itemView.findViewById(R.id.search_item_img);


        }
    }
}
