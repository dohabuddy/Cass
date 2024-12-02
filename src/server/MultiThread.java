package server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
public class MultiThread extends Thread {
    private boolean clientIsConnected;
    private int id;
    private BufferedReader datain;
    private DataOutputStream dataout;
    private Server server;
    private Socket socket;
    private Network network;

    public MultiThread(Network network, Socket socket, Server server) {
        this.network = network;
        this.socket = socket;
        this.server = server;
        clientIsConnected = true;
        //  Create the stream I/O objects on top of the socket
        try {
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void run () {
        // Server thread runs until the client terminates the connection
        while (clientIsConnected) {
            try {
                String txtOut = "";
                /*  always receives a String object with a newline (\n)
                    on the end due to how BufferedReader readLine() works.
                    The client adds it to the user's string but the BufferedReader
                    readLine() call strips it off   */
                // Using receive() instead of datain.readLine() cause...idk
                String txtIn = network.receive();
                if(txtIn != null) {
                    System.out.println("SERVER receive: " + txtIn);

                    // Sending txtIn to server instance of Server to parse the input and go through the operations
                    // txtOut is the response that parseInput returns after Server completes a process
                    txtOut = server.parseInput(txtIn);
                    //System.out.println("MT txtOut = " + txtOut); // -- debugging in case of response errors
                    if (txtOut == null || txtOut.isEmpty()) {
                        System.out.println("Response is empty.");
                        socket.setSoTimeout(5000); // prevents client from waiting for forever and ever and ever
                    } else {
                        if (txtOut.equals("5")) { // Checking for a disconnect message before responding
                            // Writing Final Response
                            dataout.writeBytes("0");
                            dataout.flush();

                            //Closing Streams
                            clientIsConnected = false;
                            datain.close();
                            server.removeID(id);

                            System.out.println("SERVER has disconnected.");
                        } else {
                            System.out.println("SERVER responding: " + txtOut);
                            dataout.writeBytes(txtOut + "\n");
                            dataout.flush();
                        }
                    }
                } else{
                        clientIsConnected = false;
                }
            }   //  End Try
            catch(IOException e) {
                e.printStackTrace();
                clientIsConnected = false;
            }
        }
    }
}