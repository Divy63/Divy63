package application.mydatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public Database() throws SQLException, FileNotFoundException, IOException {

        Properties prop = new Properties();
        String cfgFileName = "src/application/mydatabase/auth.cfg";

        try {
            FileInputStream configFile = new FileInputStream(cfgFileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException e) {
            // System.out.println("An error occurred: config file not found.");
            throw new FileNotFoundException("An error occurred: config file not found.");
            // System.exit(1);
        } catch (IOException e) {
            // System.out.println("An error occurred: could not read config file.");
            throw new IOException("An error occurred: could not read config file.");
            // System.exit(1);
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
        try {
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException se) {
            throw new SQLException("Failed to establish connection to database");
        }
        System.out.println("Connection established successfully");

        // TODO: this.initializeDatabase();
        // TODO: this.readInputData();
    }

    /**
     * Method that initializes the database on Uranium
     * It first drop all tables and then creates them again from scratch
     * If fail to initialize any of the tables it drops all the table and rollback
     * 
     */
    public void initializeDatabase() {

        dropAllTables();

        try {
            createAllTables();
            readInputData();
        } catch (SQLException e) {
            // dropAllTables();
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Error occured while initializing the database\n\nDROPING ALL OF THE DATABASE");
        } catch (IOException fnf) {
            System.out.println(fnf.getMessage());
        }
    }

    /**
     * Method that creates all the table according to the sql schema
     * 
     * @throws SQLException
     */
    private void createAllTables() throws SQLException {
        this.connection.createStatement().executeUpdate("CREATE TABLE Customer("
                + "custID VARCHAR(24) PRIMARY KEY,"
                + "fname VARCHAR(MAX) NOT NULL,"
                + "lname VARCHAR(MAX) NOT NULL)");

        this.connection.createStatement().executeUpdate("CREATE TABLE Manager("
                + "managerID INTEGER PRIMARY KEY,"
                + "fname VARCHAR(MAX) NOT NULL,"
                + "lname VARCHAR(MAX) NOT NULL)");

        this.connection.createStatement().executeUpdate("CREATE TABLE Region("
                + "regionID INTEGER PRIMARY KEY,"
                + "regionName VARCHAR(MAX),"
                + "managerID INTEGER REFERENCES Manager(managerID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE Country("
                + "countryCode VARCHAR(24) PRIMARY KEY,"
                + "name VARCHAR(MAX) NOT NULL)");

        this.connection.createStatement().executeUpdate("CREATE TABLE Address("
                + "addressID INTEGER PRIMARY KEY,"
                + "city VARCHAR(MAX) NOT NULL,"
                + "state VARCHAR(MAX) NOT NULL,"
                + "countryCode VARCHAR(24) REFERENCES Country(countryCode))");

        this.connection.createStatement().executeUpdate("CREATE TABLE Store("
                + "storeID INTEGER PRIMARY KEY,"
                + "addressID INTEGER REFERENCES Address(addressID),"
                + "regionID INTEGER REFERENCES Region(regionID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE \"order\"("
                + "orderID VARCHAR(24) PRIMARY KEY,"
                + "orderDate DATE NOT NULL,"
                + "shipDate DATE NOT NULL,"
                + "shipMode VARCHAR(24) NOT NULL,"
                + "segement VARCHAR(MAX),"
                + "custID VARCHAR(24) FOREIGN KEY REFERENCES customer(custID),"
                + "storeID INTEGER FOREIGN KEY REFERENCES Store(storeID),"
                + "isReturned INT)");

        this.connection.createStatement().executeUpdate("CREATE TABLE Category("
                + "catID INTEGER PRIMARY KEY,"
                + "name VARCHAR(MAX))");

        this.connection.createStatement().executeUpdate("CREATE TABLE SubCategory("
                + "subCatID VARCHAR(24) PRIMARY KEY,"
                + "name VARCHAR(MAX),"
                + "catID INTEGER REFERENCES Category(catID))");

        System.out.println("creating products");
        this.connection.createStatement().executeUpdate("CREATE TABLE Product("
                + "prodID VARCHAR(24) PRIMARY KEY,"
                + "name VARCHAR(MAX),"
                + "price FLOAT NOT NULL,"
                + "subCatID VARCHAR(24) REFERENCES SubCategory(subCatID))");

        this.connection.createStatement().executeUpdate("CREATE TABLE OrderDetails("
                + "odID INT PRIMARY KEY IDENTITY(1,1),"
                + "orderID VARCHAR(24) FOREIGN KEY REFERENCES \"order\"(orderID) NOT NULL, "
                + "prodID VARCHAR(24) FOREIGN KEY REFERENCES Product(prodID) NOT NULL,"
                + "sales FLOAT  NOT NULL,"
                + "quantity INT  NOT NULL,"
                + "discount FLOAT DEFAULT 0,"
                + "profit FLOAT);");
        this.connection.createStatement().executeUpdate("CREATE TABLE Inventory("
                + "prodID VARCHAR(24) FOREIGN KEY REFERENCES Product(prodID),"
                + "storeID INTEGER FOREIGN KEY REFERENCES store(storeID),"
                + "PRIMARY KEY(storeID,prodID));");
    }

    /**
     * Method that reads all csv files and inserts data into the tables we created
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void readInputData() throws SQLException, IOException {

        // TODO: while loop for all data-files
        // BufferedReader br = new BufferedReader(new FileReader(""));
        // br.readLine();

        // // TODO: read lines, prepare statements

        // br.close();

        insertIntoCustomer();
        insertIntoManager();
        insertIntoRegion();
        insertIntoCountry();
        insertIntoAddress();
        insertIntoStore();
        insertIntoOrder();
        insertIntoCat();
        insertIntoSubCat();
        insertIntoProduct();
        insertIntoInventory();
        insertIntoOrderDetails();

    }

    /**
     * Method that inserts data into Customer table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoCustomer() throws SQLException, IOException {
        try {
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
            throw new IOException("customers.csv file not found");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("Error occured while inserting into customer table");
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
            throw new IOException("products.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into product table");
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
            throw new IOException("sub-category.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into subcategory table");
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
            throw new IOException("category.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into category table");
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
            throw new IOException("stores.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into stores table");
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
            throw new IOException("region.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into region table");
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
            throw new IOException("manager.csv.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into manager table");
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
            throw new IOException("countries.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into country table");
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
            throw new IOException("address.csv file not found");
        } catch (SQLException se) {
            throw new SQLException("Error occured while inserting into address table");
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

            BufferedReader br = new BufferedReader(new FileReader("final-data-files/orders.csv"));
            PreparedStatement pstmt;
            String inputLine;
            String sql;
            String[] inputArr;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            br.readLine(); // leaving the headers

            while ((inputLine = br.readLine()) != null) {
                inputArr = inputLine.split(regex);
                sql = String.format(
                        "insert into \"order\" values(\'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\', %d, %d)",
                        inputArr[0], inputArr[1], inputArr[2], inputArr[3], inputArr[4], inputArr[5],
                        Integer.parseInt(inputArr[6]), Integer.parseInt(inputArr[7]));

                // sql string
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

                pstmt.executeUpdate();// Executing sql
            }
            br.close();
            System.out.println("Order details created");
        } catch (IOException io) {
            throw new IOException("orders.csv file not found");
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("Error occured while inserting into order table");
        }
    }

    /**
     * Method that inserts data into Order Details table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoOrderDetails() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("final-data-files/order-details.csv"));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            // sql string
            sql = String.format("insert into orderdetails values(\'%s\', \'%s\', %f, %d, %f, %f)",
                    inputArr[0], inputArr[1], Double.parseDouble(inputArr[2]), Integer.parseInt(inputArr[3]),
                    Double.parseDouble(inputArr[4]),
                    Double.parseDouble(inputArr[5]));
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();// Executing sql
        }
        br.close();
        System.out.println("Order Details created");
    }

    /**
     * Method that inserts data into Inventory table using a CSV file Input
     * 
     * @throws SQLException
     * @throws IOException
     */
    private void insertIntoInventory() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("final-data-files/inventory.csv"));
        PreparedStatement pstmt;
        String inputLine;
        String sql;
        String[] inputArr;

        br.readLine(); // leaving the headers

        while ((inputLine = br.readLine()) != null) {
            inputArr = inputLine.split(regex);
            // sql string
            sql = String.format("insert into inventory values(\'%s\', %d)",
                    inputArr[0], Integer.parseInt(inputArr[1]));
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();// Executing sql
        }
        br.close();
        System.out.println("Inventory table created");
    }

    /**
     * Method that executes sql statements to drop all tables
     */
    public void dropAllTables() {
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
            se.printStackTrace();
        }
    }

    /**
     * Method that executes and displays the persons in the database
     * having 'partOfName' in their name
     * 
     * @param partOfName
     */
    public void showPeople(String partOfName) {
        try {// try
             // SQL QUERY
            String query = "SELECT c.fname as First, c.lname as Last, c.custID as custID " +
                    "FROM Customer c \n" +
                    "WHERE c.fname LIKE ? OR c.lname LIKE ?;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, "%" + partOfName + "%");
            pstmt.setString(2, "%" + partOfName + "%");
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching the database for people with \"" + partOfName + "\" in their name");
            System.out.println(
                    "------------------------------------------------------------------------------");
            System.out.println("List of available people:");
            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print("\t" + (n + 1) + ") ");
                System.out.println(
                        result.getString("First") + " " + result.getString("Last") + " - "
                                + result.getString("Last"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }

    }

    /**
     * Method that gives all countries along with their country Code
     */
    public void showCountries() {
        try {// try
             // SQL QUERY
            String query = "SELECT countryCode AS Code,name from Country";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching the database for countries");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available countries:");
            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print("\t" + (n + 1) + ") ");
                System.out.println(
                        result.getString("name") + " - "
                                + result.getString("Code"));

                n++;
            }
            result.close();
            pstmt.close();
            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that shows all the available categories
     */
    public void showCategories() {
        try {// try
             // SQL QUERY
            String query = "SELECT catID,name from Category";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available categories with their  IDs:");
            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print("\t" + (n + 1) + ") ");
                System.out.println(
                        result.getString("name") + " - "
                                + result.getString("catID"));

                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that provides list of subcategories of given category
     * 
     * @param category
     */
    public void showSubCategories(String category) {
        try {// try
             // SQL QUERY
            String query = "SELECT sc.subCatID,sc.name from SubCategory sc INNER JOIN Category c ON sc.catID=c.catID WHERE c.name=?";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, category);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching the database for categories");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available sub-categories with their IDs:");
            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print("\t" + (n + 1) + ") ");
                System.out.println(
                        result.getString("name") + " - "
                                + result.getString("subCatID"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that will provide the data for total number of stores in a country and
     * total profit made in that country across all stores. It returns
     * the 'countrylimit' number of countries
     * 
     * @param countryLimit
     */
    public void storeProfitByCountry(int countryLimit) {
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
            System.out.println(
                    "\nSearching the database for Profit across stores for top \'" + countryLimit + "\' country");
            System.out.println(
                    "--------------------------------------------------------------------------------------");

            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print((n + 1) + ") ");
                System.out.println(
                        "Country: " + result.getString(1) + " \n\tNumber of Stores: "
                                + result.getString(2)
                                + ", Total Profit: "
                                + result.getInt(3));

                n++;
            }
            result.close();
            pstmt.close();
            System.out.println("\nQuery executed. \n" + n + " records found.\n");

        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that will provide stats of which store from a country
     * holds the most amount of the product of each category given a countryCode
     * 
     * @param countryCode
     */
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
            System.out.println("\nSearching the database for top most inventory holding store in " + countryCode
                    + " for each category:");
            System.out.println(
                    "-------------------------------------------------------------------------------------------------");
            System.out.println("Country:  " + result.getString("country_name"));
            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print((n + 1) + ")");
                System.out.println(
                        "-> Category: " + result.getString("category_name") + " Store ID: "
                                + result.getString("storeID")
                                + " Total Products: "
                                + result.getInt("total_products"));

                System.out.println();

            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that will give the number of items returned by the customer.
     * 
     * @param customerID
     */
    public void returnedItemCount(String customerID) {
        try {// try
             // SQL QUERY
            String query = "SELECT COUNT(*) AS returned_items_count, c.fname as First,c.lname as Last\r\n" + //
                    "FROM Customer c\r\n" + //
                    "JOIN [order] o ON c.custID = o.custID\r\n" + //
                    "WHERE c.custID = ?\r\n" + //
                    "AND o.isReturned = 1\r\n" +
                    "GROUP BY c.fname,c.lname;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setString(1, customerID);

            ResultSet result = pstmt.executeQuery();// executing query
            int n = 0;
            while (result.next()) {

                System.out.println(
                        "\nSearching database for number of items returned by customer with id \'" + customerID + "\'");
                System.out
                        .println(
                                "--------------------------------------------------------------------------------------");
                System.out.println(result.getString("First") + " " + result.getString("Last") + " : "
                        + result.getInt("returned_item_count"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }

    }

    /**
     * Method that gives a list of products in a specific category
     * which have discount greater than or equal to the given discount.
     * 
     * @param categoryName
     * @param discount
     */
    public void discountedProducts(String categoryName, Double discount) {
        try {// try
             // SQL QUERY
            String query = "SELECT p.name as product_name, p.price as price, o.discount as discounts FROM OrderDetails o INNER JOIN Product p ON o.prodID=p.prodID INNER JOIN SubCategory sc ON p.subCatID = sc.subCatID INNER JOIN Category c ON sc.catID=c.catID WHERE o.discount > ? AND c.name = ? ;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            pstmt.setDouble(1, discount / 100);
            pstmt.setString(2, categoryName);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching database discounted items in category \"" + categoryName
                            + "\" with discount greater than or equal to " + discount + " % : ");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------");
            int n = 0;
            while (result.next()) {
                System.out.println("\t" + (n + 1) + ") " + result.getString("product_name") +
                        String.format("%.2f", result.getDouble("price")) + ", "
                        + String.format("%.2f", result.getDouble("discounts") * 100) + " % off.");
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that finds out list of products in the given order as well as
     * its shipping mode given the orderID
     * 
     * @param orderID
     */
    public void shippingDetails(String orderID) {
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
            System.out.println("\nSearching database for order with ID \'" + orderID + "\'");
            System.out
                    .println("--------------------------------------------------------------------------------------");
            int n = 0;
            while (result.next()) {
                if (n == 0) {
                    System.out.println("Shipping Mode -" + result.getString("shipMode"));

                }
                System.out.println("\t" + (n + 1) + ") " + result.getString("name"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }

    }

    /**
     * Method that will give total sales for each category
     */
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
            int n = 0;
            while (result.next()) {
                System.out.println("\t" + (n + 1) + ") " + result.getString("category_name") + " - "
                        + String.format("%.2f", result.getDouble("total_sales")));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that will find out the total number of distinct products in each
     * sub_category,
     * along with the total number of products sold in that sub-category.
     */
    public void subCategoryInventory() {
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
            System.out.println("\nSearching database for distinct products in each sub category :");
            System.out
                    .println("--------------------------------------------------------------------------------------");
            int n = 0;
            while (result.next()) {
                System.out
                        .println("\t" + (n + 1) + ") Number of Products:" + result.getInt("num_products")
                                + ", Sub-Category: "
                                + result.getString("subcategory") + ", Category: "
                                + result.getString("category") + " Total quantity sold :"
                                + result.getInt("total_quantity_sold"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that gives products returned by specified customer given the
     * customerID
     * 
     * @param customerID
     */
    public void returnedProducts(String customerID) {
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
            System.out.println(
                    "\nSearching database for returned products of customer with customer ID \"" + customerID + "\" :");
            System.out
                    .println(
                            "-------------------------------------------------------------------------------------------");
            System.out.println("Products returned by customer with customer ID \"" + customerID + "\": ");
            int n = 0;
            while (result.next()) {
                System.out
                        .println("\t" + (n + 1) + ")" + result.getString("prod_name"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that prints all the regions in the database slong with their
     * regionID
     */
    public void showRegions() {
        try {// try
             // SQL QUERY
            String query = "SELECT regionID, regionName from Region";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement

            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println("\nSearching the database for Regions");
            System.out.println(
                    "------------------------------------------------");
            System.out.println("List of available regions:");
            int n = 0;
            // Printing the results of query
            while (result.next()) {
                System.out.print("\t" + (n + 1) + ") ");
                System.out.println(
                        result.getString("regionName") + " - "
                                + result.getString("regionID"));

                n++;
            }

            result.close();
            pstmt.close();
            System.out.println("\nQuery executed. \n" + n + " records found.\n");

        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that gives list of products returned in the specified region
     * 
     * @param regionName
     */
    public void returnedByRegion(String regionName) {
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
            System.out.println(
                    "\nSearching database for returned products in region \"" + regionName + "\" :");
            System.out
                    .println(
                            "-------------------------------------------------------------------------------------------");
            System.out.println("Products returned in region\"" + regionName + "\": ");
            int n = 0;
            while (result.next()) {
                System.out
                        .println("\t" + (n + 1) + ")" + result.getString("prod_name"));
                n++;

            }

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
            result.close();
            pstmt.close();

        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that will get the average Product price in a specified Category,
     * given the categoryID
     * 
     * @param categoryID
     */
    public void averagePrice(int categoryID) {
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
                System.out.println(
                        "\nSearching the database for Avergae Price of Products in category \""
                                + result.getString("name") + " (" + categoryID + ")" + "\" :");
                System.out.println(
                        "----------------------------------------------------------------------------------------------");
                System.out.println("Average price of product for category \"" + categoryID + "\" :");
                // Printing the results of query
                System.out.println(
                        "Category:\n\t" + result.getString("name") + " - "
                                + String.format("%.2f", result.getDouble("averagePrice")));
            } else {
                System.out.println(
                        "Searching the database for Avergae Price of Products in category with categoryID \""
                                + categoryID + "\" :");
                System.out.println(
                        "----------------------------------------------------------------------------------------------");

            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.\n");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that will will return the shipMode of order and its average days taken
     * to
     * ship the order where the order has more than x items in it.
     * 
     * @param x
     */
    public void exceedXShipMode(int x) {
        try {// try
             // SQL QUERY
            String query = "SELECT o.shipMode as ship_mode, AVG(DATEDIFF(day,1900-01-01,o.shipDate) - DATEDIFF(day,1900-01-01,o.orderDate)) AS avg_days_to_ship "
                    +
                    "FROM [order] o " +
                    "JOIN OrderDetails od ON o.orderID = od.orderID " +
                    "GROUP BY o.shipMode, " +
                    "HAVING SUM(od.quantity) > ?;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, x);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching the database for ship modes of order  quantities greater than " + x + " :");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            System.out.println("Ship Modes of  orders with quantity greater than " + x + ": ");
            // Printing the results of query
            int n = 0;
            while (result.next()) {
                System.out.println(
                        "\t" + (n + 1) + ") " + result.getString("ship_mode") + " - "
                                + result.getString("avg_days_to_ship") + " days.");
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

    /**
     * Method that gives biggest order amount for each country,
     * focusing only on orders that were returned.
     * 
     * @param x
     */
    public void largestReturnedAmount(int x) {
        try {// try
             // SQL QUERY
            String query = "SELECT TOP ? c.name AS country_name,COUNT(DISTINCT s.storeID) AS num_stores,SUM(od.profit) "
                    +
                    "FROM Store s " +
                    "INNER JOIN Address a ON s.addressID=a.addressID " +
                    "INNER JOIN Country c a ON a.countryCode=c.countryCode " +
                    "INNER JOIN [Order] ON s.storeID=o.storeID " +
                    "INNER JOIN OrderDetails od ON o.orderID=od.orderID " +
                    "GROUP BY c.name " +
                    "ORDER BY num_stores DESC;";

            PreparedStatement pstmt = connection.prepareStatement(query);// preparing a statement
            pstmt.setInt(1, x);
            ResultSet result = pstmt.executeQuery();// executing query
            System.out.println(
                    "\nSearching the database for order with largest total for each country which were returned");
            System.out.println(
                    "----------------------------------------------------------------------------------------------");
            System.out.println("Largest order total by country which were returned are: ");
            // Printing the results of query
            int n = 0;
            while (result.next()) {
                System.out.println(
                        "\t" + (n + 1) + ") " + result.getString("country_name") + " - "
                                + result.getString("max_total"));
                n++;
            }
            result.close();
            pstmt.close();

            System.out.println("\nQuery executed. \n" + n + " records found.");
        } catch (SQLException sql) {// catch block
            sql.printStackTrace(System.out);
        }
    }

}

