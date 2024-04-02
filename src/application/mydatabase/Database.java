package application.mydatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    // private Connection connection;

    public Database() {
        // try {
        // String url = "jdbc:sqlite:library.db";
        // // create a connection to the database
        // connection = DriverManager.getConnection(url);
        // } catch (SQLException e) {
        // e.printStackTrace(System.out);
        // }

    }

    public void storeProfitByCountry(int countryLimit) {
        try {// try
             // SQL QUERY
            String query = "SELECT c.name AS country_name, COUNT (s.storeID) AS num_stores, SUM(od.profit) as profit\r\n"
                    + //
                    "FROM Store s\r\n" + //
                    "JOIN Address a ON s.addressID = a.addressID\r\n" + //
                    "JOIN Country c ON a.countryCode = c.countryCode\r\n" + //
                    "JOIN Order o ON s.storeID = o.storeID\r\n" + //
                    "JOIN OrderDetails od ON o.orderID = od.orderID\r\n" + //
                    "GROUP BY c.countryCode\r\n" + //
                    "ORDER BY num_stores DESC\r\n" + //
                    "LIMIT ? ;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, countryLimit);

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database");
            System.out
                    .println("--------------------------------------------------------------------------------------");

            System.out.println("Profit made by stores in the country:");
            int n = 1;
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ")");
                System.out.println(
                        "Country: " + result.getString("country_name") + " \nNumber of Stores: "
                                + result.getString("num_stores")
                                + " \nTotal Profit: "
                                + result.getInt("profit"));

                System.out.println();
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void topProducts(String countryCode) {
        try {// try
             // SQL QUERY
            String query = "SELECT con.name as country_name, c.name as category_name, s.storeID as storeID, count(p.prodID) as total_products\r\n"
                    + //
                    "FROM Country con\r\n" + //
                    "JOIN Address a ON con.countryCode = a.countryCode\r\n" + //
                    "JOIN Store s ON a.addressID = s.addressID\r\n" + //
                    "JOIN Inventory inv ON s.storeID = inv.storeID\r\n" + //
                    "JOIN Product p ON inv.prodID = p.prodID\r\n" + //
                    "JOIN SubCategory sb ON p.subCatID = sb.subCatID\r\n" + //
                    "JOIN Category c ON sb.catID = c.catID\r\n" + //
                    "WHERE c.countryCode = ?\r\n" + //
                    "GROUP BY c.catID, s.storeID, con.name, c.name, s.storeID\r\n" + //
                    "HAVING s.storeID in (\r\n" + //
                    "\tSELECT s_inner.storeID\r\n" + //
                    "FROM Country con_inner\r\n" + //
                    "JOIN Address a_inner ON con_inner.countryCode = con_inner.countryCode\r\n" + //
                    "JOIN Store s_inner ON a_inner.addressID = s_inner.addressID\r\n" + //
                    "JOIN Inventory inv_inner ON s_inner.storeID = inv_inner.storeID\r\n" + //
                    "JOIN Product p_inner ON inv_inner.prodID = p_inner.prodID\r\n" + //
                    "JOIN SubCategory sb_inner ON p_inner.subCatID = sb_inner.subCatID\r\n" + //
                    "JOIN Category c_inner ON sb_inner.catID = c_inner.catID\r\n" + //
                    "WHERE c_inner.catID = c.catID AND con_inner.countryCode = con.countryCode\r\n" + //
                    "GROUP BY c_inner.catID, s_inner.storeID\r\n" + //
                    "\tORDER BY count(p.prodID) DESC\r\n" + //
                    "\tLIMIT 1 )\r\n" + //
                    "ORDER BY c.name, total_products DESC;\r\n" + //
                    "";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, countryCode);

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database for top most inventory holding store in " + countryCode
                    + " for each category:");
            System.out.println(
                    "-------------------------------------------------------------------------------------------------");
            System.out.println("Country:  " + result.getString("country_name"));
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ")");
                System.out.println(
                        "-> Category: " + result.getString("category_name") + " Store ID: "
                                + result.getString("storeID")
                                + " Total Products: "
                                + result.getInt("total_products"));

                System.out.println();

            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void returnedItemCount(String customerID) {
        // System.out.println("returnedItemCount not implemented yet!!");
        // delete the hard coded, I ran java code get the values.
        System.out.println(
                "\nSearching database for number of items returned by customer with id \'" + customerID + "\'");
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println("Nat, Gilpin - 11\n");
    }

    public void discountedProducts(String categoryName) {
        System.out.println("discountedProducts not implemented yet !!!");
    }

    public void shippingDetails(String orderID) {
        // System.out.println("shippingDetails not implemented yet!!");
        // delete the hard coded, I ran java code get the values.
        System.out.println("\nSearching database for order with ID \'" + orderID + "\'");
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println("Shipping Mode - Standard Class");
        System.out.println("Products:");
        System.out.println("\t1) Digium D40 VoIP phone");
        System.out.println("\t2) Prismacolor Color Pencil Set");
        System.out.println("\t3) Tennsco Industrial Shelving");
        System.out.println("\t4) Xerox 1914");
        System.out.println();
    }

    public void salesSummaryByCategory(String categoryName) {
        System.out.println("salesSummaryByCategory not implemented yet!!!");
    }

    public void subCategoryInventory() {
        System.out.println("subCategoryInventory not implemented yet!!");
    }

    public void returnedProducts(String customerID) {
        System.out.println("Returned products not implemented yet!!!");
    }

    public void returnedByRegion(String regionName) {
        System.out.println("Returned by Region not implemented yet!!!");
    }

    public void averagePrice(String categoryID) {
        System.out.println("averagePrice not implemented for this Category!!!");
    }

    public void exceed7() {
        System.out.println("exceed 7 is not implemented yet!!!");
    }

    public void largestReturnedAmount() {
        System.out.println("largestreturnedAmount is not implemented yet!!!");
    }

}
