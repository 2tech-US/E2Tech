package com.example.e2tech;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.e2tech.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment_infor extends Fragment {
    Button btnUpdate;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_infor, container, false);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdateInfor);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        userID = user.getUid();

        final TextView txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        final TextView txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        final TextView tvName = (TextView) view.findViewById(R.id.tvName);
        final TextView tvAge = (TextView) view.findViewById(R.id.tvAge);
        final TextView tvGender = (TextView) view.findViewById(R.id.tvGender);
        final TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        final TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userProfile = snapshot.getValue(UserModel.class);

                if(userProfile != null) {
                    String username = userProfile.getUsername();
                    String email = user.getEmail();
                    String address = userProfile.getAddress();
                    String age = userProfile.getAge();
                    String phone = userProfile.getPhone();
                    String gender = userProfile.getGender();

                    txtUsername.setText(username);
                    txtEmail.setText(email);
                    tvName.setText(username);
                    tvAddress.setText(address);
                    tvAge.setText(age);
                    tvPhone.setText(phone);
                    tvGender.setText(gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_fragment_infor_to_fragment_update);
            }
        });
        return view;
    }
}
