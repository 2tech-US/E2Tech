package com.example.e2tech.orderhistory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e2tech.Adapters.AdminOrderVPAdapter;
import com.example.e2tech.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistory extends Fragment {

    OrderVPAdapter viewPagerAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String[] titles = new String[]{"Waiting", "Processing", "Delivering", "Done"};

    public OrderHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistory newInstance(String param1, String param2) {
        OrderHistory fragment = new OrderHistory();
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
        View root = inflater.inflate(R.layout.order_history_main, container, false);

        viewPager2 = root.findViewById(R.id.admin_order_view_pager);
        tabLayout = root.findViewById(R.id.admin_order_tab_layout);
        viewPagerAdapter = new OrderVPAdapter(getActivity());

        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,(tab, position) -> tab.setText(titles[position])).attach();

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_pending_actions_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_processing);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_local_shipping_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_check_circle_outline_24);


        return root;
    }
}