package net.greenreceipt.greenreceipt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Boya on 3/11/15.
 */
public class ListAdapter extends ListViewAdapter {
    public ListAdapter(List items) {
        super(items);
    }
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listitem, parent, false);
        return new ReceiptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ReceiptViewHolder viewHolder = (ReceiptViewHolder)holder;
        Receipt receipt = (Receipt)getItems().get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
        viewHolder.store.setText(receipt.Store.Company.Name);
        viewHolder.detail.setText(sdf.format(receipt.PurchaseDate)+"\n$"+new DecimalFormat("##.##").format(receipt.Total));
    }
    public static class ReceiptViewHolder extends ListViewHolder {

        TextView store;
        TextView detail;

        public ReceiptViewHolder(View itemView) {
            super(itemView);

            store = (TextView)itemView.findViewById(R.id.store);
            detail = (TextView)itemView.findViewById(R.id.detail);
        }
    }
}
