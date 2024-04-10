/**
 * Database.java
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
 *          REMARKS: Database class that helps connecting to database on
 *          uranium.cs.umanitoba.ca and perform data analysis
 */
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
    private final String cfgFilePath = "auth.cfg";

    public Database() {

    }

    /**
     * This method will establish the connection to the uranium database.
     * If you are not on campus wifi, you must be connect to UofM VPN
     * Driver file according to the java version is requied, auth.cfg file to the
     * root director this project is also required
     * 
     * @return null on success, or an erro message if something went wrong
     */
    public String startup() {
        String response = null;

        Properties prop = new Properties();

        try {
            FileInputStream configFile = new FileInputStream(cfgFilePath);
            prop.load(configFile);
            configFile.close();
            final String username = (prop.getProperty("username"));
            final String password = (prop.getProperty("password"));

            String url = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                    + "database=cs3380;"
                    + "user=" + username + ";"
                    + "password= " + password + ";"
                    + "encrypt=false;trustServerCertificate=false;loginTimeout=30;";

            this.connection = DriverManager.getConnection(url);

        } catch (FileNotFoundException fnf) {
            response = "\nAn error occurred: config file not found.";
        } catch (IOException io) {
            response = "\nAn error occurred: could not read config file.";
        } catch (SQLException se) {
            response = "\nAn error occured: Failed to establish connection to database";
        }

        return response;
    }

    public String initializeDatabase() {
        String response;

        response = dropAllTables();

        if (response == null) {
                response = createAllTables();
        }

        return response;
    }

    private String createAllTables() {
        String response = null;

        try {
            insertIntoCustomer();
            insertIntoManager();
            insertIntoRegion();
            insertIntoCountry();
            insertIntoAddress();
            insertIntoStore();
            insertIntoCat();
            insertIntoSubCat();
            insertIntoProduct();
            insertIntoOrder();
            insertIntoInventory();
            insertIntoOrderDetails();
        } catch (SQLException se) {
            response = se.getMessage();
            response += "\n\tErasing the whole database";
            dropAllTables();
        } catch (IOException io) {
            response = io.getMessage();
            response += "\n\tErasing the whole database";
            dropAllTables();
        }

        return response;

    }

    /**
     * Method that inserts data into Customer table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoCustomer() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Customer("
                    + "custID VARCHAR(24) PRIMARY KEY,"
                    + "fname VARCHAR(MAX) NOT NULL,"
                    + "lname VARCHAR(MAX) NOT NULL)");
            
            BufferedReader br = new BufferedReader(new FileReader("final-data-files/customers.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);

                // sql string
                sql = String.format(
                        "insert into customer values (\"%s\", \"%s\", \"%s\")",
                        inputArr[0], inputArr[1], inputArr[2]);

                sql = "insert into customer values (?, ?, ?)";
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, inputArr[0]);
                pstmt.setString(2, inputArr[1]);
                pstmt.setString(3, inputArr[2]);
                pstmt.executeUpdate();// executing statement
            }
            br.close();
            System.out.println("Customer table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read customers.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into customer table");
        }
    }

    /**
     * Method that inserts data into Product table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoProduct() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Product("
                    + "prodID VARCHAR(24) PRIMARY KEY,"
                    + "name VARCHAR(MAX),"
                    + "price FLOAT NOT NULL,"
                    + "subCatID VARCHAR(24) REFERENCES SubCategory(subCatID))");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/products.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                sql = String.format("insert into product values(\'%s\', \'%s\', %f, \'%s\')",
                        inputArr[0], inputArr[1], Double.parseDouble(inputArr[2]), inputArr[3]);

                // sql string
                sql = String.format("insert into product values (?, ?, ?, ?)");
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, inputArr[0]);
                pstmt.setString(2, inputArr[1]);
                pstmt.setDouble(3, Double.parseDouble(inputArr[2]));
                pstmt.setString(4, inputArr[3]);
                pstmt.executeUpdate();// executing sql
            }
            br.close();
            System.out.println("Product table created");

        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read products.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into products table");
        }
    }

    /**
     * Method that inserts data into SubCategories table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoSubCat() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE SubCategory("
                    + "subCatID VARCHAR(24) PRIMARY KEY,"
                    + "name VARCHAR(MAX),"
                    + "catID INTEGER REFERENCES Category(catID))");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/sub-category.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                // sql string
                sql = String.format("insert into subcategory values(\'%s\', \'%s\', %d)",
                        inputArr[0], inputArr[1], Integer.parseInt(inputArr[2]));
                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Subcategory table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read sub-category.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into sub-category table");
        }
    }

    /**
     * Method that inserts data into Category table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoCat() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Category("
                    + "catID INTEGER PRIMARY KEY,"
                    + "name VARCHAR(MAX))");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/category.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                // sql string
                sql = String.format("insert into category values(%d, \'%s\')",
                        Integer.parseInt(inputArr[0]), inputArr[1]);
                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Category table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read category.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into category table");
        }
    }

    /**
     * Method that inserts data into Store table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoStore() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Store("
                    + "storeID INTEGER PRIMARY KEY,"
                    + "addressID INTEGER REFERENCES Address(addressID),"
                    + "regionID INTEGER REFERENCES Region(regionID))");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/stores.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                // sql string
                sql = String.format("insert into store values(%d, %d, %d)",
                        Integer.parseInt(inputArr[0]), Integer.parseInt(inputArr[1]), Integer.parseInt(inputArr[2]));

                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Store table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read stores.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into store table");
        }
    }

    /**
     * Method that inserts data into Region table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoRegion() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Region("
                    + "regionID INTEGER PRIMARY KEY,"
                    + "regionName VARCHAR(MAX),"
                    + "managerID INTEGER REFERENCES Manager(managerID))");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/region.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                sql = String.format("insert into region values(%d, \'%s\', %d)",
                        Integer.parseInt(inputArr[0]), inputArr[1], Integer.parseInt(inputArr[2]));
                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Region table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read region.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into region table");
        }
    }

    /**
     * Method that inserts data into Manager table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoManager() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Manager("
                    + "managerID INTEGER PRIMARY KEY,"
                    + "fname VARCHAR(MAX) NOT NULL,"
                    + "lname VARCHAR(MAX) NOT NULL)");
            
            BufferedReader br = new BufferedReader(new FileReader("final-data-files/manager.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                // sql string
                sql = String.format("insert into manager values(%d, \'%s\', \'%s\')",
                        Integer.parseInt(inputArr[0]), inputArr[1], inputArr[2]);

                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Manager table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read manager.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into manager table");
        }
    }

    /**
     * Method that inserts data into Country table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoCountry() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Country("
                    + "countryCode VARCHAR(24) PRIMARY KEY,"
                    + "name VARCHAR(MAX) NOT NULL)");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/countries.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                // sql string
                sql = String.format("insert into country values(\'%s\', \'%s\')",
                        inputArr[0], inputArr[1]);

                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Country table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read countries.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into country table");
        }
    }

    /**
     * Method that inserts data into Address table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoAddress() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Address("
                    + "addressID INTEGER PRIMARY KEY,"
                    + "city VARCHAR(MAX) NOT NULL,"
                    + "state VARCHAR(MAX) NOT NULL,"
                    + "countryCode VARCHAR(24) REFERENCES Country(countryCode))");

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/address.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                sql = String.format("insert into address values(%d, \'%s\', \'%s\', \'%s\')",
                        Integer.parseInt(inputArr[0]), inputArr[1], inputArr[2], inputArr[3]);
                // sql string
                sql = "insert into address values (?, ?, ?, ?)";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(inputArr[0]));
                pstmt.setString(2, inputArr[1]);
                pstmt.setString(3, inputArr[2]);
                pstmt.setString(4, inputArr[3]);
                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Address table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read address.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into address table");
        }
    }

    /**
     * Method that inserts data into Order table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoOrder() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE \"order\"("
                    + "orderID VARCHAR(24) PRIMARY KEY,"
                    + "orderDate DATE NOT NULL,"
                    + "shipDate DATE NOT NULL,"
                    + "shipMode VARCHAR(24) NOT NULL,"
                    + "segement VARCHAR(MAX),"
                    + "custID VARCHAR(24) FOREIGN KEY REFERENCES customer(custID),"
                    + "storeID INTEGER FOREIGN KEY REFERENCES Store(storeID),"
                    + "isReturned INT)");
            
            BufferedReader br = new BufferedReader(new FileReader("final-data-files/orders.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);

                sql = String.format(
                        "insert into \"order\" values(\'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\', %d, %d)",
                        inputArr[0], inputArr[1], inputArr[2], inputArr[3], inputArr[4], inputArr[5],
                        Integer.parseInt(inputArr[6]), Integer.parseInt(inputArr[7]));

                sql = String.format("insert into \"order\" values(?, ?, ?, ?, ?, ?, ?, ?)");
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, inputArr[0]);
                pstmt.setDate(2, java.sql.Date.valueOf(inputArr[1]));
                pstmt.setDate(3, java.sql.Date.valueOf(inputArr[2]));
                pstmt.setString(4, inputArr[3]);
                pstmt.setString(5, inputArr[4]);
                pstmt.setString(6, inputArr[5]);
                pstmt.setInt(7, Integer.parseInt(inputArr[6]));
                pstmt.setInt(8, Integer.parseInt(inputArr[7]));

                pstmt.executeUpdate();
            }
            br.close();
            System.out.println("Order created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read orders.csv or file does not exist");
        } catch (SQLException se) {
            throw new SQLException("An Error occured: Cannot insert into order table");
        } catch (IllegalArgumentException iao) {
            throw new IOException("An Error occured: Invalid Date format in order.csv");
        }
    }

    /**
     * Method that inserts data into Order Details table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoOrderDetails() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE OrderDetails("
                    + "odID INT PRIMARY KEY IDENTITY(1,1),"
                    + "orderID VARCHAR(24) FOREIGN KEY REFERENCES \"order\"(orderID) NOT NULL, "
                    + "prodID VARCHAR(24) FOREIGN KEY REFERENCES Product(prodID) NOT NULL,"
                    + "sales FLOAT  NOT NULL,"
                    + "quantity INT  NOT NULL,"
                    + "discount FLOAT DEFAULT 0,"
                    + "profit FLOAT);");
            

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/order-details.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                sql = String.format("insert into orderdetails values(\'%s\', \'%s\', %f, %d, %f, %f)",
                        inputArr[0], inputArr[1], Double.parseDouble(inputArr[2]), Integer.parseInt(inputArr[3]),
                        Double.parseDouble(inputArr[4]),
                        Double.parseDouble(inputArr[5]));
                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();
            }
            br.close();
            System.out.println("Order Details created");

        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read order-details.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into orderDetails table");
        }
    }

    /**
     * Method that inserts data into Inventory table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoInventory() throws SQLException, IOException {
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE Inventory("
                    + "prodID VARCHAR(24) FOREIGN KEY REFERENCES Product(prodID),"
                    + "storeID INTEGER FOREIGN KEY REFERENCES store(storeID),"
                    + "PRIMARY KEY(storeID,prodID));");

            
            
            BufferedReader br = new BufferedReader(new FileReader("final-data-files/inventory.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                sql = String.format("insert into inventory values(\'%s\', %d)",
                        inputArr[0], Integer.parseInt(inputArr[1]));
                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();
            }
            br.close();
            System.out.println("Inventory table created");
        } catch (IOException io) {
            throw new IOException("An Error occured: Cannot read inventory.csv or file does not exist");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("An Error occured: Cannot insert into inventory table");
        }
    }

    public String dropAllTables() {
        String response = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("DROP TABLE IF EXISTS OrderDetails;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Inventory;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Product;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS SubCategory;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Category;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS \"order\";");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Store;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Address;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Country;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Region;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Manager;");
            pstmt.executeUpdate();

            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Customer;");
            pstmt.executeUpdate();

        } catch (SQLException se) {
            System.out.println("Error while deleting the database");
            // se.printStackTrace();
            response = "An Error occured: Something went wrong while deleting database";
        }

        return response;
    }

    public String showOrderID(String custName) {
        String output = "";
        try{
            String query = "SELECT c.custID, c.fName, c.Lname, o.orderID " +
                            "FROM Customer c " +
                            "INNER JOIN [order] o ON c.custID = o.custID " +
                            "WHERE c.fName LIKE ? OR c.lName LIKE ?";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, "%" + custName + "%");
            pstmt.setString(2, "%" + custName + "%");

            ResultSet result = pstmt.executeQuery();
            int n = 0;
            while(result.next()){
                output += "\t" + (++n) + ") " + result.getString(1) + " - " +
                            result.getString(2) + ", " + result.getString(3) + " - " +
                            result.getString(4) + "\n";
            }
            
            if(output.equals("")){
                output = "No people containing \'" + custName + "\' in their name\n";
            }
            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException se) {
            output = "An Error occured: Something went wrong while searching for orderID\n";
        }
        
        return output;
    }

    public String showPeople(String partOfName) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT c.fname as First, c.lname as Last, c.custID as custID " +
                    "FROM Customer c \n" +
                    "WHERE c.fname LIKE ? OR c.lname LIKE ?;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, "%" + partOfName + "%");
            pstmt.setString(2, "%" + partOfName + "%");
            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                output += "\t" + (n + 1) + ") " + result.getString(1) + " "
                        + result.getString(2) + " - "
                        + result.getString(3) + "\n";
                n++;
            }

            if (output.equalsIgnoreCase("")) {
                output = "No people containing \'" + partOfName + "\' in their name\n";
            }

            output += "\nQuery executed.\n" + n + " records found.\n";

            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for people\n";
        }

        return output;
    }

    /**
     * Method that gives all countries along with their country Code
     */
    public String showCountries() {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT countryCode AS Code,name from Country";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                output += "\t" + (++n) + ") " + result.getString("name") + " - "
                        + result.getString("Code") + "\n";
            }

            if (output.equalsIgnoreCase("")) {
                output = "No countries in database\n";

            }

            output += "\nQuery executed.\n" + n + " records found.\n";

            result.close();
            pstmt.close();
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for countries\n";
        }

        return output;
    }

    /**
     * Method that shows all the available categories
     */
    public String showCategories() {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT catID,name from Category";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                output += "\t" + (++n) + ") " + result.getString("name") + " - "
                        + result.getString("catID") + "\n";
            }

            if (output.equalsIgnoreCase("")) {
                output = "No categories in database\n";

            }
            output += "\nQuery executed.\n" + n + " records found.\n";

            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for categories\n";
        }

        return output;
    }

    /**
     * Method that provides list of subcategories of given category
     * 
     * @param category
     */
    public String showSubCategories(int catID) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT sc.subCatID, sc.name from SubCategory sc WHERE sc.catID = ?";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, catID);
            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                output += "\t" + (n + 1) + ") " +
                        result.getString("name") + " - "
                        + result.getString("subCatID") + "\n";
                n++;
            }

            if (output.equalsIgnoreCase("")) {
                output = "No subcategories in database\n";
            }
            output += "\nQuery executed.\n" + n + " records found.\n";

            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for sub-categories\n";
        }

        return output;
    }

    /**
     * Method that will provide the data for total number of stores in a country and
     * total profit made in that country across all stores. It returns
     * the 'countrylimit' number of countries
     * 
     * @param countryLimit
     */
    public String storeProfitByCountry(int countryLimit) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT TOP (?) c.name, COUNT(DISTINCT s.storeID) AS num_stores, SUM(od.profit) " +
                    "FROM Store s inner JOIN Address a ON s.addressID = a.addressID " +
                    "inner JOIN Country c ON a.countryCode = c.countryCode " +
                    "inner JOIN [Order] o ON s.storeID = o.storeID " +
                    "inner JOIN OrderDetails od ON o.orderID = od.orderID " +
                    "GROUP BY c.name ORDER BY num_stores DESC";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, countryLimit);

            ResultSet result = pstmt.executeQuery();// executing query

            System.out.println("List of Countries with total stores and total profit:\n");
            int n = 0;
            // Printing the results of query
            while (result.next()) {

                output += "\t" + (n + 1) + ") " + "Country: " + result.getString(1) + " \n\t\tNumber of Stores: "
                        + result.getString(2)
                        + ", Total Profit: "
                        + result.getInt(3) + "\n";
                n++;
            }

            if (output.equalsIgnoreCase("")) {
                output = "No stores/countries in database\n";

            }
            output += "\nQuery executed.\n" + n + " records found.\n";

            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for store's profit\n";
        }
        return output;
    }

    /**
     * Method that will provide stats of which store from a country
     * holds the most amount of the product of each category given a countryCode
     * 
     * @param countryCode
     */
    public String topProducts(String countryCode) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT con.name AS CountryName, c.name AS CategoryName, s.storeID, COUNT(p.prodID) AS total_products "
                    + "FROM Country con JOIN Address a ON con.countryCode = a.countryCode " +
                    "JOIN Store s ON a.addressID = s.addressID JOIN Inventory inv ON s.storeID = inv.storeID " +
                    "JOIN Product p ON inv.prodID = p.prodID JOIN SubCategory sb ON p.subCatID = sb.subCatID " +
                    "JOIN Category c ON sb.catID = c.catID " +
                    "WHERE con.countryCode = ? GROUP BY con.name, c.name, s.storeID " +
                    "HAVING s.storeID IN ( SELECT TOP 1 s_inner.storeID FROM Country con_inner " +
                    "JOIN Address a_inner ON con_inner.countryCode = a_inner.countryCode " +
                    "JOIN Store s_inner ON a_inner.addressID = s_inner.addressID " +
                    "JOIN Inventory inv_inner ON s_inner.storeID = inv_inner.storeID " +
                    "JOIN Product p_inner ON inv_inner.prodID = p_inner.prodID " +
                    "JOIN SubCategory sb_inner ON p_inner.subCatID = sb_inner.subCatID " +
                    "JOIN Category c_inner ON sb_inner.catID = c_inner.catID " +
                    "WHERE c_inner.name = c.name AND con_inner.name = con.name " +
                    "GROUP BY c_inner.catID, s_inner.storeID ORDER BY COUNT(p_inner.prodID) DESC ) " +
                    "ORDER BY total_products DESC, s.storeID ASC;";
            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, countryCode);

            ResultSet result = pstmt.executeQuery();

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                output += "\t-> Category: " + result.getString(2) + " Store ID: "
                        + result.getString(3)
                        + " Total Products: "
                        + result.getInt(4) + "\n";
                n++;
            }
            result.close();
            pstmt.close();

            if (output.equalsIgnoreCase("")) {
                output = "No country with code \'" + countryCode + "\'\n";
            }
            output += "\nQuery executed.\n" + n + " records found.\n";

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for top product\n";
        }
        return output;
    }

    /**
     * Method that will give the number of items returned by the customer.
     * 
     * @param customerID
     */
    public String returnedItemCount(String customerID) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT c.fname AS first_name, c.lname AS last_name, SUM(od.quantity) AS returned_items " +
                    "FROM Customer c JOIN [Order] o ON c.custID = o.custID " +
                    "JOIN OrderDetails od ON o.orderID = od.orderID " +
                    "WHERE c.custID = ? AND isReturned = 1 " +
                    "GROUP BY c.fname, c.lname";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, customerID);
            ResultSet result = pstmt.executeQuery();// executing query
            int n = 0;
            if (result.next()) {

                output = "\t" + (n + 1) + ") " + result.getString(1) + ",  " + result.getString(2) + ": "
                        + result.getInt(3) + "\n";
                n++;
            } else {
                query = "SELECT fname, lname FROM customer WHERE custID = ?";
                pstmt = connection.prepareStatement(query);
                pstmt.setString(1, customerID);
                result = pstmt.executeQuery();

                if (result.next()) {
                    output = String.format("%s, %s has not returned any items yet\n", result.getString(1),
                            result.getString(2));

                } else {
                    System.out.printf("\'%s\' does not exist\n", customerID);
                }

            }
            output += "\nQuery executed.\n" + n + " records found.\n";
            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for returned products of customer\n";
        }

        return output;

    }

    /**
     * Method that gives a list of products in a specific category
     * which have discount greater than or equal to the given discount.
     * 
     * @param catID
     * @param discount
     */
    public String discountedProducts(int catID, Double discount) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT p.name as product_name, p.price as price, o.discount as discounts FROM OrderDetails o INNER JOIN Product p ON o.prodID=p.prodID INNER JOIN SubCategory sc ON p.subCatID = sc.subCatID INNER JOIN Category c ON sc.catID=c.catID WHERE o.discount >= ? AND c.catID = ? ;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            pstmt.setDouble(1, discount / 100);
            pstmt.setInt(2, catID);
            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            while (result.next()) {
                output += "\t" + (++n) + ") " + result.getString("product_name") +
                        String.format("%.2f", result.getDouble("price")) + ", "
                        + String.format("%.2f", result.getDouble("discounts") * 100) + " % off.\n";
            }

            if (output.equalsIgnoreCase("")) {
                output = "No category with id \'" + catID + "\'\n";
            }
            output += "\nQuery executed.\n" + n + " records found.\n";

            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for discounted products\n";
        }
        return output;
    }

    /**
     * Method that finds out list of products in the given order as well as
     * its shipping mode given the orderID
     * 
     * @param orderID
     */
    public String shippingDetails(String orderID) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT p.name AS name, p.price AS price, o.shipMode AS shipMode " +
                    "FROM Product p " +
                    "INNER JOIN OrderDetails od ON p.prodID = od.prodID " +
                    "INNER JOIN [order] o ON od.orderID = o.orderID " +
                    "WHERE o.orderID = ?";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, orderID);

            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            while (result.next()) {
                if (n == 0) {
                    output += "Shipping Mode - " + result.getString("shipMode") + "\n";
                }
                output += "\t" + (++n) + ") " + result.getString("name") + "\n";
            }
            result.close();
            pstmt.close();

            if (output.equalsIgnoreCase("")) {
                output = "No order with ID - \'" + orderID + "\'\n";
            }
            output += "\nQuery executed.\n" + n + " records found.\n";

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for order details\n";
        }

        return output;
    }

    /**
     * Method that will give total sales for each category
     */
    public String salesSummaryByCategory() {
        String output = "";
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
            int n = 0;
            while (result.next()) {
                output += "\t" + (++n) + ") " + result.getString("category_name") + " - "
                        + String.format("$%.2f", result.getDouble("total_sales")) + "\n";
            }
            result.close();
            pstmt.close();

            if (output.equalsIgnoreCase("")) {
                output = "No categories in database\n";
            }

            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching categories and sales\n";
        }

        return output;
    }

    /**
     * Method that will find out the total number of distinct products in each
     * sub_category,
     * along with the total number of products sold in that sub-category.
     */
    public String subCategoryInventory() {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT c.name as category, sb.name as subcategory, count(DISTINCT p.prodID) AS num_products, SUM(od.quantity) AS total_quantity_sold "
                    +
                    "FROM Product p " +
                    "JOIN SubCategory sb ON p.subCatID = sb.subCatID " +
                    "JOIN Category c ON sb.catID = c.catID " +
                    "JOIN OrderDetails od ON p.prodID = od.prodID " +
                    "JOIN [order] o ON od.orderID = o.orderID " +
                    "WHERE o.isReturned=0 " +
                    "GROUP BY  c.name, sb.name " +
                    "ORDER BY c.name, num_products desc;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            while (result.next()) {
                output += "\t" + (++n) + ") #Products: " + result.getInt("num_products")
                        + ", "
                        + result.getString("subcategory") + ", "
                        + result.getString("category") + " #Sold: "
                        + result.getInt("total_quantity_sold") + "\n";
            }
            result.close();
            pstmt.close();

            if (output.equalsIgnoreCase("")) {
                output = "No sub-categories found in database\n";
            }

            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching sub-categories and sales\n";
        }
        return output;
    }

    /**
     * Method that gives products returned by specified customer given the
     * customerID
     * 
     * @param customerID
     */
    public String returnedProducts(String customerID) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT DISTINCT p.name AS prod_name\r\n" + //
                    "FROM Product p\r\n" + //
                    "JOIN OrderDetails od ON p.prodID = od.prodID\r\n" + //
                    "JOIN [order] o ON od.orderID = o.orderID\r\n" + //
                    "JOIN Customer c ON o.custID=c.custID WHERE c.custID = ? and o.isReturned=1;\r\n";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            pstmt.setString(1, customerID);

            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            while (result.next()) {
                if (n == 0) {
                    output += "Products returned by customer with customer ID \"" + customerID + "\": \n";
                }
                output += "\t" + (++n) + ")" + result.getString("prod_name") + "\n";
            }

            if (output.equalsIgnoreCase("")) {
                query = "SELECT fname, lname FROM customer WHERE custID = ?";
                pstmt = connection.prepareStatement(query);
                pstmt.setString(1, customerID);
                result = pstmt.executeQuery();

                if (result.next()) {
                    output = String.format("%s, %s has not returned any items yet\n", result.getString(1),
                            result.getString(2));

                } else {
                    System.out.printf("\'%s\' does not exist\n", customerID);
                }
            }

            result.close();
            pstmt.close();

            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for returned products\n";
        }

        return output;
    }

    /**
     * Method that prints all the regions in the database slong with their
     * regionID
     */
    public String showRegions() {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT regionID, regionName from Region";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                output += "\t" + (n + 1) + ") " +
                        result.getString("regionName") + " - "
                        + result.getString("regionID") + "\n";

                n++;
            }
            if (output.equalsIgnoreCase("")) {
                output = String.format("No regions in the database\n");
            }

            result.close();
            pstmt.close();
            output += "\nQuery executed.\n" + n + " records found.\n";

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for regions\n";
        }
        return output;
    }

    /**
     * Method that gives list of products returned in the specified region
     * 
     * @param regionName
     */
    public String returnedByRegion(String regionName) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT DISTINCT p.name AS prod_name\r\n" + //
                    "FROM Product p\r\n" + //
                    "JOIN OrderDetails od ON p.prodID = od.prodID\r\n" + //
                    "JOIN [order] o ON od.orderID = o.orderID\r\n" + //
                    "JOIN Store s ON o.storeID=s.storeID\r\n" + //
                    "JOIN Region r ON s.regionID=r.regionID \r\n" + //
                    "WHERE r.regionName= ? and o.isReturned=1;\r\n";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, regionName);
            ResultSet result = pstmt.executeQuery();// executing query
            int n = 0;
            while (result.next()) {
                output += "\t" + (n + 1) + ")" + result.getString("prod_name") + "\n";
                n++;

            }
            if (output.equalsIgnoreCase("")) {
                output = String.format("No returns in this region.\n");
            }

            output += "\nQuery executed.\n" + n + " records found.\n";
            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for returned items in the regions\n";
        }
        return output;
    }

    /**
     * Method that will get the average Product price in a specified Category,
     * given the categoryID
     * 
     * @param categoryID
     */
    public String averagePrice(int categoryID) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT AVG(p.price) AS averagePrice,c.name as name\r\n" + //
                    "FROM Product p\r\n" + //
                    "JOIN SubCategory s ON p.subCatID=s.subCatID\r\n" + //
                    "JOIN Category c ON s.catID = c.catID\r\n" + //
                    "WHERE c.catID = ? " +
                    "GROUP BY c.name ;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, categoryID);
            ResultSet result = pstmt.executeQuery();// executing query
            int n = 0;
            if (result.next() == true) {
                n++;

                // Printing the results of query
                output += "Category:\n\t" + result.getString("name") + " - "
                        + String.format("%.2f", result.getDouble("averagePrice")) + "\n";
            }
            if (output.equalsIgnoreCase("")) {
                output = String.format("Average price not found for category.\n");
            }

            result.close();
            pstmt.close();

            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for returned items in the regions\n";
        }
        return output;
    }

    /**
     * Method that will will return the shipMode of order and its average days taken
     * to
     * ship the order where the order has more than x items in it.
     * 
     * @param x
     */
    public String exceedXShipMode(int x) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT o.shipMode as ship_mode, AVG(DATEDIFF(day,1900-01-01,o.shipDate) - DATEDIFF(day,1900-01-01,o.orderDate)) AS avg_days_to_ship "
                    +
                    "FROM [order] o " +
                    "JOIN OrderDetails od ON o.orderID = od.orderID " +
                    "GROUP BY o.shipMode " +
                    "HAVING SUM(od.quantity) > ?;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, x);
            ResultSet result = pstmt.executeQuery();// executing query

            // Printing the results of query
            int n = 0;
            while (result.next()) {
                output += "\t" + (n + 1) + ") " + result.getString("ship_mode") + " - "
                        + result.getString("avg_days_to_ship") + " days." + "\n";
                n++;
            }
            if (output.equalsIgnoreCase("")) {
                output = String.format("Unable to find ship mode of orders with item greater than  %d.\n", x);
            }

            result.close();
            pstmt.close();

            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for returned items in the regions\n";
        }
        return output;
    }

    /**
     * Method that gives biggest order amount for each country,
     * focusing only on orders that were returned.
     * 
     * @param x
     */
    public String largestReturnedAmount(int x) {
        String output = "";
        try {// try
             // SQL QUERY
            String query = "SELECT TOP (?) con.name, a.city, a.state, cust.fName, cust.lName, MAX(od_sales.order_total) AS max_total "
                    + "FROM Country con LEFT JOIN Address a ON a.countryCode = con.countryCode " +
                    "JOIN Store s ON a.addressID = s.addressID JOIN [Order] o ON s.storeID = o.storeID " +
                    "JOIN Customer cust ON o.custID = cust.custID JOIN " +
                    "( SELECT od.orderID, SUM(od.sales) as order_total FROM OrderDetails od GROUP BY od.orderID ) " +
                    "AS od_sales ON o.orderID = od_sales.orderID WHERE o.isReturned = 1 " +
                    "GROUP BY con.name, o.orderID, cust.fName, cust.lName, a.city, a.state " +
                    "ORDER BY max_total DESC;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, x);
            ResultSet result = pstmt.executeQuery();// executing query

            // Printing the results of query
            int n = 0;
            while (result.next()) {

                output += "\t" + (++n) + ") " + result.getString(1) + ", "
                        + result.getString(3) + ", " + result.getString(2) + " - " + result.getString(4) + ", "
                        + result.getString(5) + ", $" + result.getDouble(6)
                        + "\n";
            }

            if (output.equalsIgnoreCase("")) {
                output = "No countries in databse\n";
            }

            result.close();
            pstmt.close();

            output += "\nQuery executed.\n" + n + " records found.\n";
        } catch (SQLException sql) {// catch block
            output = "An Error occured: Something went wrong while searching for max returned amount\n";
        }
        return output;
    }

}
