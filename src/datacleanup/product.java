package datacleanup;

import java.util.*;
import java.io.*;

public class product {
    final static String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void main(String[] args) {
        // makeCatFile();
        makeSubCatFile();
    }
    
    private static void makeProductFile() {
        try {
            Scanner s = new Scanner(new File("data-files/order-details.csv"));
            Writer w = new BufferedWriter(new FileWriter("data-files/products.csv"));
            String write = String.format("productID,name,sub-category,category\n");
            Map<String, List<String>> product = new HashMap<>();
            List<String> temp;
            String[] input;
            String id, cat, sub_cat, name;
            s.nextLine();
            w.write(write);
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                id = input[11];
                cat = input[12];
                sub_cat = input[13];
                name = input[14];

                if (product.get(id) == null) {
                    product.put(id, new ArrayList<String>());
                    temp = product.get(id);
                    temp.add(0, name);
                    temp.add(1, sub_cat);
                    temp.add(2, cat);
                }
            }

            Set<String> keys = product.keySet();
            List<String> sortedKeys = new ArrayList<>(keys);
            Collections.sort(sortedKeys);

            for (String temp_id : sortedKeys) {
                temp = product.get(temp_id);
                write = String.format("%s,%s,%s,%s\n", temp_id, temp.get(0), temp.get(1), temp.get(2));
                w.write(write);
            }
            w.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void makeCatFile() {
        try {
            Scanner s = new Scanner(new File("data-files/order-details.csv"));
            Writer w = new BufferedWriter(new FileWriter("data-files/category.csv"));
            String write = String.format("catID,name\n");
            Set<String> category = new HashSet<>();
            String input[];
            s.nextLine();
            w.write(write);
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                category.add(input[12]);
            }

            Iterator<String> it = category.iterator();
            int catID = 1;
            while (it.hasNext()) {
                write = String.format("%d,%s\n", catID++, it.next());
                w.write(write);
            }

            w.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void makeSubCatFile() {
        try {
            Map<String, String> category = new HashMap<>();
            category.put("Furniture", "3");
            category.put("Technology", "2");
            category.put("Office Supplies", "1");

            Scanner s = new Scanner(new File("data-files/order-details.csv"));
            Writer w = new BufferedWriter(new FileWriter("data-files/sub-category1.csv"));
            String write = String.format("subCatID,name,catID\n");
            Map<String, List<String>> sub_categories = new HashMap<>();
            String input[];
            String id, name, catID;
            int tempFUR = 100;
            int tempOFF = 100;
            int tempTEC = 100;
            List<String> tempList;
            s.nextLine();
            w.write(write);
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                name = input[13];
                if (sub_categories.get(name) == null ) {
                    catID = category.get(input[12]);
                    if (input[12].charAt(0) == 'o' || input[12].charAt(0) == 'O') {
                        id = input[12].substring(0, 3).toUpperCase() + "-" + (++tempOFF);
                    } else if (input[12].charAt(0) == 't' || input[12].charAt(0) == 'T') {
                        id = input[12].substring(0, 3).toUpperCase() + "-" + (++tempTEC);
                    } else {
                        id = input[12].substring(0, 3).toUpperCase() + "-" + (++tempFUR);
                    }
                    sub_categories.put(name, new ArrayList<String>());
                    tempList = sub_categories.get(name);
                    tempList.add(0, id);
                    tempList.add(1, name);
                    tempList.add(2, catID);
                }
            }

            Set<String> keySet = sub_categories.keySet();
            List<String> sortedKeySet = new ArrayList<>(keySet);
            Collections.sort(sortedKeySet);

            for (String tempID : sortedKeySet) {
                tempList = sub_categories.get(tempID);
                write = String.format("%s,%s,%s\n", tempList.get(0), tempList.get(1), tempList.get(2));
                w.write(write);
            }
            w.close();
            s.close();

            s = new Scanner(new File("data-files/sub-category1.csv"));
            w = new BufferedWriter(new FileWriter("data-files/sub-category.csv"));
            sub_categories = new HashMap<>();
            s.nextLine();
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                id = input[0];
                if (sub_categories.get(id) == null) {
                    name = input[1];
                    catID = input[2];
                    sub_categories.put(id, new ArrayList<String>());
                    tempList = sub_categories.get(id);
                    tempList.add(0, id);
                    tempList.add(1, name);
                    tempList.add(2, catID);
                }
            }

            keySet = sub_categories.keySet();
            sortedKeySet = new ArrayList<>(keySet);
            Collections.sort(sortedKeySet);

            write = String.format("subCatID,name,catID\n");
            w.write(write);
            for (String tempID : sortedKeySet) {
                tempList = sub_categories.get(tempID);
                write = String.format("%s,%s,%s\n", tempList.get(0), tempList.get(1), tempList.get(2));
                w.write(write);
            }

            s.close();
            w.close();

            File tempFile = new File("data-files/sub-category1.csv");

            if (tempFile.exists()) {
                tempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
