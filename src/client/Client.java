package client;

public class Client {
    private static String HOST;
    private boolean isConnected = false;
    private boolean isLoggedIn = false;
    private boolean passValid = false;
    private boolean emailValid = false;
    public Network clientConnection;

//    // Tester main so we don't have to launch the GUI every single time
//    public static void main(String[] args){
//        Client client = new Client(); // empty Client object to run commands on
//        client.connect("127.0.0.1"); // running connect - sets HOST and tells Network to connect
//        client.login("lynnir","blahblah123"); // testing login with hardcoded information
//        client.disconnect();
//    }

    // Empty Client Constructor to Build Client Object for GUI to use
    public Client() {
        HOST = "";
    }

    // Connects Client to Client Network Object -- working
    public String connect(String host) {
        String guiOut = "";
        this.HOST = host;
        String data = "0" + HOST;
        String res = "";
        if (isConnected == true) {
            System.out.println("Already connected");
            guiOut = "Already connected to " + HOST;
        } else {
            clientConnection = new Network(HOST); // client needs to get a response from this
            res = clientConnection.send(data); // sending connect request
            if (res == null) { // failed connection
                System.out.println("Error connecting.");
                guiOut = "Error connecting to " + HOST;
                isConnected = false;
            } else { // successful connection
                System.out.println("Successful connection.");
                guiOut = "Connection successful to " + HOST;
                isConnected = true;
            }
        }
        return guiOut;
    }

    // Works with main tester
    public String disconnect() {
        String guiOut = "";
        String data = "5disconnect";
        String res = "";
        if (isConnected) {
            res = clientConnection.send(data); // sending disconnect request
            System.out.println("CLIENT receive: " + res);
            if (res.equals("disconnect")){
                isConnected = false;
                isLoggedIn = false;  // Automatically log out on disconnect
                System.out.println("Disconnected from " + HOST);
                guiOut = "Disconnected from " + HOST;
            } else {
                System.out.println("Error disconnecting.");
                guiOut = "Error disconnecting from " + HOST;
            }
        } else {
            System.out.println("Not connected.");
            guiOut = "No connection found.";
        }
        return guiOut;
    }

    // Working with hard coded info
    public String login(String username, String password) {
        String guiOut = "";
        String data = "1" + username + ":" + password;
        String res = "";
        if (isConnected && !isLoggedIn) {
            res = clientConnection.send(data);
            //System.out.println("CLIENT receive: " + res);
            char operation = res.charAt(0);
            switch (operation){
                case '0':
                    guiOut = "User successfully signed in.";
                    //System.out.println(guiOut);
                    isLoggedIn = true;
                    break;
                case '1':
                    guiOut = "No username matching our records.";
                    //System.out.println(guiOut);
                    break;
                case '2':
                    guiOut = "Account is locked. Please go through password recovery.";
                    //System.out.println(guiOut);
                    break;
                case '3':
                    guiOut = "Account is already logged in.";
                    //System.out.println(guiOut);
                    break;
                case '4':
                    String info = res.substring(1);
                    String[] strikes = info.split(":");
                    guiOut = "Password is incorrect. You now have " + strikes[0] + " strike(s).\nYour account will lock at 3 strikes.\nYou have " + strikes[1] +  " attempts remaining." ;
                    //System.out.println(guiOut);
                    break;
            }
        } else if (!isConnected) {
            guiOut = "Please connect to the server first.";
            System.out.println(guiOut);
        } else {
            guiOut = "Already logged in.";
            System.out.println(guiOut);
        }
        return guiOut;
    }

    public void logout() {
        String guiOut = "";
        if (isLoggedIn) {
            isLoggedIn = false;
            System.out.println("Logged out.");
        } else {
            System.out.println("Not logged in.");
        }
    }

    //Needs pass and email validation
    public String register(String username, String password, String email) {
        String guiOut = ""; // string that sends response to clientGUI
        // Prepare data for registration request
        String data = "2" + username + ":" + password + ":" + email;
        if (isConnected){
            passValid = RegexEmail.validPassword(password);
            emailValid = RegexEmail.validEmailAddress((email));
            if (passValid && emailValid) {
                // Send the registration data to the server
                String res = clientConnection.send(data);
                System.out.println("CLIENT receive: " + res);
                // Handle server response
                switch (res) {
                    case "0":
                        guiOut = "User successfully registered.";
                        System.out.println("User successfully registered.");
                        break;
                    case "1":
                        guiOut = "Username already exists.";
                        System.out.println("Username already exists.");
                        break;
                    default:
                        guiOut = "Registration failed. Please try again.";
                        System.out.println("Registration failed. Please try again.");
                        break;
                }
            } else {
                System.out.println("Please connect to the server first.");
            }
        } else if (!passValid){
            guiOut = "Please enter a valid password.";
        } else if (!emailValid){
            guiOut = "Please enter a valid email.";
        }
        return guiOut;
    }

    // New recoverPassword method
    public String recoverPassword(String username) {
        String guiOut = "";
        String data = "3" + username;
        if (isConnected) {
            String res = clientConnection.send(data);
            // Simulate sending a temporary password to the user's registered email
            System.out.println("CLIENT receive: " + res);
            switch (res){
                case "0":
                    guiOut = "Temporary password sent to the email associated with username:\n" + username;
                    System.out.println(guiOut);
                    break;
                case "1":
                    guiOut = "No account with that user exists.";
                    System.out.println(guiOut);
                    break;
                default:
                    guiOut = "An error occurred.";
                    System.out.println(guiOut);
            }
        } else {
            System.out.println("Please connect to the server first.");
        }
        return guiOut;
    }

    // Connection status method -- what is this for??
    public void printConnectionStatus() {
        System.out.println("Server: " + HOST);
        System.out.println("Connected: " + isConnected);
        System.out.println("Logged In: " + isLoggedIn);
    }
}

//once u log in
//whos on how many acc are logged in how many accounts are not/ connection status