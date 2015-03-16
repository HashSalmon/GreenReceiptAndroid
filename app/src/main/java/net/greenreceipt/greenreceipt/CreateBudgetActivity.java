package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hotchemi.stringpicker.StringPicker;


public class CreateBudgetActivity extends Activity {
    LinearLayout container;
    ImageButton add;
    StringPicker editName;
    EditText limit;
    Button save;
    List<String> categoryList = new ArrayList<>();
    List<BudgetItem> items = new ArrayList<BudgetItem>();
    ProgressDialog spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);
        if(Model.categories != null)
        {
            for(Category c: Model.categories)
                categoryList.add(c.Name);
        }
        container = (LinearLayout) findViewById(R.id.container);
        add = (ImageButton) findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBudgetCategory();
            }
        });
        save = (Button) findViewById(R.id.save);
        Model.getInstance().setCreateBudgetListener(new Model.CreateBudgetListener() {
            @Override
            public void onCreateSuccess() {
                spinner.dismiss();
                finish();
            }

            @Override
            public void onCreateFailed(String error) {
                spinner.dismiss();
                Helper.AlertBox(CreateBudgetActivity.this,"Error",error);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Budget b = new Budget();
                b.BudgetItems = items;
                b.CreatedDate = new Date();
                b.Name = "Budget";
                spinner = ProgressDialog.show(CreateBudgetActivity.this,null,"Processing...");
                Model.getInstance().CreateBudget(b,CreateBudgetActivity.this);
            }
        });
    }

    private void addBudgetCategory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Add",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make sure there's valid info to add
                View view = getLayoutInflater().inflate(R.layout.edit_budget,null);
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setText(editName.getCurrentValue());
                EditText displayLimit = (EditText) view.findViewById(R.id.limit);
                displayLimit.setText(limit.getText().toString());
                Button delete = (Button) view.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        container.removeView((ViewGroup) v.getParent().getParent());
                        container.invalidate();
                    }
                });
                BudgetItem item = new BudgetItem();
                if(Model.categories !=null)
                item.Category = Model.categories[editName.getCurrent()];
                item.AmountAllowed = Double.parseDouble(limit.getText().toString());
                item.AmountUsed = 0;
                items.add(item);
                container.addView(view);
                if(save.getVisibility() == View.GONE)
                    save.setVisibility(View.VISIBLE);

            }
        });
        builder.setTitle("Add Item");
        View add = View.inflate(this,R.layout.add_budget_category,null);
        builder.setView(add);
        editName = (StringPicker) add.findViewById(R.id.name);
        if(Model.categories != null)
        {
            for(Category c: Model.categories)
                categoryList.add(c.Name);
        }
        editName.setValues(categoryList);
        if(categoryList.size() == 0)
            categoryList.add("");
        limit = (EditText) add.findViewById(R.id.amtAllowed);

        AlertDialog addDialog = builder.create();
        addDialog.show();
    }

}
