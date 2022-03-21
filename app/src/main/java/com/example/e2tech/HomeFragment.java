package com.example.e2tech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e2tech.Activities.LoginActivity;
import com.example.e2tech.Adapters.BannerSliderAdapter;
import com.example.e2tech.Adapters.CategoryAdapter;
import com.example.e2tech.Adapters.PopularAdapter;
import com.example.e2tech.Models.BannerModel;
import com.example.e2tech.Models.CategoryModel;
import com.example.e2tech.Models.ProductModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    Button btnLogout;
    TextView tvUsername;

    SliderView bannerSliderView;
    List<BannerModel> bannerList;
    BannerSliderAdapter bannerSliderAdapter;

    RecyclerView categoryRecyclerView;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    RecyclerView popularRecyclerView;
    PopularAdapter popularAdapter;
    ArrayList<ProductModel> productList;


    TextView tvSeeAll;
    NavController navController;

    RecyclerView favoriteRecyclerView;
    PopularAdapter favoriteAdapter;
    ArrayList<ProductModel> favoriteList;

    MainActivity mainActivity;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        navController = NavHostFragment.findNavController(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();

        tvUsername = root.findViewById(R.id.tv_username);
        tvUsername.setText(email);


        btnLogout = root.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });

        bannerList = new ArrayList<>();
        bannerSliderView = root.findViewById(R.id.imageBannerSlider);

//        bannerList.add(new BannerModel(R.drawable.home_games_slide_2));
//        bannerList.add(new BannerModel(R.drawable.home_tec_slide_4));
//        bannerList.add(new BannerModel(R.drawable.home_tecnologia_slide_6));

        bannerSliderAdapter = new BannerSliderAdapter(getActivity(), bannerList);
        bannerSliderView.setSliderAdapter(bannerSliderAdapter);

        db.collection("Banners")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                BannerModel bannerModel = documentSnapshot.toObject(BannerModel.class);
                                bannerList.add(bannerModel);
                                bannerSliderAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        categoryModelList = new ArrayList<>();
        categoryRecyclerView = root.findViewById(R.id.home_category_recycler);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getActivity(), categoryModelList, R.layout.category_item_home);
        categoryRecyclerView.setAdapter(categoryAdapter);

        db.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                                String id = documentSnapshot.getId();
                                categoryModel.setId(id);

                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        tvSeeAll = root.findViewById(R.id.home_see_all_category);
        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_homeFragment_to_categoryFragment);
            }
        });

        productList = new ArrayList<>();
        popularRecyclerView = root.findViewById(R.id.home_popular_recycler);
        popularRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        popularAdapter = new PopularAdapter(getActivity(), productList);
        popularRecyclerView.setAdapter(popularAdapter);

        db.collection("PopularProducts")
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
                                popularAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.e("FIREBASE", "ERROR" + task.getException());
                        }
                    }
                });


        favoriteList = new ArrayList<>();
        favoriteRecyclerView = root.findViewById(R.id.home_favorite_recycler);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        favoriteAdapter = new PopularAdapter(getActivity(), favoriteList);
        favoriteRecyclerView.setAdapter(favoriteAdapter);

        ArrayList<String> userFavoriteProducts = new ArrayList<>();


        CollectionReference collectionReference = db.collection("Users").document(currentUser.getUid()).collection("Favorites");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String productId = documentSnapshot.getId();
                        userFavoriteProducts.add(productId);
                    }

                    mainActivity = (MainActivity) getActivity();
                    mainActivity.setUserFavoriteProducts(userFavoriteProducts);

                    if (!userFavoriteProducts.isEmpty()) {
                        db.collection("PopularProducts").whereIn(FieldPath.documentId(), userFavoriteProducts)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                                String id = documentSnapshot.getId();
                                                productModel.setId(id);
                                                favoriteList.add(productModel);
                                                favoriteAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                            Log.e("FIREBASE", "ERRROR" + task.getException());
                                        }
                                    }
                                });
                    }


                } else {
                    Log.e("FIRESTORE", task.getException().toString());
                }
            }
        });

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}