package com.example.e2tech;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminOrderAdapter;
import com.example.e2tech.Adapters.AdminProductAdapter;
import com.example.e2tech.Models.OrderModel;
import com.example.e2tech.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminWaitingOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminWaitingOrderFragment extends Fragment {

    RecyclerView recyclerView;
    AdminOrderAdapter orderAdapter;

    ArrayList<OrderModel> orderList;

    Button btnAddProduct;
    FirebaseFirestore db;


    public AdminWaitingOrderFragment() {
        // Required empty public constructor
    }

    public static AdminWaitingOrderFragment newInstance(String param1, String param2) {
        AdminWaitingOrderFragment fragment = new AdminWaitingOrderFragment();
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

        recyclerView = root.findViewById(R.id.admin_order_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        orderAdapter = new AdminOrderAdapter(getActivity(), orderList);
        recyclerView.setAdapter(orderAdapter);
//        Log.v("CATEGORY", category);



//        db.collection("Orders")
//                .whereEqualTo("status", "waiting")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
//                                OrderModel order = documentSnapshot.toObject(OrderModel.class);
//                                String id = documentSnapshot.getId();
//                                order.setId(id);
//
//                                orderList.add(order);
//                                orderAdapter.notifyDataSetChanged();
//                            }
//                        } else {
//                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

        orderList.add(new OrderModel("OSIJOJJDGPG430JF04J","BR-VT", "18/03/2022", "Laptop Asus and other products...", 3000000, 3));
        orderList.add(new OrderModel("12443TGFC","HCM", "20/03/2022", "Iphone 13 and other products...", 34000000, 2));
        orderAdapter.notifyDataSetChanged();

        return root;
    }
}