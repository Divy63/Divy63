
class Database {
    private Connection connection;

    public MyDatabase() {
		try {
			String url = "jdbc:sqlite:library.db";
			// create a connection to the database
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}

	}

    public void storeProfitByCountry(int countryLimit) {
        System.out.println("storeProfitByCountry not implemented yet!!");
    }

    public void topProducts(String countryCode) {
        System.out.println("topProducts not implemented for this country!");
    }

    public void returnedItemCount(String customerID) {
        System.out.println("returnedItemCount not implemented yet!!");
    }

    public void discountedProducts(String categoryName) {
        System.out.println("discountedProducts not implemented yet !!!");
    }

    public void shippingDetails(String orderID) {
        System.out.println("shippingDetails not implemented yet!!");
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
