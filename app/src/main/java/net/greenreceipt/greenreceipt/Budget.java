package net.greenreceipt.greenreceipt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Boya on 2/23/15.
 */
public class Budget
{
    public String Name;
    public String UserId;
    public List<BudgetItem> BudgetItems = new ArrayList<>();
    public int Id;
    public boolean IsDeleted;
    public Date CreatedDate;

}
