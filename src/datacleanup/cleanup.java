package datacleanup;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;

public class cleanup {
    private static final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void main(String[] args) {
        System.out.println("\nGenerating seperate .csv files for database\n");
        long start = System.currentTimeMillis(), end;
        try {
            makeDirectory("final-data-files");
            run();
            product pr = new product();
            pr.run();
            update up = new update();
            up.run();
            System.out.println("All required files created in \'final-data-files\' directory :)\n");
            end = System.currentTimeMillis();
        } catch (IOException io) {
            System.out.println("Error while creating directory for storing data files ;(\n");
            System.exit(1);
            end = System.currentTimeMillis();
        }
        // System.out.println("Time take: " + (end - start)/ 1000 + " seconds");
    }
    
    private static void run() {
        updateProductIDAndRegion();
        writeOrderDetails();
        updatePriceInOrderDetails();
        makeCountryData();
        makeAddressData();
        makeCustomerData();
        makeRegionData();
        makeStoreFile();
        makeOrderFile();
        makeInventoryFile();
    }

    private static void makeDirectory(String dirPath) throws IOException{
        Path dir = Paths.get(dirPath);
            Files.createDirectories(dir);
    }

    private static void makeCountryData() {
        try {
            Scanner read = new Scanner(new File("final-data-files/order-details.csv"));
            Writer write = new BufferedWriter(new FileWriter("final-data-files/countries.csv"));

            String name;

            Map<String, String> countries = new HashMap<>();

            read.nextLine();
            while (read.hasNextLine()) {
                name = read.nextLine().split(regex)[9];
                if (countries.get(name) == null) {
                    if (name.equals("United States")) {
                        countries.put(name, "USA");
                    } else if (name.equals("United Kingdom")) {
                        countries.put(name, "UNK");
                    } else {
                        countries.put(name, name.substring(0, 3).toUpperCase());
                    }
                }
            }
            Set<String> keySet = countries.keySet();
            Iterator<String> it = keySet.iterator();

            write.write("countryCode,name\n");
            while (it.hasNext()) {
                name = it.next();
                write.write(countries.get(name) + "," + name + "\n");
            }
            write.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void makeAddressData() {
        try {
            Scanner read = new Scanner(new File("final-data-files/order-details.csv"));
            Writer write = new BufferedWriter(new FileWriter("data-files/address-unsorted.csv"));

            Map<String, List<String>> address = new HashMap<>();
            int addressID = 1;
            String[] input;
            String city, state, country, hash, buffer;
            List<String> tempList;

            read.nextLine();
            while (read.hasNextLine()) {
                input = read.nextLine().split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                city = input[7];
                state = input[8];
                country = input[9];
                hash = city + state + country;
                tempList = address.get(hash);

                if (tempList == null) {
                    address.put(hash, new ArrayList<>());
                    tempList = address.get(hash);
                    tempList.add(Integer.toString(addressID++));
                    tempList.add(city);
                    tempList.add(state);
                    tempList.add(country);
                }
            }

            Set<String> keySet = address.keySet();
            Iterator<String> it = keySet.iterator();

            while (it.hasNext()) {
                hash = it.next();
                tempList = address.get(hash);
                buffer = String.format("%s,%s,%s,%s\n", tempList.get(0), tempList.get(1), tempList.get(2),
                        tempList.get(3));
                write.write(buffer);
            }

            read.close();
            write.close();

            read = new Scanner(new File("data-files/address-unsorted.csv"));
            write = new BufferedWriter(new FileWriter("final-data-files/address.csv"));
            write.write("addressID,city,state,country\n");
            Map<Integer, String> sortedAddress = new HashMap<>();

            // read.nextLine();
            while (read.hasNextLine()) {
                hash = read.nextLine();
                int tempID = Integer.parseInt(hash.split(regex)[0]);
                sortedAddress.put(tempID, hash);
            }

            Set<Integer> keySet2 = sortedAddress.keySet();
            List<Integer> sortedKeySet = new ArrayList<>(keySet2);
            Collections.sort(sortedKeySet);
            Iterator<Integer> it2 = sortedKeySet.iterator();

            while (it2.hasNext()) {
                write.write(sortedAddress.get(it2.next()) + "\n");
            }

            read.close();
            write.close();
            File tempFile = new File("data-files/address-unsorted.csv");

            if (tempFile.exists()) {
                tempFile.delete();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void makeCustomerData() {
        try {
            Scanner read = new Scanner(new File("final-data-files/order-details.csv"));
            Writer write = new BufferedWriter(new FileWriter("final-data-files/customers.csv"));

            Map<String, List<String>> customers = new HashMap<>();
            String[] input;
            String custID, fullName, firstName, lastName, writeString;
            List<String> tempList;

            read.nextLine();
            while (read.hasNextLine()) {
                input = read.nextLine().split(regex);
                custID = input[4];
                fullName = input[5];
                firstName = fullName.split(" ")[0];
                lastName = fullName.split(" ")[1];

                if (customers.get(custID) == null) {
                    customers.put(custID, new LinkedList<>());
                }

                tempList = customers.get(custID);
                tempList.add(firstName);
                tempList.add(lastName);
            }

            Set<String> keySet = customers.keySet();
            Iterator<String> it = keySet.iterator();

            write.write("custID,fName,lName\n");
            while (it.hasNext()) {
                custID = it.next();
                tempList = customers.get(custID);
                writeString = String.format("%s,%s,%s\n", custID, tempList.get(0), tempList.get(1));
                write.write(writeString);
            }

            read.close();
            write.close();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void updateProductIDAndRegion() {
        try {
            Scanner s = new Scanner(new File("data-files/us-orders.csv"));
            Writer w = new BufferedWriter(new FileWriter("data-files/new-us-orders.csv"));
            String[] input;
            String write;
            w.write(s.nextLine() + "\n");
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                write = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,US-%s,US-%s,%s,%s,%s,%s,%s,%s,%s\n",
                        input[0],
                        input[1], input[2], input[3], input[4], input[5], input[6], input[7], input[8], input[9],
                        input[10], input[11], input[12], input[13], input[14], input[15], input[16], input[17],
                        input[18], input[19], input[20]);
                w.write(write);
            }
            s.close();
            w.close();

            s = new Scanner(new File("data-files/eu-orders.csv"));
            w = new BufferedWriter(new FileWriter("data-files/new-eu-orders.csv"));
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                write = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,EU-%s,EU-%s,%s,%s,%s,%s,%s,%s,%s\n", input[0],
                        input[1], input[2], input[3], input[4], input[5], input[6], input[7], input[8], input[9],
                        input[10], input[11], input[12], input[13], input[14], input[15], input[16], input[17],
                        input[18], input[19]);
                w.write(write);
            }
            s.close();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeOrderDetails() {
        try {
            Scanner s = new Scanner(new File("data-files/new-us-orders.csv"));
            String[] input;
            String write;
            Writer w = new BufferedWriter(new FileWriter("final-data-files/order-details.csv"));
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                write = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", input[1], input[2],
                        input[3], input[4], input[5], input[6], input[7], input[9], input[10], input[8], input[12],
                        input[13], input[14], input[15], input[16], input[17], input[18], input[19], input[20]);
                w.write(write);
            }
            s.close();
            s = new Scanner(new File("data-files/new-eu-orders.csv"));
            s.nextLine();
            while (s.hasNextLine()) {
                input = s.nextLine().split(regex);
                write = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", input[1], input[2],
                        input[3], input[4], input[5], input[6], input[7], input[8], input[9], input[10], input[11],
                        input[12], input[13], input[14], input[15], input[16], input[17], input[18], input[19]);
                w.write(write);
            }
            s.close();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void makeRegionData() {
        int regionID = 1;
        int managerID = 1;
        Scanner in1, in2, in3;
        Writer out;
        try {
            in1 = new Scanner(new File("data-files/eu-store-managers.csv"));
            in2 = new Scanner(new File("data-files/us-store-managers.csv"));
            out = new BufferedWriter(new FileWriter("final-data-files/manager.csv"));
            String[] input;
            String fullName, fName, lName;

            out.write("managerID,fName,lName\n");

            in1.nextLine();
            while (in1.hasNextLine()) {
                input = in1.nextLine().split(regex);
                fullName = input[1];
                fName = fullName.split(" ")[0];
                lName = fullName.split(" ")[1];

                out.write(managerID++ + "," + fName + "," + lName + "\n");
            }

            in2.nextLine();
            while (in2.hasNextLine()) {
                input = in2.nextLine().split(regex);
                fullName = input[0];
                fName = fullName.split(" ")[0];
                lName = fullName.split(" ")[1];

                out.write(managerID++ + "," + fName + "," + lName + "\n");
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
        try {
            in1 = new Scanner(new File("data-files/us-store-managers.csv"));
            in2 = new Scanner(new File("final-data-files/manager.csv"));
            in3 = new Scanner(new File("data-files/eu-store-managers.csv"));

            out = new BufferedWriter(new FileWriter("final-data-files/region.csv"));
            Map<String, String> region = new HashMap<>();
            Map<String, List<String>> manager = new HashMap<>();
            List<String> tempList = new ArrayList<>();
            String inputLine, manID;
            String[] input;
            in2.nextLine();
            while (in2.hasNextLine()) {
                input = in2.nextLine().split(regex);
                manID = input[0];
                tempList = manager.get(manID);
                if (tempList == null) {
                    manager.put(manID, new ArrayList<>());
                }
                tempList = manager.get(manID);
                tempList.add(input[1]);
                tempList.add(input[2]);
            }

            String regionName;
            in1.nextLine();
            while (in1.hasNextLine()) {
                input = in1.nextLine().split(regex);
                regionName = input[1];
                String fName = input[0].split(" ")[0];
                String lName = input[0].split(" ")[1];
                manID = getManID(manager, fName, lName);
                if (manID != null) {
                    region.put(regionName, manID);
                } else {
                    System.out.println("Error in making region.csv file");
                }
            }

            in3.nextLine();
            while (in3.hasNextLine()) {
                input = in3.nextLine().split(regex);
                regionName = input[0];
                String fName = input[1].split(" ")[0];
                String lName = input[1].split(" ")[1];
                manID = getManID(manager, fName, lName);
                if (manID != null) {
                    region.put(regionName, manID);
                } else {
                    System.out.println("Error in making region.csv file");
                }
            }

            out = new BufferedWriter(new FileWriter("final-data-files/region.csv"));

            Set<String> keySet = region.keySet();

            out.write("regionID,regionName,managerID\n");
            for (String string : keySet) {
                out.write(regionID++ + "," + string + "," + region.get(string) + "\n");
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static String getManID(Map<String, List<String>> managers, String fName, String lName) {
        String manID = null;
        Set<String> keySet = managers.keySet();
        List<String> tempList;
        for (String s : keySet) {
            tempList = managers.get(s);
            if (tempList.contains(lName) && tempList.contains(fName)) {
                manID = s;
            }
        }
        return manID;
    }

    /**
     * Method that finds discouinted total price of the order
     */
    private static String getDiscountedPrice(double price, double discount, int quantity) {
        double eachPrice = price / ((1 - discount) * quantity);
        return String.format("%.2f", eachPrice);
    }

    /**
     * Method that updates the reads a csv file and finds out discounted price of
     * each product and stores it in the file
     */
    private static void updatePriceInOrderDetails() {
        ArrayList<String> fileData = new ArrayList<>();
        try {
            Scanner odScanner = new Scanner(new FileReader("final-data-files/order-details.csv"));

            // Adding title to the first line
            String firstLine = odScanner.nextLine();
            firstLine += ",prod_price";
            fileData.add(firstLine);

            // Going through all lines in the file and getting dicounte price
            // and adding that price in the string
            while (odScanner.hasNextLine()) {
                String data = odScanner.nextLine();
                String[] temp = data.split(regex);
                double price = Double.parseDouble(temp[15]);
                double discount = Double.parseDouble(temp[17]);
                int quantity = Integer.parseInt(temp[16]);

                String eachPrice = getDiscountedPrice(price, discount, quantity);

                data += "," + eachPrice;

                fileData.add(data);
            }
            odScanner.close();
        } catch (FileNotFoundException foe) {
            System.out.println("File Not Found");
        }

        /**
         * Writing the updated lines back in the file
         */
        try {
            Writer out = new BufferedWriter(new FileWriter("final-data-files/order-details.csv"));
            for (String d : fileData) {
                out.write(d + "\n");
            }

            out.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    private static void makeStoreFile() {
        try {
            Scanner in = new Scanner(new File("final-data-files/address.csv"));
            Scanner in2 = new Scanner(new File("final-data-files/region.csv"));
            List<String> region = new ArrayList<>();
            List<String> address = new ArrayList<>();
            List<String> orderDetails = new ArrayList<>();

            String addressStr, inputLine;
            String[] input;

            while (in2.hasNextLine()) {
                input = in2.nextLine().split(regex);
                region.add(input[0]);
                region.add(input[1]);
            }

            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                address.add(input[0]);
                address.add(input[1] + "," + input[2] + "," + input[3]);
            }

            in.close();
            in2.close();

            in = new Scanner(new File("final-data-files/order-details.csv"));

            List<String> stores = new ArrayList<>();
            int storeID = 1000;
            Writer out = new BufferedWriter(new FileWriter("final-data-files/stores.csv"));
            out.write("storeID,addressID,regionID\n");

            in.nextLine();
            while (in.hasNextLine()) {
                inputLine = in.nextLine();
                input = inputLine.split(regex);
                addressStr = input[7] + "," + input[8] + "," + input[9];
                if (!stores.contains(addressStr)) {
                    stores.add(addressStr);
                    stores.add(Integer.toString(storeID));
                    String storeStr = Integer.toString(storeID++) + ","
                            + address.get(address.indexOf(addressStr) - 1) + ","
                            + region.get(region.indexOf(input[10]) - 1);

                    out.write(storeStr + "\n");
                }
                orderDetails.add(inputLine + "," + stores.get(stores.indexOf(addressStr) + 1));
            }

            in.close();
            out.close();

            out = new BufferedWriter(new FileWriter("final-data-files/order-details.csv"));
            out.write(
                    "orderID,orderDate,shipDate,shipMode,custID,Customer Name,Segment,City,State,Country/Region,Region,Product ID,Category,Sub-Category,Product Name,Sales,Quantity,Discount,Profit,prod_price,storeID\n");
            for (String string : orderDetails) {
                out.write(string + "\n");
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void makeOrderFile() {
        try {
            Scanner in = new Scanner(new File("final-data-files/order-details.csv"));
            Map<String, String> orders = new HashMap<>();
            List<String> tempList = new ArrayList<>();
            String[] input;

            in.nextLine();
            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                if (orders.get(input[0]) == null) {
                    orders.put(input[0], input[0] + "," + changeDateFormat(input[1]) + "," + changeDateFormat(input[2])
                            + "," + input[3] + "," + input[6]
                            + "," + input[4] + "," + input[20]);
                }
            }
            in.close();

            Writer out = new BufferedWriter(new FileWriter("final-data-files/orders.csv"));
            out.write("orderID,orderDate,shipData,shipMode,segment,custID,storeID\n");

            Set<String> keys = orders.keySet();
            List<String> sortedKeys = new ArrayList<>(keys);
            Collections.sort(sortedKeys);

            for (String string : sortedKeys) {
                out.write(orders.get(string) + "\n");
            }
            out.close();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static String changeDateFormat(String date) {
        String[] dateArr = date.split("/");

        return "20" + dateArr[2] + "-" + dateArr[1] + "-" + dateArr[0];
    }

    private static void makeInventoryFile() {
        try {
            Scanner in = new Scanner(new File("final-data-files/order-details.csv"));
            Set<String> inventory = new HashSet<>();
            String[] input;
            String invStr;

            in.nextLine();
            while (in.hasNextLine()) {
                input = in.nextLine().split(regex);
                invStr = String.format("%s,%s", input[11], input[20]);
                inventory.add(invStr);
            }
            in.close();

            Writer out = new BufferedWriter(new FileWriter("final-data-files/inventory.csv"));
            out.write("prodID,storeID\n");
            Iterator<String> it = inventory.iterator();

            while (it.hasNext()) {
                out.write(it.next() + "\n");
            }
            out.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}
