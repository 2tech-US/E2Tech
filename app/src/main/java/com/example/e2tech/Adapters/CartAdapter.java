package com.example.e2tech.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.CartModel;
import com.example.e2tech.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartModel> cartModelList;
    int totalPrice = 0;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public CartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(cartModelList.get(position).getProductImageURL()).into(holder.productImage);

        holder.name.setText(cartModelList.get(position).getProductName());
        holder.price.setText(String.valueOf(cartModelList.get(position).getProductPrice()));
        holder.quantity.setText(String.valueOf(cartModelList.get(position).getTotalQuantity()));
        int priceCount = cartModelList.get(position).getProductPrice() * cartModelList.get(position).getTotalQuantity();
        holder.totalPrice.setText(String.valueOf(priceCount));

        totalPrice += priceCount;
        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModelList.get(position).getTotalQuantity() > 1) {
                    cartModelList.get(position).setTotalQuantity(cartModelList.get(position).getTotalQuantity() - 1);
                    holder.quantity.setText(String.valueOf(cartModelList.get(position).getTotalQuantity()));
                    holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getProductPrice() * (cartModelList.get(position).getTotalQuantity())));

                    CollectionReference cartRef = db.collection("AddToCart").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .collection("CurrentUser");
                    Query query = cartRef.whereEqualTo("productId", cartModelList.get(position).getProductId());
                    query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            db.collection("AddToCart").document(mAuth.getCurrentUser().getUid())
                                    .collection("CurrentUser").document(documentSnapshot.getId())
                                    .update("totalQuantity", cartModelList.get(position).getTotalQuantity());
                        }
                    });

                    totalPrice -= cartModelList.get(position).getProductPrice();
                    intent.putExtra("totalAmount", totalPrice);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                }
            }
        });
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartModelList.get(position).setTotalQuantity(cartModelList.get(position).getTotalQuantity() + 1);
                holder.quantity.setText(String.valueOf(cartModelList.get(position).getTotalQuantity()));
                holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getProductPrice() * (cartModelList.get(position).getTotalQuantity())));

                CollectionReference cartRef = db.collection("AddToCart").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .collection("CurrentUser");
                Query query = cartRef.whereEqualTo("productId", cartModelList.get(position).getProductId());
                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        db.collection("AddToCart").document(mAuth.getCurrentUser().getUid())
                                .collection("CurrentUser").document(documentSnapshot.getId())
                                .update("totalQuantity", cartModelList.get(position).getTotalQuantity());
                    }
                });

                totalPrice += cartModelList.get(position).getProductPrice();
                intent.putExtra("totalAmount", totalPrice);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference cartRef = db.collection("AddToCart").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .collection("CurrentUser");
                Query query = cartRef.whereEqualTo("productId", cartModelList.get(position).getProductId());
                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        db.collection("AddToCart").document(mAuth.getCurrentUser().getUid())
                                .collection("CurrentUser").document(documentSnapshot.getId())
                                .delete();
                    }
                });

                for (int i = position; i < cartModelList.size(); i++) {
                    totalPrice -= cartModelList.get(i).getProductPrice() * cartModelList.get(i).getTotalQuantity();
                }

                Intent intent = new Intent("MyTotalAmount");
                intent.putExtra("totalAmount", totalPrice);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                cartModelList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartModelList.size());


            }
        });
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, quantity, totalPrice;
        ImageView productImage;
        ImageView addBtn, removeBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.total_price);

            productImage = itemView.findViewById(R.id.product_image);

            addBtn = itemView.findViewById(R.id.add_btn);
            removeBtn = itemView.findViewById(R.id.remove_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

        }
    }
}