package com.example.e2tech;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e2tech.Adapters.CategoryAdapter;
import com.example.e2tech.Models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {


    FirebaseAuth mAuth;
    FirebaseFirestore db;

    RecyclerView categoryRecyclerView;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;


    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment  newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
        View root = inflater.inflate(R.layout.fragment_category,container,false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        categoryModelList =  new ArrayList<>();
        categoryRecyclerView = root.findViewById(R.id.bigCategoryRecycleView);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),
                2,GridLayoutManager.VERTICAL,false);
        categoryRecyclerView.setLayoutManager(layoutManager);

        categoryAdapter =  new CategoryAdapter(getActivity(),categoryModelList,R.layout.catetory_item_category);
        categoryRecyclerView.setAdapter(categoryAdapter);

        db.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                                String id = documentSnapshot.getId();
                                categoryModel.setId(id);

//                                Log.v("CATEGORY","\n\n" +categoryModel.getName());
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(getActivity(),"Error" + task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });


        return root;
    }
}
