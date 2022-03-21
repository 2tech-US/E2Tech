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
import android.widget.Toast;

import com.example.e2tech.Models.ProductModel;
import com.example.e2tech.databinding.FragmentAdminAddCategoryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AdminAddCategoryFragment extends Fragment {

    FragmentAdminAddCategoryBinding binding;
    Uri imgUri;
    FirebaseStorage storage;
    FirebaseFirestore db;
    String imgUrl = "";
    ProductModel product;
    String name;

    public AdminAddCategoryFragment() {
        // Required empty public constructor
    }


    public static AdminAddCategoryFragment newInstance(String param1, String param2) {
        AdminAddCategoryFragment fragment = new AdminAddCategoryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
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
        //return inflater.inflate(R.layout.fragment_admin_add_category, container, false);
        binding = FragmentAdminAddCategoryBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
//                binding.imgAdminProductDetail.setImageURI(result);
                binding.imgCategoryAdd.setImageURI(result);
                imgUri = result;
                if (imgUri != null) {
                    uploadImage();
                }
            }
        });

        binding.btnAdminSelectCategoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        binding.btnAdminCategoryAddNewCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCategory();
            }
        });


        return binding.getRoot();
    }

    private void addNewCategory() {
        name = binding.edtAdminCategoryAddTitle.getText().toString();

        if (TextUtils.isEmpty(name)) {
            binding.edtAdminCategoryAddTitle.setError("Title is empty");
            binding.edtAdminCategoryAddTitle.setFocusable(true);
            return;
        }

        binding.progressBarAddCategory.setVisibility(View.VISIBLE);

        Map<String, Object> map = new HashMap();
        map.put("name", name);
        map.put("img_url", imgUrl);

        db.collection("Categories").whereEqualTo("name", name).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size()>0) {

                            Toast.makeText(getActivity(), "This title is existed", Toast.LENGTH_LONG).show();
                        } else {
                            db.collection("Categories")
                                    .add(map)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                binding.progressBarAddCategory.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), "Add new product successfully", Toast.LENGTH_LONG).show();
                                            } else {
                                                binding.progressBarAddCategory.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), "Add new product fail", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });


                        }
                    }
                });

    }

    private void uploadImage() {
        StorageReference reference = storage.getReference().child("categories/" + UUID.randomUUID().toString());

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



}