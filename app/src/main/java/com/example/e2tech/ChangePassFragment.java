package com.example.e2tech;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.UserModel;
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

import java.util.HashMap;

public class ChangePassFragment extends Fragment {
    Button btnConfirm;
    EditText edtOldPass, edtNewPass, edtRetypePass;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference dbreference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        btnConfirm = view.findViewById(R.id.btnConfirmChangePass);
        edtOldPass = (EditText) view.findViewById(R.id.edtOldPass);
        edtNewPass = (EditText) view.findViewById(R.id.edtNewPass);
        edtRetypePass = (EditText) view.findViewById(R.id.edtRetypeNewPass);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbreference = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        userID = user.getUid();


        dbreference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userProfile = snapshot.getValue(UserModel.class);

                if (userProfile != null) {
                    String pass = userProfile.getPassword();

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String oldPassStr = edtOldPass.getText().toString().trim();
                            String newPassStr = edtNewPass.getText().toString().trim();
                            String retypePassStr = edtRetypePass.getText().toString().trim();

                            if (!newPassStr.equals(retypePassStr) || TextUtils.isEmpty(retypePassStr)) {
                                edtRetypePass.setError("Password retype is wrong");
                                edtRetypePass.setFocusable(true);
                                return;
                            } else if (newPassStr.length() < 6) {
                                edtNewPass.setError("Length Must be greater than 6 character");
                                edtNewPass.setFocusable(true);
                            } else if (TextUtils.isEmpty(newPassStr)) {
                                edtNewPass.setError("Please enter your new password to confirm update!");
                                edtNewPass.requestFocus();
                                return;
                            } else if (TextUtils.isEmpty(oldPassStr) || (!oldPassStr.equalsIgnoreCase(pass))) {
                                edtOldPass.setError("Wrong old password!");
                                edtOldPass.requestFocus();
                                return;
                            } else {
                                user.updatePassword(newPassStr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("ChangePassword", "Change password successfully!");
                                    }
                                });

                                HashMap hashMap = new HashMap();
                                hashMap.put("password", edtNewPass.getText().toString());
                                dbreference.child(userID).updateChildren(hashMap).
                                        addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                                navController.navigate(R.id.action_changePassFragment_to_meFragment);
                                                Toast.makeText(getActivity(), "Update password successful!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
