package com.example.e2tech.orderhistory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e2tech.Adapters.OrderHistoryDetailAdapter;
import com.example.e2tech.Interface.OnOrderDeleted;
import com.example.e2tech.Models.CartModel;
import com.example.e2tech.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OrderDetailDialog extends DialogFragment {
    RecyclerView orderDetailList;
    Button deleteOrderBtn;

    OrderHistoryDetailAdapter orderDetailAdapter;
    List<CartModel> cartModelList;
    String orderId;

    FirebaseFirestore db;

    OnOrderDeleted listener;

    public OrderDetailDialog(OnOrderDeleted listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_detail, container, false);

        db = FirebaseFirestore.getInstance();
        cartModelList = new ArrayList<>();

        orderId = requireArguments().getString("orderId");
        db.collection("Orders").document(orderId).collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        CartModel cartModel = documentSnapshot.toObject(CartModel.class);
                        cartModelList.add(cartModel);
                        orderDetailAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        orderDetailList = view.findViewById(R.id.order_detail_list);
        orderDetailAdapter = new OrderHistoryDetailAdapter(getContext(), cartModelList);
        orderDetailList.setAdapter(orderDetailAdapter);
        orderDetailList.setLayoutManager(new LinearLayoutManager(getContext()));

        deleteOrderBtn = view.findViewById(R.id.delete_order_btn);
        deleteOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> listProductId = new ArrayList<>();

                for (CartModel cartItems : cartModelList)
                    listProductId.add(cartItems.getProductId());

                db.runTransaction(
                        transaction -> {
                            List<DocumentSnapshot> documentSnapshots = new ArrayList<>();
                            List<DocumentReference> documentReferences = new ArrayList<>();

                            for (int i = 0; i < listProductId.size(); i++) {
                                DocumentReference documentReference = db.collection("Products").document(listProductId.get(i));
                                DocumentSnapshot snapshot = transaction.get(documentReference);
                                if(snapshot.exists()) {
                                    documentReferences.add(documentReference);
                                    documentSnapshots.add(snapshot);
                                }
                            }
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                DocumentReference documentReference = documentReferences.get(i);
                                DocumentSnapshot snapshot = documentSnapshots.get(i);
                                Long remainStock = snapshot.getLong("remain");
                                String productName = snapshot.getString("name");
                                if (remainStock == null) {
                                    throw new Error("Hệ thống gặp trục trặc");
                                }
                                long newRemainStock = remainStock + cartModelList.get(i).getTotalQuantity();
                                transaction.update(documentReference, "remain", newRemainStock);
                            }
                            return null;
                        }
                ).addOnSuccessListener(new OnSuccessListener<Object>() {

                    @Override
                    public void onSuccess(Object o) {
                        db.collection("Orders").document(orderId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dismiss();
                                    Toast.makeText(getContext(), "Hủy đơn thành công", Toast.LENGTH_SHORT).show();
                                    listener.onOrderDeleted(orderId);
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        db.collection("Orders").document(orderId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dismiss();
                                    Toast.makeText(getContext(), "Hủy đơn thành công", Toast.LENGTH_SHORT).show();
                                    listener.onOrderDeleted(orderId);
                                }
                            }
                        });
                        Toast.makeText(getContext(), "Lỗi Hệ Thống!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        return view;
    }
}
