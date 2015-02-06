package net.greenreceipt.greenreceipt;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class CategoryReportFragment extends Fragment {
    private PieChart pieChar;

    public CategoryReportFragment() {
        // Required empty public constructor

    }
    public static Fragment newInstance() {
        return new CategoryReportFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_report, container, false);
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        yVals1.add(new Entry((float)109.58,0));
        yVals1.add(new Entry((float)55.20,1));
        yVals1.add(new Entry((float)127.50,2));
        yVals1.add(new Entry((float)36.99,3));

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Clothing");
        xVals.add("Dining");
        xVals.add("Grocery");
        xVals.add("Entertainment");

        PieDataSet set1 = new PieDataSet(yVals1, "Categories");
        set1.setSliceSpace(3f);

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

        PieData data = new PieData(xVals, set1);

        pieChar = (PieChart) view.findViewById(R.id.pieChart);
        pieChar.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i) {
                if (pieChar.isUsePercentValuesEnabled())
                    pieChar.setUsePercentValues(false);
                else
                    pieChar.setUsePercentValues(true);
                pieChar.invalidate();
            }

            @Override
            public void onNothingSelected() {
                pieChar.setUsePercentValues(true);
            }
        });
        pieChar.setData(data);
        pieChar.setDrawHoleEnabled(true);
        pieChar.setHoleRadius(30);
        pieChar.setUsePercentValues(true);
        pieChar.invalidate();
        pieChar.setRotationEnabled(false);
        view.invalidate();



        return view;
    }
}
