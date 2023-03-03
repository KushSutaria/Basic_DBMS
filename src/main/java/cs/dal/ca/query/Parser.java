package cs.dal.ca.query;

import cs.dal.ca.StaticVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

public class Parser {
    static List<String> queryList;
    public String queryListString;
    public String tableName;

    /**
     * parseData function takes input from the user indefinitely until user enters "exit;"
     * It will process the input query and call the corresponding functions to process the input query
     *
     * @throws IOException - throws exception is file can't be read or written
     */
    public void parseData() throws IOException {
        System.out.println("Enter your query: (Query will end with semi colon ;)");
        String userQuery;
        Scanner sc = new Scanner(System.in);
        queryList = new ArrayList<>();
        userQuery = queryLoop(sc);

        while (!(userQuery.equalsIgnoreCase(StaticVariables.EXIT) || userQuery.equalsIgnoreCase(StaticVariables.EXIT + ";"))) {
            switch (userQuery.toLowerCase()) {
                case StaticVariables.CREATE:
                    create();
                    break;
                case StaticVariables.SELECT:
                    select();
                    break;
                case StaticVariables.INSERT:
                    insert();
                    break;
                case StaticVariables.UPDATE:
                    update();
                    break;
                case StaticVariables.DROP:
                    drop();
                    break;
                case StaticVariables.DELETE:
                    delete();
                    break;
                default:
                    System.out.println("The entered SQL query is invalid. Please enter a valid Query");
            }

            System.out.println("Enter your query: ");
            userQuery = queryLoop(sc);
        }
    }

    /**
     * This function makes the input query lowercase and creates a string "queryListString" and a list "queryList"
     * which are used by many functions to process the query. It takes input till user enters ; in the console.
     * It also removes ; from the end of the input string
     *
     * @param sc - takes input query using Scanner
     * @return - String
     */
    private String queryLoop(Scanner sc) {
        String loop_scan;
        String userQuery;
        loop_scan = sc.next().toLowerCase();
        userQuery = loop_scan;
        queryList.clear();
        queryListString = "";
        while (!loop_scan.endsWith(";")) {
            loop_scan = sc.next();
            queryList.add(loop_scan);
            queryListString = queryListString.concat(loop_scan).concat(" ");
        }
        return userQuery;
    }


    /**
     * This function checks if "WHERE" and "=" are present in the input. If not, it is invalid.
     * It matches the value of the attribute on the right side of "WHERE" and before "=" and
     * deletes all the entries which match the value
     *
     * @throws IOException - exception handling for file manipulation
     */
    private void delete() throws IOException {

        int wherePosition = queryListString.indexOf(StaticVariables.WHERE);                 // finds index of where clause
        int attributesEqualToIndex = queryListString.indexOf(StaticVariables.EQUAL_TO);     // finds index of =

        if (wherePosition == -1 || attributesEqualToIndex == -1) {                          // if where or = is not present, invalid
            System.out.println("Invalid query!");
        } else {
            tableName = queryList.get(1);                                                       // table name from input

            queryListString = queryListString.strip();                                      // removes white spaces
            if (queryListString.endsWith(StaticVariables.SEMICOLON)) {                      // removes semi colon
                queryListString = queryListString.substring(0, queryListString.length() - 1);
            }


            queryListString = queryListString.substring(wherePosition);                 // substrings from where clause
            attributesEqualToIndex = queryListString.indexOf(StaticVariables.EQUAL_TO);  // finds index of = in case .strip() changed the index

            String data;
            tableName = tableName.concat(StaticVariables.TXT_EXTENSION);                    // add .txt to the table name to access the file
            File myObj = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));    // opens the file

            if (!myObj.exists()) {
                System.out.println("Invalid query, table doesn't exist!");
            } else {
                Scanner myReader;
                try {
                    myReader = new Scanner(myObj);                                              // reading the file
                } catch (FileNotFoundException e) {
                    System.out.println("Invalid query. table doesn't exist!");
                    throw new RuntimeException(e);
                }

                ArrayList<ArrayList<String>> findUpdate = new ArrayList<>();                // 2D arraylist to store the data

                int counter = 0;
                int tableAttributeCount = 0;
                int lineNumber = 0;
                while (myReader.hasNextLine()) {                                            // reads the file till the last line
                    data = myReader.nextLine();                                             // data = content of current line
                    findUpdate.add(new ArrayList<>());                                      // adds arraylist inside arraylist to create space for the data
                    for (String i : data.split(StaticVariables.DELIMITER_REGEX)) {          // splits the file content using delimiter | and loops over it

                        findUpdate.get(counter).add(i);                                 // adds the data in the arraylist

                    }
                    counter++;
                }
                wherePosition = queryListString.indexOf(StaticVariables.WHERE);
                String attributeToMatch = queryListString.substring(wherePosition + 5, attributesEqualToIndex).strip(); // finds the attribute from the input whose value is to be found
                String valueToMatch = queryListString.substring(attributesEqualToIndex + 1).strip();        // finds the value from the input

                for (String tableAttributeName : findUpdate.get(0)) {  // loop through the table to find the match in that column

                    if (tableAttributeName.strip().equalsIgnoreCase(attributeToMatch.strip())) {                // if attribute in the field and attribute in the table matches
                        while (lineNumber < findUpdate.size()) {                                                // iterate over that column till the last entry
                            if (findUpdate.get(lineNumber).get(tableAttributeCount).equalsIgnoreCase(valueToMatch)) {   // if value in that column and in the query matches

                                findUpdate.get(lineNumber).clear();                                             // delete that row
                                System.out.println("values deleted!");
                            }
                            lineNumber++;                                                       // go to next line in that row
                        }

                    }
                    tableAttributeCount++;                                              // go to next column in the table

                }

                // Write it in the table/file
                FileWriter fileWriter;
                try {
                    fileWriter = new FileWriter(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                lineNumber = 0;
                boolean blankLine = false;          // for checking if the deleted entry leaves blank line
                while (lineNumber < findUpdate.size()) {        // iterate over the arraylist
                    try {
                        if (!Objects.equals(findUpdate.get(lineNumber).get(0), StaticVariables.EMPTY_STRING)) { // if it is not the deleted line
                            for (String ij : findUpdate.get(lineNumber)) {
                                String[] content = ij.split(StaticVariables.DELIMITER_REGEX);               // split the data via regex |
                                for (String j : content) {
                                    //System.out.println(j);
                                    if (j.length() != 0) {                                              // if it is not the deleted value
                                        blankLine = true;
                                        fileWriter.write(j + StaticVariables.PIPE_DELIMITER);
                                    } else blankLine = false;
                                }

                            }
                        }
                    } catch (
                            Exception e) {                                 // if it is a deleted value in the arraylist, it will catch exception
                        blankLine = false;
                    }

                    if (blankLine)                                          // boolean to remove that blank line which the deleted value would leave
                        fileWriter.write("\n");
                    lineNumber++;
                }
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * This function creates new table/file if it doesn't exist already.
     * It checks if there is at least one table attribute else it throws error.
     */
    public void create() {
        FileWriter fileWriter;
        String tableAttributes = "";

        try {
            if (queryListString.contains(StaticVariables.OPEN_BRACKET) && queryListString.contains(StaticVariables.CLOSE_BRACKET)) {        // check if it has ( and )

                int attributesOpenBracketIndex = queryListString.indexOf(StaticVariables.OPEN_BRACKET);                         // indices of "(" and ")"
                int attributesClosingBracketIndex = queryListString.indexOf(StaticVariables.CLOSE_BRACKET);

                tableName = queryListString.substring(6, attributesOpenBracketIndex);                                   // table name from input query
                tableName = queryListFix(tableName, attributesOpenBracketIndex, attributesClosingBracketIndex);         // parses the name using queryListFix function

                for (String i : queryList) {                        //  iterates over queryList
                    i = i.strip();                                  // removes whitespaces
                    String[] head = i.split(" ");               // if there is a blank space

                    if (head.length % 2 != 0 || head.length == 1) {         // only takes attribute names
                        tableAttributes = tableAttributes.concat(head[0]).concat(StaticVariables.PIPE_DELIMITER);

                    } else {
                        tableAttributes = tableAttributes.concat(head[1]).concat(StaticVariables.PIPE_DELIMITER);
                    }
                }

                tableAttributes = tableAttributes.substring(0, tableAttributes.length() - 1);       // removes the last |


                tableName = tableName.concat(StaticVariables.TXT_EXTENSION);    // adds .txt extension

                File file = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                if (file.exists()) {                                    // checks if file exists
                    System.out.println("table already exists!");
                } else {                                                    // if not, it will create file
                    fileWriter = new FileWriter(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                    System.out.println("table " + tableName.substring(0, tableName.length() - 4) + " created.");

                    fileWriter.write(tableAttributes);
                    fileWriter.close();

                }

            } else {
                System.out.println("Table should have at least one column. Invalid query!");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * This function parses the input query like removing whitespaces, semicolon and
     * assigns queryListString string just the data inside the brackets.
     *
     * @param tableName                     - name of the table from the input
     * @param attributesOpenBracketIndex    - index of open bracket "("
     * @param attributesClosingBracketIndex - index of closing bracket ")"
     * @return - table name without any semicolon or whitespace
     */
    private String queryListFix(String tableName, int attributesOpenBracketIndex, int attributesClosingBracketIndex) {
        if (tableName.endsWith(" ")) {
            tableName = tableName.substring(0, tableName.length() - 1);     // removes space after the name if exists
        }

        queryListString = queryListString.substring(attributesOpenBracketIndex + 1, attributesClosingBracketIndex); // only considers data inside ( and )
        if (queryListString.startsWith(" ")) {              // if there are spaces between ( and values
            queryListString = queryListString.substring(1);
        }

        queryList = new ArrayList<>(Arrays.asList(queryListString.split(StaticVariables.COMMA)));     // splits it using , as delimiter
        return tableName;
    }

    /**
     * This function checks for attributes like * or table attributes and returns corresponding table data.
     */
    public void select() {
        try {
            int fromPosition = queryList.indexOf(StaticVariables.FROM.toLowerCase());       // gets index of "from"
            if (fromPosition == -1) {
                System.out.println("Invalid query");
            } else {
                tableName = queryList.get(fromPosition + 1);                // table name is what comes after from
                if (tableName.endsWith(StaticVariables.SEMICOLON)) {            // removes semi colon if it exists
                    tableName = tableName.substring(0, tableName.length() - 1);
                    tableName = tableName.strip();              // removes whitespace
                }
                String selectParameter = queryList.subList(0, fromPosition).toString();     // the attributes which is in the query

                tableName = tableName.concat(StaticVariables.TXT_EXTENSION);            // adds .txt extension

                File file = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));

                if (file.exists()) {
                    String data;
                    File myObj = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                    Scanner myReader = new Scanner(myObj);                  // reads through the file
                    if (selectParameter.contains(StaticVariables.ASTERISK)) {           // if the attribute is *
                        while (myReader.hasNextLine()) {
                            data = myReader.nextLine();
                            System.out.println(data);                               // print everything
                        }
                    } else {
                        ArrayList<ArrayList<String>> table = new ArrayList<>(1);        // 2D array table to store the values of the file
                        table.add(new ArrayList<>());
                        ArrayList<String> returnRow = new ArrayList<>();                // Header/attribute name
                        for (String para2 : queryList.subList(0, fromPosition)) {           // all parameters
                            if (para2.equalsIgnoreCase(StaticVariables.COMMA)) {
                            } else {
                                if (para2.contains(StaticVariables.COMMA)) {
                                    String[] temp = para2.split(StaticVariables.COMMA);

                                    Collections.addAll(returnRow, temp);
                                } else {
                                    returnRow.add(para2);                   // add attributes in the first line
                                }
                            }
                        }
                        while (returnRow.contains(StaticVariables.EMPTY_STRING)) {
                            returnRow.remove(StaticVariables.EMPTY_STRING);         // removes blank lines
                        }


                        int lineNumber = 0;
                        while (myReader.hasNextLine()) {
                            data = myReader.nextLine();

                            for (String i : data.split(StaticVariables.DELIMITER_REGEX)) {      // adds all the values in the 2D table
                                table.get(lineNumber).add(i);

                            }
                            table.add(new ArrayList<>());
                            lineNumber++;
                        }
                        table.remove(table.size() - 1);         // removes blank array ( last)
                        ArrayList<ArrayList<String>> ans = new ArrayList<>();
                        ans.add(new ArrayList<>());
                        ans.get(0).addAll(returnRow);
                        ans.add(new ArrayList<>());
                        lineNumber = 1;
                        int tableHeaderIterator = 0;

                        for (String returnHeader : returnRow) {         // this loop matches the entries of table and attributes in the query
                            while (tableHeaderIterator < table.get(0).size()) {

                                if (returnHeader.equalsIgnoreCase(table.get(0).get(tableHeaderIterator))) {

                                    while (lineNumber < table.size()) {

                                        ans.get(lineNumber).add(table.get(lineNumber).get(tableHeaderIterator));

                                        lineNumber++;
                                        if (ans.size() < table.size())
                                            ans.add(new ArrayList<>());

                                    }
                                    lineNumber = 1;

                                }
                                tableHeaderIterator++;

                            }
                            tableHeaderIterator = 0;
                        }
                        try {
                            if (ans.get(1).get(0) == null) {        // if there is no such attribute, catch exception
                            } else {
                                for (ArrayList<String> i : ans) {
                                    //
                                    for (String j : i)              // else, add to the file
                                        System.out.print(j.concat(StaticVariables.PIPE_DELIMITER));
                                    System.out.println();
                                }


                            }

                        } catch (Exception e) {
                            System.out.println("No such row exists!");
                        }

                    }

                    myReader.close();

                } else {
                    System.out.println("table doesn't exist!");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This function adds new data in the table. It checks for some keywords like "into" and "values"
     * and the length of input compared to the table attributes count. If it is more or less, it discards it
     *
     * @throws IOException - throws exception if file is missing
     */
    public void insert() throws IOException {
        int intoPosition = queryList.indexOf(StaticVariables.INTO);     // index of into
        int valuesPosition = queryList.indexOf(StaticVariables.VALUES);     // index of values

        if (intoPosition == -1 || valuesPosition == -1) {
            System.out.println("Invalid query");
        } else {
            tableName = queryList.get(intoPosition + 1);            // gets table name after into clause
            if (tableName.endsWith(";")) {
                tableName = tableName.substring(0, tableName.length() - 1);     // removes ;
                tableName = tableName.strip();                  // removes whitespace
            }


            if (queryListString.contains(StaticVariables.OPEN_BRACKET) && queryListString.contains(StaticVariables.CLOSE_BRACKET)) {
                int attributesOpenBracketIndex = queryListString.indexOf(StaticVariables.OPEN_BRACKET);
                int attributesClosingBracketIndex = queryListString.indexOf(StaticVariables.CLOSE_BRACKET);

                tableName = queryListFix(tableName, attributesOpenBracketIndex, attributesClosingBracketIndex);
                String tableAttributes = "";
                for (String i : queryList) {
                    i = i.strip();

                    tableAttributes = tableAttributes.concat(i).concat(StaticVariables.PIPE_DELIMITER); // gets the data separated by |
                }

                tableAttributes = tableAttributes.substring(0, tableAttributes.length() - 1);   // removes last |

                if (queryList.size() != tableAttributes.split(StaticVariables.DELIMITER_REGEX).length) {        // if entries more or less than attributes of table
                    System.out.println("Incorrect insertion, data entered doesn't match the length of table rows!");
                } else {
                    tableName = tableName.concat(StaticVariables.TXT_EXTENSION);        // add .txt extension

                    File file = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                    if (!file.exists()) {               // if table doesn't exist
                        System.out.println("No such table exists!");
                    } else {                    // write data
                        FileWriter fileWriter = new FileWriter(StaticVariables.DATABASE_FILE_PATH.concat(tableName), true);
                        fileWriter.write("\n" + tableAttributes);
                        fileWriter.close();
                        System.out.println("Data inserted.");
                    }
                }
            }
        }
    }

    /**
     * This function first copies the data in a 2D arraylist and iterates over it.
     * when the match is found, it updates the value and rewrites it in the file
     */
    public void update() {
        boolean queryValidation = false;            // if query is wrong
        try {
            tableName = queryList.get(0);           // get table name
            if (!(queryListString.contains(StaticVariables.EQUAL_TO) && queryListString.contains(StaticVariables.WHERE)     // if any keyword is missing
                    && queryListString.contains(StaticVariables.SET) && queryListString.contains(StaticVariables.VALUES))) {
                System.out.println("Invalid query!");
            } else {
                int attributesEqualToIndex;         // "=" related to attribute ( first equal)

                queryListString = queryListString.strip();
                if (queryListString.endsWith(StaticVariables.SEMICOLON)) {
                    queryListString = queryListString.substring(0, queryListString.length() - 1);       // remove ;
                }

                attributesEqualToIndex = queryListString.indexOf(StaticVariables.EQUAL_TO);     // "=" related to attribute ( first equal)
                int wherePosition;
                int valuesPosition = queryListString.indexOf(StaticVariables.VALUES);       // index of "values"
                String attributeToChange = queryListString.substring(valuesPosition + 6, attributesEqualToIndex).strip();       // attribute whose value is to be changed

                int whereEqual = queryListString.lastIndexOf(StaticVariables.EQUAL_TO);     // "=" after where clause

                String data;
                tableName = tableName.concat(StaticVariables.TXT_EXTENSION);
                File myObj = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                Scanner myReader;
                try {
                    myReader = new Scanner(myObj);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                ArrayList<ArrayList<String>> findUpdate = new ArrayList<>();

                int counter = 0;
                int tableAttributeCount = 0;
                int lineNumber = 0;
                while (myReader.hasNextLine()) {
                    data = myReader.nextLine();
                    findUpdate.add(new ArrayList<>());
                    for (String i : data.split(StaticVariables.DELIMITER_REGEX)) {

                        findUpdate.get(counter).add(i);

                    }
                    counter++;
                }
                wherePosition = queryListString.indexOf(StaticVariables.WHERE);         // index of Where keyword
                String attributeToMatch = queryListString.substring(wherePosition + 5, whereEqual).strip(); // attribute after "where"
                String valueToMatch = queryListString.substring(whereEqual + 1).strip();        // value of the attribute which we are searching
                String newUpdate = queryListString.substring(attributesEqualToIndex + 1, wherePosition).strip();     // new value which is to be updated

                int valueChangeTableAttributeCounter = 0;           // Attribute index that we are looking for
                for (String abc : findUpdate.get(0)) {              // iterate over the row
                    if (abc.strip().equalsIgnoreCase(attributeToChange.strip())) { // if match found in the attribute of the table and query attribute
                        break;
                    } else
                        valueChangeTableAttributeCounter++;
                }
                // iterate over the arraylist and update the value when the match is found
                for (String tableAttributeName : findUpdate.get(0)) {
                    if (tableAttributeName.strip().equalsIgnoreCase(attributeToMatch.strip())) {
                        while (lineNumber < findUpdate.size()) {

                            if (findUpdate.get(lineNumber).get(tableAttributeCount).equalsIgnoreCase(valueToMatch)) {

                                findUpdate.get(lineNumber).set(valueChangeTableAttributeCounter, newUpdate);
                                System.out.println("values updated!");
                            }
                            lineNumber++;
                        }

                    }
                    tableAttributeCount++;

                }

                // write over the file
                FileWriter fileWriter;
                try {
                    fileWriter = new FileWriter(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                lineNumber = 0;

                while (lineNumber < findUpdate.size()) {
                    for (String ij : findUpdate.get(lineNumber)) {
                        String[] content = ij.split(StaticVariables.DELIMITER_REGEX);
                        for (String j : content) {
                            fileWriter.write(j + StaticVariables.PIPE_DELIMITER);
                        }

                    }
                    fileWriter.write("\n");
                    lineNumber++;
                }

                fileWriter.close();
            }
        } catch (Exception e) {
            queryValidation = false;
            System.out.println("Invalid Query!");
            //   throw new RuntimeException(e);
        }
        if (queryValidation)
            System.out.println("Data inserted.");

    }


    /**
     * This function finds the table and deletes it if it exists
     */
    public void drop() {
        if (!queryList.get(0).equalsIgnoreCase(StaticVariables.TABLE)) {
            System.out.println("Invalid query");
        } else {
            tableName = queryList.get(1);   // query keywords positions in the array: table=0 , tableName=1
            if (tableName.endsWith(StaticVariables.SEMICOLON)) {
                tableName = tableName.substring(0, tableName.length() - 1);
            }
            tableName = tableName.strip();
            tableName = tableName.concat(StaticVariables.TXT_EXTENSION);

            File file = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
            if (file.exists()) {
                File myObj = new File(StaticVariables.DATABASE_FILE_PATH.concat(tableName));
                myObj.delete();
                System.out.println("table dropped.");
            } else {
                System.out.println("table doesn't exist!");
            }

        }
    }


}