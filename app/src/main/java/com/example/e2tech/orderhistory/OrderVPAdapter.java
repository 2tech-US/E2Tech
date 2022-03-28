package com.example.e2tech.orderhistory;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrderVPAdapter extends FragmentStateAdapter {

    private String[] titles = new String[]{"Waiting", "Processing", "Delivering", "Done"};

    public OrderVPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OrderDeliveryList("waiting");
            case 1:
                return new OrderDeliveryList("processing");
            case 2:
                return new OrderDeliveryList("delivering");
            case 3:
                return new OrderDeliveryList("done");
        }
        return new OrderDeliveryList("waiting");
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}

