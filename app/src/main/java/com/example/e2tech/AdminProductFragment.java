package com.example.e2tech;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminCategoryAdapter;
import com.example.e2tech.Adapters.CategoryAdapter;
import com.example.e2tech.Models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProductFragment extends Fragment {

    FirebaseFirestore db;
    Button btnAddCategory;

    ArrayList<CategoryModel> categories;
    RecyclerView recyclerView;
    AdminCategoryAdapter adminCategoryAdapter;



    public AdminProductFragment() {
        // Required empty public constructor
    }

    public static AdminProductFragment newInstance(String param1, String param2) {
        AdminProductFragment fragment = new AdminProductFragment();
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
        View root = inflater.inflate(R.layout.fragment_admin_product, container, false);

        categories = new ArrayList<>();
        db = FirebaseFirestore.getInstance();


        recyclerView = root.findViewById(R.id.admin_category_recyclerview_in_cate_screen);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adminCategoryAdapter = new AdminCategoryAdapter(getActivity(), categories,R.layout.admin_category_item);
        recyclerView.setAdapter(adminCategoryAdapter);

        db.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                                String id = documentSnapshot.getId();
                                categoryModel.setId(id);
                                categories.add(categoryModel);
                                adminCategoryAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });



        return root;
    }
}