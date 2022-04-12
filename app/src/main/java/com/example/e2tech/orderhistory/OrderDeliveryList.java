package com.example.e2tech.orderhistory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e2tech.Adapters.OrderAdapter;
import com.example.e2tech.Models.OrderModel;
import com.example.e2tech.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderDeliveryList extends Fragment {

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;

    ArrayList<OrderModel> orderList;

    Button btnAddProduct;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String status = "waiting";


    public OrderDeliveryList(String status) {
        this.status = status;
    }

    public static OrderDeliveryList newInstance(String param1, String param2) {
        OrderDeliveryList fragment = new OrderDeliveryList("waiting");
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_waiting_order, container, false);
        orderList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.admin_order_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        orderAdapter = new OrderAdapter(getActivity(), orderList);
        recyclerView.setAdapter(orderAdapter);


        // get orders from firebase
        db.collection("Orders")
                .whereEqualTo("status", status).whereEqualTo("orderBy", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       OrderModel orderModel = document.toObject(OrderModel.class);
                                                       // get image from firebase
                                                       db.collection("Orders")
                                                               .document(document.getId()).
                                                               collection("Items")
                                                               .get()
                                                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                       if (task.isSuccessful()) {
                                                                           for (QueryDocumentSnapshot document : task.getResult()) {
                                                                               String image = document.getString("productImageURL");
                                                                               orderModel.setImage(document.getString("productImageURL"));
                                                                               orderList.add(orderModel);
                                                                               orderAdapter.notifyDataSetChanged();
                                                                               break;
                                                                           }

                                                                       }
                                                                   }
                                                               });
                                                   }

                                               } else {
                                                   Toast.makeText(getActivity(), "Error getting documents.", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
                );

//        orderList.add(new OrderModel("OSIJOJJDGPG430JF04J", "BR-VT", "18/03/2022", "Laptop Asus and other products...", 3000000, 3));
//        orderList.add(new OrderModel("12443TGFC", "HCM", "20/03/2022", "Iphone 13 and other products...", 34000000, 2));
        orderAdapter.notifyDataSetChanged();

        return root;
    }
}