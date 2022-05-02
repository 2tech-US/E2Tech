package com.example.e2tech;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Models.CartModel;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.Models.UserModel;
import com.example.e2tech.Models.VoucherModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetail extends Fragment {
    private ProgressDialog progressDialog;

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;

    EditText edtName, edtPhone, edtAddress, edtNote, edtVoucher;
    TextView txtTotal, txtOderNumber, txtDiscount;
    Button btnOrder, btnVoucher;

    List<CartModel> cartModelList;
    float totalBill;
    float reduction;

    String username;
    String userPhone;
    String userAddress;

    public OrderDetail() {
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
    public static OrderDetail newInstance(String param1, String param2) {
        OrderDetail fragment = new OrderDetail();
        Bundle args = new Bundle();
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
        edtVoucher = root.findViewById(R.id.edt_voucher);

        txtTotal = root.findViewById(R.id.txt_total);
        txtOderNumber = root.findViewById(R.id.txt_oder_number);
        txtDiscount = root.findViewById(R.id.txt_discount);
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        String totalAmount = decimalFormat.format(totalBill);
        txtTotal.setText(String.valueOf(totalAmount) + " VND");
        txtOderNumber.setText(String.valueOf(totalAmount) + " VND");

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
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin! Có thể bỏ qua note", Toast.LENGTH_SHORT).show();
                } else {
                    submitOrder(name, phone, address, note);
                    removeCart();
                    // navigate to cart fragment
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_oderDetail_to_cartFragment);

                }
            }
        });

        btnVoucher = root.findViewById(R.id.btn_voucher);
        btnVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String voucher = edtVoucher.getText().toString();
                if (voucher.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập mã voucher", Toast.LENGTH_SHORT).show();
                } else {
                    applyVoucher(voucher);
                }
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").
                getReference("Users").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                username = user.getUsername();
                userPhone = user.getPhone();
                userAddress = user.getAddress();
                edtName.setText(username);
                edtPhone.setText(userPhone);
                edtAddress.setText(userAddress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không lấy được thông tin user", Toast.LENGTH_SHORT).show();
            }
        });

        for (int i = 0; i < cartModelList.size(); i++) {
            Log.v("ProductId", cartModelList.get(i).getProductId());
        }
        return root;
    }

    private void applyVoucher(String voucher) {
        // check voucher exist

        db.collection("Promotions").whereEqualTo("code", voucher).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Toast.makeText(getContext(), "Mã giảm giá không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        VoucherModel voucherModel = document.toObject(VoucherModel.class);
                        int discount = voucherModel.getDiscount();
                        reduction = (totalBill * discount) / 100;
                        // round reduction
                        int roundedReduction = ((int) reduction + 500) / 1000 * 1000;
                        float total = totalBill - roundedReduction;
                        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
                        String totalAmount = decimalFormat.format(total);
                        String totalReduction = decimalFormat.format(roundedReduction);
                        txtDiscount.setText(totalReduction + " VND");
                        txtTotal.setText(String.valueOf(totalAmount + " VND"));
                        Toast.makeText(getContext(), "Đã áp dụng mã giảm giá", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCart() {
        for (CartModel cartModel : cartModelList) {
            db.collection("Users").document(user.getUid()).collection("Cart").document(cartModel.getId()).delete();
        }
    }

    private void submitOrder(String name, String phone, String address, String note) {
        progressDialog.setMessage("Placing order...");
        progressDialog.show();

        List<String> listProductId = new ArrayList<>();

        for (CartModel cartItems : cartModelList)
            listProductId.add(cartItems.getProductId());


        db.runTransaction(
                transaction -> {
                    List<DocumentSnapshot> documentSnapshots = new ArrayList<>();
                    List<DocumentReference> documentReferences = new ArrayList<>();

                    for (int i = 0; i < listProductId.size(); i++) {
                        DocumentReference documentReference = db.collection("Products").document(listProductId.get(i));
                        documentReferences.add(documentReference);
                        DocumentSnapshot snapshot = transaction.get(documentReference);
                        documentSnapshots.add(snapshot);
                    }
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentReference documentReference = documentReferences.get(i);
                        DocumentSnapshot snapshot = documentSnapshots.get(i);
                        Long remainStock = snapshot.getLong("remain");
                        String productName = snapshot.getString("name");
                        if (remainStock == null) {
                            throw new Error("Hệ thống gặp trục trặc");
                        }
                        if (remainStock < cartModelList.get(i).getTotalQuantity()) {
                            throw new Error(productName + " Đã Hết Hàng");
                        }
                        long newRemainStock = remainStock - cartModelList.get(i).getTotalQuantity();
                        Log.v("RemainStock", Long.toString(newRemainStock));

                        transaction.update(documentReference, "remain", newRemainStock);
                    }
                    return null;
                }
        ).addOnSuccessListener(new OnSuccessListener<Object>() {

            @Override
            public void onSuccess(Object o) {
                String timestamp = String.valueOf(System.currentTimeMillis());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", timestamp);
                hashMap.put("address", address);
                hashMap.put("receiverName", name);
                hashMap.put("phone", phone);
                hashMap.put("createAt", timestamp);
                hashMap.put("status", "waiting");
                hashMap.put("total", totalBill);
                hashMap.put("orderBy", user.getUid());
                hashMap.put("note", note);
                hashMap.put("quantity", cartModelList.size());
                hashMap.put("username", username);
                // add to db
                db.collection("Orders").document(timestamp).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        for (CartModel cartModel : cartModelList) {
                            db.collection("Orders").document(timestamp).collection("Items").document(cartModel.getProductId()).set(cartModel);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Đặt hàng thất bại", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Đặt hàng thất bại," + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

//
//        String timestamp = String.valueOf(System.currentTimeMillis());
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("id", timestamp);
//        hashMap.put("address", address);
//        hashMap.put("receiverName", name);
//        hashMap.put("phone", phone);
//        hashMap.put("createAt", timestamp);
//        hashMap.put("status", "waiting");
//        hashMap.put("total", totalBill);
//        hashMap.put("orderBy", user.getUid());
//        hashMap.put("note", note);
//        hashMap.put("quantity", cartModelList.size());
//        hashMap.put("username", username);
//        // add to db
//        db.collection("Orders").document(timestamp).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                for (CartModel cartModel : cartModelList) {
//                    db.collection("Orders").document(timestamp).collection("Items").document(cartModel.getProductId()).set(cartModel);
//                }
//                progressDialog.dismiss();
//                Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_LONG).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(getContext(), "Đặt hàng thất bại", Toast.LENGTH_LONG).show();
//            }
//        });
}
