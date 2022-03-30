package com.example.e2tech;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e2tech.Adapters.AdminPopularAdapter;
import com.example.e2tech.Adapters.PopularAdapter;
import com.example.e2tech.Models.ProductModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    LineChart saleChart;
    List<Entry> entries = new ArrayList<Entry>();
    ArrayList<Entry> entries2 = new ArrayList<>();
    LineData lineData;

    RecyclerView popularRecyclerView;
    AdminPopularAdapter popularAdapter;
    ArrayList<ProductModel> productList;

    TextView tvTotalSale, tvTotalOrder, tvPendingOrder, tvTodayOrder;
    long totalSales,totalOrders, pendingOrders, todayOrders;
    Calendar cal;

    public static int[] orderByMonths = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static long[] totalSalesByMonth = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    public AdminHomeFragment() {
        // Required empty public constructor
    }


    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        cal = Calendar.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);


        tvTotalSale = root.findViewById(R.id.tv_admin_home_total_sale);
        tvTotalOrder = root.findViewById(R.id.tv_admin_home_total_order);
        tvPendingOrder = root.findViewById(R.id.tv_admin_home_pending_order);
        tvTodayOrder = root.findViewById(R.id.tv_admin_home_today_order);
        saleChart = root.findViewById(R.id.admin_chart_total_sale);


        totalOrders = 0;
        totalSales = 0;
        todayOrders = 0;
        pendingOrders = 0;
        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        totalOrders = task.getResult().getDocuments().size();
                        //Log.v("total", String.valueOf(totalOrders));
                        tvTotalOrder.setText(String.valueOf(totalOrders));

                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            String status = documentSnapshot.get("status").toString();
                            long tempTotalMoney = Long.parseLong(documentSnapshot.get("total").toString());
                            if (status.equals("done")) {
                                totalSales += tempTotalMoney;
                            } else {
                                pendingOrders++;
                            }
                            String createAt = documentSnapshot.getString("createAt");
                            long currentDateTime = System.currentTimeMillis();


                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                            Date orderDate = new Date(Long.parseLong(createAt));
                            String strDate = sdf.format(orderDate);

//                            Log.v("Date", strDate);
//                            Log.v("ngay", String.valueOf(orderDate.getDate()));
//                            Log.v("thang", String.valueOf(orderDate.getMonth() + 1));
//                            Log.v("nam", String.valueOf(orderDate.getYear() + 1900));
//
//
//                            Log.v("ngay calendar", String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
//                            Log.v("thang calendar", String.valueOf(cal.get(Calendar.MONTH)+1));
//                            Log.v("nam calendar", String.valueOf(cal.get(Calendar.YEAR)));

                            if (orderDate.getYear()+ 1900 == cal.get(Calendar.YEAR) && orderDate.getMonth() + 1 == cal.get(Calendar.MONTH)+1 && orderDate.getDate() == cal.get(Calendar.DAY_OF_MONTH)) {
                                todayOrders++;
                            }
                            if (orderDate.getYear()+ 1900 == cal.get(Calendar.YEAR) ) {
                                Log.v("FLAG", "inside 1900");
                                int month = orderDate.getMonth() + 1;
                                orderByMonths[month]++;
//                                Log.v("order inside " + String.valueOf(month), String.valueOf(orderByMonths[month]));
                                totalSalesByMonth[month] += tempTotalMoney;
                            }
                        }
                        tvTotalSale.setText(String.valueOf(totalSales));
                        tvPendingOrder.setText(String.valueOf(pendingOrders));
                        tvTodayOrder.setText(String.valueOf(todayOrders));


                        for (int i = 0; i<orderByMonths.length; i++) {
                            Log.v("thang " + String.valueOf(i), String.valueOf(orderByMonths[i]));
                        }
                        for (int i = 1; i<=12; i++) {
                            entries2.add(new Entry(i,orderByMonths[i]));
                        }
                        saleChart.invalidate();
                    }
                });






        productList = new ArrayList<>();
        popularRecyclerView = root.findViewById(R.id.admin_home_top_product_recycler_view);
        popularRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        popularAdapter = new AdminPopularAdapter(getActivity(), productList);
        popularRecyclerView.setAdapter(popularAdapter);

        db.collection("Products").limit(10).orderBy("numberSold", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                String id = documentSnapshot.getId();
                                productModel.setId(id);
                                productList.add(productModel);
                                popularAdapter.notifyDataSetChanged();
//                                Log.v("popu", productModel.getName());
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.e("FIREBASE", "ERROR" + task.getException());
                        }
                    }
                });





//        entries.add(new Entry(3,14));
//        entries.add(new Entry(5,10));
//        entries.add(new Entry(7,31));
//        entries.add(new Entry(10,20));

        for (int i = 1; i<=12; i++) {
            entries.add(new Entry(i,i));
        }


//        entries2.add(new Entry(1,2));
//        entries2.add(new Entry(2,2));
//        entries2.add(new Entry(3,6));
//        entries2.add(new Entry(4,9));
//        entries2.add(new Entry(5,2));
//        entries2.add(new Entry(6,10));
//        entries2.add(new Entry(7,2));
//        entries2.add(new Entry(8,2));
//        entries2.add(new Entry(9,2));
//        entries2.add(new Entry(10,6));
//        entries2.add(new Entry(11,9));
//        entries2.add(new Entry(12,5));


        LineDataSet lineDataSet1 = new LineDataSet(entries,"sales");
        LineDataSet lineDataSet2 = new LineDataSet(entries2,"orders");
//        lineDataSet2.setColor(R.color.card_3);
        lineDataSet2.setDrawFilled(true);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        lineData = new LineData(dataSets);
        saleChart.setData(lineData);
//        saleChart.setVisibleXRangeMaximum(12);


        String[] months = {"Month:", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        XAxis xAxis = saleChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormater(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        saleChart.setDrawGridBackground(true);
        saleChart.animateY(2000);
        saleChart.invalidate();
        return root;
    }

    public class MyXAxisValueFormater extends IndexAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormater(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return mValues[(int) value];
        }
    }
}