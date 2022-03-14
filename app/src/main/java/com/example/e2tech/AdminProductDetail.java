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
import com.example.e2tech.databinding.FragmentAdminProductDetailBinding;
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
public class AdminProductDetail extends Fragment implements View.OnClickListener {

    ProductModel product;

    ImageView imgProduct;
    EditText edtName, edtPrice, edtRemain, edtDescription, edtDiscount;

    Button btnUpdate, btnDelete, btnUpdateImage, btnSelectImage;
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseStorage storage;

    Uri imgUri;

    FragmentAdminProductDetailBinding binding;

    public AdminProductDetail() {
        // Required empty public constructor
    }


    public static AdminProductDetail newInstance(String param1, String param2) {
        AdminProductDetail fragment = new AdminProductDetail();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (ProductModel) getArguments().getSerializable("product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAdminProductDetailBinding.inflate(getLayoutInflater());



        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_product_detail, container, false);

        edtName = root.findViewById(R.id.edt_admin_product_detail_name);
        edtPrice = root.findViewById(R.id.edt_admin_product_detail_price);
        edtDiscount = root.findViewById(R.id.edt_admin_product_detail_discount);
        edtDescription = root.findViewById(R.id.edt_admin_product_detail_description);
        edtRemain = root.findViewById(R.id.edt_admin_product_detail_remain);
        imgProduct = root.findViewById(R.id.img_admin_product_detail);
        Glide.with(getActivity()).load(product.getImg_url()).into(imgProduct);

        edtName.setText(product.getName());
        edtPrice.setText(Integer.toString(product.getPrice()));
        edtDiscount.setText(Double.toString(product.getDiscount()));
        edtDescription.setText(product.getDescription());
        edtRemain.setText(Integer.toString(product.getRemain()));

        btnUpdate = root.findViewById(R.id.btn_admin_product_detail_update);
        btnDelete = root.findViewById(R.id.btn_admin_product_detail_delete);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
//                binding.imgAdminProductDetail.setImageURI(result);
                imgProduct.setImageURI(result);
                imgUri = result;
            }
        });



        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = storage.getReference();

        btnSelectImage = root.findViewById(R.id.btn_admin_select_image_product);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        btnUpdateImage = root.findViewById(R.id.btn_admin_update_image_product);
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

    private void selectImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);



    }

    private void updateImage() {
//        // Create a reference to "mountains.jpg"
//        StorageReference mountainsRef = storageReference.child("user.png");
//
//        // Create a reference to 'images/mountains.jpg'
//        StorageReference mountainImagesRef = storageReference.child("images/user.png");
//
//        // While the file names are the same, the references point to different files
//        mountainsRef.getName().equals(mountainImagesRef.getName());    // true

        StorageReference reference = storage.getReference().child("Products/" + product.getName() + UUID.randomUUID().toString());

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


                            db.collection("PopularProducts").document(product.getId()).update(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isComplete()) {
                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();

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

        String name = edtName.getText().toString();
        String description = edtDescription.getText().toString();
        String priceStr = edtPrice.getText().toString();
        String discountStr = edtDiscount.getText().toString();
        String remainStr = edtRemain.getText().toString();


        if (TextUtils.isEmpty(name)) {
            edtName.setError("Product name is empty");
            edtName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            edtPrice.setError("Price is empty");
            edtPrice.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(description)) {
            edtDescription.setError("Price is empty");
            edtDescription.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(discountStr)) {
            edtDiscount.setError("Price is empty");
            edtDiscount.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(remainStr)) {
            edtRemain.setError("Price is empty");
            edtRemain.setFocusable(true);
            return;
        }

        product.setName(name);
        product.setPrice(Integer.parseInt(priceStr));
        product.setDiscount(Double.parseDouble(discountStr));
        product.setRemain(Integer.parseInt(remainStr));
        product.setDescription(description);

        HashMap map = new HashMap();
        map.put("name", name);
        map.put("discount", Double.parseDouble(discountStr));
        map.put("price", Integer.parseInt(priceStr));
        map.put("remain", Integer.parseInt(remainStr));
        map.put("description", description);


        db.collection("PopularProducts").document(product.getId()).update(map).addOnCompleteListener(new OnCompleteListener() {
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