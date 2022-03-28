package com.example.e2tech;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminOrderAdapter;
import com.example.e2tech.ConcreteBuilders.ConcreteProductBuilder;
import com.example.e2tech.Models.OrderModel;
import com.example.e2tech.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProcessingOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProcessingOrderFragment extends Fragment {


    RecyclerView recyclerView;
    AdminOrderAdapter orderAdapter;

    ArrayList<OrderModel> orderList;

    Button btnAddProduct;
    FirebaseFirestore db;


    public AdminProcessingOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminProcessingOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminProcessingOrderFragment newInstance(String param1, String param2) {
        AdminProcessingOrderFragment fragment = new AdminProcessingOrderFragment();
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
        View root = inflater.inflate(R.layout.fragment_admin_processing_order, container, false);
        orderList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.admin_order_recycler_processing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        orderAdapter = new AdminOrderAdapter(getActivity(), orderList);
        recyclerView.setAdapter(orderAdapter);
//        Log.v("CATEGORY", category);



        db.collection("Orders")
                .whereEqualTo("status", "processing")
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
                                String createAt = documentSnapshot.getString("createAt");
                                String id = documentSnapshot.getString("id");
                                String note = documentSnapshot.getString("note");
                                String phone = documentSnapshot.getString("phone");
//                                String status = documentSnapshot.getString("status");
                                String status = "Đang xử lý";
                                String orderBy = documentSnapshot.getString("orderBy");
                                String userName = documentSnapshot.getString("userName");
//                                String nameProducts = documentSnapshot.getString("receiverName");
//                                String fee_ship = documentSnapshot.getString("receiverName");


                                long currentDateTime = System.currentTimeMillis();
                                Log.v("Time", String.valueOf(currentDateTime));
                                Log.v("Created", createAt);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                                Date orderDate = new Date(Long.parseLong(createAt));
                                String strDate = sdf.format(orderDate);




                                int fee_ship = 0;
                                int total = Math.toIntExact(documentSnapshot.getLong("total"));
//                                int subTotal = Integer.parseInt(documentSnapshot.getString("receiverName"));
                                int subTotal = total;
                                int quantity = Math.toIntExact(documentSnapshot.getLong("quantity"));
//                                int productList = Integer.parseInt(documentSnapshot.getString("receiverName"));

                                db.collection("Orders").document(id).collection("Items")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> subTask) {
                                                String nameProducts;
                                                ArrayList<ProductModel> productList = new ArrayList<>();
                                                for (DocumentSnapshot subDocument: subTask.getResult()) {
                                                    String productId = subDocument.getString("id");
                                                    String productImageURL = subDocument.getString("productImageURL");
                                                    String productName = subDocument.getString("productName");
                                                    int productPrice = Math.toIntExact(subDocument.getLong("productPrice"));
                                                    int totalQuantity = Math.toIntExact(subDocument.getLong("totalQuantity"));

                                                    ProductModel newProduct = new ConcreteProductBuilder()
                                                            .productID(productId)
                                                            .productImage(productImageURL)
                                                            .productName(productName)
                                                            .productPrice(productPrice)
                                                            .productQuantityOnOrder(totalQuantity)
                                                            .build();
                                                    productList.add(newProduct);
                                                }


                                                if (productList.size()>1) {
                                                    nameProducts = productList.get(0).getName() + " và các sản phẩm khác...";
                                                } else if (productList.size()>0) {
                                                    nameProducts = productList.get(0).getName();

                                                } else {
                                                    nameProducts = "NOTHING HERE";
                                                }
                                                OrderModel order = new OrderModel(receiverName, address, strDate, id, note, phone, status, orderBy, userName, nameProducts, fee_ship, subTotal, total, quantity, productList);



                                                orderList.add(order);
                                                orderAdapter.notifyDataSetChanged();
                                            }
                                        });




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