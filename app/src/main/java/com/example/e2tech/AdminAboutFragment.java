package com.example.e2tech;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.e2tech.Activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAboutFragment extends Fragment {

    Button btnLogout;
    FirebaseAuth auth;
    public AdminAboutFragment() {

    }


    public static AdminAboutFragment newInstance(String param1, String param2) {
        AdminAboutFragment fragment = new AdminAboutFragment();
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
        View root = inflater.inflate(R.layout.fragment_admin_about, container, false);
        auth = FirebaseAuth.getInstance();

        btnLogout = root.findViewById(R.id.btn_admin_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
        return root;
    }
}