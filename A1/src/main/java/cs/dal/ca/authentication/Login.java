package cs.dal.ca.authentication;

import cs.dal.ca.StaticVariables;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;


public class Login extends TwoFactorAuthentication {
    static TwoFactorAuthentication twoFA = new TwoFactorAuthentication();


    /**
     *login function receives username and password parameters from the console and
     * verifies if they exist in the database and are correct
     *
     * @param username - username entered by the user in the console
     * @param password - password entered by the user in the console
     * @return boolean - a flag to indicate if the user has logged in
     */
    public boolean login(String username, String password) {
        boolean userExists=false;
        boolean loginSuccess=false;
        try {
            File myObj = new File(StaticVariables.USERDATA_FILE_PATH);
            Scanner myReader = new Scanner(myObj);
            Scanner sc = new Scanner(System.in);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String question, answer;

                String[] records = data.split("\\|");  // splitting data using the delimiter '|'
                String dbUsernames = records[StaticVariables.DB_USERNAME];   // records[0] is the usernames column in user_records.txt file

                String dbHashedPassword = records[StaticVariables.DB_PASSWORD];
                String hashedInputPassword = Md5Hashing.getMd5Hash(Md5Hashing.getMd5Hash(username) + Md5Hashing.getMd5Hash(password));
                String dbHashedAnswer = records[StaticVariables.DB_ANSWER];
                if (dbUsernames.equals(username)) {
                    userExists=true;
                    if (dbHashedPassword.equals(hashedInputPassword)) {
                        question = records[StaticVariables.DB_QUESTION];
                        System.out.println(question + "\nPlease enter answer: ");
                        answer = sc.nextLine();
                        if (twoFA.authenticate(question, answer, dbHashedAnswer)) {
                            System.out.println("Logged in!");
                            loginSuccess=true;
                        } else {
                            System.out.println("Incorrect answer! Exiting the query...");
                            loginSuccess=false;

                        }
                    } else {
                        System.out.println("Incorrect password, login unsuccessful.");
                        loginSuccess=false;
                    }
                }
            }
            if(userExists==false) {
                System.out.println("Incorrect credentials. User doesn't exist!");
                loginSuccess=false;

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return loginSuccess;
    }
}
