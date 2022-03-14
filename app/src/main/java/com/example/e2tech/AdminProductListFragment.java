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
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminProductAdapter;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProductListFragment extends Fragment {

    ArrayList<ProductModel> productList;
    RecyclerView recyclerView;
    AdminProductAdapter adminProductAdapter;

    FirebaseFirestore db;


    public AdminProductListFragment() {
        // Required empty public constructor
    }


    public static AdminProductListFragment newInstance(String param1, String param2) {
        AdminProductListFragment fragment = new AdminProductListFragment();
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
        View root = inflater.inflate(R.layout.fragment_admin_product_list, container, false);
        productList = new ArrayList<>();
        String category = getArguments().getString("category");
        db = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.admin_product_recyclerview_in_list_screen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adminProductAdapter = new AdminProductAdapter(getActivity(), productList);
        recyclerView.setAdapter(adminProductAdapter);
        Log.v("CATEGORY", category);

        db.collection("PopularProducts")
                .whereEqualTo("type", category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                ProductModel product = documentSnapshot.toObject(ProductModel.class);
                                String id = documentSnapshot.getId();
                                product.setId(id);

                                productList.add(product);
                                adminProductAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return root;
    }
}