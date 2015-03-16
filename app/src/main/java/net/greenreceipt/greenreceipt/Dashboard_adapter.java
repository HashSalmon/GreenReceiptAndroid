package net.greenreceipt.greenreceipt;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Boya on 3/12/15.
 */
public class Dashboard_adapter extends ListViewAdapter {
    public Dashboard_adapter(List items) {
        super(items);
    }
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listitem, parent, false);
        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        DashboardViewHolder viewHolder = (DashboardViewHolder)holder;
//        Receipt receipt = (Receipt)getItems().get(position);
        if(position == 0)
        {
            viewHolder.store.setText("Total receipts");
            viewHolder.detail.setText(Model.getInstance().getTotalReceiptCount()+"\n$ "+new DecimalFormat("##.##").format(Model.getInstance().getReceiptsTotal()));
        }
        else
        {
            Pair currentMonth = Model.getInstance().getCurrentMonthReceiptCount();
            viewHolder.store.setText("This Month");
            viewHolder.detail.setText(currentMonth.first+"\n$ "+new DecimalFormat("##.##").format(currentMonth.second));
        }
    }
    public static class DashboardViewHolder extends ListViewHolder {

        TextView store;
        TextView detail;

        public DashboardViewHolder(View itemView) {
            super(itemView);

            store = (TextView)itemView.findViewById(R.id.store);
            detail = (TextView)itemView.findViewById(R.id.detail);
        }
    }
}