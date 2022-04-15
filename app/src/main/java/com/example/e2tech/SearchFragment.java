package com.example.e2tech;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e2tech.Adapters.PopularAdapter;
import com.example.e2tech.Adapters.SearchAdapter;
import com.example.e2tech.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    SearchView searchView;
    FirebaseFirestore db;


    RecyclerView searchRecyclerView;
    SearchAdapter searchAdapter;
    ArrayList<ProductModel> productList;
    EditText edtSearch;
    MenuItem searchItem;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        db = FirebaseFirestore.getInstance();

        productList = new ArrayList<>();
        searchRecyclerView = root.findViewById(R.id.search_recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        searchAdapter = new SearchAdapter(getActivity(), productList);
        searchRecyclerView.setAdapter(searchAdapter);

        db.collection("Products").orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                String id = documentSnapshot.getId();
                                productModel.setId(id);
                                productList.add(productModel);
                                searchAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


//        SearchView searchBar = getActivity().findViewById(R.id.searchBar);

//        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Log.v("SUBMIT", "\n\n\n" + query);
//                //searchProduct(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.v("SEARCH", "\n\n\n" + newText);
//                searchProduct(newText);
//                return false;
//            }
//        });

        edtSearch = root.findViewById(R.id.edt_search);

        edtSearch.requestFocus();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                searchProduct(charSequence.toString());
                searchAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchAdapter.getFilter().filter(editable.toString());
            }
        });

        CharSequence tempChar = "";
        searchAdapter.getFilter().filter(tempChar);


        return root;
    }

//    private void searchProduct(String query) {
//        if (!query.equals("")) {
//            db.collection("PopularProducts").orderBy("name").startAt(query).endAt(query+"\uf8ff")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                                    String id = documentSnapshot.getId();
//                                    productModel.setId(id);
//                                    boolean exist = false;
//                                    for (ProductModel productModel1: productList) {
//                                        if (productModel1.getId().equals(id)) {
//                                            exist = true;
//                                            break;
//                                        }
//                                    }
//                                    if (exist == false) {
//                                        productList.add(productModel);
//                                    }
//                                }
//                            } else {
//                                Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        } else {
//            productList.clear();
//        }
//
//        searchAdapter.notifyDataSetChanged();
//
//
//
//
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}