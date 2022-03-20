package com.example.e2tech;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Models.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OderDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OderDetail extends Fragment {
    private ProgressDialog progressDialog;

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;

    EditText edtName, edtPhone, edtAddress, edtNote;
    TextView txtTotal, txtOderNumber;
    Button btnOrder;

    List<CartModel> cartModelList;
    int totalBill;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OderDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OderDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static OderDetail newInstance(String param1, String param2) {
        OderDetail fragment = new OderDetail();
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
            cartModelList = (List<CartModel>) getArguments().getSerializable("cartModelList");
            totalBill = getArguments().getInt("totalBill");
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_oder_detail, container, false);

        edtAddress = root.findViewById(R.id.edt_shipping_address);
        edtName = root.findViewById(R.id.edt_name);
        edtPhone = root.findViewById(R.id.edt_phone_number);
        edtNote = root.findViewById(R.id.edt_note);

        txtTotal = root.findViewById(R.id.txt_total);
        txtOderNumber = root.findViewById(R.id.txt_oder_number);
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        String totalAmount = decimalFormat.format(totalBill);
        txtTotal.setText(String.valueOf(totalAmount));
        txtOderNumber.setText(String.valueOf(totalAmount));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnOrder = root.findViewById(R.id.btn_order);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String phone = edtPhone.getText().toString();
                String address = edtAddress.getText().toString();
                String note = edtNote.getText().toString();

                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields! The note is not required", Toast.LENGTH_SHORT).show();
                } else {
                    submitOrder(name, phone, address, note);

                }


            }
        });

        return root;
    }

    private void submitOrder(String name, String phone, String address, String note) {
        progressDialog.setMessage("Placing order...");
        progressDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", timestamp);
        hashMap.put("address", address);
        hashMap.put("receiverName", name);
        hashMap.put("phone", phone);
        hashMap.put("createAt", timestamp);
        hashMap.put("status", "In Progress");
        hashMap.put("total", totalBill);
        hashMap.put("orderBy", user.getUid());
        hashMap.put("note", note);

        // add to db
        db.collection("Orders").document(timestamp).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for (CartModel cartModel : cartModelList) {
                    db.collection("Orders").document(timestamp).collection("Items").document(cartModel.getProductId()).set(cartModel);
                }
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Order Placed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}