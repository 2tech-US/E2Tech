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
import com.example.e2tech.Adapters.CartAdapter;
import com.example.e2tech.Models.CartModel;
import com.example.e2tech.Models.OrderModel;
import com.example.e2tech.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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



        db.collection("Orders")
                .whereEqualTo("status", "waiting")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
//                                OrderModel order = documentSnapshot.toObject(OrderModel.class);
//                                String id = documentSnapshot.getId();
//                                order.setId(id);

                                String receiverName = documentSnapshot.getString("receiverName");
                                String address = documentSnapshot.getString("address");
                                String createAt = documentSnapshot.getString("address");
                                String id = documentSnapshot.getString("id");
                                String note = documentSnapshot.getString("note");
                                String phone = documentSnapshot.getString("phone");
                                String status = documentSnapshot.getString("status");
                                String orderBy = documentSnapshot.getString("orderBy");
                                String userName = documentSnapshot.getString("userName");
//                                String nameProducts = documentSnapshot.getString("receiverName");
//                                String fee_ship = documentSnapshot.getString("receiverName");

                                String nameProducts = "something names";
                                int fee_ship = 0;
                                int total = Math.toIntExact(documentSnapshot.getLong("total"));
//                                int subTotal = Integer.parseInt(documentSnapshot.getString("receiverName"));
                                int subTotal = total;
                                int quantity = Math.toIntExact(documentSnapshot.getLong("quantity"));
//                                int productList = Integer.parseInt(documentSnapshot.getString("receiverName"));
                                ArrayList<ProductModel> productList = new ArrayList<>();

                                OrderModel order = new OrderModel(receiverName, address, createAt, id, note, phone, status, orderBy, userName, nameProducts, fee_ship, subTotal, total, quantity, productList);



                                orderList.add(order);
                                orderAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });






//        orderList.add(new OrderModel("OSIJOJJDGPG430JF04J","BR-VT", "18/03/2022", "Laptop Asus and other products...", 3000000, 3));
//        orderList.add(new OrderModel("12443TGFC","HCM", "20/03/2022", "Iphone 13 and other products...", 34000000, 2));
        orderAdapter.notifyDataSetChanged();

        return root;
    }
}