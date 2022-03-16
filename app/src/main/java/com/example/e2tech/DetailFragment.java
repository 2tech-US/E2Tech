package com.example.e2tech;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.e2tech.Models.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * a
 */
public class DetailFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ImageView ivFavorite;

    ImageView ivProductImage;
    TextView tvProductName;
    TextView tvProductPrice;
    TextView tvProductAvailable;
    TextView tvProductDescription;
    TextView tvProductBrand;
    TextView tvProductCategory;
    RatingBar rbProductRating;

    TextView tvProductRate;
    TextView tvCommentSeeAll;
    ImageView ivComment;

    ProductModel product;

    NavController navController;

    private String productId;
    private String collection;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productId = getArguments() != null ? getArguments().getString("id") : null;
        collection = getArguments().getString("collection");

//        Transition transition = TransitionInflater.from(requireContext())
//                .inflateTransition(R.transition.product_share_transition);
//        setSharedElementEnterTransition(transition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        navController = NavHostFragment.findNavController(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchView(root);

        String url_image = getArguments() != null ? getArguments().getString("img_url") : null;

        Glide.with(this)
                .load(url_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(ivProductImage);

        FetchDataFromDatabase(collection, productId);


        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", product.getId());
                navController.navigate(R.id.commentDialogFragment, bundle);
            }
        });

        tvCommentSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", product.getId());
                navController.navigate(R.id.action_detailFragment_to_commentFragment, bundle);
            }
        });

        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.product_share_transition);
        setSharedElementEnterTransition(transition);

        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
        ViewCompat.setTransitionName(ivProductImage, getArguments().getString("img_url"));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postponeEnterTransition();
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v("PRODUCT-ID", productId);
        Log.v("PRODUCT-COLLECTION", collection);

        DocumentReference docRef = db.collection(collection).document(productId);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    product = documentSnapshot.toObject(ProductModel.class);
                    assert product != null;
                    String id = documentSnapshot.getId();
                    product.setId(id);
                    fetchViewByData(product);
                } else if (error != null) {
                    Log.w("Product", "Got an exception:", error);
                }
            }
        });

    }

    private void fetchView(View root) {
        tvProductName = root.findViewById(R.id.tv_product_name);
        tvProductAvailable = root.findViewById(R.id.tv_product_available);
        tvProductPrice = root.findViewById(R.id.tv_product_price);
        tvProductDescription = root.findViewById(R.id.tv_product_description);
        ivProductImage = root.findViewById(R.id.iv_product_image);
        tvProductBrand = root.findViewById(R.id.tv_product_brand);
        tvProductCategory = root.findViewById(R.id.tv_product_category);
        rbProductRating = root.findViewById(R.id.ratingBar);
        ivFavorite = root.findViewById((R.id.iv_product_favorite));

        tvProductRate = root.findViewById(R.id.tv_product_rate);
        tvCommentSeeAll = root.findViewById(R.id.tv_comment_seeall);
        ivComment = root.findViewById((R.id.iv_product_comment));

    }


    private void FetchDataFromDatabase(String collection, String id) {
        DocumentReference docRef = db.collection(collection).document(id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    product = documentSnapshot.toObject(ProductModel.class);
                    assert product != null;
                    String id = documentSnapshot.getId();
                    product.setId(id);
                    fetchViewByData(product);
                } else {
                    Log.e("FIRESTORE", "Document don't exist or something wrong happen");
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void fetchViewByData(ProductModel product) {
        tvProductName.setText(product.getName());
        tvProductPrice.setText(product.getPrice() + "VNĐ");
        tvProductDescription.setText(R.string.lorem_product_description);
        tvProductBrand.setText(product.getCompany());
        tvProductCategory.setText(product.getType());
//        tvProductRate.setText(Double.toString(product.getRating()) + '⭐');
//        rbProductRating.setRating(product.getRating());

        float fakeRating = (float) Math.round((0 + Math.random() * (5))*10)/10;
        tvProductRate.setText(fakeRating +"⭐");
        rbProductRating.setRating(fakeRating);

        if (product.getRemain() == 0) {
            tvProductAvailable.setTextColor(getResources().getColor(R.color.red,null));
            tvProductAvailable.setText("Out Stock");
        }
        else tvProductAvailable.setText("In Stock");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(product.getName());


//        Glide.with(this).load(product.getImg_url()).into(ivProductImage);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString("product_id",this.productId);
        edit.putString("collection",this.collection);

        edit.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.productId = pref.getString("product_id", "empty");
        this.collection = pref.getString("collection","empty");
    }
}