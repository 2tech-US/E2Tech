package com.example.e2tech;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.e2tech.Activities.LoginActivity;
import com.example.e2tech.Adapters.BannerSliderAdapter;
import com.example.e2tech.Models.BannerModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Button btnLogout;
    TextView tvUsername;

    SliderView bannerSliderView;
    List<BannerModel> bannerList;
    BannerSliderAdapter bannerSliderAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();

        tvUsername = root.findViewById(R.id.tv_username);
        tvUsername.setText(email);


        btnLogout = root.findViewById(R.id.btn_logout);
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

        bannerList.add(new BannerModel(R.drawable.home_games_slide_2));
        bannerList.add(new BannerModel(R.drawable.home_tec_slide_4));
        bannerList.add(new BannerModel(R.drawable.home_tecnologia_slide_6));

        bannerSliderAdapter = new BannerSliderAdapter(getActivity(), bannerList);
        bannerSliderView.setSliderAdapter(bannerSliderAdapter);

        return root;
    }
}