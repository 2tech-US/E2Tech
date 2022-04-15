package com.example.e2tech;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_update extends Fragment {
    EditText edtName, edtEmail, edtAge, edtAddress, edtPhone, edtPassword;
    TextView tvUpdateAvatar;
    CircleImageView avatar;
    Button btnSave;
    RadioGroup radioGroupGender;
    RadioButton selectedGender;
    private FirebaseUser user;
    private DatabaseReference dbreference;
    private StorageReference storageReference;
    private String userID;

    FirebaseStorage storage;
    FirebaseFirestore db;
    Uri imgUri;
    String imgUrl = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbreference = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        userID = user.getUid();

        tvUpdateAvatar = (TextView) view.findViewById(R.id.update_avatar);
        avatar = (CircleImageView) view.findViewById(R.id.profile_image);
        edtName = (EditText) view.findViewById(R.id.update_name);
        edtEmail = (EditText) view.findViewById(R.id.update_email);
        edtAge = (EditText) view.findViewById(R.id.update_age);
        edtAddress = (EditText) view.findViewById(R.id.update_address);
        edtPhone = (EditText) view.findViewById(R.id.update_phone);
        edtPassword = (EditText) view.findViewById(R.id.password_confirmUpdate);
        btnSave = (Button) view.findViewById(R.id.btnSaveUpdate);
        radioGroupGender = (RadioGroup) view.findViewById(R.id.radioGroupGender);
        avatar = (CircleImageView) view.findViewById(R.id.profile_image);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                avatar.setImageURI(result);
                imgUri = result;
                if (imgUri != null) {
                    uploadImage();
                }
            }
        });

        tvUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        dbreference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userProfile = snapshot.getValue(UserModel.class);

                if (userProfile != null) {
                    String name = userProfile.getUsername();
                    String email = user.getEmail();
                    String address = userProfile.getAddress();
                    String age = userProfile.getAge();
                    String phone = userProfile.getPhone();
                    String pass = userProfile.getPassword();

                    edtName.setHint(name);
                    edtEmail.setText(email);
                    edtEmail.setEnabled(false);
                    edtAddress.setHint(address);
                    edtAge.setHint(age);
                    edtPhone.setHint(phone);

                    if(TextUtils.isEmpty(userProfile.getImg_url())) {
                        Glide.with(getActivity()).load(R.drawable.profile_pic).into(avatar);
                    } else {
                        Glide.with(getActivity()).load(userProfile.getImg_url()).into(avatar);
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        for (UserInfo profile : user.getProviderData()) {
                            // Id of the provider (ex: google.com)
                            String providerId = profile.getProviderId();

                            if (providerId.equals("facebook.com") | providerId.equals("google.com")) {
//                                Log.d("Signed_in_method", "FB or GG method!");
                                edtPassword.setEnabled(false);
                                edtPassword.setBackgroundColor(Color.rgb(181, 180, 179));
                            } else if (providerId.equals("password")) {
//                                Log.d("Signed_in_method", "Email password method!");
                                edtPassword.setEnabled(true);
                            }

                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String addressStr = edtAddress.getText().toString();
                                    String ageStr = edtAge.getText().toString();
                                    String phoneStr = edtPhone.getText().toString();
                                    String passwordStr = edtPassword.getText().toString();
                                    String emailStr = edtEmail.getText().toString().trim();

                                    if (TextUtils.isEmpty(ageStr)) {
                                        edtAge.setHint("Nhập tuổi...");
                                        edtAge.requestFocus();
                                        return;
                                    }
                                    if (TextUtils.isEmpty(phoneStr)) {
                                        edtPhone.setHint("Nhập sđt...");
                                        edtPhone.requestFocus();
                                        return;
                                    }

                                    if (imgUrl.equals("")) {
                                        Toast.makeText(getActivity(), "Vui lòng chọn ảnh trước", Toast.LENGTH_LONG).show();
                                    }

                                    if (TextUtils.isEmpty(addressStr)) {
                                        edtAddress.setHint("Nhập địa chỉ...");
                                        edtAddress.requestFocus();
                                        return;
                                    }

                                    if ((TextUtils.isEmpty(passwordStr) || (!passwordStr.equalsIgnoreCase(pass))) && providerId.equals("password")) {
                                        edtPassword.setError("Vui lòng nhập password để xác nhận cập nhật");
                                        edtPassword.requestFocus();
                                        return;
                                    } else if (Integer.parseInt(ageStr) < 6 || Integer.parseInt(ageStr) > 105) {
                                        edtAge.setError("Vui lòng nhập tuổi phù hợp");
                                        edtAge.requestFocus();
                                        return;
                                    } else if (TextUtils.isEmpty(edtName.getText().toString())) {
                                        edtName.setError("Vui lòng nhập tên của bạn");
                                        edtName.requestFocus();
                                        return;
                                    } else if (TextUtils.isEmpty(emailStr)) {
                                        edtEmail.setError("Vui lòng nhập email");
                                        edtEmail.requestFocus();
                                        return;
                                    } else {
                                        if (!emailStr.equalsIgnoreCase(email)) {
                                            AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
                                            user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d("Re-authenticated", "User re-authenticated.");
                                                            user.updateEmail(emailStr)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Log.d("Update email", "User email address updated.");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                        HashMap hashMap = new HashMap();

                                        hashMap.put("address", edtAddress.getText().toString());
                                        hashMap.put("age", edtAge.getText().toString());
                                        int selectedID = radioGroupGender.getCheckedRadioButtonId();
                                        selectedGender = (RadioButton) radioGroupGender.findViewById(selectedID);
                                        hashMap.put("gender", selectedGender.getText().toString());
                                        hashMap.put("phone", edtPhone.getText().toString());
                                        hashMap.put("username", edtName.getText().toString());
                                        hashMap.put("email", edtEmail.getText().toString());
                                        hashMap.put("img_url", imgUrl);

                                        db.collection("Avatars").add(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "Thêm avatar thành công", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getActivity(), "Bị lỗi", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                        dbreference.child(userID).updateChildren(hashMap).
                                                addOnSuccessListener(new OnSuccessListener() {
                                                    @Override
                                                    public void onSuccess(Object o) {
                                                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                                        navController.navigate(R.id.action_fragment_update_to_meFragment);
                                                        Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Có lỗi đâu đó", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void uploadImage() {
        StorageReference reference = storage.getReference().child("Avatars/" + UUID.randomUUID().toString());

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
