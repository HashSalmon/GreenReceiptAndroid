package net.greenreceipt.greenreceipt;

import java.util.Date;

/**
 * Created by Boya on 12/14/14.
 */
public class Item
{
    public String ItemName;
    public double Price;
    public int Id;
    public Date CreatedDate = new Date();
    public Category Category = new Category();

}
