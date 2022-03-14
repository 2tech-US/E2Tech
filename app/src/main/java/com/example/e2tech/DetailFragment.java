package com.example.e2tech;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Objects;

/**
 * a
 */
public class DetailFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    ImageView ivFavorite;
    ImageView ivComment;

    ImageView ivProductImage;
    TextView tvProductName;
    TextView tvProductPrice;
    TextView tvProductAvailable;
    TextView tvProductDescription;
    Button btnAddToCart;

    ProductModel product;

    NavController navController;


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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        fetchView(root);

        String id = getArguments().getString("id");
        String collection = getArguments().getString("collection");
        String url_image = getArguments().getString("img_url");

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

        FetchDataFromDatabase(collection, id);


        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", product.getId());
                bundle.putString("userEmail",email);
                navController.navigate(R.id.commentDialogFragment,bundle);
            }
        });


        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.product_share_transition);
        setSharedElementEnterTransition(transition);

        if(savedInstanceState == null) {
            postponeEnterTransition();
        }
        ViewCompat.setTransitionName(ivProductImage,getArguments().getString("img_url"));

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        return root;
    }

    private void addToCart() {
        final HashMap<String, Object> cart = new HashMap<>();
        cart.put("productId", product.getId());
        cart.put("productName", product.getName());
        cart.put("productPrice", product.getPrice());
        cart.put("productImageURL", product.getImg_url());
        cart.put("totalQuantity", 1);
        cart.put("totalPrice", product.getPrice());

        db.collection("AddToCart").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).
                collection("CurrentUser").add(cart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postponeEnterTransition();
    }

    @Override
    public void onStart() {
        super.onStart();

        String id = getArguments().getString("id");
        String collection = getArguments().getString("collection");

        Log.v("PRODUCT-ID",id);
        Log.v("PRODUCT-COLLECTION",collection);

        DocumentReference docRef = db.collection(collection).document(id);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    product = documentSnapshot.toObject(ProductModel.class);
                    assert product != null;
                    String id = documentSnapshot.getId();
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
        ivFavorite = root.findViewById((R.id.iv_product_favorite));
        ivComment = root.findViewById((R.id.iv_product_comment));
        btnAddToCart = root.findViewById(R.id.btn_add_to_cart);
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


    private void fetchViewByData(ProductModel product) {
        tvProductName.setText(product.getName());
        tvProductAvailable.setText(product.getCompany());
        tvProductPrice.setText(Integer.toString(product.getPrice()));
        tvProductDescription.setText(R.string.lorem_product_description);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(product.getName());


//        Glide.with(this).load(product.getImg_url()).into(ivProductImage);
    }

}