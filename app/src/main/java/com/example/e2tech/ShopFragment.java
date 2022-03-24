package com.example.e2tech;

import android.os.Bundle;
import android.util.Log;
import android.util.MutableInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment implements View.OnClickListener {

    // NavController
    NavController navController;

    // FireBase
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    // Category RecycleView
    RecyclerView categoryRecyclerView;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    // Product RecycleView
    RecyclerView productRecycleView;
    PopularAdapter productAdapter;
    ArrayList<ProductModel> productList;


    // View: see all category
    TextView tvSeeAll;

    // view: filter
    TextView tvFilterPopular;
    TextView tvFilterRate;
    TextView tvFilterPrice;
    TextView tvFilterDiscount;

    AppCompatButton btnFilter;
    // Filter logic
    private IntRef  filterPopular = new IntRef();
    private IntRef filterRate = new IntRef();
    private IntRef  filterPrice = new IntRef();
    private IntRef  filterDiscount = new IntRef();

    // Query with Filter
    private Query filterProductQuery;

    // Pagination Logic
    private DocumentSnapshot lastVisible;
    private final int limit_query = 2;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

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

        fetchViewById(root);

        fetchViewByOnClickListener();

        categoryModelList = new ArrayList<>();

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getActivity(), categoryModelList, R.layout.category_item_home);
        categoryRecyclerView.setAdapter(categoryAdapter);

        db.collection("Categories")
                .get()
                .addOnCompleteListener(task -> {
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
                });

        productList = new ArrayList<>();

        productRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        productAdapter = new PopularAdapter(getActivity(), productList);
        productRecycleView.setAdapter(productAdapter);

//        productRecycleView.setNestedScrollingEnabled(false);

        assert getArguments() != null;
        String category = getArguments().getString("collection");
        Log.v("GET", category);

        filterProductQuery = db.collection("Products").whereEqualTo("type", category);
        Query queryProducts = db.collection("Products").whereEqualTo("type", category);

//        db.collection("Products").whereEqualTo("type",category).limit(limit_query)

        queryProducts.limit(limit_query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                            String id = documentSnapshot.getId();
                            productModel.setId(id);
                            productList.add(productModel);
                        }
                        productAdapter.notifyDataSetChanged();

                        if (!task.getResult().isEmpty()) {
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                            productRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrolling = true;
                                    }
                                    // this is alternative solution
                                    if (!recyclerView.canScrollVertically(1)) {
                                        GridLayoutManager gridLayoutManager = (GridLayoutManager) productRecycleView.getLayoutManager();
//
                                        int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                        int visibleItemCount = gridLayoutManager.getChildCount();
                                        int totalItemCount = gridLayoutManager.getItemCount();

                                        if (isScrolling && ((firstVisibleItemPosition + visibleItemCount) >= totalItemCount) && !isLastItemReached) {
                                            isScrolling = false;

                                            Log.v("FIST_ITEM", Integer.toString(firstVisibleItemPosition));
                                            Log.v("VISIBLE_COUNT", Integer.toString(visibleItemCount));
                                            Log.v("TOTAL_COUNT", Integer.toString(totalItemCount));

                                            Query nextQuery = queryProducts.limit(limit_query).startAfter(lastVisible);
                                            nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                                            String id = documentSnapshot.getId();
                                                            productModel.setId(id);
                                                            productList.add(productModel);
                                                            Log.v("HEHEHHE", "please work");
                                                        }
                                                        productAdapter.notifyDataSetChanged();

                                                        if (!task.getResult().isEmpty()) {
                                                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                                            if (task.getResult().size() < limit_query) {
                                                                isLastItemReached = true;
                                                                Log.v("ITEM", "LAST");
                                                            }
                                                        } else isLastItemReached = true;

                                                    }
                                                }
                                            });
                                        }
                                    }

                                }

                                // onScrolled only call once ? I will use alternative solution
                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
//                                    GridLayoutManager gridLayoutManager = (GridLayoutManager) productRecycleView.getLayoutManager();
//
//                                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
//                                    int visibleItemCount = gridLayoutManager.getChildCount();
//                                    int totalItemCount = gridLayoutManager.getItemCount();
//
//                                    Log.v("FIST_ITEM", Integer.toString(firstVisibleItemPosition));
//                                    Log.v("VISIBLE_COUNT", Integer.toString(visibleItemCount));
//                                    Log.v("TOTAL_COUNT", Integer.toString(totalItemCount));
//
//
//                                    if (isScrolling && ((firstVisibleItemPosition + visibleItemCount) >= totalItemCount) && !isLastItemReached) {
//                                        isScrolling = false;
//
//                                        Query nextQuery = products.limit(limit_query).startAfter(lastVisible);
//                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
//                                                        String id = documentSnapshot.getId();
//                                                        productModel.setId(id);
//                                                        productList.add(productModel);
//                                                        Log.v("HEHEHHE","please work");
//                                                    }
//                                                    productAdapter.notifyDataSetChanged();
//
//                                                    if(!task.getResult().isEmpty()) {
//                                                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
//
//                                                        if (task.getResult().size() < limit_query) {
//                                                            isLastItemReached = true;
//                                                            Log.v("ITEM", "LAST");
//                                                        }
//                                                    }
//                                                    else isLastItemReached = true;
//
//                                                }
//                                            }
//                                        });
//                                    }
                                }
                            });
                        }

                    } else {
                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e("FIREBASE", "ERROR" + task.getException());
                    }
                });

        return root;
    }

    private void fetchViewById(View root) {
        // recycle view
        categoryRecyclerView = root.findViewById(R.id.product_category_recycle);
        productRecycleView = root.findViewById(R.id.shop_product_recycle);
        // text view
        tvSeeAll = root.findViewById(R.id.tv_shop_see_all_text);
        tvFilterPopular = root.findViewById(R.id.shop_filter_popular);
        tvFilterPrice = root.findViewById(R.id.shop_filter_price);
        tvFilterDiscount = root.findViewById(R.id.shop_filter_discount);
        tvFilterRate = root.findViewById(R.id.shop_filter_rate);

        btnFilter = root.findViewById(R.id.shop_fillter_button);
    }

    private void fetchViewByOnClickListener() {
        tvSeeAll.setOnClickListener(this);
        tvFilterRate.setOnClickListener(this);
        tvFilterPopular.setOnClickListener(this);
        tvFilterDiscount.setOnClickListener(this);
        tvFilterPrice.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_filter_popular:
                changeFilterLogic((TextView) view, filterPopular);
                break;
            case R.id.shop_filter_discount:
                changeFilterLogic((TextView) view, filterDiscount);
                break;
            case R.id.shop_filter_price:
                changeFilterLogic((TextView) view, filterPrice);
                break;
            case R.id.shop_filter_rate:
                changeFilterLogic((TextView) view, filterRate);
                break;
            case R.id.shop_fillter_button:
                if(filterRate.value != 0) {
                    if(filterRate.value==-1) {
                        filterProductQuery.orderBy("rating", Query.Direction.DESCENDING);
                    } else filterProductQuery.orderBy("rating", Query.Direction.ASCENDING);
                }
                if(filterPopular.value != 0) {
                    if(filterPopular.value ==-1) {
                        filterProductQuery.orderBy("rating", Query.Direction.DESCENDING);
                    } else filterProductQuery.orderBy("rating", Query.Direction.ASCENDING);
                }
                if(filterPrice.value != 0) {
                    if(filterPrice.value==-1) {
                        filterProductQuery.orderBy("rating", Query.Direction.DESCENDING);
                    } else filterProductQuery.orderBy("rating", Query.Direction.ASCENDING);
                }
                if(filterDiscount.value != 0) {
                    if(filterDiscount.value ==-1) {
                        filterProductQuery.orderBy("rating", Query.Direction.DESCENDING);
                    } else filterProductQuery.orderBy("rating", Query.Direction.ASCENDING);
                }

//                productRecycleView.clearOnScrollListeners();
                productList.clear();
                filterProductQuery.limit(limit_query)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                    String id = documentSnapshot.getId();
                                    productModel.setId(id);
                                    productList.add(productModel);
                                }
                                productAdapter.notifyDataSetChanged();
                                Log.v("FUCK", "100%");

                                if (!task.getResult().isEmpty()) {
                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                    Log.v("FUCK", "WORKING PLEZSE");

                                    productRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                                isScrolling = true;
                                            }
                                            // this is alternative solution
                                            if (!recyclerView.canScrollVertically(1)) {
                                                GridLayoutManager gridLayoutManager = (GridLayoutManager) productRecycleView.getLayoutManager();
//
                                                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                                int visibleItemCount = gridLayoutManager.getChildCount();
                                                int totalItemCount = gridLayoutManager.getItemCount();

                                                if (isScrolling && ((firstVisibleItemPosition + visibleItemCount) >= totalItemCount) && !isLastItemReached) {
                                                    isScrolling = false;

                                                    Log.v("FIST_ITEM", Integer.toString(firstVisibleItemPosition));
                                                    Log.v("VISIBLE_COUNT", Integer.toString(visibleItemCount));
                                                    Log.v("TOTAL_COUNT", Integer.toString(totalItemCount));

                                                    Query nextQuery = filterProductQuery.limit(limit_query).startAfter(lastVisible);
                                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                                                    String id = documentSnapshot.getId();
                                                                    productModel.setId(id);
                                                                    productList.add(productModel);
                                                                    Log.v("HEHEHHE", "please work");
                                                                }
                                                                productAdapter.notifyDataSetChanged();

                                                                if (!task.getResult().isEmpty()) {
                                                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                                                    if (task.getResult().size() < limit_query) {
                                                                        isLastItemReached = true;
                                                                        Log.v("ITEM", "LAST");
                                                                    }
                                                                } else isLastItemReached = true;

                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    });
                                }

                            } else {
                                Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                Log.e("FIREBASE", "ERROR" + task.getException());
                            }
                        });

                break;
            case R.id.tv_shop_see_all_text:
                navController.navigate(R.id.action_shopFragment_to_categoryFragment);
                break;
        }
    }

    private void changeFilterLogic(TextView view,IntRef currentState) {
        if(currentState.value == -1) {
            currentState.value++;
            view.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0,
                    0);
        }
        else if(currentState.value == 0) {
            currentState.value++;
            view.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_baseline_arrow_upward_12,
                    0, 0, 0);
        }
        else if(currentState.value == 1) {
            currentState.value = -1;
            view.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_baseline_arrow_downward_12,
                    0, 0, 0);
        }
    }

    public static class IntRef {
        public int value;

        IntRef() {
            value = 0;
        }
    }

}