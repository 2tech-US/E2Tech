package com.example.e2tech;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminProductAdapter;
import com.example.e2tech.Adapters.AdminVoucherAdapter;
import com.example.e2tech.Adapters.VoucherAdapter;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.Models.VoucherModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminVoucherListFragment extends Fragment {

    ArrayList<VoucherModel> voucherList;
    RecyclerView recyclerView;
    AdminVoucherAdapter adminVoucherAdapter;
    Button btnAddVoucher;
    FirebaseFirestore db;


    public AdminVoucherListFragment() {
        // Required empty public constructor
    }


    public static AdminVoucherListFragment newInstance(String param1, String param2) {
        AdminVoucherListFragment fragment = new AdminVoucherListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_voucher_list, container, false);
        voucherList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.admin_voucher_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adminVoucherAdapter = new AdminVoucherAdapter(getActivity(), voucherList);
        recyclerView.setAdapter(adminVoucherAdapter);

        btnAddVoucher = root.findViewById(R.id.btnAddVoucher);
        btnAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.adminAddVoucherFragment);
            }
        });

        db.collection("Promotions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                VoucherModel voucher = documentSnapshot.toObject(VoucherModel.class);
                                String id = documentSnapshot.getId();
                                voucher.setId(id);

                                voucherList.add(voucher);
                                adminVoucherAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return root;
    }
}