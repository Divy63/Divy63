package application.mydatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private Connection connection;
    private static final String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public Database() throws SQLException {

        Properties prop = new Properties();
        String cfgFileName = "src/application/mydatabase/auth.cfg";

        try {
            FileInputStream configFile = new FileInputStream(cfgFileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: config file not found.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("An error occurred: could not read config file.");
            System.exit(1);
        }

        String username = (prop.getProperty("username"));
        String password = (prop.getProperty("password"));

        // TODO: uranium connection (VPN or campus)
        String url = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + username + ";"
                + "password= " + password + ";"
                + "encrypt=false;trustServerCertificate=false;loginTimeout=30;";

        // create a connection to the database
        this.connection = DriverManager.getConnection(url);
        System.out.println("Connection established successfully");

        // TODO: this.initializeDatabase();
        // TODO: this.readInputData();
    }

    public void initializeDatabase() {

        dropAllTables();

        try {
            createAllTables();
            readInputData();
        } catch (SQLException e) {
            System.out.println("Error occured while initializing the database\n\nDROPING ALL OF THE DATABASE");
            dropAllTables();
        }
    }

    private void createAllTables() throws SQLException {
        this.connection.createStatement().executeUpdate("CREATE TABLE customer("
                + "custID VARCHAR(8) PRIMARY KEY,"
                + "fname TEXT NOT NULL,"
                + "lname TEXT NOT NULL)");

        this.connection.createStatement().executeUpdate("CREATE TABLE product("
                + "prodID VARCHAR(18) PRIMARY KEY,"
                + "name TEXT,"
                + "price DECIMAL NOT NULL,"
                + "subCatID VARCHAR(7) REFERENCES subcategory(subCatID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE subcategory("
                + "subCatID VARCHAR(7) PRIMARY KEY,"
                + "name TEXT,"
                + "catID INTEGER REFERENCES category(catID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE category("
                + "catID INTEGER PRIMARY KEY,"
                + "name TEXT)");

        this.connection.createStatement().executeUpdate("CREATE TABLE store("
                + "storeID INTEGER PRIMARY KEY autoincrement,"
                + "addressID INTEGER REFERENCES address(addressID),"
                + "regionID INTEGER REFERENCES region(regionID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE region("
                + "regionID INTEGER PRIMARY KEY,"
                + "regionName TEXT,"
                + "managerID INTEGER REFERENCES manager(managerID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE manager("
                + "managerID INTEGER PRIMARY KEY,"
                + "fname TEXT NOT NULL,"
                + "lname TEXT NOT NULL)");

        this.connection.createStatement().executeUpdate("CREATE TABLE country("
                + "countryCode VARCHAR(3) PRIMARY KEY,"
                + "name TEXT NOT NULL)");

        this.connection.createStatement().executeUpdate("CREATE TABLE address("
                + "addressID INTEGER PRIMARY KEY,"
                + "city TEXT NOT NULL,"
                + "state TEXT NOT NULL,"
                + "countryCode VARCHAR(3) REFERENCES country(countryCode))");
        this.connection.createStatement().executeUpdate("CREATE TABLE order("
                    + "orderID VARCHAR(11) PRIMARY KEY,"
                    + "shipDate DATE NOT NULL,"
                    + "shipMode VARCHAR(20) NOT NULL,"
                    + "orderDate DATE NOT NULL,"
                    + "isReturned BIT"
                    + "storeID INTEGER FOREIGN KEY REFERENCES store(storeID));");
         this.connection.createStatement().executeQuery("CREATE TABLE orderdetails("
                    + "orderID VARCHAR(11) FOREIGN KEY REFERENCES order(orderID) NOT NULL, "
                    + "prodID VARCHAR(18) FOREIGN KEY REFERENCES product(prodID) NOT NULL,"
                    + "sales DECIMAL  NOT NULL,"
                    + "quantity INT  NOT NULL,"
                    + "discount DECIMAL DEFAULT 0,"
                    + "profit DECIMAL"
                    + "PRIMARY KEY(orderID,prodID));");
            this.connection.createStatement().executeQuery("CREATE TABLE inventory("
                    + "storeID INTEGER FOREIGN KEY store(storeID),"
                    + "prodID VARCHAR(18) FOREIGN KEY REFERENCES product(prodID)"
                    + "PRIMARY KEY(storeID,podID));");
    }

    private void readInputData(String filename) throws SQLException, IOException {

        // TODO: while loop for all data-files
        // BufferedReader br = new BufferedReader(new FileReader(""));
        // br.readLine();

        // // TODO: read lines, prepare statements

        // br.close();

    }

    private void insertIntoCustomer() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                    inputArr[0], inputArr[1], inputArr[2]);
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
        }
        br.close();
    }

    private void insertIntoProduct() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into product values(%s, %s, %s)",
                    inputArr[0], inputArr[1], inputArr[2]);
        }
        br.close();
    }

    private void insertIntoSubCat() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoCat() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoStore() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoRegion() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoManager() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoCountry() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoAddress() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoOrder() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoOrderDetails() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    private void insertIntoInventory() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(""));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            sql = String.format("insert into customer values(%s, %s, %s)",
                                inputArr[0], inputArr[1], inputArr[2]);
        }
    }

    public void dropAllTables() {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DROP TABLE IF EXISTS customer;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS product;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS address;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS store;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS country;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS order;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS orderdetails;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS inventory;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS subcategory;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS category;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS region;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS manager;");
            pstmt.executeUpdate();

        } catch (SQLException se) {
            System.out.println("Error while deleting the database");
        }
    }

    public void showPeople(String partOfName) {
        try {// try
             // SQL QUERY
            String query = "SELECT c.fname as First, c.lnamed as Last, c.custID as custID FROM Customer c WHERE First LIKE ? OR Last LIKE ?;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, partOfName);
            pstmt.setString(2, partOfName);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database for people with \"" + partOfName + "\" in their name");
            System.out.println(
                    "------------------------------------------------------------------------------");
            System.out.println("List of available people:");
            int n = 1;
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ") ");
                System.out.println(
                        "CustomerID: " + result.getString("custID") + ", Name: " + result.getString("First")
                                + result.getString("Last"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }

    }

    public void showCountries() {
        try {// try
             // SQL QUERY
            String query = "SELECT countryCode,name from Country";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database for countries");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available countries:");
            int n = 1;
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ") ");
                System.out.println(
                        "Country Name" + result.getString("name") + ", Country Code: "
                                + result.getString("name"));

                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void showCategories() {
        try {// try
             // SQL QUERY
            String query = "SELECT catID,name from Category";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available categories with their  IDs:");
            int n = 1;
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ") ");
                System.out.println(
                        "Category Name: " + result.getString("name") + ", Category ID: "
                                + result.getString("catID"));

                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void showSubCategories() {
        try {// try
             // SQL QUERY
            String query = "SELECT subCatID,name,c.name as category from SubCategory NATURAL JOIN Category c";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available sub-categories with their IDs:");
            int n = 1;
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ") ");
                System.out.println(
                        "Sub-Category ID: " + result.getString("subCatID") + ", Sub-Category Name: "
                                + result.getString("name") + ", Category Name: " + result.getString("category"));

                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
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
                    "TOP ? ;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, countryLimit);

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
                    "\tTOP 1 )\r\n" + //
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
            int n = 1;
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
        try {// try
             // SQL QUERY
            String query = "SELECT COUNT(*) AS returned_items_count, c.fname as First,c.lname as Last\r\n" + //
                    "FROM Customer c\r\n" + //
                    "JOIN Orders o ON c.custID = o.custID\r\n" + //
                    "WHERE c.custID = '?'\r\n" + //
                    "AND o.isReturned = 1;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, customerID);

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching database for number of items returned by customer with id \'" + customerID + "\'");
            System.out
                    .println("--------------------------------------------------------------------------------------");
            System.out.println(result.getString("Last") + ",  " + result.getString("First") + ": "
                    + result.getString("returned_item_count"));

            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
        // // System.out.println("returnedItemCount not implemented yet!!");
        // // delete the hard coded, I ran java code get the values.
        // System.out.println(
        // "\nSearching database for number of items returned by customer with id \'" +
        // customerID + "\'");
        // System.out.println("--------------------------------------------------------------------------------------");
        // System.out.println("Nat, Gilpin - 11\n");
    }

    public void discountedProducts(String categoryName, int discount) {
        try {// try
             // SQL QUERY
            String query = "SELECT p.name as product_name, p.price as price, p.discount as discounts\r\n" + //
                    "FROM Product p\r\n" + //
                    "INNER JOIN Category c ON p.categoryID = c.id  \r\n" + //
                    "WHERE p.discount > ? \r\n" + //
                    "AND c.name = '?';";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(2, categoryName);
            pstmt.setInt(1, discount);

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching database discounted items in category" + categoryName
                            + " with discount greater than or equal to " + discount + ": ");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------");
            int n = 1;
            while (result.next()) {
                System.out.println(n + ") " + "Product Name: " + result.getString("product_name") + ", Price:"
                        + result.getInt("price") + ", " + result.getInt("discounts") + "% off.");
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void shippingDetails(String orderID) {
        try {// try
             // SQL QUERY
            String query = "SELECT p.name as name, p.price as price, o.shipMode as shipMode\r\n" + //
                    "FROM Product p\r\n" + //
                    "INNER JOIN OrderDetails c ON p.prodID = c.prodID \r\n" + //
                    "INNER JOIN Order o ON c.orderID = o.orderID \r\n" + //
                    "WHERE o.orderID = ?;\r\n" + //
                    "";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, orderID);

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching database for order with ID \'" + orderID + "\'");
            System.out
                    .println("--------------------------------------------------------------------------------------");
            System.out.println("Shipping Mode -" + result.getString("shipMode"));
            int n = 1;
            while (result.next()) {
                System.out.println("\t" + n + ") " + result.getString("name"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
        // // System.out.println("shippingDetails not implemented yet!!");
        // // delete the hard coded, I ran java code get the values.
        // System.out.println("\nSearching database for order with ID \'" + orderID +
        // "\'");
        // System.out.println("--------------------------------------------------------------------------------------");
        // System.out.println("Shipping Mode - Standard Class");
        // System.out.println("Products:");
        // System.out.println("\t1) Digium D40 VoIP phone");
        // System.out.println("\t2) Prismacolor Color Pencil Set");
        // System.out.println("\t3) Tennsco Industrial Shelving");
        // System.out.println("\t4) Xerox 1914");
        // System.out.println();
    }

    public void salesSummaryByCategory() {
        try {// try
             // SQL QUERY
            String query = "SELECT Category.name AS category_name,SUM(OrderDetails.sales) AS total_sales\r\n" + //
                    "FROM Category\r\n" + //
                    "JOIN SubCategory ON Category.catID = SubCategory.catID\r\n" + //
                    "JOIN Product ON SubCategory.subCatID = Product.subCatID\r\n" + //
                    "JOIN OrderDetails ON Product.prodID = OrderDetails.prodID\r\n" + //
                    "GROUP BY Category.name;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching database for total sales of each category :");
            System.out
                    .println("--------------------------------------------------------------------------------------");
            int n = 1;
            while (result.next()) {
                System.out.println("\t" + n + ") " + result.getString("category_name") + ", Total Sale: "
                        + result.getInt("total_sales"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void subCategoryInventory() {
        try {// try
             // SQL QUERY
            String query = "SELECT c.name as category, sb.name as subcategory, count(prodID) AS num_products, SUM(od.quantity) AS total_quantity_sold\r\n"
                    + //
                    "FROM products p\r\n" + //
                    "JOIN SubCategory sb ON p.subCatID = sb.subCatID\r\n" + //
                    "JOIN Category c ON sb.catID = c.catID\r\n" + //
                    "JOIN OrderDetails od ON p.prodID = od.prodID\r\n" + //
                    "JOIN Order o ON od.orderID = o.orderID\r\n" + //
                    "GROUP BY sb.subCatID, od.orderID, c.name, sb.name\r\n" + //
                    "HAVING o.isReturned = 0\r\n" + //
                    "ORDER BY c.name, num_products desc;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching database for distinct products in each sub category :");
            System.out
                    .println("--------------------------------------------------------------------------------------");
            int n = 1;
            while (result.next()) {
                System.out
                        .println("\t" + n + ") Number of Products:" + result.getInt("num_products") + ", Sub-Category: "
                                + result.getString("subcategory") + ", Category: "
                                + result.getInt("category"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void returnedProducts(String customerID) {
        try {// try
             // SQL QUERY
            String query = "SELECT DISTINCT p.name AS prod_name\r\n" + //
                    "FROM Products p\r\n" + //
                    "JOIN OrderDetails od ON p.prodID = od.prodID\r\n" + //
                    "JOIN Order o ON od.orderID = o.orderID\r\n" + //
                    "JOIN Customer c ON o.custID=o.custID WHERE c.custID='?' and o.isReturned=1;\r\n";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, customerID);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching database for returned products of customer with customer ID \"" + customerID + "\" :");
            System.out
                    .println(
                            "-------------------------------------------------------------------------------------------");
            System.out.println("Products returned by customer with customer ID \"" + customerID + "\": ");
            int n = 1;
            while (result.next()) {
                System.out
                        .println("\t" + n + ")" + result.getString("prod_name"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void showRegions() {
        try {// try
             // SQL QUERY
            String query = "SELECT regionID,name from Region";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("Searching the database for Regions");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available regions:");
            int n = 1;
            // Printing the results of query
            while (result.next()) {
                System.out.print(n + ") ");
                System.out.println(
                        "Region Name" + result.getString("name") + ", Region Code: "
                                + result.getString("regionID"));

                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void returnedByRegion(String regionName) {
        try {// try
             // SQL QUERY
            String query = "SELECT DISTINCT p.name AS prod_name\r\n" + //
                    "FROM Products p\r\n" + //
                    "JOIN OrderDetails od ON p.prodID = od.prodID\r\n" + //
                    "JOIN Order o ON od.orderID = o.orderID\r\n" + //
                    "JOIN Store s ON o.storeID=s.storeID\r\n" + //
                    "JOIN Region r ON s.regionID=r.regionID \r\n" + //
                    "WHERE r.name='?' and o.isReturned=1;\r\n";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, regionName);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching database for returned products in region \"" + regionName + "\" :");
            System.out
                    .println(
                            "-------------------------------------------------------------------------------------------");
            System.out.println("Products returned in region\"" + regionName + "\": ");
            int n = 1;
            while (result.next()) {
                System.out
                        .println("\t" + n + ")" + result.getString("prod_name"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
        System.out.println("Returned by Region not implemented yet!!!");
    }

    public void averagePrice(String categoryID) {
        try {// try
             // SQL QUERY
            String query = "SELECT AVG(p.price) AS averagePrice,c.name as name\r\n" + //
                    "FROM Product p\r\n" + //
                    "JOIN SubCategory s ON p.subCatID=s.subCatID\r\n" + //
                    "JOIN Category c ON s.catID = c.catID\r\n" + //
                    "WHERE c.catID = CID;\r\n";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "Searching the database for Avergae Price of Products in category \"" + categoryID + "\" :");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            System.out.println("Average price of product for category \"" + categoryID + "\" :");
            // Printing the results of query
            while (result.next()) {
                System.out.println(
                        "Category:" + result.getString("name") + " - "
                                + result.getString("averagePrice"));
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    public void exceedXShipMode(int x) {
        try {// try
             // SQL QUERY
            String query = "SELECT o.shipMode as ship_mode, AVG(julianday(o.shipDate) - julianday(o.orderDate)) AS avg_days_to_ship,o.orderID as orderID\r\n"
                    + //
                    "FROM Order o\r\n" + //
                    "JOIN OrderDetails od ON o.orderID = od.orderID \r\n" +
                    "GROUP BY o.shipMode, od.orderID\r\n" +
                    "HAVING SUM(od.quantity) > 7;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, x);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "Searching the database for ship modes of order  quantities greater than " + x + " :");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            System.out.println("Ship Modes of  orders with quantity greater than " + x + ": ");
            // Printing the results of query
            int n = 1;
            while (result.next()) {
                System.out.println(
                        "\t" + n + ") " + result.getString("orderID") + " - "
                                + "Ship Mode: " + result.getString("ship_mode") + ", Average Shipping Time: "
                                + result.getString("avg_days_to_ship"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
        System.out.println("exceed 7 is not implemented yet!!!");
    }

    public void largestReturnedAmount(int x) {
        try {// try
             // SQL QUERY
            String query = "SELECT con.name as country_name, a.city, a.state, cust.fName, cust.lName, MAX(od_sales.order_total) AS max_total\r\n"
                    + //
                    "FROM Country con\r\n" + //
                    "LEFT JOIN Address a ON a.countryCode = con.countryCode\r\n" + //
                    "JOIN Store s ON a.addressID = s.addressID\r\n" + //
                    "JOIN Orders o ON s.storeID = o.storeID\r\n" + //
                    "JOIN Customer cust ON o.custID = cust.custID\r\n" + //
                    "JOIN (\r\n" + //
                    "SELECT od.orderID, SUM(od.sales) as order_total\r\n" + //
                    "\tFROM OrderDetails od\r\n" + //
                    "\tGROUP BY od.orderID \r\n" + //
                    ") AS od_sales ON o.orderID = od_sales.OrderID\r\n" + //
                    "WHERE o.isReturned = 1\r\n" + //
                    "GROUP BY con.name, o.orderID, cust.fName, cust.lName, a.city, a.state\r\n" + //
                    "TOP ?\r\n" + //
                    "";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, x);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "Searching the database for order with largest total for each country which were returned");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            System.out.println("Largest order total by country which were returned are: ");
            // Printing the results of query
            int n = 1;
            while (result.next()) {
                System.out.println(
                        "\t" + n + ") " + result.getString("country_name") + " - "
                                + result.getString("max_total"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("Query executed!");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

}
