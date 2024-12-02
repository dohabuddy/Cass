package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) {
        startServer();
    }
    // Flag to control server state
    private boolean isRunning = true;
    //  Instantiate list for active client threads
    private static Vector<MultiThread> clientConnections;
    //  Socket waits for client connections
    public static ServerSocket serversocket;
    // Int For Next Connection ID
    private int nextId = 0;
    // Port Server Will Be Listening To
    public static final int PORT = 8000;
    //public SendEmail email = new SendEmail();
    DBMS userDB;
    // -- Server Constructor  --
    public Server() {
        //  Construct the list of active client threads
        clientConnections = new Vector<>();
        this.userDB = new DBMS();
        userDB.loadUserList();
        userDB.printUserList();
    }   //  --  End Server Constructor  --
    //  --  Start Server From GUI Method    --
    public static void startServer(){
        new Server();
    }   //  --  End Start Server Method --
    //  --  Stop Server Method  --
    public void stop() {
        isRunning = false;  // Set flag to stop the loop
        try {
            if (serversocket != null && !serversocket.isClosed()) {
                serversocket.close();  // Close the ServerSocket
            }

            // Interrupt all client connections
            for (MultiThread connection : clientConnections) {
                connection.interrupt();  // Assuming MultiThread extends Thread
            }
            clientConnections.clear();  // Clear the list of client connections

            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    //  --  Get Port Method --
    public static int getPort() {
        return PORT;
    }   //  --  End Get Port Method --
    //  --  Remove ID of individual Client Object Method    --
    public void removeID(int ID) {  // Called by a ServerThread after a client is terminated
        //  Find the object belonging to the client thread being terminated
        for (int i = 0; i < clientConnections.size(); ++i) {
            MultiThread cc = clientConnections.get(i);
            long targetID = cc.getId();
            if (targetID == ID) {   //  If matching ID is found
                // Remove ID from the clientConnections list and the connection thread will terminate itself
                clientConnections.remove(i);
                //  Place some text in the area to let the server operator know what is going on
                System.out.println("SERVER: connection closed for client id " + ID + "\n");
                break;
            }   //  End If
        }   //  End For
    }   //  --  End Remove ID Method    --
    //  --  Peer Connection Creation Method --
    private void peerConnection(Socket socket) {
        //  Create a thread communication when client arrives
        Network networkConnection = new Network(nextId, socket);
        MultiThread connection = new MultiThread(networkConnection, socket, this);
        //  Add the new thread to the active client threads list
        clientConnections.add(connection);
        //  Start the thread
        connection.start();
        //  Place some text in the area to let the server operator know what is going on
        System.out.println("SERVER: connection received for id " + nextId + "\n");
        ++nextId;
    }   //  --  End Peer Connection Method  --
    //  --  Server Listens for new connections  --
    public void listen() {
        try {
            serversocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (isRunning) {
                try {
                    Socket socket = serversocket.accept(); // Accept client connections
                    if (!isRunning) break; // Double-check in case the socket was closed
                    peerConnection(socket);
                } catch (IOException e) {
                    if (!isRunning) {
                        System.out.println("Server socket closed.");
                        break; // Exit the loop if the server is stopping
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serversocket != null && !serversocket.isClosed()) {
                    serversocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server stopped listening.");
        }
    }
    //  -- SERVER OPERATIONS --
    //  -- Log in to Server Method  --
    public String login(String user, String pass){
        //  Login variables
        String response = "";
        userDB.loadUserList();
        for(int i = 0; i <User.userList.size(); i++){   //  Check user list
            User account = User.userList.get(i);
            if(!user.equals(account.getUser())){ // if user doesn't exist / match
                response = "1"; // no user exists
            } else if (account.isLocked()){ // checks if acc is locked out
                response = "2"; // account is locked
                break;
            } else if (account.isLogged()){ // checks if account is already signed in
                response = "3"; // account is already signed in
                break;
            } else if (!pass.equals(account.getPass())){ // checks if pass matches
                int strikes = account.getStrikes();
                if(strikes == 3){
                    account.setLocked(true);
                    response = "2";
                } else {
                    int attempts = 3 - strikes;
                    response = "4" + strikes + ":" + attempts;
                    account.setStrikes(0);
                }
            } else { // successful login
                account.setLogged(true);
                response = "0";
                break;
            }
        }
        return response;
    }
    // tester password recovery function -- working with mock accounts
    public String passRecover(String user) {
        SendEmail email = new SendEmail();
        String res = "";
        for (int i = 0; i < User.userList.size(); i++) {
            User account = User.userList.get(i);
            if (!user.equals(account.getUser())) {
                res = "1"; // user does not exist
            } else {
                String address = account.getEmail(user); // getting user email
                System.out.println(address);    //  Display Logic
                String newPass = email.generateEmail(address); // sending email
                System.out.println(newPass);    //  Display Logic
                userDB.updateUserPassword(user,newPass);    // setting accounts password to temp password
                res = "0"; // sending back to parseInput
                break;
            }
        }
        return res;
    }
    // tester register function
    public String register(String user, String pass, String add){
        String res = "";
        boolean userExists = false;
        for (int i = 0; i < User.userList.size(); i++){
            User account = User.userList.get(i);
            System.out.println(account.getUser());
            if (user.equals(account.getUser())){ // if account with user already exists
                userExists = true;
                res = "1";
                break;
            }
        }
        if (!userExists){
            User account = new User(user, pass, add); // create new user
            User.userList.add(account); // add new user to arraylist
            System.out.println(User.userList.size());
            res = "0"; // successful registration
        }
        return res;
    }
    // Using 0 and 1 for True and False responses in places applicable, extending beyond 0 and 1 when needed
    public String parseInput(String data){
        System.out.println("Received data: " + data);
        char operation;
        String result = "";
        String response = "";
        if(data != null) {
            operation = data.charAt(0); // grabbing operation from string
            System.out.println("1. Operation sent: " + operation);
            if(!data.isEmpty()) {
                System.out.println("2. Entering if loop.");
                result = data.substring(1);
                String[] info = result.split(":");
                System.out.println(info.length);
                System.out.println("3. Remaining info: " + result);
                switch (operation) {
                    case '0':
                        // we wouldn't get here without the connection working so just say it's working?
                        response = "0"; // Connection successful
                        break;
                    case '1':
                        System.out.println("Entering login case.");
                        // gathering user information from the substring
                        String user = info[0];
                        String pass = info[1];
                        System.out.println("User Info: username - " + user + " password - " + pass);
                        // calling login function here so the response can go back to Network
                        response = login(user, pass);
                        //System.out.println(response);
                        break;
                    case '2': // needs register function in Server
                        System.out.println("Entering register user case.");
                        // Gathering registration information from the substring
                        String newUser = info[0]; // Assume info[0] contains the username
                        String newPass = info[1]; // Assume info[1] contains the password
                        String newEmail = info[2];
                        System.out.println("Registering user: username - " + newUser + ", password - " + newPass + ", email - " + newEmail);
                        // Calling register function and storing the response
                        response = register(newUser, newPass, newEmail);
                        // Optionally print or log the response for debugging purposes
                        System.out.println("Register response: " + response);
                        break;
                    case '3':
                        System.out.println("Entering passRec.");
                        String username = info[0];
                        response = passRecover(username);
                        break;
                    case '4':
                        System.out.println("Logout");
                        break;
                    case '5':
                        System.out.println("Disconnect");
                        response = info[0];
                        break;
                    default : // in case it's not entering a case for some reason so we know
                       response = ("Error with switch loop.");
                }   //  End Switch (operation)
            }   //  End If (data length > 1)
        }   //  End If (Data is not null)
        System.out.println("SERVER sending: " + response);
        return response;
    }
}
