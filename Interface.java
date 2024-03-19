import java.io.IOException;
import java.util.Scanner;

public class Interface {
    public static void main(String[] args) {
        simulate();
    }

    private static void simulate() {

        Scanner consoleIn = new Scanner(System.in);// Scanner that takes input from console
        System.out.println();// Getting on a new line
        System.out.println("Welcome to Store Management!");// label

        // Menu for user's selection of operation
        displayMenu();

    }

    /*
     * Method that prints the menu to be displayed
     */
    private static void displayMenu() {
        System.out.println("Enter your choice to get the specific data from the following menu:");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "1.Stores and Profit by Country- It gives you the profit made in 'N' countries with highest number of stores.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "2.Top Product Holders by Category - It gives list of stores from a given country which hold most amount of products.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "3.Customer Returned Item Count Analysis-It gives  count of items returned by each customer.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "4.Discounted Products in Specific Category-It gives list of discounted products given a category.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "5.Shipping Details for Ordered Products - It gives list of products in an order and its shipping mode.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("6.Category Sales Summary - It gives toal sales amount of a provided category.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "7.Sub-Category Product Inventory and Sales Overview -It  will show you all products in each sub category along with number of products sold given a subcategory.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "8.Products Returned by Customer - It gives list of products returned by a customer provided a customer ID.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "9.Product Returns by Region - It gives list of products returned by region for a provided region.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "10.Average Product Price in Category - It gives average price of products in a specified category");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "11.Order Shipping Details for Orders Exceeding 7 Items - It gives order shipping details and average time order took to deliver.");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "12.Country-wise Largest Returned Order Amount - It gives  details about country wise largest returned order amount.");
    }
}
