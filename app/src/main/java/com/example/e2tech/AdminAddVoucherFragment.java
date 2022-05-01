package com.example.e2tech;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.e2tech.Models.VoucherModel;
import com.example.e2tech.databinding.FragmentAdminAddVoucherBinding;
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

public class AdminAddVoucherFragment extends Fragment {

    FragmentAdminAddVoucherBinding binding;

    FirebaseStorage storage;
    FirebaseFirestore db;
    Uri imgUri;
    String imgUrl = "";
    VoucherModel voucher;

    public AdminAddVoucherFragment() {
        // Required empty public constructor
    }


    public static AdminAddVoucherFragment newInstance(String param1, String param2) {
        AdminAddVoucherFragment fragment = new AdminAddVoucherFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminAddVoucherBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.imgAdminAddVoucher.setImageURI(result);
                imgUri = result;
                if (imgUri != null) {
                    uploadImage();
                }
            }
        });


        binding.btnAdminAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = binding.edtAdminAddVoucherCode.getText().toString();
                String description = binding.edtAdminAddVoucherDescription.getText().toString();
                String discountStr = binding.edtAdminAddVoucherDiscount.getText().toString();


                if (TextUtils.isEmpty(code)) {
                    binding.edtAdminAddVoucherCode.setError("Voucher code is empty!");
                    binding.edtAdminAddVoucherCode.setFocusable(true);
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    binding.edtAdminAddVoucherDescription.setError("Description is empty!");
                    binding.edtAdminAddVoucherDescription.setFocusable(true);
                    return;
                }

                if (TextUtils.isEmpty(discountStr)) {
                    binding.edtAdminAddVoucherDiscount.setError("Giá trị giảm giá đang trống");
                    binding.edtAdminAddVoucherDiscount.setFocusable(true);
                    return;
                }


                if (imgUrl.equals("")) {
                    Toast.makeText(getActivity(), "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
                } else {
                    binding.adminAddVoucherProgressbar.setVisibility(View.VISIBLE);
                    voucher = new VoucherModel();

                    voucher.setCode(code);
                    voucher.setDiscount(Integer.parseInt(discountStr));
                    voucher.setDescription(description);
                    voucher.setImg_url(imgUrl);

                    Map<String, Object> map = new HashMap();
                    map.put("code", code);
                    map.put("discount", Integer.parseInt(discountStr));
                    map.put("description", description);
                    map.put("img_url", imgUrl);

                    db.collection("Promotions")
                            .add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        binding.adminAddVoucherProgressbar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        binding.adminAddVoucherProgressbar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    Navigation.findNavController(view).navigate(R.id.adminVoucherListFragment);
                }
            }
        });

        binding.btnAdminCancelAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.adminVoucherListFragment);
            }
        });

        binding.btnAdminSelectVoucherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        return binding.getRoot();
    }

    private void uploadImage() {
        StorageReference reference = storage.getReference().child("Vouchers/" + UUID.randomUUID().toString());

        reference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadUrl = uri;
                            imgUrl = downloadUrl.toString();
                        }
                    });
                }
            }
        });

    }
}
