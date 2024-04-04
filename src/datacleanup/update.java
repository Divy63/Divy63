package datacleanup;

import java.util.*;
import java.io.*;

public class update {
    private static final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final String ORDER_DETAILS = "data-files/order-details.csv";
    private static final String PRODUCTS = "final-data-files/products.csv";

    public void run() {
        updateAddressData();
        updateProducts();
        updateOrderDetails();
        updateReturnedOrders();
    }

    private void updateOrderDetails() {
        try {
            Scanner in = new Scanner(new File("final-data-files/order-details.csv"));
            List<String> od = new ArrayList<>();
            String[] input;
            String odStr;

            in.nextLine();
            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);

                odStr = String.format("%s,%s,%s,%s,%s,%.2f",
                        input[0], input[11], input[15], input[16],
                        input[17], Double.parseDouble(input[18]));

                od.add(odStr);
            }
            in.close();
            Writer out = new BufferedWriter(new FileWriter("final-data-files/order-details.csv"));
            out.write("orderID,prodID,sales,quantity,discount,profit\n");

            for (String string : od) {
                out.write(string + "\n");
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void updateAddressData() {
        try {
            Scanner in = new Scanner(new File("final-data-files/countries.csv"));
            Map<String, String> country = new HashMap<>();
            String[] input;
            in.nextLine();

            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                if (country.get(input[1]) == null) {
                    country.put(input[1], input[0]);
                }
            }
            in.close();

            List<String> address = new ArrayList<>();
            in = new Scanner(new File("final-data-files/address.csv"));

            in.nextLine();
            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                address.add(input[0] + "," + input[1] + "," + input[2] + "," + country.get(input[3]));
            }
            in.close();

            Writer out = new BufferedWriter(new FileWriter("final-data-files/address.csv"));
            out.write("addressID,city,state,countryCode\n");
            for (String string : address) {
                out.write(string + "\n");
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void updateProducts() {
        try {
            Scanner in = new Scanner(new File("final-data-files/sub-category.csv"));
            Map<String, String> subCat = new HashMap<>();
            String temp;
            String[] input;

            in.nextLine();
            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                if (subCat.get(input[1]) == null) {
                    subCat.put(input[1], input[0]);
                }
            }
            in.close();

            in = new Scanner(new File("final-data-files/products.csv"));
            List<String> products = new ArrayList<>();

            in.nextLine();
            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                products.add(input[0] + "," + input[1] + "," + input[4] + "," + subCat.get(input[2]));
            }
            in.close();

            Writer out = new BufferedWriter(new FileWriter("final-data-files/products.csv"));

            out.write("prodID,name,price,subCatID\n");
            for (String string : products) {
                out.write(string + "\n");
            }
            out.close();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void updateReturnedOrders() {
        try{
            Scanner in = new Scanner(new File("final-data-files/orders.csv"));
            Scanner in2 = new Scanner(new File("data-files/eu-store-returns.csv"));
            List<String> returnedOrder = new ArrayList<>();
            List<String> orders = new ArrayList<>();
            String inputLine;

            in2.nextLine();
            while(in2.hasNextLine()){
                returnedOrder.add(in2.nextLine().split(regex)[0]);
            }
            in2.close();

            in2 = new Scanner(new File("data-files/us-store-returns.csv"));
            in2.nextLine();
            while (in2.hasNextLine()) {
                returnedOrder.add(in2.nextLine().split(regex)[1]);
            }
            in2.close();

            in.nextLine();
            while (in.hasNextLine()) {
                inputLine = in.nextLine();
                if (returnedOrder.contains(inputLine.split(regex)[0])) {
                    orders.add(inputLine + ",1");
                } else {
                    orders.add(inputLine + ",0");
                }
            }
            in.close();

            Writer out = new BufferedWriter(new FileWriter("final-data-files/orders.csv"));
            out.write("orderID,orderDate,shipDate,shipMode,segment,custID,storeID,isReturned\n");
            for (String string : orders) {
                out.write(string + "\n");
            }
            out.close();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}