package cs.dal.ca.authentication;

import cs.dal.ca.StaticVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Registration {

    /**
     *
     * @param username - username entered by the user in the console
     * @param password - password entered by the user in the console
     * @param question - question entered by the user in the console
     * @param answer - answer entered by the user in the console
     * @throws FileNotFoundException
     */
    public void register(String username, String password, String question, String answer) throws IOException {
        boolean usernameExists = false;   //flag to check if a username is already present in the db
        File myObj = new File(StaticVariables.USERDATA_FILE_PATH);
        if(!myObj.exists()){
            myObj.createNewFile();
        }
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //loop runs until it reads all the lines in the user_records.txt file
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine(); //reads data from the user_records.txt file line by line

            String[] records = data.split(StaticVariables.DELIMITER_REGEX);  //splitting data using the delimiter '|'
            String dbUsername = records[0];   //records[0] is the username column in user_records.txt file

            // IF statement checks whether the username provided by the user already exists in the database (user_records.txt)
            // if the username exists, boolean usernameExists returns true
            if (dbUsername.equalsIgnoreCase(username)) {
                System.out.println("Username already exists! Please choose another username");
                usernameExists = true;
                break;
            }
        }
        //if username is not present in the database, a new entry will be added in the database
        if (!usernameExists) {
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(StaticVariables.USERDATA_FILE_PATH, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // hashing password and answer provided by the user for privacy
            String hashedPassword = Md5Hashing.getMd5Hash(Md5Hashing.getMd5Hash(username) + Md5Hashing.getMd5Hash(password));
            String hashedAnswer = Md5Hashing.getMd5Hash(Md5Hashing.getMd5Hash(question) + Md5Hashing.getMd5Hash(answer));

            // pipe | is used as a delimiter to separate username, hashed password, question and hashed answer
            String username_password_record=username.concat("|").concat(hashedPassword).concat("|");
            String question_answer_record=question.concat("|").concat(hashedAnswer).concat("\n");

            String user_records = username_password_record.concat(question_answer_record);

            //the above string user_records is written in the database
            try {
                fileWriter.write(user_records);
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}



