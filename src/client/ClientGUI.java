package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI {
    private Client client;
    private JFrame connectFrame, loginFrame, dashboardFrame, forgottenPassFrame, registerFrame, updatePasswordFrame;

    public static void main(String[] args) {
        new ClientGUI();
        new ClientGUI();
    }
    public class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            } catch (Exception e) {
                System.err.println("Error loading background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public ClientGUI() {
        client = new Client();
        showConnectWindow();
    }

    private void showConnectWindow() {
        connectFrame = new JFrame("Connect");
        connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectFrame.setSize(400, 200);
        connectFrame.setLayout(new GridBagLayout());

        JLabel IPLabel = new JLabel("Server IP:");
        JTextField IPField = new JTextField(20);
        JButton connectButton = new JButton("Connect");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        connectFrame.add(IPLabel, gbc);

        gbc.gridx = 1;
        connectFrame.add(IPField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        connectFrame.add(connectButton, gbc);

        connectButton.addActionListener(e -> {
            String serverIP = IPField.getText();
            String result = client.connect(serverIP);
            char success = result.charAt(0);
            String output = result.substring(1);
            if (success == '0') {
                JOptionPane.showMessageDialog(connectFrame, output);
                connectFrame.dispose();
                showLoginWindow();
            } else {
                JOptionPane.showMessageDialog(connectFrame, result, "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        connectFrame.setVisible(true);
    }

    private void showLoginWindow() {
        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(new GridBagLayout());

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel registerLabel = new JLabel("<html><a href='#'>Not registered? Register</a></html>");
        JLabel forgottenPassLabel = new JLabel("<html><a href='#'>Forgot Password?</a></html>");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginFrame.add(usernameLabel, gbc);

        gbc.gridx = 1;
        loginFrame.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginFrame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        loginFrame.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginFrame.add(loginButton, gbc);

        gbc.gridy = 3;
        loginFrame.add(registerLabel, gbc);

        gbc.gridy = 4;
        loginFrame.add(forgottenPassLabel, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String result = client.login(username, password);
            char success = result.charAt(0);
            String output = result.substring(1);
            if (success == '0') {
                JOptionPane.showMessageDialog(loginFrame, output);
                loginFrame.dispose();
                showDashboardWindow();
            } else {
                JOptionPane.showMessageDialog(loginFrame, result, "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openRegisterWindow();
            }
        });

        forgottenPassLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openForgottenPassWindow();
            }
        });

        loginFrame.setVisible(true);
    }

    private void showDashboardWindow() {
        dashboardFrame = new JFrame("Dashboard");
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setSize(400, 300);
        dashboardFrame.setLayout(new GridBagLayout());

        JButton shutdownButton = new JButton("Shutdown");
        JButton updatePasswordButton = new JButton("Update Password");
        JButton logoutButton = new JButton("Log Out");
        JButton serverAppButton = new JButton("Server Application");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dashboardFrame.add(shutdownButton, gbc);

        gbc.gridy = 1;
        dashboardFrame.add(updatePasswordButton, gbc);

        gbc.gridy = 2;
        dashboardFrame.add(logoutButton, gbc);

        gbc.gridy = 3;
        dashboardFrame.add(serverAppButton, gbc);

        shutdownButton.addActionListener(e -> {
            client.logout();
            client.disconnect();
            JOptionPane.showMessageDialog(dashboardFrame, "Shutdown complete.");
            dashboardFrame.dispose();
        });

        updatePasswordButton.addActionListener(e -> showUpdatePasswordWindow());

        logoutButton.addActionListener(e -> {
            client.logout();
            JOptionPane.showMessageDialog(dashboardFrame, "Logged out.");
            dashboardFrame.dispose();
            showLoginWindow();
        });

        // serverAppButton.addActionListener(e -> {
        //   String response = Server.serverapplication();
        // JOptionPane.showMessageDialog(dashboardFrame, response, "Server Application", JOptionPane.INFORMATION_MESSAGE);
        //   });

        dashboardFrame.setVisible(true);
    }

    private void showUpdatePasswordWindow() {
        updatePasswordFrame = new JFrame("Update Password");
        updatePasswordFrame.setSize(400, 200);
        updatePasswordFrame.setLayout(new GridBagLayout());

        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField(20);
        JLabel verifyPasswordLabel = new JLabel("Verify Password:");
        JPasswordField verifyPasswordField = new JPasswordField(30);
        JButton updateButton = new JButton("Update");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        updatePasswordFrame.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        updatePasswordFrame.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        updatePasswordFrame.add(verifyPasswordLabel, gbc);

        gbc.gridx = 1;
        updatePasswordFrame.add(verifyPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        updatePasswordFrame.add(updateButton, gbc);

        updateButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String verifyPassword = new String(verifyPasswordField.getPassword());

            // if (newPassword.equals(verifyPassword)) {
            //  String result = client.updatePassword(newPassword);
            //  JOptionPane.showMessageDialog(updatePasswordFrame, result);
            //  updatePasswordFrame.dispose();
            //  } else {
            //      JOptionPane.showMessageDialog(updatePasswordFrame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            //  }
        });

        updatePasswordFrame.setVisible(true);
    }

    private void openRegisterWindow() {
        // Set up the registration frame with background image
        String registerBackgroundPath = "/Users/yasmine/Downloads/imagess.jpeg"; // Update the path as needed
        registerFrame = new JFrame("Register") {
            {
                setContentPane(new BackgroundPanel(registerBackgroundPath));
            }
        };
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(550, 550);
        registerFrame.setLayout(new GridBagLayout());
        registerFrame.setVisible(true);
        registerFrame.setLocation(500, 300);

        JLabel headingLabel = new JLabel("REGISTER");
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel.setForeground(Color.BLUE);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.LIGHT_GRAY);

        JPasswordField passwordField = new JPasswordField(20);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.LIGHT_GRAY);
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        JTextField emailField = new JTextField(20);
        JButton registerButton = new JButton("Register");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        registerFrame.add(headingLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        registerFrame.add(emailLabel, gbc);

        // Add email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerFrame.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        registerFrame.add(usernameLabel, gbc);

        // Add username field
        gbc.gridx = 0;
        gbc.gridy = 4;
        registerFrame.add(usernameField, gbc);

        // Add password label
        gbc.gridx = 0;
        gbc.gridy = 5;
        registerFrame.add(passwordLabel, gbc);

        // Add password field
        gbc.gridx = 0;
        gbc.gridy = 6;
        registerFrame.add(passwordField, gbc);

        // Add register button
        gbc.gridx = 0;
        gbc.gridy = 7;
        registerFrame.add(registerButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String successMsg = "User successfully registered.";

                String response = client.register(username, password, email);
                JOptionPane.showMessageDialog(registerFrame, response);

                // Close the registration frame
                if(response.equals(successMsg)) {
                    registerFrame.dispose();
                }
            }
        });

        registerFrame.setLocation(500, 300);
        registerFrame.setVisible(true);
    }

    private void openForgottenPassWindow() {
        String forgottenPassBackgroundPath = "/Users/yasmine/Downloads/imagess.jpeg"; // Update as needed
        forgottenPassFrame = new JFrame("Recovery");
        forgottenPassFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        forgottenPassFrame.setContentPane(new BackgroundPanel(forgottenPassBackgroundPath));
        forgottenPassFrame.setSize(500, 500);
        forgottenPassFrame.setLayout(new GridBagLayout());

        JLabel headingLabel = new JLabel("RECOVERY");
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        headingLabel.setForeground(Color.BLUE);

        JTextField usernameField = new JTextField(20);
        JButton recoverButton = new JButton("Recover");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        forgottenPassFrame.add(headingLabel, gbc);

        gbc.gridy++;
        forgottenPassFrame.add(new JLabel("Username:"), gbc);

        gbc.gridy++;
        forgottenPassFrame.add(usernameField, gbc);

        gbc.gridy++;
        forgottenPassFrame.add(recoverButton, gbc);

        recoverButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            if (!username.isEmpty()) {
                String response = client.recoverPassword(username);
                System.out.println(response);
                JOptionPane.showMessageDialog(forgottenPassFrame, response);
            }
//            else {
//                JOptionPane.showMessageDialog(forgottenPassFrame, "Please enter a username!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
            forgottenPassFrame.dispose();
        });

        forgottenPassFrame.setVisible(true);
        forgottenPassFrame.getRootPane().setDefaultButton(recoverButton);
    }

}

// first window that pops up should be "connect" i will ask for the IP and the connect button
// once connected a window pops up that asks to log in keeping the register link (that opens the register window if clicked) and the forgotten password ( that if clicked the recovery window pops open)
// if the user logs in then a window pops open called dashboard and it has a shutdown button that shuts down calls log out and disconnect and another button called update password that pops a window open asks to enter new password and another one to verify password entered  (compare the two fields) and a log out button
//then a big red button server application - calls new operation (server application) - returns a string (pops up new window w the string)
