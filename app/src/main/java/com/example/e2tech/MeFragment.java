package com.example.e2tech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e2tech.Activities.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.example.e2tech.Models.UserModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MeFragment extends Fragment {
    private Button btnInfor, btnCart, btnHistory, btnFavorite, btnAddProduct, btnOrderManage;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    Button btnLogout, btnChangePass, btnMyVouchers;
    private CircleImageView avatar;
    private Activity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        btnInfor = (Button) view.findViewById(R.id.btnInfor);
        btnChangePass = (Button) view.findViewById(R.id.btnChangePass);
        btnMyVouchers = (Button) view.findViewById(R.id.btnMyVouchers);
        avatar = (CircleImageView) view.findViewById(R.id.profile_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                if (providerId.equals("facebook.com") | providerId.equals("google.com")) {
                    btnChangePass.setVisibility(View.GONE);
                } else if (providerId.equals("password")) {
                    btnChangePass.setVisibility(View.VISIBLE);
                }
            }
        }
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });

        btnInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_meFragment_to_fragment_infor);
            }
        });

        btnMyVouchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_meFragment_to_myVouchersFragment);
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_meFragment_to_changePassFragment);
            }
        });

        btnHistory = (Button) view.findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_meFragment_to_orderHistory);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://e2tech-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        userID = user.getUid();
        Log.d("UserID", userID);

        final TextView txtUsername = (TextView) view.findViewById(R.id.txtMeUsername);

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userProfile = snapshot.getValue(UserModel.class);

                if (userProfile != null) {
                    String username = userProfile.getUsername();
                    txtUsername.setText(username);
//                    if (TextUtils.isEmpty(userProfile.getImg_url().toString())) {
//                        Glide.with(getActivity()).load(R.drawable.profile_pic).into(avatar);
//                    } else {
//                        Glide.with(getActivity()).load(userProfile.getImg_url()).into(avatar);
//                    }
                    Glide.with(mainActivity.getApplicationContext()).load(userProfile.getImg_url()).error(R.drawable.profile_pic).into(avatar);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "C?? l???i x???y ra", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}