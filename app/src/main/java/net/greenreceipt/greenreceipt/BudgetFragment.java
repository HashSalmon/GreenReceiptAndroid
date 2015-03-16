package net.greenreceipt.greenreceipt;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    Spinner options;
    String[] optionItem = {"Options","Edit Budget","Delete Budget"};
    StringPicker categoryPicker;
    EditText limit;
    List<String> categoryList = new ArrayList<>();
    public BudgetFragment() {
        // Required empty public constructor


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_budget, container, false);
        this.container = (LinearLayout) view.findViewById(R.id.budgetContainer);
        message = (TextView) view.findViewById(R.id.message);
        createBudget = (Button) view.findViewById(R.id.createBudget);
        options = (Spinner) view.findViewById(R.id.options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,optionItem);
        options.setAdapter(adapter);
        options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        options.setSelection(0);
                        Intent editIntent = new Intent(getActivity(),EditBudgetActivity.class);

                        startActivity(editIntent);
                        break;
                    case 2:
                        options.setSelection(0);
                        //delete


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        Model.getInstance().setGetCurrentBudgetListener(new Model.GetCurrentBudgetListener() {
            @Override
            public void onGetBudgetSuccess() {
                message.setVisibility(View.GONE);
                createBudget.setVisibility(View.GONE);

                if(Model.currentBudget.BudgetItems.size()>0)
                {
                    container.removeAllViews();
                    options.setVisibility(View.VISIBLE);
                    for(BudgetItem item : Model.currentBudget.BudgetItems)
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
                        bar.setProgress((float) item.AmountUsed / (float) item.AmountAllowed);
                        container.addView(budgetItem);
                    }
                }
            }

            @Override
            public void onGetBudgetFailed(String error) {
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
