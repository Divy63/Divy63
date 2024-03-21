package application;

import application.mydatabase.Database;

import java.io.IOException;
import java.util.Scanner;

public class Interface {
    public static void main(String[] args) {
        Database db = new Database();
        simulate(db);
    }

    private static void simulate(Database db) {

        Scanner consoleIn = new Scanner(System.in);// Scanner that takes input from console
        System.out.println();// Getting on a new line
        System.out.println("Welcome to Store Management!");// label

        System.out.print("To get started, ENTER 'm' for Menu: ");
        String command = consoleIn.nextLine();
        String[] parts;
        // String arg = "";

        while (command != null && !command.equals("e")) {
            parts = command.split("\\s+");
            // if (command.indexOf(" ") > 0)
            // arg = command.substring(command.indexOf(" ")).trim();

            if (parts[0].equals("m"))
                displayMenu();
            else if (parts[0].equals("spc")) {
                if (parts.length >= 2) {
                    try {
                        int limit = Integer.parseInt(parts[1]);
                        db.storeProfitByCountry(limit);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Limit must be an integer.");
                    }
                } else {
                    System.out.println("Require an argument for this command");
                }
            }

            else if (parts[0].equals("topproducts")) {
                if (parts.length >= 2) {
                    db.topProducts(parts[1]);
                } else {
                    System.out.println("Require an argument for this command");
                }
            }

            else if (parts[0].equals("rc")) {
                if (parts.length >= 2) {
                    db.returnedItemCount(parts[1]);
                } else {
                    System.out.println("Require an argument for this command");
                }
            }

            else if (parts[0].equals("dp")) {
                if (parts.length >= 2) {
                    db.discountedProducts(parts[1]);
                } else {
                    System.out.println("Require an argument for this command");
                }
            }

            else if (parts[0].equals("sd")) {
                if (parts.length >= 2)
                    db.shippingDetails(parts[1]);
                else
                    System.out.println("Require an argument for this command");
            }

            else if (parts[0].equals("ss")) {
                db.salesSummaryByCategory(parts[1]);
            }

            else if (parts[0].equals("subcp")) {
                db.subCategoryInventory();
            }

            else if (parts[0].equals("rp")) {
                if (parts.length >= 2) {
                    db.returnedProducts(parts[1]);
                } else {
                    System.out.println("Require an argument for this command");
                }
            } else if (parts[0].equals("rpr")) {
                if (parts.length >= 2) {
                    db.returnedByRegion(parts[1]);
                } else {
                    System.out.println("Require an argument for this command");
                }
            } else if (parts[0].equals("avgp")) {
                if (parts.length >= 2) {
                    db.averagePrice(parts[1]);
                } else {
                    System.out.println("Require an argument for this command");
                }
            } else if (parts[0].equals("exceed")) {
                db.exceed7();
            } else if (parts[0].equals("lra")) {
                db.largestReturnedAmount();
            }

            else
                System.out.println("Enter 'm' for Menu, else Enter your choice:");

            System.out.print("Choice >> ");
            command = consoleIn.nextLine();
        }

        consoleIn.close();

    }

    /*
     * Method that prints the menu to be displayed
     */
    private static void displayMenu() {

        System.out.println(
                "\tspc <country limit> - Stores and Profit by Country");
        System.out.println(
                "\ttopproducts <country code> - Top Product Holders by Category");
        System.out.println(
                "\trc <customerID> - Customer Returned Item Count Analysis");
        System.out.println(
                "\tdp <category name> - Discounted Products in Specific Category");
        System.out.println(
                "\tsd <orderID> - Shipping Details for Ordered Products");
        System.out.println(
                "\tss <category name> - Category Sales Summary");
        System.out.println(
                "\tsubcp - Sub-Category Product Inventory and Sales Overview");
        System.out.println(
                "\trp <custID> - Products Returned by Customer");
        System.out.println(
                "\trpr <region> - Product Returns by Region");
        System.out.println(
                "\tavgp <categoryID> - Average Product Price in Category");
        System.out.println(
                "\texceed - Order Shipping Mode Details for Orders Exceeding 7 Items");
        System.out.println(
                "\tlra <country limit> - Country-wise Largest Returned Order Amount");
        System.out.println("\tm - Display the Menu.");
        System.out.println("\te - Exit the system.");

    }
}

// class Database {
//     // private Connection connection;

//     public Database() {
//         // try {
//         // String url = "jdbc:sqlite:library.db";
//         // // create a connection to the database
//         // connection = DriverManager.getConnection(url);
//         // } catch (SQLException e) {
//         // e.printStackTrace(System.out);
//         // }

//     }

//     public void storeProfitByCountry(int countryLimit) {
//         System.out.println("storeProfitByCountry not implemented yet!!");
//     }

//     public void topProducts(String countryCode) {
//         System.out.println("topProducts not implemented for this country!");
//     }

//     public void returnedItemCount(String customerID) {
//         System.out.println("returnedItemCount not implemented yet!!");
//     }

//     public void discountedProducts(String categoryName) {
//         System.out.println("discountedProducts not implemented yet !!!");
//     }

//     public void shippingDetails(String orderID) {
//         System.out.println("shippingDetails not implemented yet!!");
//     }

//     public void salesSummaryByCategory(String categoryName) {
//         System.out.println("salesSummaryByCategory not implemented yet!!!");
//     }

//     public void subCategoryInventory() {
//         System.out.println("subCategoryInventory not implemented yet!!");
//     }

//     public void returnedProducts(String customerID) {
//         System.out.println("Returned products not implemented yet!!!");
//     }

//     public void returnedByRegion(String regionName) {
//         System.out.println("Returned by Region not implemented yet!!!");
//     }

//     public void averagePrice(String categoryID) {
//         System.out.println("averagePrice not implemented for this Category!!!");
//     }

//     public void exceed7() {
//         System.out.println("exceed 7 is not implemented yet!!!");
//     }

//     public void largestReturnedAmount() {
//         System.out.println("largestreturnedAmount is not implemented yet!!!");
//     }

// }