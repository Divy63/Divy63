BIN_DIR := bin
SRC_DIR := src

mydatabase:

interface:

buildapp:
	javac -d $(BIN_DIR) $(SRC_DIR)/application/mydatabase/*.java
	javac -d $(BIN_DIR) -cp $(BIN_DIR) $(SRC_DIR)/application/*.java

run: buildapp
	java -cp $(BIN_DIR) application.App
