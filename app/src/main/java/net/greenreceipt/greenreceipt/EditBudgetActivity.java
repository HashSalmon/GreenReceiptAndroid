package net.greenreceipt.greenreceipt;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.List;

import hotchemi.stringpicker.StringPicker;


public class EditBudgetActivity extends ActionBarActivity {
    LinearLayout container;
    List<BudgetItem> copy;
    boolean changed;
    private ActionBar actionBar;
    private BroadcastReceiver receiver;
    StringPicker categoryPicker;
    EditText limit;
    List<String> categoryList = new ArrayList<>();
    StringPicker editName;
    List<BudgetItem> items = new ArrayList<>();
    List<Integer> deleted = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        copy = new ArrayList<>();
        if(Model.getInstance().currentBudget!=null && Model.getInstance().currentBudget.BudgetItems!=null)
            copy.addAll(Model.getInstance().currentBudget.BudgetItems);
        container = (LinearLayout) findViewById(R.id.container);
        if(Model.getInstance().currentBudget.BudgetItems!=null && Model.getInstance().currentBudget.BudgetItems.size()>0)
        {
            int counter=0;
            for(BudgetItem item : Model.getInstance().currentBudget.BudgetItems)
            {
                View view = getLayoutInflater().inflate(R.layout.edit_budget,null);
                view.setId(counter);
                counter++;
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setText(item.Category.Name);
                EditText limit = (EditText) view.findViewById(R.id.limit);
                limit.setText(item.AmountAllowed+"");
                Button delete = (Button) view.findViewById(R.id.delete);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View p = (View) v.getParent().getParent();
                        int position = p.getId();
                        BudgetItem b = Model.getInstance().currentBudget.BudgetItems.get(position);
                        deleted.add(b.Id);
                        changed = true;
//                        Model.getInstance().DeleteBudgetItem(id,EditBudgetActivity.this);
                        container.removeView(p);
//                        copy.remove(position);

                    }
                });
                container.addView(view);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_budget, menu);
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
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction() == Model.DELETE_BUDGET_ITEM_SUCCESS)
                {
                    int id = intent.getIntExtra("id",-1);
                    if(id!=-1)
                        Model.getInstance().removeBudgetItem(id);
                }
                else
                {
                    container.removeAllViews();
                    if(Model.getInstance().currentBudget.BudgetItems!=null && Model.getInstance().currentBudget.BudgetItems.size()>0)
                    {
                        int counter=0;
                        for(BudgetItem item : Model.getInstance().currentBudget.BudgetItems)
                        {
                            View view = getLayoutInflater().inflate(R.layout.edit_budget,null);
                            view.setId(counter);
                            counter++;
                            TextView name = (TextView) view.findViewById(R.id.name);
                            name.setText(item.Category.Name);
                            final EditText limit = (EditText) view.findViewById(R.id.limit);
                            limit.setText(item.AmountAllowed+"");

                            Button delete = (Button) view.findViewById(R.id.delete);

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    View p = (View) v.getParent().getParent();
                                    int position = p.getId();
                                    int id = Model.getInstance().currentBudget.BudgetItems.get(position).Id;
                                    Model.getInstance().currentBudget.BudgetItems.remove(position);
//                                    Model.getInstance().DeleteBudgetItem(id,EditBudgetActivity.this);

//                        container.removeView(p);
//                        copy.remove(position);

                                }
                            });
                            container.addView(view);
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(Model.DELETE_BUDGET_ITEM_SUCCESS);
        IntentFilter fail = new IntentFilter(Model.DELETE_BUDGET_ITEM_FAIL);
        registerReceiver(receiver, filter);
        registerReceiver(receiver,fail);
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
                try {
                    BudgetItem item = new BudgetItem();
                    if (Model.getInstance().categories != null)
                        item.Category = Model.getInstance().categories[editName.getCurrent()];
                    item.AmountAllowed = Double.parseDouble(limit.getText().toString());
                    item.AmountUsed = 0;
                    item.BudgetId = Model.getInstance().currentBudget.Id;
                    items.add(item);
                    container.addView(view);
                    changed = true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        builder.setTitle("Add Item");
        View add = View.inflate(this,R.layout.add_budget_category,null);
        builder.setView(add);
        editName = (StringPicker) add.findViewById(R.id.name);
        if(Model.getInstance().categories != null)
        {
            for(Category c: Model.getInstance().categories)
                categoryList.add(c.Name);
        }
        editName.setValues(categoryList);
        if(categoryList.size() == 0)
            categoryList.add("");
        limit = (EditText) add.findViewById(R.id.amtAllowed);

        AlertDialog addDialog = builder.create();
        addDialog.show();
    }
    public void save()
    {
        for(int i = 0; i<Model.getInstance().currentBudget.BudgetItems.size(); i++)
        {
            LinearLayout v = (LinearLayout) container.getChildAt(i);
            LinearLayout l = (LinearLayout)v.getChildAt(1);
            EditText allowed = (EditText) l.getChildAt(0);
            Model.getInstance().currentBudget.BudgetItems.get(i).AmountAllowed = Double.parseDouble(allowed.getText().toString());
        }

            Model.getInstance().SaveBudgetItems(Model.getInstance().currentBudget.BudgetItems, this);
            Model.getInstance().SaveBudgetItems(items, this);
            Model.getInstance().DeleteBudgetItems(deleted,this);
    }

}
