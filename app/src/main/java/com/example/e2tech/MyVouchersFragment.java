package com.example.e2tech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e2tech.Adapters.VoucherAdapter;
import com.example.e2tech.Models.VoucherModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyVouchersFragment extends Fragment {
    ArrayList<VoucherModel> voucherList;
    RecyclerView recyclerView;
    VoucherAdapter voucherAdapter;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_vouchers, container, false);

        return view;
    }
}
