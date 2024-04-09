--  1 SELECT c.fname as First, c.lname as Last, c.custID as custID 
-- FROM Customer c 
-- WHERE c.fname LIKE ? OR c.lname LIKE ?;
-- SELECT countryCode ,name from Country 
-- -- SELECT catID,name from Category
SELECT *
FROM [Customer]
-- SELECT sc.subCatID,sc.name,c.name as category from SubCategory sc INNER JOIN Category c ON sc.catID=c.catID;
-- SELECT p.name as product_name, p.price as price, o.discount as discounts FROM OrderDetails o INNER JOIN Product p ON o.prodID=p.prodID INNER JOIN SubCategory sc ON p.subCatID = sc.subCatID INNER JOIN Category c ON sc.catID=c.catID WHERE o.discount > 0.6 AND c.name = 'Office Supplies' ;
-- SELECT * FROM Category
-- SELECT p.name AS name, p.price AS price, o.shipMode AS shipMode
-- FROM Product p
-- INNER JOIN OrderDetails od ON p.prodID = od.prodID
-- INNER JOIN [order] o ON od.orderID = o.orderID
-- WHERE o.orderID = 'CA-2019-105270'; -- Using parameterized query
-- SELECT Category.name AS category_name, SUM(OrderDetails.sales) AS total_sales
-- FROM Category
--     JOIN SubCategory ON Category.catID = SubCategory.catID
--     JOIN Product ON SubCategory.subCatID = Product.subCatID
--     JOIN OrderDetails ON Product.prodID = OrderDetails.prodID
-- GROUP BY Category.name;
-- SELECT * FROM [order]
-- SELECT * FROM Product
-- SELECT c.name as category, sb.name as subcategory, count(DISTINCT p.prodID) AS num_products, SUM(od.quantity) AS total_quantity_sold
-- FROM Product p
--     JOIN SubCategory sb ON p.subCatID = sb.subCatID
--     JOIN Category c ON sb.catID = c.catID
--     JOIN OrderDetails od ON p.prodID = od.prodID
--     JOIN [order] o ON od.orderID = o.orderID
-- WHERE o.isReturned=0
-- GROUP BY  c.name, sb.name
-- ORDER BY c.name, num_products desc;
-- SELECT * FROM Customer
SELECT *
FROM Category

SELECT TOP (7)
    con.name, a.city, a.state, cust.fName, cust.lName, MAX(od_sales.order_total) AS max_total
FROM Country con
    LEFT JOIN Address a ON a.countryCode = con.countryCode
    JOIN Store s ON a.addressID = s.addressID
    JOIN [Order] o ON s.storeID = o.storeID
    JOIN Customer cust ON o.custID = cust.custID
    JOIN (
    SELECT od.orderID, SUM(od.sales) as order_total
    FROM OrderDetails od
    GROUP BY od.orderID
) AS od_sales ON o.orderID = od_sales.orderID
WHERE o.isReturned = 1
GROUP BY con.name, o.orderID, cust.fName, cust.lName, a.city, a.state
ORDER BY max_total DESC;

SELECT od.orderID, SUM(od.sales) as order_total
FROM OrderDetails od
GROUP BY od.orderID
ORDER BY order_total DESC;

SELECT *
FROM [order] o
    join OrderDetails od on o.orderID = od.orderID
    join Product p on od.prodID = p.prodID
where o.orderID = 'CA-2022-140151'


SELECT o.shipMode as ship_mode, AVG(DATEDIFF(day,1900-01-01,o.shipDate) - DATEDIFF(day,1900-01-01,o.orderDate)) AS avg_days_to_ship
FROM [order] o
    JOIN OrderDetails od ON o.orderID = od.orderID
GROUP BY o.shipMode
HAVING SUM(od.quantity) > 7