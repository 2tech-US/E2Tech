package com.example.e2tech;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.databinding.FragmentAdminAddProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAddProductFragment extends Fragment {

    FragmentAdminAddProductBinding binding;

    FirebaseStorage storage;
    FirebaseFirestore db;
    Uri imgUri;
    String imgUrl = "";
    ProductModel product;

    public AdminAddProductFragment() {
        // Required empty public constructor
    }


    public static AdminAddProductFragment newInstance(String param1, String param2) {
        AdminAddProductFragment fragment = new AdminAddProductFragment();


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
        //return inflater.inflate(R.layout.fragment_admin_add_product, container, false);

        binding = FragmentAdminAddProductBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        ArrayList<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, categories);
        binding.spinnerAdminAddTypeProduct.setAdapter(adapter);

        db.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                String category = document.get("name").toString();
                                categories.add(category);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
//                binding.imgAdminProductDetail.setImageURI(result);
                binding.imgAdminAddProduct.setImageURI(result);
                imgUri = result;
                if (imgUri != null) {
                    uploadImage();
                }
            }
        });


//        binding.spinnerAdminAddTypeProduct.setOnItemSelectedListener();

        binding.btnAdminProductAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewProduct();
            }
        });

        binding.btnAdminAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });






        return binding.getRoot();

    }

    private void uploadImage() {
        StorageReference reference = storage.getReference().child("Products/" + UUID.randomUUID().toString());

        reference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadUrl = uri;
                            imgUrl = downloadUrl.toString();
                        }});
                }
            }
        });

    }

    private void selectImage() {

    }

    private void addNewProduct() {

        String name = binding.edtAdminProductAddName.getText().toString();
        String description = binding.edtAdminProductAddDescription.getText().toString();
        String priceStr = binding.edtAdminProductAddPrice.getText().toString();
        String discountStr = binding.edtAdminProductAddDiscount.getText().toString();
        String remainStr = binding.edtAdminProductAddQuantity.getText().toString();
        String type = binding.spinnerAdminAddTypeProduct.getSelectedItem().toString();
        String company = binding.edtAdminProductAddCompany.getText().toString();


        if (TextUtils.isEmpty(name)) {
            binding.edtAdminProductAddName.setError("Product name is empty");
            binding.edtAdminProductAddName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            binding.edtAdminProductAddPrice.setError("Price is empty");
            binding.edtAdminProductAddPrice.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.edtAdminProductAddDescription.setError("Description is empty");
            binding.edtAdminProductAddDescription.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(discountStr)) {
            binding.edtAdminProductAddDiscount.setError("Discount is empty");
            binding.edtAdminProductAddDiscount.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(remainStr)) {
            binding.edtAdminProductAddQuantity.setError("Quantity is empty");
            binding.edtAdminProductAddQuantity.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(company)) {
            binding.edtAdminProductAddCompany.setError("Company is empty");
            binding.edtAdminProductAddCompany.setFocusable(true);
            return;
        }

        if (imgUrl.equals("")) {
            Toast.makeText(getActivity(), "Please select image first", Toast.LENGTH_LONG).show();
        }

        binding.adminAddProductProgressbar.setVisibility(View.VISIBLE);
        product = new ProductModel();

        product.setName(name);
        product.setPrice(Integer.parseInt(priceStr));
        product.setDiscount(Double.parseDouble(discountStr));
        product.setRemain(Integer.parseInt(remainStr));
        product.setDescription(description);
        product.setCompany(company);
        product.setType(type);
        product.setImg_url(imgUrl);

        Map<String, Object> map = new HashMap();
        map.put("name", name);
        map.put("discount", Double.parseDouble(discountStr));
        map.put("price", Integer.parseInt(priceStr));
        map.put("remain", Integer.parseInt(remainStr));
        map.put("description", description);
        map.put("img_url", imgUrl);
        map.put("company", company);
        map.put("type", type);


        db.collection("Products")
                .add(map)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            binding.adminAddProductProgressbar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Add new product successfully", Toast.LENGTH_LONG).show();
                        } else {
                            binding.adminAddProductProgressbar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Add new product fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}