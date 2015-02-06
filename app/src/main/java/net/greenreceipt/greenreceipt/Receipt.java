package net.greenreceipt.greenreceipt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Boya on 12/14/14.
 */
public class Receipt
{
    public List<Item> ReceiptItems = new ArrayList<Item>();
    public Store Store = new Store();
//    public Date date;
    public double SubTotal;
    public double Tax;
    public double Total;
    public double Discount;
    public String Address;
    public Date ReturnDate;
    public String Category="";
    public boolean ReturnReminder;
    public String BarCode;
    public Date PurchaseDate;
    public int CardType;
    public String LastFourCardNumber;
    public String MiscMessage;
    public double Latitude;
    public double Longitude;
    public int StoreId;
    public Date CreatedDate;
    public int Id;



    public double getTotal()
    {
        if(SubTotal !=0)
        {
            return SubTotal + Tax;
        }
        else {
            double total = 0;
            for (Item i : ReceiptItems) {
                total += i.Price;
            }
            return total + Tax;
        }
    }
    public double getSubTotal()
    {
        double total = 0;
        for(Item i: ReceiptItems)
        {
            total+=i.Price;
        }
        return total;
    }
    public Receipt()
    {
        ReceiptItems = new ArrayList<Item>();
    }
    public int getItemCount()
    {
        return ReceiptItems.size();
    }
    public void addItem(Item item)
    {
        ReceiptItems.add(item);
    }
    public void addItems(Collection<Item> items)
    {
        this.ReceiptItems.addAll(items);
    }
    public void removeItem(Item item)
    {
        ReceiptItems.remove(item);
    }
    public Item getItem(int index)
    {
        return ReceiptItems.get(index);
    }
}
