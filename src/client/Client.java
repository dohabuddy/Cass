package client;
//  Core Program Logic
//  Client Class creates a connection and allows the user to perform operations
public class Client {
    //  Client variables
    private static String HOST;
    private boolean clientIsConnected = false;
    private boolean clientIsLoggedIn = false;
    private boolean passwordIsValid = false;
    private boolean emailIsValid = false;
    public Network clientConnection;
    // --   Client Constructor for GUI to build Client Object   --
    public Client() {
        HOST = "";
    }   //  --  End Client Constructor  --
    //  --  CLIENT OPERATIONS   --
    //  !!NOTE: Operations are identified via a character key!!
    //  Operations Key: 0=Connect, 1=Login, 2=Register User, 3=Password Recovery, 4=Logout, 5=Disconnect
    // --   Connect Clients to Server Method (0)    --
    public String connect(String host) {
        //  Connection variables
        String outputGUI = "";
        this.HOST = host;
        String request = "0" + HOST;   //  Append operation character key
        String response = "";
        if (clientIsConnected) {  //  Prevent connecting if connection already exists
            outputGUI = "Already connected to " + HOST;
            System.out.println(outputGUI);    //  Display Logic

        } else {    //  Attempt connection
            clientConnection = new Network(HOST);   //  Client creates connection request
            response = clientConnection.send(request); //   Client sends connect request
            if (response == null) {  //  Failed connection
                outputGUI = "Error connecting to " + HOST;
                System.out.println(outputGUI);    //  Display Logic
                clientIsConnected = false;
            } else {    //  Successful connection
                outputGUI = "Connection successful to " + HOST;
                System.out.println(outputGUI);   //  Display Logic
                clientIsConnected = true;
            }   //  End Else
        }   //  End Else
        return outputGUI;
    }   //  --  End Connect Method  --
    //  --  Login Request Method (1)    --
    public String login(String username, String password) {
        //  Login variables
        String outputGUI = "";
        String request = "1" + username + ":" + password;  //  Append operation character key
        String response = "";
        //  Check if Client is connected but not logged in
        if (clientIsConnected && !clientIsLoggedIn) {   //  If Client is connected but not logged in
            response = clientConnection.send(request);
            System.out.println("CLIENT receive: " + response);   //  Display Logic
            char readServerOperation = response.charAt(0);
            switch (readServerOperation){   //  Read Server response with first character key
                case '0':   //  '0'=Success
                    outputGUI = "User successfully signed in.";
                    System.out.println(outputGUI);  //  Display Logic
                    clientIsLoggedIn = true;
                    break;
                case '1':   //  '1'=Failed, wrong/new username
                    outputGUI = "No username matching our records.";
                    System.out.println(outputGUI);  //  Display Logic
                    break;
                case '2':   //  '2'=Failed, three strikes and user's account is locked
                    outputGUI = "Account is locked. Please go through password recovery.";
                    System.out.println(outputGUI);  //  Display Logic
                    break;
                case '3':   //  '3'=Failed due to Server issue
                    outputGUI = "Account is already logged in.";
                    System.out.println(outputGUI);  //  Display Logic
                    break;
                case '4':   //  '4'=Failed, wrong password and plus one strike
                    String info = response.substring(1);
                    String[] strikes = info.split(":");
                    outputGUI = "Password is incorrect. You now have " + strikes[0] + " strike(s).\nYour account will lock at 3 strikes.\nYou have " + strikes[1] +  " attempts remaining." ;
                    System.out.println(outputGUI);  //  Display Logic
                    break;
            }   //  End Switch
        } else if (!clientIsConnected) {    //  If Client is not connected but trying to log in
            outputGUI = "Please connect to the server first.";
            System.out.println(outputGUI);  //  Display Logic
        } else {    //  If login attempted when client is connected and already logged in
            outputGUI = "Already logged in.";
            System.out.println(outputGUI);  //  Display Logic
        }   //  End Else
        return outputGUI;
    }   //  --  End Login Method    --
    //  --  Register A New User Method (2)  --
    public String register(String newUsername, String userPassword, String userEmail) {
        //  Register variables
        String outputGUI = "";
        String request = "2" + newUsername + ":" + userPassword + ":" + userEmail;    //  Append operation character key
        if (clientIsConnected){ //  If Client is connected
            passwordIsValid = RegexEmail.validPassword(userPassword);   //  Check valid password
            emailIsValid = RegexEmail.validEmailAddress((userEmail));   //  Check valid email format
            if (passwordIsValid && emailIsValid) {  //  If user input had a valid password and email
                // Send the registration data to the server
                String response = clientConnection.send(request);
                System.out.println("CLIENT receive: " + response);  //  Display Logic
                //  Handle server response
                char readServerOperation = response.charAt(0);
                switch (readServerOperation) {  //  Read Server response with first character key
                    case '0':   //  '0'=Success
                        outputGUI = "User successfully registered.";
                        System.out.println("User successfully registered.");    //  Display Logic
                        break;
                    case '1':   //  '1'=Failed due to duplicate username
                        outputGUI = "Username already exists.";
                        System.out.println("Username already exists."); //  Display Logic
                        break;
                    default:    //  Failed for some other reason
                        outputGUI = "Registration failed. Please try again.";
                        System.out.println("Registration failed. Please try again.");   //  Display Logic
                        break;
                }   //  End Switch
            } else {    //  If Client is not connected
                System.out.println("Please connect to the server first.");
            }   //  End Else
        } else if (!passwordIsValid){   //  If password is invalid
            outputGUI = "Please enter a valid password.";
        } else if (!emailIsValid){  //  If email is invalid
            outputGUI = "Please enter a valid email.";
        }   //  End Else If
        return outputGUI;
    }   //  --  End Register    --
    //  --  Recover Password Method (3) --
    public String recoverPassword(String username) {
        //  Recover password variables
        String outputGUI = "";
        String request = "3" + username;    //  Append operation character key
        if (clientIsConnected) {    //  If Client is connected
            String response = clientConnection.send(request);
            //  Simulate sending a temporary password to the user's registered email
            System.out.println("CLIENT receive: " + response);  //  Display Logic
            //  Handle server response
            char readServerOperation = response.charAt(0);
            switch (readServerOperation) {  //  Read Server response with first character key
                case '0':   //  '0'=Success
                    outputGUI = "Temporary password sent to the email associated with username:\n" + username;
                    System.out.println(outputGUI);  //  Display Logic
                    break;
                case '1':   //  '1'=Failed, no matching usernames in database
                    outputGUI = "No account with that user exists.";
                    System.out.println(outputGUI);  //  Display Logic
                    break;
                default:    //  Failed for some other reason
                    outputGUI = "An error occurred.";
                    System.out.println(outputGUI);  //  Display Logic
            }   //  End Switch
        } else {    //  If Client is not connected
            System.out.println("Please connect to the server first.");
        }   //  End Else
        return outputGUI;
    }   //  --  End Recover Password Method --
    //  --  Logout Method (4)   --
    public String logout() {
        //  Logout variables
        String outputGUI = "";
        String request = "4";   //  Append operation character key
        String response = "";
        if (clientIsLoggedIn) { //  If client is logged in
            response = clientConnection.send(request);  //  Send logout request
            System.out.println("CLIENT receive: " + response);  //  Display Logic
            //  Handle server response
            char readServerOperation = response.charAt(0);
            //  Read Server response with first character key
            if (readServerOperation == '0') {   //  '0'=Success
                outputGUI = "Client Logged Out Successfully";
                System.out.println(outputGUI);  //  Display Logic
                clientIsLoggedIn = false;
            } else {    //  If response is not a '0' then logout failed for some reason
                outputGUI = "An error occurred.";
                System.out.println(outputGUI);  //  Display Logic
            }   //  End Else
        } else {    //  If Client is not logged in
            System.out.println("Not logged in.");   //  Display Logic
        }   //  End Else
        return outputGUI;
    }   //  --  End Logout Method   --
    //  --  Disconnect Client from Server Method    --
    public String disconnect() {
        //  Disconnect variables
        String outputGUI = "";
        String request = "5disconnect"; //  Append operation character key
        String response = "";
        if (clientIsConnected) {    //  If Client is connected
            response = clientConnection.send(request); // Send disconnect request
            System.out.println("CLIENT receive: " + response);
            if (response.equals("disconnect")){ //  If server disconnects
                outputGUI = "Disconnected from " + HOST;
                System.out.println(outputGUI);  //  Display Logic
                clientIsConnected = false;
                clientIsLoggedIn = false;   //  Automatically log out on disconnect
            } else {    //  If disconnect fails
                outputGUI = "Error disconnecting from " + HOST;
                System.out.println(outputGUI);  //  Display Logic
            }   //  End Else
        } else {    //  If Client is not connected
            System.out.println("Not connected.");
            outputGUI = "No connection found.";
        }   //  End Else
        return outputGUI;
    }   //  --  End Disconnect Method   --
    // Print connection status method???
    public void printConnectionStatus() {
        System.out.println("Server: " + HOST);
        System.out.println("Connected: " + clientIsConnected);
        System.out.println("Logged In: " + clientIsLoggedIn);
    }
}   //  END CLIENT CLASS