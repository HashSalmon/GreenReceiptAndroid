package net.greenreceipt.greenreceipt;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;


public class TrendingReportFragment extends Fragment {
    private BarChart barChart;
    BroadcastReceiver receiver;
    EditText endDate;
    EditText startDate;
    String today;
    String startOfMonth;
    ArrayList<Integer> colors;
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
        View view = inflater.inflate(R.layout.fragment_category_report, container, false);
        endDate = (EditText) view.findViewById(R.id.endDate);

        startDate = (EditText) view.findViewById(R.id.startDate);

            view.setBackgroundColor(Color.WHITE);
            barChart = (BarChart) view.findViewById(R.id.barChart);

        return view;
        // add a lot of colors



    }

    @Override
    public void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        int m = Calendar.MONTH;
        m++;
        String month = ""+ m;
        String year = calendar.get(Calendar.YEAR)+"";
        startOfMonth = year+"-"+month+"-01";

        today = year+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        endDate.setText(today);
        startDate.setText(startOfMonth);
        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction() == Model.ACTION_TRENDING_SUCCESS)
                {
                    ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                    ArrayList<String> xVals = new ArrayList<String>();
                    int count = 0;
                    for(TrendingReportItem item : Model.trendingReport.TrendingReportItems)
                    {
                        yVals1.add(new BarEntry((float)item.Total,count));
                        xVals.add(item.Month);
                        count++;
                    }
                    BarDataSet set1 = new BarDataSet(yVals1, "Month");


                    set1.setColors(colors);
                    BarData data = new BarData(xVals,set1);
                    barChart.setData(data);
                    barChart.invalidate();
                }
                else
                {

                }
            }
        };
        IntentFilter filter = new IntentFilter(Model.ACTION_TRENDING_SUCCESS);
        IntentFilter fail = new IntentFilter(Model.ACTION_TRENDING_FAIL);
        getActivity().registerReceiver(receiver, filter);
        getActivity().registerReceiver(receiver,fail);
        Model.getInstance().GetTrendingReport(startOfMonth,today,getActivity());
        colors = new ArrayList<Integer>();

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
    }
}
