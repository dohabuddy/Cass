
package server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JTable connectedUsersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel registeredAccountsLabel;

    private Server serverInstance;
    private DBMS databaseManager;
    private Thread serverThread;
    private boolean isServerRunning = false;

    public ServerGUI() {
        // Initialize the GUI
        setTitle("Server Management Console");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create control panel
        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        // Create status panel
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Server Status: Stopped");
        registeredAccountsLabel = new JLabel("Registered Accounts: 0");
        statusPanel.add(statusLabel);
        statusPanel.add(registeredAccountsLabel);

        // Create connected users table
        String[] columnNames = {"Client ID", "Username", "Connection Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        connectedUsersTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(connectedUsersTable);

        // Add components to frame
        add(controlPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Add action listeners
        setupActionListeners();
    }

    private void setupActionListeners() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });
    }

    private void startServer() {
        if (!isServerRunning) {
            // Initialize database connection using getter methods for credentials
            try {
                Server.startServer();
                //updateRegisteredAccountsCount();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Could not connect to database: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Start server in a separate thread
            serverThread = new Thread(() -> {
                serverInstance = new Server();
                serverInstance.listen();
            });
            serverThread.start();

            // Update UI
            statusLabel.setText("Server Status: Running");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            isServerRunning = true;

            // Start a thread to update connected users
            //startConnectionMonitorThread();
        }
    }

    private void stopServer() {
        if (isServerRunning) {
            try {
                // Close server socket
                if (Server.serversocket != null) {
                    Server.serversocket.close();
                }

                // Interrupt server thread
                if (serverThread != null) {
                    serverThread.interrupt();
                }

                // Clear connected users
                tableModel.setRowCount(0);

                // Update UI
                statusLabel.setText("Server Status: Stopped");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                isServerRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
//    private void startConnectionMonitorThread() {
//        Thread monitorThread = new Thread(() -> {
//            while (isServerRunning) {
//                // Update connected users
//                updateConnectedUsers();
//
//                // Update registered accounts count
//                updateRegisteredAccountsCount();
//
//                try {
//                    Thread.sleep(5000); // Update every 5 seconds
//                } catch (InterruptedException e) {
//                    break;
//                }
//            }
//        });
//        monitorThread.start();
//    }
//
//    private void updateConnectedUsers() {
//        SwingUtilities.invokeLater(() -> {
//            // Clear existing table
//            tableModel.setRowCount(0);
//
//            // Populate table with current connections
//            if (serverInstance != null && Server.clientConnections != null) {
//                for (MultiThread connection : Server.clientConnections) {
//                    User user = serverInstance.getUserByConnectionId((int) connection.getId()); // Cast to long if needed
//                    if (user != null) {
//                        tableModel.addRow(new Object[] {
//                                (int) connection.getId(),                   // Cast to int if getId() returns a long
//                                user.getUser(),                             // Username
//                                serverInstance.getClientConnectionTime((int) connection.getId()) // Cast to long if needed
//                        });
//                    }
//                }
//            }
//        });
//    }
//
//    private void updateRegisteredAccountsCount() {
//        try {
//            // Query to count registered accounts
//            String countQuery = "SELECT COUNT(*) FROM users";
//            Connection conn = DriverManager.getConnection(
//                    "jdbc:mysql://127.0.0.1:3306/sys",
//                    DBMS.getUsernameSQL(), // Use the getter method
//                    DBMS.getPasswordSQL()  // Use the getter method
//            );
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(countQuery);
//
//            if (rs.next()) {
//                int accountCount = rs.getInt(1);
//                registeredAccountsLabel.setText("Registered Accounts: " + accountCount);
//            }
//
//            conn.close();
//        } catch (SQLException e) {
//            registeredAccountsLabel.setText("Registered Accounts: N/A");
//        }
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}
//updated verrr
