package net.greenreceipt.greenreceipt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class EditBudgetActivity extends Activity {
    LinearLayout container;
    Button save;
    List<BudgetItem> copy;
    boolean changed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);
        copy = new ArrayList<>();
        if(Model.currentBudget.BudgetItems!=null)
            copy.addAll(Model.currentBudget.BudgetItems);
        container = (LinearLayout) findViewById(R.id.container);
        save = (Button) findViewById(R.id.save);
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
                        container.removeView(p);
                        copy.remove(position);

                    }
                });
                container.addView(view);
            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Model.currentBudget!=null)
                {
                    Model.currentBudget.BudgetItems = copy;
                    Model.getInstance().CreateBudget(Model.currentBudget,EditBudgetActivity.this);
                }
                finish();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Model.currentBudget.BudgetItems.size() != copy.size() || !Model.currentBudget.BudgetItems.containsAll(copy))
        {
            //have been changed

        }
    }
}
