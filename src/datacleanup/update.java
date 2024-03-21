package datacleanup;

import java.util.*;
import java.io.*;

public class update {
    private static final String ORDER_DETAILS = "data-files/order-details.csv";
    private static final String PRODUCTS = "final-data-files/products.csv";
    public static void main(String[] args) {

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