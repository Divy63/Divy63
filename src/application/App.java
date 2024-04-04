package application;

import application.mydatabase.Database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        try{
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
                    db.discountedProducts(parts[1], Integer.parseInt(parts[2]));
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
                db.salesSummaryByCategory();
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
                if (parts.length >= 2) {
                    db.exceedXShipMode(Integer.parseInt(parts[3]));
                } else {
                    System.out.println("Require an argument for this command");
                }
            } else if (parts[0].equals("lra")) {
                if (parts.length >= 2) {
                    db.largestReturnedAmount(Integer.parseInt(parts[1]));
                } else {
                    System.out.println("Require an argument for this command");
                }
            } else if (parts[0].equals("sc")) {
                db.showCountries();

            } else if (parts[0].equals("gcID")) {
                if (parts.length >= 2) {
                    db.showPeople(parts[2]);
                } else {
                    System.out.println("Require an argument for this command");
                }

            } else if (parts[0].equals("scategories")) {
                db.showCategories();
            } else if (parts[0].equals("sSubCategories")) {
                db.showSubCategories();
            } else if (parts[0].equals("sRegions")) {
                db.showRegions();
            } else if (parts[0].equals("i")) {
                db.initializeDatabase();
            }
            else {
                System.out.println("Enter 'm' for Menu, else Enter your choice:");
            }
            System.out.print("Choice >> ");
            command = consoleIn.nextLine();
        }

        System.out.println("\nExiting Store Management interface. Have a great day!\n");
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
                "\texceed - Order Shipping Mode Details for Orders Exceeding 7 Items");
        System.out.println(
                "\tlra <country limit> - Country-wise Largest Returned Order Amount");
        System.out.println("\ti - To initialize the database");
        System.out.println("\tm - Display the Menu.");
        System.out.println("\te - Exit the system.");

    }
}

>>>>>>> ae5e34c5ca1179744bc3efa10f81a055bbe767b3
