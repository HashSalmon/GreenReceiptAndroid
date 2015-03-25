package net.greenreceipt.greenreceipt;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import Util.Helper;


public class CategoryReportFragment extends Fragment {
    private BarChart barChart;
    TextView endDate;
    TextView startDate;
    String today;
    String startOfMonth;
    ArrayList<Integer> colors;
    int mYear;
    int mMonth;
    int mDay;
    int eYear;
    int eMonth;
    int eDay;
    ImageButton update;
    public CategoryReportFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        eYear = c.get(Calendar.YEAR);
        eMonth = c.get(Calendar.MONTH);
        eDay = c.get(Calendar.DAY_OF_MONTH);

        endDate = (TextView) view.findViewById(R.id.endDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                            {
                                endDate.setText((monthOfYear + 1) + "/"+ dayOfMonth + "/" + year);
                                eYear = year;
                                eMonth = monthOfYear;
                                eDay = dayOfMonth;
                            }
                        }, eYear, eMonth, eDay);
                dpd.setIcon(R.drawable.ic_action_time);
                dpd.setTitle("Set Start Date");

                dpd.show();
            }
        });
        startDate = (TextView) view.findViewById(R.id.startDate);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                            {
                                startDate.setText((monthOfYear + 1) + "/"+ dayOfMonth + "/" + year);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                dpd.setIcon(R.drawable.ic_action_time);
                dpd.setTitle("Set Start Date");

                dpd.show();
            }
        });
        update = (ImageButton) view.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getInstance().GetCategoryReport(startDate.getText().toString(), endDate.getText().toString());
            }
        });

        view.setBackgroundColor(Color.WHITE);
        barChart = (BarChart) view.findViewById(R.id.barChart);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Calendar calendar = Calendar.getInstance();
        int m = Calendar.MONTH;
        m++;
        String month = ""+ m;
        String year = calendar.get(Calendar.YEAR)+"";
        startOfMonth = year+"/"+month+"/01";

        today = year+"/"+month+"/"+calendar.get(Calendar.DAY_OF_MONTH);
        endDate.setText(today);
        startDate.setText(startOfMonth);
        Model.getInstance().setGetCategoryReportListener(new Model.GetCateogryReportListener() {
            @Override
            public void onGetCateogryReportSuccess(CategoryReport report)
            {
                ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
                ArrayList<String> xVals = new ArrayList<String>();
                int count = 0;
                for(CategoryReportItem item : report.CategoryReportItems)
                {
                    yVals1.add(new BarEntry((float)item.Total,count));
                    xVals.add(item.CategoryName);
                    count++;
                }
                BarDataSet set1 = new BarDataSet(yVals1, "Categories");


                set1.setColors(colors);
                BarData data = new BarData(xVals,set1);
                barChart.setData(data);
                barChart.invalidate();
            }

            @Override
            public void onGetCategoryReportFailed(String error) {
                Helper.AlertBox(getActivity(), "Error", error);
            }
        });
        Model.getInstance().GetCategoryReport(startOfMonth,today);
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
    //    private PieChart pieChar;
//
//    public CategoryReportFragment() {
//        // Required empty public constructor
//
//    }
//    public static Fragment newInstance() {
//        return new CategoryReportFragment();
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_report, container, false);
//        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
//        yVals1.add(new Entry((float)109.58,0));
//        yVals1.add(new Entry((float)55.20,1));
//        yVals1.add(new Entry((float)127.50,2));
//        yVals1.add(new Entry((float)36.99,3));
//
//        ArrayList<String> xVals = new ArrayList<String>();
//
//        xVals.add("Clothing");
//        xVals.add("Dining");
//        xVals.add("Grocery");
//        xVals.add("Entertainment");
//
//        PieDataSet set1 = new PieDataSet(yVals1, "Categories");
//        set1.setSliceSpace(3f);
//
//        // add a lot of colors
//
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());
//
//        set1.setColors(colors);
//
//        PieData data = new PieData(xVals, set1);
//
//        pieChar = (PieChart) view.findViewById(R.id.pieChart);
//        pieChar.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry entry, int i) {
//                if (pieChar.isUsePercentValuesEnabled())
//                    pieChar.setUsePercentValues(false);
//                else
//                    pieChar.setUsePercentValues(true);
//                pieChar.invalidate();
//            }
//
//            @Override
//            public void onNothingSelected() {
//                pieChar.setUsePercentValues(true);
//            }
//        });
//        pieChar.setData(data);
//        pieChar.setDrawHoleEnabled(true);
//        pieChar.setHoleRadius(30);
//        pieChar.setUsePercentValues(true);
//        pieChar.invalidate();
//        pieChar.setRotationEnabled(false);
//        view.invalidate();
//
//
//
//        return view;
//    }
}
