/**
 * App.java
 *
 * COMP 2150 SECTION A01
 * INSTRUCTOR Heather Matheson
 * Project Project
 * 
 * @author Het Patel, 7972424
 * @author Divy Patel,7951650
 * @author Vince Ibero, //TODO Vince needs to write student number
 * @version 2024-04-10
 *
 *          REMARKS: Program that provides interface to user to get connected to the database and perform analysis on it
 */
package application;

import application.mydatabase.Database;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        try {
            Database db = new Database();
            simulate(db);
        } catch (SQLException se) {
            System.out.println("Suitable Driver not found to establish connection with database");
            System.exit(1);
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
            System.exit(1);
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
    }

    /**
     * Method that gets the Database object and then calls other methods in this
     * class for simulation purposes.
     * It takes user input and calls the methods according to the user input and
     * provides user with the data
     * they want.
     * 
     * @param db
     */
    private static void simulate(Database db) {

        Scanner consoleIn = new Scanner(System.in);// Scanner that takes input from console
        System.out.println();// Getting on a new line
        System.out.println("Welcome to Store Management!");// label

        System.out.print("To get started, ENTER 'm' for Menu: ");
        String command = consoleIn.nextLine();
        String[] parts;

        while (command != null && !command.equals("e")) {
            parts = command.split("\\s+");

            if (parts[0].equals("m"))// menu
                displayMenu();
            else if (parts[0].equals("spc")) {// Stores and Profit by Country
                if (parts.length >= 2) {
                    try {
                        int limit = Integer.parseInt(parts[1]);
                        db.storeProfitByCountry(limit);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Limit must be an integer.");
                    }
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            }

            else if (parts[0].equals("topproducts")) {// Top Product Holders by Category
                if (parts.length >= 2) {
                    db.topProducts(parts[1]);
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            }

            else if (parts[0].equals("rc")) {// Customer Returned Item Count Analysis
                if (parts.length >= 2) {
                    db.returnedItemCount(parts[1]);
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            }

            else if (parts[0].equals("dp")) {// Discounted Products in Specific Category
                if (parts.length >= 4) {
                    System.out.println(parts[3]);
                    db.discountedProducts(parts[1] + " " + parts[2], Double.parseDouble(parts[3]));
                } else if (parts.length == 3) {
                    db.discountedProducts(parts[1], Double.parseDouble(parts[2]));
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            }

            else if (parts[0].equals("sd")) {// Shipping Details for Ordered Products
                if (parts.length >= 2)
                    db.shippingDetails(parts[1]);
                else
                    System.out.println("Require an argument for this command\n");
            }

            else if (parts[0].equals("ss")) {// Category Sales Summary
                db.salesSummaryByCategory();
            }

            else if (parts[0].equals("subcp")) {// Sub-Category Product Inventory and Sales Overview
                db.subCategoryInventory();
            }

            else if (parts[0].equals("rp")) {// Products Returned by Customer
                if (parts.length >= 2) {
                    db.returnedProducts(parts[1]);
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            } else if (parts[0].equals("rpr")) {// Product Returns by Region
                if (parts.length >= 2) {
                    db.returnedByRegion(parts[1]);
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            } else if (parts[0].equals("avgp")) {// Average Product Price in Category
                if (parts.length >= 2) {
                    db.averagePrice(Integer.parseInt(parts[1]));
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            } else if (parts[0].equals("exceed")) {// Order Shipping Mode Details for Orders Exceeding 'X' Items
                if (parts.length >= 2) {
                    db.exceedXShipMode(Integer.parseInt(parts[1]));
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            } else if (parts[0].equals("lra")) {// Country-wise Largest Returned Order Amount
                if (parts.length >= 2) {
                    db.largestReturnedAmount(Integer.parseInt(parts[1]));
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            } else if (parts[0].equals("sc")) {// Show all the Countries along with their Country Code
                db.showCountries();

            } else if (parts[0].equals("gcID")) {// Gets the Name of all the customer with 'part of the name of the
                                                 // customer' int their name
                if (parts.length >= 2) {
                    db.showPeople(parts[1]);
                } else {
                    System.out.println("Require an argument for this command\n");
                }

            } else if (parts[0].equals("scategories")) {// Show all the Categories
                db.showCategories();
            } else if (parts[0].equals("sSubCategories")) {// Show all the Sub-Categories along with their Category
                if (parts.length >= 3) {
                    db.showSubCategories(parts[1] + " " + parts[2]);
                } else if (parts.length == 2) {
                    db.showSubCategories(parts[1]);
                } else {
                    System.out.println("Require an argument for this command\n");
                }
            } else if (parts[0].equals("sRegions")) {// Show all the Regions
                db.showRegions();
            } else if (parts[0].equals("i")) {// Initialize the database
                db.initializeDatabase();
            } else {
                System.out.println("Command not  recognized.\n");
                System.out.println("Enter 'm' for Menu, else Enter your choice:");
            }
            System.out.print("Input >> ");
            command = consoleIn.nextLine();
        }

        System.out.println("\nExiting Store Management database. Have a great day!\n");// Exit
        consoleIn.close();

    }

    /*
     * Method that prints the menu to be displayed
     */
    private static void displayMenu() {
        System.out.println(
                "\tgcID <part of the name of customer> - Gets the Name of all the customer with 'part of the name of the customer' int their name");
        System.out.println(
                "\tsc - Show all the Countries along with their Country Code");
        System.out.println(
                "\tsRegions - Show all the Regions");
        System.out.println(
                "\tscategories - Show all the Categories");
        System.out.println(
                "\tsSubCategories - Show all the Sub-Categories along with their Category");
        System.out.println(
                "\tspc <country limit> - Stores and Profit by Country");
        System.out.println(
                "\ttopproducts <country code> - Top Product Holders by Category");
        System.out.println(
                "\trc <customerID>  - Customer Returned Item Count Analysis");
        System.out.println(
                "\tdp <category name> <minimum discount> - Discounted Products in Specific Category");
        System.out.println(
                "\tsd <orderID> - Shipping Details for Ordered Products");
        System.out.println(
                "\tss - Category Sales Summary");
        System.out.println(
                "\tsubcp - Sub-Category Product Inventory and Sales Overview");
        System.out.println(
                "\trp <custID> - Products Returned by Customer");
        System.out.println(
                "\trpr <region> - Product Returns by Region");
        System.out.println(
                "\tavgp <categoryID> - Average Product Price in Category");
        System.out.println(
                "\texceed <numProducts>- Order Shipping Mode Details for Orders Exceeding 7 Items");
        System.out.println(
                "\tlra <country limit> - Country-wise Largest Returned Order Amount");
        System.out.println("\ti - Initialize the database");
        System.out.println("\tm - Display the Menu.");
        System.out.println("\te - Exit the system.");

    }
}
