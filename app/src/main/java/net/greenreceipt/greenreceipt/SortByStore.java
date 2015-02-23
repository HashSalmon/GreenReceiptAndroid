package net.greenreceipt.greenreceipt;

import java.util.Comparator;

/**
 * Created by Boya on 2/18/15.
 */
public class SortByStore implements Comparator<Receipt> {
    @Override
    public int compare(Receipt lhs, Receipt rhs) {
        return lhs.Store.Company.Name.compareToIgnoreCase(rhs.Store.Company.Name);
    }
}
