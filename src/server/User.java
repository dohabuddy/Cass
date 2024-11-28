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

    public User(String user, String pass, String add){
        this.username = user;
        this.password = pass;
        this.email = add;
        logged = false;
        locked = false;
        strikes = 0;
    }

    //Getters and Setters
    public String getUser(){
        return this.username;
    }
    public void setUser(String username){
        this.username = username;
    }

    public String getPass(){
        return this.password;
    }
    public void setPass(String password){
        this.password = password;
    }

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String address){
        this.email = address;
    }

    public boolean getLogged(){
        return this.logged;
    }
    public void setLog(boolean logged){
        this.logged = logged;
    }

    public boolean getLocked(){
        return this.locked;
    }
    public void setLocked(boolean locked){
        this.locked = locked;
    }

    public int getStrikes(){
        return this.strikes;
    }
    public void setStrikes(int strikes){
        this.strikes = strikes;
    }

    public void addToUserList(User user){
        userList.add(user);
    }
}
