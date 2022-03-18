package com.example.e2tech.Adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.e2tech.AdminDeliveringOrderFragment;
import com.example.e2tech.AdminDoneOrderFragment;
import com.example.e2tech.AdminProcessingOrderFragment;
import com.example.e2tech.AdminWaitingOrderFragment;
import com.example.e2tech.HomeFragment;

public class AdminOrderVPAdapter extends FragmentStateAdapter {

    private String[] titles = new String[]{"Waiting", "Processing", "Delivering", "Done"};

    public AdminOrderVPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminWaitingOrderFragment();
            case 1:
                return new AdminProcessingOrderFragment();
            case 2:
                return new AdminDeliveringOrderFragment();
            case 3:
                return new AdminDoneOrderFragment();
        }
        return new AdminWaitingOrderFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
