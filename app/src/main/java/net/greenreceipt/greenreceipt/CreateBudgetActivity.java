package net.greenreceipt.greenreceipt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Util.Helper;
import hotchemi.stringpicker.StringPicker;


public class CreateBudgetActivity extends ActionBarActivity {
    LinearLayout container;
    StringPicker categoryPicker;
    EditText limit;
    Button save;
    List<String> categoryList = new ArrayList<>();
    List<BudgetItem> items = new ArrayList<BudgetItem>();
    ProgressDialog spinner;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(Model.getInstance().categories != null)
        {
            for(Category c: Model.getInstance().categories)
                categoryList.add(c.Name);
        }
        container = (LinearLayout) findViewById(R.id.container);

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
                Helper.AlertBox(CreateBudgetActivity.this, "Error", error);

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
        if(Model.getInstance().categories != null)
        {
            categoryList.clear();
            for(Category c: Model.getInstance().categories)
                categoryList.add(c.Name);
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_budget, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.add:
                addBudgetCategory();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
                name.setText(categoryPicker.getCurrentValue());
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
                int index = categoryPicker.getCurrent();
                String s = categoryPicker.getCurrentValue();
                if(Model.getInstance().categories !=null)
                item.Category = Model.getInstance().categories[categoryPicker.getCurrent()];
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
        categoryPicker = (StringPicker) add.findViewById(R.id.name);
        categoryPicker.setValues(categoryList);
        if(categoryList.size() == 0)
            categoryList.add("");
        limit = (EditText) add.findViewById(R.id.amtAllowed);

        AlertDialog addDialog = builder.create();
        addDialog.show();
    }

}
