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
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String reponse;
        Database db = new Database();
        reponse = db.startup();

        if (reponse != null) {
            System.out.println(reponse);
        } else {
            simulate(db);
        }
        
        System.out.println("\nEnd of processing\n");
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
        String cmd = nextNonEmptyLine(consoleIn, "To get started, ENTER 'm' for Menu: ");

        String[] parts;
        boolean cont = true;
        String message;

        while (cont) {
            processCommand(db, cmd);
            cmd = nextNonEmptyLine(consoleIn, "Choice >> ");
            cont = cmd != null && !cmd.equalsIgnoreCase("e");
        }

        System.out.println("\nExiting Store Management interface. Have a great day!\n");
        consoleIn.close();

    }
    
    private static void processCommand(Database db, String cmd){
        String[] parts = cmd.split("\\s+");
        // if (command.indexOf(" ") > 0)
        // arg = command.substring(command.indexOf(" ")).trim();

        if (parts[0].equalsIgnoreCase("m"))
            displayMenu();
        else if (parts[0].equalsIgnoreCase("spc")) {
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

        else if (parts[0].equalsIgnoreCase("topproducts")) {
            if (parts.length >= 2) {
                db.topProducts(parts[1]);
            } else {
                System.out.println("Require an argument for this command");
            }
        }

        else if (parts[0].equalsIgnoreCase("rc")) {
            if (parts.length >= 2) {
                db.returnedItemCount(parts[1]);
            } else {
                System.out.println("Require an argument for this command");
            }
        }

        else if (parts[0].equals("dp")) {
            if (parts.length >= 4) {
                System.out.println(parts[3]);
                db.discountedProducts(parts[1] + " " + parts[2], Double.parseDouble(parts[3]));
            } else if (parts.length == 3) {
                db.discountedProducts(parts[1], Double.parseDouble(parts[2]));
            } else {
                System.out.println("Require an argument for this command");
            }
        }

        else if (parts[0].equalsIgnoreCase("sd")) {
            if (parts.length >= 2)
                db.shippingDetails(parts[1]);
            else
                System.out.println("Require an argument for this command");
        }

        else if (parts[0].equalsIgnoreCase("ss")) {
            db.salesSummaryByCategory();
        }

        else if (parts[0].equalsIgnoreCase("subcp")) {
            db.subCategoryInventory();
        }

        else if (parts[0].equalsIgnoreCase("rp")) {
            if (parts.length >= 2) {
                db.returnedProducts(parts[1]);
            } else {
                System.out.println("Require an argument for this command");
            }
        } else if (parts[0].equalsIgnoreCase("rpr")) {
            if (parts.length >= 2) {
                db.returnedByRegion(parts[1]);
            } else {
                System.out.println("Require an argument for this command");
            }
        } else if (parts[0].equalsIgnoreCase("avgp")) {
            if (parts.length >= 2) {
                db.averagePrice(Integer.parseInt(parts[1]));
            } else {
                System.out.println("Require an argument for this command");
            }
        } else if (parts[0].equalsIgnoreCase("exceed")) {
            if (parts.length >= 2) {
                db.exceedXShipMode(Integer.parseInt(parts[3]));
            } else {
                System.out.println("Require an argument for this command");
            }
        } else if (parts[0].equalsIgnoreCase("lra")) {
            if (parts.length >= 2) {
                db.largestReturnedAmount(Integer.parseInt(parts[1]));
            } else {
                System.out.println("Require an argument for this command");
            }
        } else if (parts[0].equalsIgnoreCase("sc")) {
            System.out.println("\nSearching the database for countries");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available countries:");
            db.showCountries();

        } else if (parts[0].equalsIgnoreCase("gcID")) {
            if (parts.length >= 2) {
                System.out.println("Searching the database for people with \"" + parts[1] + "\" in their name");
                System.out.println(
                        "------------------------------------------------------------------------------");
                System.out.println("List of available people:");
                String message = db.showPeople(parts[1]);
                System.out.println(message);
            } else {
                System.out.println("Require an argument for this command");
            }

        } else if (parts[0].equalsIgnoreCase("scategories")) {
            System.out.println("\nSearching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available categories with their  IDs:");
            db.showCategories();
        } else if (parts[0].equals("sSubCategories")) {
            if (parts.length >= 3) {
                db.showSubCategories(parts[1] + " " + parts[2]);
            } else if (parts.length == 2) {
                db.showSubCategories(parts[1]);
            } else {
                System.out.println("Require an argument for this command");
            }
        } else if (parts[0].equals("sRegions")) {
            db.showRegions();
        } else if (parts[0].equalsIgnoreCase("i")) {
            String message = db.initializeDatabase();
            if (message != null) {
                System.out.println(message);
            }
        } else {
            System.out.println("Invalid choice. Enter 'm' for Menu");
        }

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

    /**
     * Helper method for Scanner to skip over empty lines.
     * Print the prompt on each line of input.
     */
    private static String nextNonEmptyLine(Scanner in, String prompt) {
        String line = null;

        System.out.print(prompt);
        while (line == null && in.hasNextLine()) {
            line = in.nextLine();
            if (line.trim().length() == 0) {
                line = null;
                System.out.print(prompt);
            }
        }

        return line;
    }
}
