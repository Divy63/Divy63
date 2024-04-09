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
        String output;

        boolean cont = true;

        while (cont) {
            output = processCommand(db, cmd);
            System.out.println(output);
            cmd = nextNonEmptyLine(consoleIn, "Choice >> ");
            cont = cmd != null && !cmd.equalsIgnoreCase("e");
        }

        System.out.println("\nExiting Store Management interface. Have a great day!\n");
        consoleIn.close();

    }

    private static String processSPC(Database db, String[] args) {
        String response;

        if (args.length >= 2) {
            try {
                int limit = Integer.parseInt(args[1]);
                System.out.println(
                        "\nSearching the database for Profit across stores for top \'" + limit
                                + "\' country");
                System.out.println(
                        "--------------------------------------------------------------------------------------");
                response = db.storeProfitByCountry(limit);
            } catch (NumberFormatException nfe) {
                response = "Limit must be an integer.";
            }
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processTopProducts(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println("\nSearching the database for top most inventory holding store in " + args[1]
                    + " for each category:");
            System.out.println(
                    "-------------------------------------------------------------------------------------------------");
            response = db.topProducts(args[1]);
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processRC(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println(
                    "\nSearching database for number of items returned by customer with id \'" + args[1] + "\'");
            System.out
                    .println(
                            "--------------------------------------------------------------------------------------\n");
            response = db.returnedItemCount(args[1]);
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processDP(Database db, String[] args) {
        String response;
        if (args.length >= 4) {
            System.out.println(
                    "\nSearching database discounted items in category \"" + args[1] + " " + args[2]
                            + "\" with discount greater than or equal to " + Double.parseDouble(args[3])
                            + " % : ");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------");
            response = db.discountedProducts(args[1] + " " + args[2], Double.parseDouble(args[3]));
        } else if (args.length == 3) {
            System.out.println(
                    "\nSearching database discounted items in category \"" + args[1]
                            + "\" with discount greater than or equal to " + Double.parseDouble(args[2])
                            + " % : ");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------");
            response = db.discountedProducts(args[1], Double.parseDouble(args[2]));
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processSD(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println("\nSearching database for order with ID \'" + args[1] + "\'");
            System.out
                    .println(
                            "--------------------------------------------------------------------------------------");
            response = db.shippingDetails(args[1]);
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processSS(Database db) {
        System.out.println("\nSearching database for total sales of each category :");
        System.out
                .println("--------------------------------------------------------------------------------------");

        return db.salesSummaryByCategory();
    }

    private static String processSubCP(Database db) {
        System.out.println("\nSearching database for distinct products in each sub category :");
        System.out
                .println("--------------------------------------------------------------------------------------");
        return db.subCategoryInventory();
    }

    private static String processRP(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println(
                    "\nSearching database for returned products of customer with customer ID \"" + args[1]
                            + "\" :");
            System.out
                    .println(
                            "-------------------------------------------------------------------------------------------");
            response = db.returnedProducts(args[1]);
        } else {
            response = "Require an argument for this command";
        }
        return response;
    }

    private static String processRPR(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println(
                    "\nSearching database for returned products in region \"" + args[2] + "\" :");
            System.out
                    .println(
                            "-------------------------------------------------------------------------------------------");
            response = db.returnedByRegion(args[1]);
        } else {
            response = "Require an argument for this command";
        }
        return response;
    }

    private static String processAVGP(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println(
                    "\nSearching the database for Avergae Price of Products in category with category ID \""
                            + args[2] + "\" :");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            response = db.averagePrice(Integer.parseInt(args[1]));
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processExceed(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println(
                    "\nSearching the database for ship modes of order quantities greater than " + args[1] + " :");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            response = db.exceedXShipMode(Integer.parseInt(args[1]));
        } else {
            response = "Require an argument for this command";
        }
        return response;
    }

    private static String processLRA(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println(
                    "\nSearching the database for order with largest total for each country which were returned");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            response = db.largestReturnedAmount(Integer.parseInt(args[1]));
        } else {
            response = "Require an argument for this command";
        }

        return response;
    }

    private static String processSC(Database db) {
        System.out.println("\nSearching the database for countries");
        System.out.println(
                "------------------------------------------------");
        System.out.println("List of available countries:");
        return db.showCountries();
    }

    private static String processGCID(Database db, String[] args) {
        String response;
        if (args.length >= 2) {
            System.out.println("Searching the database for people with \"" + args[1] + "\" in their name");
            System.out.println(
                    "------------------------------------------------------------------------------");
            System.out.println("List of available people:");
            response = db.showPeople(args[1]);
        } else {
            response = "Require an argument for this command";
        }
        return response;
    }

    private static String processSCat(Database db) {
        System.out.println("\nSearching the database for categories");
        System.out.println(
                "------------------------------------------------");
        System.out.println("List of available categories with their  IDs:");
        return db.showCategories();
    }

    private static String processSubCat(Database db, String[] args) {
        String response;
        if (args.length >= 3) {
            System.out.println("\nSearching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available sub-categories with their IDs:");
            response = db.showSubCategories(args[1] + " " + args[2]);
        } else if (args.length == 2) {
            System.out.println("\nSearching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available sub-categories with their IDs:");
            response = db.showSubCategories(args[1]);
        } else {
            response = "Require an argument for this command";
        }
        return response;
    }

    private static String processSRegion(Database db) {
        System.out.println("\nSearching the database for Regions");
        System.out.println(
                "------------------------------------------------");
        return db.showRegions();
    }

    private static String processDatabase(Database db) {
        String message = db.initializeDatabase();
        if (message == null) {
            message = "Error in database initialization.";
        }
        return message;
    }

    private static String processCommand(Database db, String cmd) {
        String[] args = cmd.split("\\s+");
        // if (command.indexOf(" ") > 0)
        // arg = command.substring(command.indexOf(" ")).trim();

        if (args[0].equalsIgnoreCase("m")) {
            displayMenu();
            return "";
        }

        else if (args[0].equalsIgnoreCase("spc")) {
            return processSPC(db, args);
        }

        else if (args[0].equalsIgnoreCase("tp")) {
            return processTopProducts(db, args);
        }

        else if (args[0].equalsIgnoreCase("rc")) {
            return processRC(db, args);
        }

        else if (args[0].equalsIgnoreCase("dp")) {
            return processDP(db, args);
        }

        else if (args[0].equalsIgnoreCase("sd")) {
            return processSD(db, args);
        }

        else if (args[0].equalsIgnoreCase("ss")) {
            return processSS(db);
        }

        else if (args[0].equalsIgnoreCase("subcp")) {
            return processSubCP(db);
        }

        else if (args[0].equalsIgnoreCase("rp")) {
            return processRP(db, args);
        }

        else if (args[0].equalsIgnoreCase("rpr")) {
            return processRPR(db, args);
        }

        else if (args[0].equalsIgnoreCase("avgp")) {
            return processAVGP(db, args);
        }

        else if (args[0].equalsIgnoreCase("exceed")) {
            return processExceed(db, args);
        }

        else if (args[0].equalsIgnoreCase("lra")) {
            return processLRA(db, args);
        }

        else if (args[0].equalsIgnoreCase("sc")) {
            return processSC(db);
        }

        else if (args[0].equalsIgnoreCase("gcID")) {
            return processGCID(db, args);
        }

        else if (args[0].equalsIgnoreCase("scategories")) {
            return processSCat(db);
        }

        else if (args[0].equals("sSubCategories")) {
            return processSubCat(db, args);
        }

        else if (args[0].equals("sRegions")) {
            return processSRegion(db);
        }

        else if (args[0].equalsIgnoreCase("i")) {
            return processDatabase(db);
        }

        else {
            return "Invalid choice. Enter 'm' for Menu";
        }

    }

    /*
     * Method that prints the menu to be displayed
     */
    private static void displayMenu() {
        System.out.println(
                "\tgcID <part of the name of customer> - Gets the Name of all the customer with 'part of the name of the customer' int their name\n");
        System.out.println(
                "\tsc - Show all the Countries along with their Country Code\n");
        System.out.println(
                "\tsRegions - Show all the Regions\n");
        System.out.println(
                "\tscategories - Show all the Categories\n");
        System.out.println(
                "\tsSubCategories - Show all the Sub-Categories along with their Category\n");
        System.out.println(
                "\tspc <country limit> - Stores and Profit by Country\n");
        System.out.println(
<<<<<<< HEAD
                "\ttp <country code> - Top Product Holders by Category");
=======
                "\ttopproducts <country code> - Top Product Holders by Category\n");
>>>>>>> e99254ec4363954c8dc5a3eea258663468cd8368
        System.out.println(
                "\trc <customerID>  - Customer Returned Item Count Analysis\n");
        System.out.println(
                "\tdp <category name> <minimum discount> - Discounted Products in Specific Category\n");
        System.out.println(
                "\tsd <orderID> - Shipping Details for Ordered Products\n");
        System.out.println(
                "\tss - Category Sales Summary\n");
        System.out.println(
                "\tsubcp - Sub-Category Product Inventory and Sales Overview\n");
        System.out.println(
                "\trp <custID> - Products Returned by Customer\n");
        System.out.println(
                "\trpr <region> - Product Returns by Region\n");
        System.out.println(
                "\tavgp <categoryID> - Average Product Price in Category\n");
        System.out.println(
                "\texceed <numProducts>- Order Shipping Mode Details for Orders Exceeding X Items\n");
        System.out.println(
                "\tlra <country limit> - Country-wise Largest Returned Order Amount\n");
        System.out.println("\ti - Initialize the database\n");
        System.out.println("\tm - Display the Menu.\n");
        System.out.println("\te - Exit the system.\n");

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
