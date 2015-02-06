package net.greenreceipt.greenreceipt;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class TrendingReportFragment extends Fragment {
    private BarChart barChart;
    public TrendingReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trending_report, container, false);
        view.setBackgroundColor(Color.WHITE);
        barChart = (BarChart) view.findViewById(R.id.barChart);
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yVals1.add(new BarEntry((float)1980.89,0));
        yVals1.add(new BarEntry((float)2112.35,1));
        yVals1.add(new BarEntry((float)2332.43,2));
        yVals1.add(new BarEntry((float)1890.34,3));

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("September");
        xVals.add("October");
        xVals.add("November");
        xVals.add("December");

        BarDataSet set1 = new BarDataSet(yVals1, "Months");

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        set1.setColors(colors);
        BarData data = new BarData(xVals,set1);
        barChart.setData(data);
        // Inflate the layout for this fragment
        return view;
    }



}
