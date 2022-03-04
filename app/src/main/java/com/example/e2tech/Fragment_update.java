package com.example.e2tech;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.e2tech.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment_update extends Fragment {
    EditText edtName, edtEmail, edtAge, edtAddress, edtPhone;
    Button btnSave;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        userID = user.getUid();

        edtName = (EditText) view.findViewById(R.id.update_name);
        edtEmail = (EditText) view.findViewById(R.id.update_email);
        edtAge = (EditText) view.findViewById(R.id.update_age);
        edtAddress = (EditText) view.findViewById(R.id.update_address);
        edtPhone = (EditText) view.findViewById(R.id.update_phone);
        btnSave = (Button) view.findViewById(R.id.btnSaveUpdate);

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userProfile = snapshot.getValue(UserModel.class);

                if(userProfile != null) {
                    String name = userProfile.getUsername();
                    String email = user.getEmail();
                    String address = userProfile.getAddress();
                    String age = userProfile.getAge();
                    String phone = userProfile.getPhone();
                    String gender = userProfile.getGender();

                    edtName.setText(name);
                    edtEmail.setText(email);
                    edtAddress.setText(address);
                    edtAge.setText(age);
                    edtPhone.setText(phone);

                    String addressStr = edtAddress.getText().toString();
                    String ageStr = edtAge.getText().toString();
                    String phoneStr = edtPhone.getText().toString();

                    if(TextUtils.isEmpty(ageStr)) {
                        edtAge.setHint("Enter your age...");
                        edtAge.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(phoneStr)) {
                        edtPhone.setHint("Enter your phone number...");
                        edtPhone.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(addressStr)) {
                        edtAddress.setHint("Enter your address...");
                        edtAddress.requestFocus();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        return view;
    }
}
