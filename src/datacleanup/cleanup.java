package datacleanup;

import java.util.*;
import java.io.*;

public class cleanup {
    private static final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void main(String[] args) {
        updateProductIDAndRegion();
        writeOrderDetails();
        makeCountryData();
        makeAddressData();
        makeCustomerData();
        makeRegionData();
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
}
