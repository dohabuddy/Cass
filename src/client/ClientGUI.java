package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI {
    private Client client;
    private JFrame loginFrame;
    private JFrame forgottenPassFrame;
    private JFrame registerFrame;

    public static void main(String[] args) {
        new ClientGUI();
    }

    // New BackgroundPanel class
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
        // Initialize the client
        client = new Client();
        // Setup the login frame with background image
        String loginBackgroundPath = "/Users/yasmine/Downloads/Background.jpg"; // Update this so it pulls from within the project, not computer
        loginFrame = new JFrame("Login") {
            {
                setContentPane(new BackgroundPanel(loginBackgroundPath));
            }
        };
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(500, 500);
        loginFrame.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        // Show the login frame
        loginFrame.setVisible(true);

        JLabel headingLabel = new JLabel("LOGIN");
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Set font to bold and size 24
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the text
        //changing the color for login to blue thank god i finally found how
        headingLabel.setForeground(Color.BLUE);

        // Create components
        JLabel IPLabel = new JLabel("IP:");
        IPLabel.setForeground(Color.LIGHT_GRAY);
        JTextField IPField = new JTextField(20);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.LIGHT_GRAY);

        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.LIGHT_GRAY);

        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel registerLabel = new JLabel("<html><a href='#'>Not registered? Register</a></html>");
        JLabel forgottenPassLabel = new JLabel("<html><a href='#'>Forgot Password?</a></html>");
        JButton connectButton = new JButton("Connect");

        // GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(headingLabel, gbc);

        gbc.gridy++;
        loginFrame.add(IPLabel, gbc);

        gbc.gridy++;
        loginFrame.add(IPField, gbc);

        gbc.gridy++;
        loginFrame.add(connectButton, gbc);

        gbc.gridy++;
        loginFrame.add(usernameLabel, gbc);

        gbc.gridy++;
        loginFrame.add(usernameField, gbc);

        gbc.gridy++;
        loginFrame.add(passwordLabel, gbc);

        gbc.gridy++;
        loginFrame.add(passwordField, gbc);

        gbc.gridy++;
        loginFrame.add(loginButton, gbc);

        gbc.gridy++;
        loginFrame.add(forgottenPassLabel, gbc);

        gbc.gridy++;
        loginFrame.add(registerLabel, gbc);

        // -- ACTION LISTENERS --

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String serverIP = IPField.getText();
                String result = client.connect(serverIP);
                JOptionPane.showMessageDialog(loginFrame, result);
            }
        });

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Grabbing Info
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Attempting Login Thru Client
                String result = client.login(username, password);

                // -- Debugging Test
                //System.out.println("CLIENT GUI received: " + result);

                //Displaying Response From Client
                JOptionPane.showMessageDialog(loginFrame, result);
            }
        });

        // Action listener for Forgotten Password link
        forgottenPassLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openForgottenPassWindow();
            }
        });

        // Action listener for register link
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openRegisterWindow();
            }

        });


// Add heading label at the top using GridBagLayout constraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.gridx = 0; // Center horizontally
        gbc.gridy = 0; // Place at the top
        gbc.anchor = GridBagConstraints.CENTER; // Ensure center alignment
        loginFrame.add(headingLabel, gbc);
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

    // Method to open the registration window
    // Method to open the registration window
    private void openRegisterWindow() {
        // Set up the registration frame with background image
        String registerBackgroundPath = "/Users/yasmine/Downloads/imagess.jpeg"; // Update the path as needed
        registerFrame = new JFrame("Register") {
            {
                setContentPane(new BackgroundPanel(registerBackgroundPath));
            }
        };
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this frame when closed
        registerFrame.setSize(550, 550);
        registerFrame.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        registerFrame.setVisible(true);
        registerFrame.setLocation(500, 300); // Example position (x: 500, y: 300)

        JLabel headingLabel = new JLabel("REGISTER");
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Set font to bold and size 24
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the text
        headingLabel.setForeground(Color.BLUE); // Changing the color to gray

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
        gbc.insets = new Insets(20, 20, 20, 20); // Add padding around components

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.gridx = 0; // Center horizontally
        gbc.gridy = 0; // Place at the top
        gbc.anchor = GridBagConstraints.CENTER; // Ensure center alignment
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

        // Action listener for register button
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

        // Show the registration frame
        registerFrame.setLocation(500, 300); // Example position (x: 500, y: 200)
        registerFrame.setVisible(true);
    }
    // add background
    public class SwingDemo extends JFrame {
        Image img = Toolkit.getDefaultToolkit().getImage("/Users/yasmine/Downloads/background.jpg");

        public SwingDemo() {
            this.setContentPane(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            });
            setSize(800, 600);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

}
//add when user clicks enter on their keyboard it should work
// find how to make server work with client ask prof on how to do that

//use mysql workbech connect (check mark it in sql) the database add a query to the gui
//do it as seprate object
//connection thread and user interface goes to see whos logged in whos logged out talks to the database
//databse object everything static


//add IP section

