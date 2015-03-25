package net.greenreceipt.greenreceipt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class EditBudgetActivity extends ActionBarActivity {
    LinearLayout container;
    List<BudgetItem> copy;
    boolean changed;
    private ActionBar actionBar;
    private BroadcastReceiver receiver;
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
        if(Model.currentBudget!=null && Model.currentBudget.BudgetItems!=null)
            copy.addAll(Model.currentBudget.BudgetItems);
        container = (LinearLayout) findViewById(R.id.container);
        if(Model.currentBudget.BudgetItems!=null && Model.currentBudget.BudgetItems.size()>0)
        {
            int counter=0;
            for(BudgetItem item : Model.currentBudget.BudgetItems)
            {
                View view = getLayoutInflater().inflate(R.layout.edit_budget,null);
                view.setId(counter);
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
                        int id = Model.currentBudget.BudgetItems.get(position).Id;
                        Model.getInstance().DeleteBudgetItem(id,EditBudgetActivity.this);
//                        container.removeView(p);
//                        copy.remove(position);

                    }
                });
                container.addView(view);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Model.currentBudget.BudgetItems.size() != copy.size() || !Model.currentBudget.BudgetItems.containsAll(copy))
        {
            //have been changed

        }
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
//                    if(id!=-1)
//                        Model.currentBudget.BudgetItems.remove()
                }
                else
                {

                }
            }
        };
        IntentFilter filter = new IntentFilter(Model.DELETE_BUDGET_ITEM_SUCCESS);
        IntentFilter fail = new IntentFilter(Model.DELETE_BUDGET_ITEM_FAIL);
        registerReceiver(receiver, filter);
        registerReceiver(receiver,fail);
    }
}
