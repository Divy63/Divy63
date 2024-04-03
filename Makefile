BIN_DIR := bin
SRC_DIR := src

mydatabase:

interface:

buildapp:
	javac -d $(BIN_DIR) $(SRC_DIR)/application/mydatabase/*.java
	javac -d $(BIN_DIR) -cp $(BIN_DIR) $(SRC_DIR)/application/*.java

run: buildapp
	java -cp $(BIN_DIR) application.App

buildClean:
	javac -d $(BIN_DIR) $(SRC_DIR)/datacleanup/*.java

createfiles: buildClean
	java -cp $(BIN_DIR) datacleanup.cleanup
	java -cp $(BIN_DIR) datacleanup.product
	java -cp $(BIN_DIR) datacleanup.update

clean:
	rm -rf $(BIN_DIR)
