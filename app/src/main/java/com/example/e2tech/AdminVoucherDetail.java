package com.example.e2tech;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e2tech.Activities.RegisterActivity;
import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.Models.VoucherModel;
import com.example.e2tech.databinding.FragmentAdminProductDetailBinding;
import com.example.e2tech.databinding.FragmentAdminVoucherDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProductDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminVoucherDetail extends Fragment implements View.OnClickListener {

    VoucherModel voucher;

    ImageView imgVoucher;
    EditText edtVoucherCode, edtVoucherDiscount, edtVoucherDescription;

    Button btnUpdate, btnDelete, btnUpdateImage, btnSelectImage;
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage;

    Uri imgUri;

    FragmentAdminVoucherDetailBinding binding;

    public AdminVoucherDetail() {
        // Required empty public constructor
    }


    public static AdminVoucherDetail newInstance(String param1, String param2) {
        AdminVoucherDetail fragment = new AdminVoucherDetail();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            voucher = (VoucherModel) getArguments().getSerializable("voucher");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminVoucherDetailBinding.inflate(getLayoutInflater());

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_voucher_detail, container, false);

        edtVoucherCode = root.findViewById(R.id.edt_admin_voucher_detail_code);
        edtVoucherDiscount = root.findViewById(R.id.edt_admin_voucher_detail_discount);
        edtVoucherDescription = root.findViewById(R.id.edt_admin_voucher_detail_description);

        imgVoucher = root.findViewById(R.id.img_admin_voucher_detail);
        Glide.with(getActivity()).load(voucher.getImg_url()).into(imgVoucher);

        edtVoucherCode.setText(voucher.getCode());
        edtVoucherDiscount.setText(Integer.toString(voucher.getDiscount()));
        edtVoucherDescription.setText(voucher.getDescription());

        btnUpdate = root.findViewById(R.id.btn_admin_voucher_detail_update);
        btnDelete = root.findViewById(R.id.btn_admin_voucher_detail_delete);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imgVoucher.setImageURI(result);
                imgUri = result;
            }
        });



        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = storage.getReference();

        btnSelectImage = root.findViewById(R.id.btn_admin_select_image_voucher);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        btnUpdateImage = root.findViewById(R.id.btn_admin_update_image_voucher);
        btnUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgUri != null) {
                    updateImage();
                } else {
                    Toast.makeText(getActivity(), "You must select image first", Toast.LENGTH_LONG).show();
                }
            }
        });



        return root;
    }

    private void updateImage() {
        StorageReference reference = storage.getReference().child("Vouchers/" + voucher.getCode() + UUID.randomUUID().toString());

        reference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadUrl = uri;

                            HashMap map = new HashMap();
                            map.put("img_url", downloadUrl.toString());


                            db.collection("Promotions").document(voucher.getCode()).update(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isComplete()) {
                                        Toast.makeText(getActivity(), "Voucher has been updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }});
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_admin_product_detail_update) {
            updateProduct();
        }
    }

    private void updateProduct() {

        String code = edtVoucherCode.getText().toString();
        String description = edtVoucherDescription.getText().toString();
        String discountStr = edtVoucherDiscount.getText().toString();


        if (TextUtils.isEmpty(code)) {
            edtVoucherCode.setError("Voucher code is empty!");
            edtVoucherCode.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(description)) {
            edtVoucherDescription.setError("Description is empty!");
            edtVoucherDescription.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(discountStr)) {
            edtVoucherDiscount.setError("Discount value is empty!");
            edtVoucherDiscount.setFocusable(true);
            return;
        }

        voucher.setCode(code);
        voucher.setDiscount(Integer.parseInt(discountStr));
        voucher.setDescription(description);

        HashMap map = new HashMap();
        map.put("code", code);
        map.put("discount", Integer.parseInt(discountStr));
        map.put("description", description);


        db.collection("Promotions").document(voucher.getCode()).update(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isComplete()) {
                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();

                }
            }
        });



    }
}