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
        userDB.syncUserList();
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
            userDB.disconnectAll(userDB);
            userDB.logoutAll(userDB);

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
    //  --  Server Listens For New Connections Method  --
    public void listen() {
        try {
            serversocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (isRunning) { //  Control infinite loop
                try {
                    Socket socket = serversocket.accept(); // Accept client connections
                    if (!isRunning) break; // Double-check in case the socket was closed
                    peerConnection(socket);
                } catch (IOException e) {
                    if (!isRunning) {
                        System.out.println("Server socket closed.");
                        break; // Exit the loop if the server is stopping
                    } else {
                        System.out.println(e);
                    }   //  End Else
                }   //  End Catch
            }   //  End While
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (serversocket != null && !serversocket.isClosed()) {
                    serversocket.close();
                }   //  End If
            } catch (IOException e) {
                System.out.println(e);
            }   //  End Catch
            System.out.println("Server stopped listening.");
        }   //  End Finally
    }   //  --  End Listen Method   --
    //  -- SERVER OPERATIONS --
    //  -- Log in to Server Method  --
    public String login(String username, String pass) {
        //  Login variables
        String response = "";
        int i = 0;
        while (i < User.userList.size()) {   //  Check username list
            User test = User.userList.get(i);
            User account = null;
            int index = -1;
            if (username.equals(test.getUsername())) {
                index = i;
                account = User.userList.get(index);
            }   //  End If
            if (index == -1 || account == null) {
                response = "1"; //  No username exists
            } else if (account.isLocked()) { // checks if acc is locked out
                response = "2"; // account is locked
                break;
            } else if (account.isLogged()) { // checks if account is already signed in
                response = "3"; // account is already signed in
                break;
            } else if (!account.getPassword().equals(pass)) {
                account.addStrike(userDB);
                response = "4" + account.getStrikes(); //  Incorrect password plus one strike
                break;
            } else {    //  Successful login
                account.setLogged(true, userDB);
                account.resetStrikes(userDB);
                account.setConnected(true, userDB);
                response = "0";
                break;
            }   //  End Else
            ++i;
        }   //  End While
        return response;
    }   //  --  End Login Method    --
    // tester password recovery function -- working with mock accounts
    public String passRecover(String user) {
        SendEmail email = new SendEmail();
        String response = "";
        for (int i = 0; i < User.userList.size(); i++) {
            User account = User.userList.get(i);
            if (!user.equals(account.getUsername())) {
                response = "1"; // user does not exist
            } else {
                String address = account.getEmail(); // getting user email
                System.out.println(address);    //  Display Logic
                String newPassword = email.generateEmail(address); // sending email
                System.out.println(newPassword);    //  Display Logic
                userDB.updateUserPassword(account, newPassword, userDB);    // setting accounts password to temp password
                response = "0"; //  Sending back to parseInput
                break;
            }
        }
        return response;
    }
    // tester register function
    public String register(String username, String password, String email){
        String res = "";
        boolean userExists = false;
        for (int i = 0; i < User.userList.size(); i++){
            User account = User.userList.get(i);
            System.out.println(account.getUsername());
            if (username.equals(account.getUsername())){ // if account with user already exists
                userExists = true;
                res = "1";
                break;
            }
        }
        if (!userExists){
            User account = new User(username, password, email); // create new user
            userDB.registerUser(account); // add new user to arraylist
            userDB.syncUserList();
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
    public synchronized List<String> getLoggedInUsers() {
        List<String> loggedInUsers = new ArrayList<>();
        for (User account : User.userList) {
            if (account.isLogged()) {
                loggedInUsers.add(account.getUsername());
            }
        }
        return loggedInUsers;
    }

    public synchronized List<String> getLockedOutUsers() {
        List<String> LockedOutUsers = new ArrayList<>();
        for (User account : User.userList) {
            if (account.getStrikes() == 3) {
                LockedOutUsers.add(account.getUsername());
            }
        }
        return LockedOutUsers;
    }

    public synchronized int getNumberOfRegisteredUsers() {
        return User.userList.size();
    }
    public String serverApplication(){
        String result = "Server Use!";
        return result;
    }
}
