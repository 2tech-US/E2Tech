package com.example.e2tech;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
    private Bundle productBundleRecyclerViewState;

    // View: see all category
    TextView tvSeeAll;

    // view: filter
    TextView tvFilterPopular;
    TextView tvFilterRate;
    TextView tvFilterPrice;
    TextView tvFilterDiscount;
    // Filter logic
    private final IntRef  filterPopular = new IntRef();
    private final IntRef filterRate = new IntRef();
    private final IntRef  filterDiscount = new IntRef();
    private int sortParam;

    private final IntRef  filterPrice = new IntRef();

    // Query with Filter
    private Query queryProducts;
    // Pagination Logic
    private DocumentSnapshot lastVisible;
    private final int limit_query = 4;
    private boolean isScrolling = false;
    private boolean isLastItemReached;


    private MainActivity mainActivity;

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
        currentUser = mAuth.getCurrentUser();

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

        resumeFilterState();

        productList = new ArrayList<>();
        productRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        productAdapter = new PopularAdapter(getActivity(), productList);
        productRecycleView.setAdapter(productAdapter);

//        productRecycleView.setNestedScrollingEnabled(false);

        mainActivity = (MainActivity)getActivity();
        if(getArguments() != null) {
            String category = getArguments().getString("collection");
            mainActivity.toolbar.setTitle(category);
            queryProducts = db.collection("Products").whereEqualTo("type", category);
        } else {
            categoryRecyclerView.setVisibility(View.GONE);
            tvSeeAll.setVisibility(View.GONE);
            ArrayList<String> userFavoriteProducts = mainActivity.getUserFavoriteProducts();
            queryProducts = db.collection("Products").whereIn(FieldPath.documentId(),userFavoriteProducts);
        }

        setupFilterAndQuery();

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

    }

    private void fetchViewByOnClickListener() {
        tvSeeAll.setOnClickListener(this);
        tvFilterRate.setOnClickListener(this);
        tvFilterPopular.setOnClickListener(this);
        tvFilterDiscount.setOnClickListener(this);
        tvFilterPrice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_filter_popular:
                enableFilterBackground((TextView) view,filterPopular);
                setupFilterAndQuery();
                break;
            case R.id.shop_filter_discount:
                enableFilterBackground((TextView) view,filterDiscount);
                setupFilterAndQuery();
                break;
            case R.id.shop_filter_rate:
                enableFilterBackground((TextView) view,filterRate);
                setupFilterAndQuery();
                break;
            case R.id.shop_filter_price:
                changeFilterLogic((TextView) view,filterPrice);
                setupFilterAndQuery();
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


    // 0 : None
    // 1 : Popular
    // 2 : Rate
    // 3 : Discount
    private void enableFilterBackground(TextView v, IntRef viewLogic) {
        if(v.getBackground() == null) {
            resetFilter();
            v.setBackgroundResource(R.drawable.bg_grey_corner_10);
            viewLogic.setValue(1);
        } else {
            Log.v("SOMETHING_FUCKUP","YES");
            resetFilter();
        }
    }

    private void resetFilter() {
        tvFilterPopular.setBackgroundResource(0);
        filterPopular.setValue(0);

        tvFilterRate.setBackgroundResource(0);
        filterRate.setValue(0);

        tvFilterDiscount.setBackgroundResource(0);
        filterDiscount.setValue(0);
    }



    private void setupFilterAndQuery() {
        isLastItemReached = false;
        Query filterProductQuery = queryProducts;

        if (filterRate.value != 0) {
            filterProductQuery = filterProductQuery.orderBy("rating", Query.Direction.DESCENDING);
            sortParam = 2;
        } else if (filterPopular.value != 0) {
            filterProductQuery = filterProductQuery.orderBy("numberSold", Query.Direction.DESCENDING);
            sortParam = 1;
        } else if (filterDiscount.value != 0) {
            filterProductQuery = filterProductQuery.orderBy("discount", Query.Direction.DESCENDING);
            sortParam = 3;
        } else {
            sortParam = 0;
        }

        if(filterPrice.value != 0) {
            if(filterPrice.value==-1) {
                filterProductQuery = filterProductQuery.orderBy("price", Query.Direction.DESCENDING);
            } else filterProductQuery = filterProductQuery.orderBy("price", Query.Direction.ASCENDING);
        }

        productRecycleView.clearOnScrollListeners();
        productList.clear();
        queryFirebaseAndSetUpPaginationRecycleView(filterProductQuery);
    }


    private void queryFirebaseAndSetUpPaginationRecycleView(Query queryProducts) {
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
                                    // this is alternative solution (this should be place in onScroll)
                                    if (!recyclerView.canScrollVertically(1)) {
                                        GridLayoutManager gridLayoutManager = (GridLayoutManager) productRecycleView.getLayoutManager();
//
                                        int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                        int visibleItemCount = gridLayoutManager.getChildCount();
                                        int totalItemCount = gridLayoutManager.getItemCount();

                                        if (isScrolling && ((firstVisibleItemPosition + visibleItemCount) >= totalItemCount) && !isLastItemReached) {
                                            isScrolling = false;

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
                                // onScrolled only call once ? (I am using alternative solution RecycleView.canScrollVertically(1))
                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                }
                            });
                        }

                    } else {
                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
//                        Log.e("FIREBASE", "ERROR" + task.getException());
                    }
                });
    }



    // save state recycle view
    @Override
    public void onPause() {
        super.onPause();

        Log.v("onPause","START");
        SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("SORT_PARAM",sortParam);
        editor.putInt("SORT_PRICE",filterPrice.value);

        editor.commit();

//        productBundleRecyclerViewState = new Bundle();
//        productBundleRecyclerViewState.putParcelable("LIST_STATE",productRecycleView.getLayoutManager().onSaveInstanceState());
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    public void resumeFilterState() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        Log.v("SORT_PRICE",Integer.toString(pref.getInt("SORT_PRICE",0)));
        Log.v("SORT_PARAM",Integer.toString(pref.getInt("SORT_PARAM",0)));

        filterPrice.value = pref.getInt("SORT_PRICE",0) -1;
        if(filterPrice.value == -2) filterPrice.value =1;
        changeFilterLogic(tvFilterPrice,filterPrice);
        int temp = pref.getInt("SORT_PARAM",0);
        if(temp == 1) {
            enableFilterBackground(tvFilterPopular,filterPopular);
        } else if(temp ==2) {
            enableFilterBackground(tvFilterRate,filterRate);
        } else if (temp ==3) {
            enableFilterBackground(tvFilterDiscount,filterDiscount);
        }
    }

    public static class IntRef {
        public int value;
        IntRef() {
            value = 0;
        }
        public void setValue(int value) {
            this.value = value;
        }

    }

}