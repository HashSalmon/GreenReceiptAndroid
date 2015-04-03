package abbyy.ocrsdk.android;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegularEx
{
    private HashMap<String, Double> itemsList = new HashMap<String, Double>();
    private double sum = 0.0;
    private double subtotal = 0.0;

    public HashMap isMatch(String str)
    {
        String pattern = "(\\w+\\s)+(\\s)+\\$?(\\d)+.(\\d)+";

        Pattern r = Pattern.compile(pattern);
        Matcher match = r.matcher(str);

        if (match.find())
        {
            String itemNamePattern = "(\\w+\\s)+";
            Pattern p1 = Pattern.compile(itemNamePattern);
            Matcher m1 = p1.matcher(str);

            if (m1.find())
            {
                String itemName = str.substring(m1.start(), m1.end()).trim();
                String itemPrice = str.substring(m1.end()).trim();
                double price = Double.parseDouble(itemPrice.substring(1));
                itemsList.put(itemName, price);
                sum += sum;
                System.out.println("Item Name: " + itemName + "\n" + "Item Price: " + itemPrice);
            }
        }
        return itemsList;
    }
//
//    public static void main(String[] args)
//    {
//        RegularEx test = new RegularEx();
//        String str = "LTO the Meats      $4.99";
//        boolean stop = false;
//
//        if (str.toLowerCase().contains("subtotal") || str.toLowerCase().contains("total"))
//        {
//            stop = true;
//        }
//
//        //while (!stop)
//        //{
//        test.isMatch(str);
//        //}
//
//        if (test.sum > test.subtotal)
//        {
//            System.out.println("Throw exception");
//        }
//    }
}
