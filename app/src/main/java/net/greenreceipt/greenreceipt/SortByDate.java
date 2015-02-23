package net.greenreceipt.greenreceipt;

import java.util.Comparator;

/**
 * Created by Boya on 2/18/15.
 */
public class SortByDate implements Comparator<Receipt> {
    @Override
    public int compare(Receipt lhs, Receipt rhs) {
        return lhs.PurchaseDate.compareTo(rhs.PurchaseDate);
    }
}
