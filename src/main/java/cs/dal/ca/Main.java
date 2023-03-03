package cs.dal.ca;

import cs.dal.ca.authentication.Login;
import cs.dal.ca.authentication.Registration;
import cs.dal.ca.query.Parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Registration registration = new Registration();
    static Login login = new Login();
    static Parser parser = new Parser();
    static String CurrentUser = null;

    public static void main(String[] args) throws IOException {
        String username;
        String password;
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter your command: (register,login,query,exit,user,log out/logout)");
        String input = sc.nextLine();
        while (!input.equalsIgnoreCase(StaticVariables.EXIT)) {
            switch (input.toLowerCase()) {
                case StaticVariables.REGISTER -> {
                    System.out.println("Enter username: ");
                    username = sc.nextLine();
                    System.out.println("Enter password: ");
                    password = sc.nextLine();
                    System.out.println("Enter security question: ");
                    String question = sc.nextLine();
                    System.out.println("Enter security answer: ");
                    String answer = sc.nextLine();
                    try {
                        registration.register(username, password, question, answer);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                case StaticVariables.LOGIN -> {
                    if (CurrentUser!=null) {
                        System.out.println("User " + CurrentUser + " is already logged in! Log out first to continue.");
                    }
                    else{
                        System.out.println("Enter username: ");
                        username = sc.nextLine();
                        System.out.println("Enter password: ");
                        password = sc.nextLine();
                        if (login.login(username, password)) {
                            CurrentUser = username;
                        }
                    }

                }
                case StaticVariables.QUERY -> {
                    if(CurrentUser==null){
                        System.out.println("Action denied!Please log in first!");
                    }
                    else parser.parseData();
                }
                case StaticVariables.USER -> {
                    if (CurrentUser != null) {
                        System.out.println("Active User: " + CurrentUser);
                    } else {
                        System.out.println("No user is logged in.");
                    }
                }
                case StaticVariables.LOG_OUT, StaticVariables.LOGOUT -> {
                    if (CurrentUser == null) {
                        System.out.println("You are not logged in.");
                    } else {
                        System.out.println("User " + CurrentUser + " logged out.");
                        CurrentUser = null;
                    }
                }

                default -> System.out.println("Unknown query! Please try again.");
            }
            System.out.println("Enter your command: (register,login,query,exit,user,log out/logout)");
            input = sc.nextLine();
        }
    }
}