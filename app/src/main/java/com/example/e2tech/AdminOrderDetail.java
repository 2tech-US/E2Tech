package com.example.e2tech;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminOrderAdapter;
import com.example.e2tech.Adapters.AdminOrderDetailAdapter;
import com.example.e2tech.Models.CartModel;
import com.example.e2tech.Models.OrderModel;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.databinding.FragmentAdminOrderDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.StructuredQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminOrderDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminOrderDetail extends Fragment implements View.OnClickListener {

    TextView tvOrderID, tvDate, tvReceiver, tvStatus, tvPhone, tvAddress, tvSubTotal, tvTotal, tvFee;
    OrderModel order;

    RecyclerView recyclerView;
    Button btnStatus, btnCancel;
    FirebaseFirestore db;
    ProgressBar progressBar;



    public AdminOrderDetail() {
        // Required empty public constructor
    }

    public static AdminOrderDetail newInstance(String param1, String param2) {
        AdminOrderDetail fragment = new AdminOrderDetail();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (OrderModel) getArguments().getSerializable("order");
        }
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_order_detail, container, false);

        tvOrderID = root.findViewById(R.id.tv_admin_order_detail_id_order);
        tvDate = root.findViewById(R.id.tv_admin_order_detail_date);
        tvStatus = root.findViewById(R.id.tv_admin_order_detail_status);
        tvReceiver = root.findViewById(R.id.tv_admin_order_detail_receiver_name);
        tvPhone = root.findViewById(R.id.tv_admin_order_detail_phone);
        tvAddress = root.findViewById(R.id.tv_admin_order_detail_address);
        tvSubTotal = root.findViewById(R.id.tv_admin_order_detail_sub_total);
        tvFee = root.findViewById(R.id.tv_admin_order_detail_fee);
        tvTotal = root.findViewById(R.id.tv_admin_order_detail_total);
        recyclerView = root.findViewById(R.id.admin_product_recyclerview_in_order_detail_screen);
        btnStatus = root.findViewById(R.id.btn_admin_order_detail);
        btnCancel = root.findViewById(R.id.btn_admin_order_detail_cancel);


        tvOrderID.setText(order.getId());
        tvDate.setText(order.getCreateAt());
        tvStatus.setText(order.getStatus());
        tvReceiver.setText(order.getReceiverName());
        tvPhone.setText(order.getPhone());
        tvAddress.setText(order.getAddress());
        tvSubTotal.setText(String.valueOf(order.getSubTotal()));
        tvTotal.setText(String.valueOf(order.getTotal()));
        progressBar = root.findViewById(R.id.progressBar_admin_order_detail);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AdminOrderDetailAdapter orderAdapter = new AdminOrderDetailAdapter(getActivity(), order.getProductList());
        recyclerView.setAdapter(orderAdapter);


        btnStatus.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (order.getStatus().equals("done")) {
            btnCancel.setEnabled(false);
            btnCancel.setVisibility(View.GONE);
            btnStatus.setEnabled(false);
            btnStatus.setVisibility(View.GONE);
        }

        if (order.getStatus().equals("waiting")) {
            btnStatus.setText("Xác nhận");
        } else if (order.getStatus().equals("processing")) {
            btnStatus.setText("Giao hàng");
        } else if (order.getStatus().equals("delivering")) {
            btnStatus.setText("Đã giao");
        }

        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_admin_order_detail) {
            progressBar.setVisibility(View.VISIBLE);
            String nextState = "processing", trangThai = "Đang xử lý";
            if (order.getStatus().equals("waiting")) {
                nextState = "processing";
                trangThai = "Đang xử lý";
            } else if (order.getStatus().equals("processing")) {
                nextState = "delivering";
                trangThai = "Đang giao";
            } else if (order.getStatus().equals("delivering")) {
                trangThai = "Đã giao";
                nextState = "done";
            }
            Log.v("now state", nextState);
            String finalTrangThai = trangThai;
            String finalNextState = nextState;
            db.collection("Orders").document(order.getId())
                    .update("status", nextState)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            tvStatus.setText(finalTrangThai);
//                            Toast.makeText(getActivity(), finalNextState, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            order.setStatus(finalNextState);
                            if (order.getStatus().equals("waiting")) {
                                btnStatus.setText("Xác nhận");
                            } else if (order.getStatus().equals("processing")) {
                                btnStatus.setText("Giao hàng");
                            } else if (order.getStatus().equals("delivering")) {
                                btnStatus.setText("Đã giao");
                            } else {
                                btnCancel.setEnabled(false);
                                btnCancel.setVisibility(View.GONE);
                                btnStatus.setEnabled(false);
                                btnStatus.setVisibility(View.GONE);
                            }
                            Intent intent = new Intent("order update");
                            intent.putExtra("update", "update order detail");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "thử lại", Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (view.getId() == R.id.btn_admin_order_detail_cancel) {
            db.collection("Orders").document(order.getId())
                    .update("status", "cancel");
        }
    }

}