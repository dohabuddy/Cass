package server;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String email;
    private boolean logged;
    private boolean locked;
    private int strikes;

    public static ArrayList<User> userList = new ArrayList<>();

    public User(String user, String pass, String add) {
        this.username = user;
        this.password = pass;
        this.email = add;
        this.logged = false;
        this.locked = false;
        this.strikes = 0;
    }

    // Getters and setters
    public String getUsername() {
        return this.username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public String getPass() {
        return this.password;
    }

    public void setPass(String password) {
        this.password = password;
    }

    public String getEmail(String username) {
        // Search for the user in the userList by username
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user.email; // Return the email if a match is found
            }
        }
        return "Email not found for username: " + username; // Return error message if no match
    }

    public void setEmail(String address) {
        this.email = address;
    }

    public boolean isLogged() {
        return this.logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getStrikes() {
        return this.strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    // Method to format user details into a readable string for MySQL
    public String getUserDetails() {
        return "User Details: " +
                "Username='" + username + '\'' +
                ", Password='" + password + '\'' +
                ", Email='" + email + '\'' +
                ", Logged=" + logged +
                ", Locked=" + locked +
                ", Strikes=" + strikes;
    }

    // Print all users in the list
    public static void printUserList() {
        for (User user : userList) {
            System.out.println(user.getUserDetails());
        }
    }
}