package com.example.e2tech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e2tech.Adapters.AdminOrderVPAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminOrderFragment extends Fragment {

    AdminOrderVPAdapter viewPagerAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String[] titles = new String[]{"Chờ xác nhận", "Đang xử lý", "Đang giao", "Đã giao"};

    public AdminOrderFragment() {
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
    public static AdminOrderFragment newInstance(String param1, String param2) {
        AdminOrderFragment fragment = new AdminOrderFragment();
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
        View root = inflater.inflate(R.layout.fragment_admin_order, container, false);

        viewPager2 = root.findViewById(R.id.admin_order_view_pager);
        tabLayout = root.findViewById(R.id.admin_order_tab_layout);
        viewPagerAdapter = new AdminOrderVPAdapter(getActivity());

        viewPager2.setAdapter(viewPagerAdapter);


        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReceiver,new IntentFilter("order update"));

        new TabLayoutMediator(tabLayout, viewPager2,(tab, position) -> tab.setText(titles[position])).attach();

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_pending_actions_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_processing);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_local_shipping_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_check_circle_outline_24);


        return root;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewPagerAdapter.notifyDataSetChanged();
            Log.v("refresh", "ressesfefs");
        }
    };

}