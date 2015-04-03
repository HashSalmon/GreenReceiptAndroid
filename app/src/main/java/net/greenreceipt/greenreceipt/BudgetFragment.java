package net.greenreceipt.greenreceipt;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;
import java.util.List;

import hotchemi.stringpicker.StringPicker;

//Test

public class BudgetFragment extends Fragment {

    LinearLayout container;
    TextView message;
    Button createBudget;
    StringPicker categoryPicker;
    EditText limit;
    List<String> categoryList = new ArrayList<>();
    StringPicker editName;
    ProgressDialog spinner;
    public BudgetFragment() {
        // Required empty public constructor


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_budget, container, false);
        this.container = (LinearLayout) view.findViewById(R.id.budgetContainer);
        message = (TextView) view.findViewById(R.id.message);
        createBudget = (Button) view.findViewById(R.id.createBudget);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.budget, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.edit:
                if(Model.getInstance().currentBudget!=null) {
                    Intent editIntent = new Intent(getActivity(), EditBudgetActivity.class);

                    startActivity(editIntent);
                }
                else
                {
                    Intent createIntent = new Intent(getActivity(), CreateBudgetActivity.class);

                    startActivity(createIntent);
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();


        Model.getInstance().setGetCurrentBudgetListener(new Model.GetCurrentBudgetListener() {
            @Override
            public void onGetBudgetSuccess() {
                spinner.dismiss();
                message.setVisibility(View.GONE);
                createBudget.setVisibility(View.GONE);

                if(Model.getInstance().currentBudget.BudgetItems.size()>0)
                {
                    container.removeAllViews();
                    for(BudgetItem item : Model.getInstance().currentBudget.BudgetItems)
                    {
                        View budgetItem = getActivity().getLayoutInflater().inflate(R.layout.budget_item, container, false);
                        TextView name = (TextView) budgetItem.findViewById(R.id.categoryName);
                        name.setText(item.Category.Name);
                        TextView used = (TextView) budgetItem.findViewById(R.id.amtUsed);
                        used.setText(item.AmountUsed+"");
                        TextView allowed = (TextView) budgetItem.findViewById(R.id.amtAllowed);
                        allowed.setText(item.AmountAllowed+"");
                        RoundCornerProgressBar bar = (RoundCornerProgressBar) budgetItem.findViewById(R.id.amtProgress);
                        bar.setMax(1);
                        double progress = (float) item.AmountUsed / (float) item.AmountAllowed;
                        bar.setProgress((float) item.AmountUsed / (float) item.AmountAllowed);
                        Resources resources = getResources();
                        if(progress < 0.8)
                        {
                            bar.setProgressColor(resources.getColor(R.color.custom_progress_green_progress));
                        }
                        else if(progress > 0.8 && progress < 0.9)
                        {
                            bar.setProgressColor(resources.getColor(R.color.custom_progress_orange_header));
                        }
                        else
                        {
                            bar.setProgressColor(resources.getColor(R.color.custom_progress_red_progress));
                        }
                        container.addView(budgetItem);
                    }
                }
            }

            @Override
            public void onGetBudgetFailed(String error) {
                spinner.dismiss();
//                Helper.AlertBox(getActivity(),"Error",error);
                message.setVisibility(View.VISIBLE);
                createBudget.setVisibility(View.VISIBLE);
                createBudget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent createBudgetIntent = new Intent(getActivity(),CreateBudgetActivity.class);
                        startActivity(createBudgetIntent);
                    }
                });


            }
        });
        Model.getInstance().GetCurrentBudget();
        spinner = ProgressDialog.show(getActivity(), null, "Loading...");
    }

//    private void addBudgetCategory()
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setPositiveButton("Add",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                BudgetItem item = new BudgetItem();
//                if(Model.categories !=null)
//                    item.Category = Model.categories[categoryPicker.getCurrent()];
//                item.AmountAllowed = Double.parseDouble(limit.getText().toString());
//                item.AmountUsed = 0;
//                b.BudgetItems.add(item);
//                Model.getInstance().CreateBudget(b);
//
//            }
//        });
//        builder.setTitle("Add Item");
//        View add = View.inflate(getActivity(),R.layout.add_budget_category,null);
//        builder.setView(add);
//        if(Model.categories != null)
//        {
//            for(Category c: Model.categories)
//                categoryList.add(c.Name);
//        }
//        categoryPicker = (StringPicker) add.findViewById(R.id.name);
//        categoryPicker.setValues(categoryList);
//        if(categoryList.size()==0)
//            categoryList.add("");
//        limit = (EditText) add.findViewById(R.id.amtAllowed);
//
//        AlertDialog addDialog = builder.create();
//        addDialog.show();
//    }
}
