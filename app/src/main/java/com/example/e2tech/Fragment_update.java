package com.example.e2tech;

import android.graphics.Bitmap;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.e2tech.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;

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

    private Uri filepath;
    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbreference = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
//        storageReference = FirebaseStorage.getInstance().getReference();
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
                    edtEmail.setHint(email);
                    edtAddress.setHint(address);
                    edtAge.setHint(age);
                    edtPhone.setHint(phone);


                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String addressStr = edtAddress.getText().toString();
                            String ageStr = edtAge.getText().toString();
                            String phoneStr = edtPhone.getText().toString();
                            String passwordStr = edtPassword.getText().toString();
                            String emailStr = edtEmail.getText().toString().trim();

                            if (TextUtils.isEmpty(ageStr)) {
                                edtAge.setHint("Enter your age...");
                                edtAge.requestFocus();
                                return;
                            }
                            if (TextUtils.isEmpty(phoneStr)) {
                                edtPhone.setHint("Enter your phone number...");
                                edtPhone.requestFocus();
                                return;
                            }
                            if (TextUtils.isEmpty(addressStr)) {
                                edtAddress.setHint("Enter your address...");
                                edtAddress.requestFocus();
                                return;
                            }

                            if (TextUtils.isEmpty(passwordStr) && (!passwordStr.equalsIgnoreCase(pass))) {
                                edtPassword.setError("Please enter the right password to confirm update!");
                                edtPassword.requestFocus();
                                return;
                            } else if (Integer.parseInt(ageStr) < 6 || Integer.parseInt(ageStr) > 105) {
                                edtAge.setError("Please enter a valid age!");
                                edtAge.requestFocus();
                                return;
                            }
                            else if(TextUtils.isEmpty(emailStr)) {
                                edtEmail.setError("Please enter your email!");
                                edtEmail.requestFocus();
                                return;
                            }
                            else {
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

                                dbreference.child(userID).updateChildren(hashMap).
                                        addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                                navController.navigate(R.id.action_fragment_update_to_meFragment);
                                                Toast.makeText(getActivity(), "Update successful!", Toast.LENGTH_SHORT).show();
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
