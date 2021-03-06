package com.example.e2tech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Adapters.CartAdapter;
import com.example.e2tech.Interface.OnCartItemChange;
import com.example.e2tech.Models.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements OnCartItemChange {

    FirebaseFirestore db;
    FirebaseAuth auth;
    TextView overToTalAmount;
    RecyclerView recyclerView;
    Button checkoutBtn;

    int totalBill = 0;
    CartAdapter cartAdapter;
    List<CartModel> cartModelList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerView = root.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        checkoutBtn = root.findViewById(R.id.checkoutBtn);

        overToTalAmount = root.findViewById(R.id.textView);
        overToTalAmount.bringToFront();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));

        cartModelList = new ArrayList<>();
        cartAdapter = new CartAdapter(getActivity(), cartModelList, this);
        recyclerView.setAdapter(cartAdapter);

        db.collection("Users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        CartModel cartModel = documentSnapshot.toObject(CartModel.class);
                        cartModelList.add(cartModel);
                    }
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModelList.size() > 0) {

                    List<String> listProductId = new ArrayList<>();

                    for (CartModel cartItems : cartModelList)
                        listProductId.add(cartItems.getProductId());


                    db.runTransaction(
                            transaction -> {
                                for (int i = 0; i < listProductId.size(); i++) {
                                    DocumentReference documentReference = db.collection("Products").document(listProductId.get(i));
                                    DocumentSnapshot snapshot = transaction.get(documentReference);
                                    Long remainStock = snapshot.getLong("remain");
                                    String productName = snapshot.getString("name");
                                    if (remainStock == null) {
                                        throw new Error("H??? th???ng g???p tr???c tr???c");
                                    }
                                    if (remainStock < cartModelList.get(i).getTotalQuantity()) {
                                        throw new Error(productName + " ???? H???t H??ng");
                                    }
                                }
                                return null;
                            }
                    ).addOnSuccessListener(new OnSuccessListener<Object>() {

                        @Override
                        public void onSuccess(Object o) {
                            Bundle extras = new Bundle();
                            extras.putSerializable("cartModelList", (Serializable) cartModelList);
                            extras.putInt("totalBill", totalBill);

                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                            navController.navigate(R.id.action_cartFragment_to_oderDetail, extras);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "S???n ph???m " + e.getMessage().substring(16), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Gi??? h??ng ??ang tr???ng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            totalBill = intent.getIntExtra("totalAmount", 0);

            DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
            String totalAmount = decimalFormat.format(totalBill);

            overToTalAmount.setText("T???ng ti???n: " + totalAmount + " VND");
        }
    };

    public void onCartItemChange(int data) {
        totalBill = data;

        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        String totalAmount = decimalFormat.format(totalBill);

        overToTalAmount.setText("T???ng ti???n: " + totalAmount + " VND");
    }
}