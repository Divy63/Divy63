package datacleanup;

import java.util.*;
import java.io.*;

public class update {
    private static final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final String ORDER_DETAILS = "data-files/order-details.csv";
    private static final String PRODUCTS = "final-data-files/products.csv";

    public static void main(String[] args) {
        updateAddress();
    }
    
    private static void updateAddress() {
        try{
            Scanner in = new Scanner(new File("final-data-files/countries.csv"));
            Scanner in2 = new Scanner(new File("final-data-files/address.csv"));
            Map<String, List<String>> address = new HashMap<>();
            List<String> regions = new ArrayList<>();
            List<String> country = new ArrayList<>();
            String[] input;
            String id, countryName;
            in.nextLine();
            while (in.hasNextLine()){
                input = in.nextLine().split(regex);
                country.add(input[0]);
                country.add(input[1]);
            }
            in2.nextLine();
            while (in2.hasNextLine()) {
                input = in2.nextLine().split(regex);
                countryName = input[3];
                int index = country.indexOf(countryName);
                regions.add(input[0] + "," + input[1] + "," + input[2] + "," + country.get(index - 1) + "\n");
            }
            in.close();
            in2.close();

            Writer out = new BufferedWriter(new FileWriter("final-data-files/address.csv"));

            out.write("addressID,city,state,country\n");
            for (String string : regions) {
                out.write(string);
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    
    private static void updateProductInOrderDetals() {
        try{
            Scanner readOrders = new Scanner(new File(ORDER_DETAILS));
            Scanner readProducts = new Scanner(new File(PRODUCTS));


            Map<String, String> products = new HashMap<>();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}