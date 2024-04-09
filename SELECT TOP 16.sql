SELECT TOP 16
    c.name, COUNT (DISTINCT s.storeID) AS num_stores, SUM(od.profit) 
FROM Store s
    inner JOIN Address a ON s.addressID = a.addressID
    inner JOIN Country c ON a.countryCode = c.countryCode
    inner JOIN [Order] o ON s.storeID = o.storeID
    inner JOIN OrderDetails od ON o.orderID = od.orderID
GROUP BY c.name
ORDER BY num_stores DESC;