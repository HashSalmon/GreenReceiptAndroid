package net.greenreceipt.greenreceipt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boya on 2/8/15.
 */
public class ListReceiptAdapter extends BaseAdapter implements Filterable {
    private List<Receipt> originalData = Model._receipts;
    private List<Receipt>filteredData = Model._receipts;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public ListReceiptAdapter(Context context) {

        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return filteredData.get(position).Id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Receipt r = filteredData.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
//        Date today = new Date();
        View view = convertView;
        if(view == null)
            view = mInflater.inflate(R.layout.listitem, null);
        TextView store = (TextView) view.findViewById(R.id.store);
        if(r.Store!=null)
            store.setText(r.Store.Company.Name);
        else
            store.setText("");
        TextView detail = (TextView) view.findViewById(R.id.detail);
//        if(r.CreatedDate == null)
        detail.setText(sdf.format(r.PurchaseDate)+"\n$"+new DecimalFormat("##.##").format(r.Total));
//        else
//            detail.setText(sdf.format(r.date) + "\n$" + new DecimalFormat("##.##").format(r.getTotal()));
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Receipt> list = originalData;

            int count = list.size();
            final ArrayList<Receipt> nlist = new ArrayList<Receipt>(count);

            Receipt receipt ;

            for (int i = 0; i < count; i++) {
                receipt = list.get(i);
                if (receipt.Store.Company.Name.toLowerCase().contains(filterString)) {
                    nlist.add(receipt);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Receipt>) results.values;
            notifyDataSetChanged();
        }

    }
}
