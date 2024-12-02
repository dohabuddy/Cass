package server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JButton updateButton;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel registeredAccountsLabel;

    private Server serverInstance;
    private Thread serverThread;
    private boolean isServerRunning = false;

    private int registeredUsers;
    public ServerGUI() {
        setTitle("Server Management Console");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        updateButton = new JButton("Update");
        stopButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(updateButton);

        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Server Status: Stopped");
        registeredAccountsLabel = new JLabel("Registered Accounts: 0");
        statusPanel.add(statusLabel);
        statusPanel.add(registeredAccountsLabel);

        String[] columnNames = {"Logged-In Users", "Locked-Out Users"};
        tableModel = new DefaultTableModel(columnNames, 0);
        usersTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(usersTable);

        add(controlPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        setupActionListeners();
    }

    private void setupActionListeners() {
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        updateButton.addActionListener(e -> updateServerStatus());
    }

    private void startServer() {
        if (!isServerRunning) {
            serverThread = new Thread(() -> {
                try {
                    serverInstance = new Server();
                    serverInstance.listen();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error starting server: " + e.getMessage(),
                            "Server Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            serverThread.start();

            statusLabel.setText("Server Status: Running");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            isServerRunning = true;
        }
    }

    private void stopServer() {
        if (isServerRunning) {
            try {
                if (serverInstance != null) {
                    serverInstance.stop();
                }

                if (serverThread != null) {
                    serverThread.interrupt();
                }

                statusLabel.setText("Server Status: Stopped");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                isServerRunning = false;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error stopping server: " + e.getMessage(),
                        "Server Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void updateServerStatus() {
        if (isServerRunning && serverInstance != null) {
            try {
                // Fetch data from the server instance
                int registeredUsers = serverInstance.getNumberOfRegisteredUsers();
                List<String> loggedInUsers = serverInstance.getLoggedInUsers();
                List<String> lockedOutUsers = serverInstance.getLockedOutUsers();

                registeredAccountsLabel.setText("Registered Accounts: " + registeredUsers);

                tableModel.setRowCount(0);
                int maxRows = Math.max(loggedInUsers.size(), lockedOutUsers.size());
                for (int i = 0; i < maxRows; i++) {
                    String loggedInUser = i < loggedInUsers.size() ? loggedInUsers.get(i) : "";
                    String lockedOutUser = i < lockedOutUsers.size() ? lockedOutUsers.get(i) : "";
                    tableModel.addRow(new Object[]{loggedInUser, lockedOutUser});
                }

                if (!loggedInUsers.isEmpty()) {
                    String loggedInDetails = String.join(", ", loggedInUsers);
                    statusLabel.setText("Logged-In Users (" + loggedInUsers.size() + "): " + loggedInDetails);
                } else {
                    statusLabel.setText("Logged-In Users: None");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating server status: " + e.getMessage(),
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Server is not running. Please start the server first.",
                    "Update Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}