package com.example.e2tech.orderhistory;

import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        });
        return view;
    }
}
