package server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JTable connectedUsersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel registeredAccountsLabel;

    private Server serverInstance;
    private Thread serverThread;
    private boolean isServerRunning = false;

    public ServerGUI() {
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
            // Start server in a separate thread
            serverThread = new Thread(() -> {
                try {
                    serverInstance = new Server();  // Initialize the server
                    serverInstance.listen();       // Start listening for connections
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error starting server: " + e.getMessage(),
                            "Server Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            serverThread.start();
            // Update UI
            statusLabel.setText("Server Status: Running");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            isServerRunning = true;
        }
    }

    private void stopServer() {
        if (isServerRunning) {
            try {
                // Stop the server
                if (serverInstance != null) {
                    serverInstance.stop();  // Implement a `stop` method in `Server`
                }
                // Interrupt the server thread
                if (serverThread != null) {
                    serverThread.interrupt();
                }

                // Update UI
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}