package com.example.e2tech;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {

    LineChart saleChart;
    List<Entry> entries = new ArrayList<Entry>();
    LineData lineData;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);

        saleChart = root.findViewById(R.id.admin_chart_total_sale);
//        entries.add(new Entry(3,14));
//        entries.add(new Entry(5,10));
//        entries.add(new Entry(7,31));
//        entries.add(new Entry(10,20));

        for (int i = 1; i<=12; i++) {
        entries.add(new Entry(i,i*3-i));
        }

        ArrayList<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(1,22));
        entries2.add(new Entry(2,2));
        entries2.add(new Entry(3,6));
        entries2.add(new Entry(4,9));
        entries2.add(new Entry(5,2));
        entries2.add(new Entry(6,10));
        entries2.add(new Entry(7,2));
        entries2.add(new Entry(8,2));
        entries2.add(new Entry(9,2));
        entries2.add(new Entry(10,6));
        entries2.add(new Entry(11,9));
        entries2.add(new Entry(12,5));


        LineDataSet lineDataSet1 = new LineDataSet(entries,"man");
        LineDataSet lineDataSet2 = new LineDataSet(entries2,"women");
        lineDataSet2.setColor(R.color.card_3);
        lineDataSet2.setDrawFilled(true);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        lineData = new LineData(dataSets);
        saleChart.setData(lineData);
//        saleChart.setVisibleXRangeMaximum(12);


        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "bonus"};

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