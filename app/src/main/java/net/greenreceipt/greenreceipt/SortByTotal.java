package net.greenreceipt.greenreceipt;

import java.util.Comparator;

/**
 * Created by Boya on 2/18/15.
 */
public class SortByTotal implements Comparator<Receipt> {
    @Override
    public int compare(Receipt lhs, Receipt rhs) {
        if(lhs.Total > rhs.Total)
            return 1;
        else if(lhs.Total < rhs.Total)
            return -1;
        else
            return 0;
    }
}
