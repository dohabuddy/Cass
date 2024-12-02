package client;
import java.io.*;
import java.net.*;
// Independent Constructors for Client and Server
// Client: Send, then Receive/listen
// Server: Receive/Listen then Send
public class Network {
    //  Port is a constant variable
    private static final int PORT = 8000;
    //  Essential indexing variables
    private String name;
    private int id;
    // Handling peer-to-peer communication
    private Socket socket;
    private BufferedReader datain;
    private DataOutputStream dataout;
    //  --  Network Object Constructor for Client Program   --
    public Network(String host){
        try {
            // Construct peer-to-peer socket
            socket = new Socket(host, PORT);
            // Wrap socket in I/O stream objects
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            System.out.println("Host " + host + " at port " + PORT + " is unavailable.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Unable to create I/O streams.");
            System.exit(1);
        }   //  End Catch
    }   //  --  End Client Network Object   --
    //  --  Network Object Constructor for Server Program   --
    public Network(int id, Socket socket) {
        this.id = id;
        this.name = Integer.toString(id);
        try {
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }   //  End Catch
    }   //  --  End Server Network Object   --
    // ClientGUI -> Client -> Network -> "Server" -> Network -> Client -> ClientGUI
    // Client -> Network - clientConnection.Send(String)
    // Network -> Server - server.parseInput(txtIn)
    // Server -> Network - the return message from parseInput()
    //  --  Send String Method  --
    public String send(String message) {
        String returnMessage = "";
        try {
            dataout.writeBytes(message + "\n"); //  Write string to bytes
            dataout.flush();    // Send string to server
            returnMessage = "";    //  Empty string for response
            do {    //  Read for input while the response string is empty
                //socket.setSoTimeout(5000);  //  Timeout after 5 seconds if something goes wrong
                returnMessage = datain.readLine();
            } while (returnMessage.equals(""));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }   //  End Catch
        return returnMessage;
    }   //  --  End Send Method --
    //  --  Receive Response Method --
    public String receive(){
        String response = "";
      try {
          response = datain.readLine();
      } catch (IOException e){
          e.printStackTrace();
          System.exit(1);
      } //  End Catch
      return response;
    }   //  --  End Receive --
}   //  END NETWORK CLASS