package com.example.e2tech;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e2tech.Activities.LoginActivity;
import com.example.e2tech.Adapters.BannerSliderAdapter;
import com.example.e2tech.Models.BannerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAboutFragment extends Fragment {

    Button btnLogout;
    FirebaseAuth mAuth;
    SliderView bannerSliderView;
    List<BannerModel> bannerList;
    BannerSliderAdapter bannerSliderAdapter;
    FirebaseFirestore db;

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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        bannerList = new ArrayList<>();
        bannerSliderView = root.findViewById(R.id.imageBannerSlider);
        bannerSliderAdapter = new BannerSliderAdapter(getActivity(), bannerList);
        bannerSliderView.setSliderAdapter(bannerSliderAdapter);

        db.collection("Banners")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                BannerModel bannerModel = documentSnapshot.toObject(BannerModel.class);
                                bannerList.add(bannerModel);
                                bannerSliderAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return root;
    }
}