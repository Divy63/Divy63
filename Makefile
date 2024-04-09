BIN_DIR := bin
SRC_DIR := src
LIB_DIR := lib

buildapp:
	javac -d $(BIN_DIR) $(SRC_DIR)/application/mydatabase/*.java
	javac -d $(BIN_DIR) -cp $(BIN_DIR) $(SRC_DIR)/application/*.java

run: buildapp
	java -cp $(BIN_DIR):$(LIB_DIR)/mssql-jdbc-11.2.0.jre18.jar application.App

buildClean:
	javac -d $(BIN_DIR) $(SRC_DIR)/datacleanup/*.java

createfiles: buildClean
	java -cp $(BIN_DIR) datacleanup.cleanup

clean:
	rm -rf $(BIN_DIR)
