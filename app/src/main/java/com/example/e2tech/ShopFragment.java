package com.example.e2tech;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e2tech.Adapters.CategoryAdapter;
import com.example.e2tech.Adapters.PopularAdapter;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.Models.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;


    RecyclerView categoryRecyclerView;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    RecyclerView popularRecyclerView;
    PopularAdapter popularAdapter;
    ArrayList<ProductModel> productList;

    TextView tvSeeAll;
    NavController navController;

    SearchView searchView;

    public ShopFragment() {
        // Required empty public constructor
    }


    public static ShopFragment newInstance() {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
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
        View root = inflater.inflate(R.layout.fragment_shop, container, false);

        navController = NavHostFragment.findNavController(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        settingCategoryAdapter(root);
        db.collection("Categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                            String id = documentSnapshot.getId();
                            categoryModel.setId(id);
                            categoryModelList.add(categoryModel);
                            categoryAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });


        tvSeeAll = root.findViewById(R.id.tv_shop_see_all_text);
        tvSeeAll.setOnClickListener(view -> navController.navigate(R.id.action_shopFragment_to_categoryFragment));

        productList = new ArrayList<>();
        popularRecyclerView = root.findViewById(R.id.shop_product_recycle);
        popularRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        popularAdapter = new PopularAdapter(getActivity(), productList);
        popularRecyclerView.setAdapter(popularAdapter);

        assert getArguments() != null;
        String category = getArguments().getString("collection");
        Log.v("GET",category);
        db.collection(category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                            String id = documentSnapshot.getId();
                            productModel.setId(id);
                            productList.add(productModel);
                            popularAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e("FIREBASE","ERROR" + task.getException());
                    }
                });

        return root;
    }

    private void settingCategoryAdapter(View root) {
        categoryModelList = new ArrayList<>();
        categoryRecyclerView = root.findViewById(R.id.product_category_recycle);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getActivity(), categoryModelList,R.layout.category_item_home);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }
}