import java.io.IOException;
import java.util.Scanner;

public class Interface {
    public static void main(String[] args) {

    }

    private static void simulate(){
        try{
            Scanner consoleIn=new Scanner(System.in);//Scanner that takes input from console
            System.out.println();//Getting on a new line
            System.out.println("Welcome to Store Management!");//label
            
            //Menu for user's selection of operation
            displayMenu();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    /*
     * Method that prints the menu to be displayed
     */
    private static void displayMenu(){
        System.out.println("Enter your choice to get the data from the following menu:");
        System.out.println("\t1.Stores and Profit by Country- It gives you the profit made in 'N' countries with highest number of stores.");
        System.out.println("\t2.Top Product Holders by Category - It gives list of stores from a given country which hold most amount of products.");
        System.out.println("\t3.Customer Returned Item Count Analysis-It gives  count of items returned by each customer.");
        System.out.println("\t4.Discounted Products in Specific Category-It gives list of discounted products given a category");
        
    }
}
