import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.util.*;
import java.util.List;

class User {
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

public class ChatApplication {

    private static Map<String, User> users = new HashMap<>();
    private static Map<String, java.util.List<String>> chats = new HashMap<>();
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultListModel<String> chatListModel;
    private JPanel chatWindowPanel;

    public ChatApplication() {
        frame = new JFrame("Chat Application");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        chatListModel = new DefaultListModel<>();

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createRegistrationPanel(), "Register");
        mainPanel.add(createMainChatPanel(), "MainChat");

        frame.add(mainPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new LineBorder(Color.GRAY, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (users.containsKey(username) && users.get(username).getPassword().equals(password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful");
                cardLayout.show(mainPanel, "MainChat");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials");
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "Register"));

        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        return panel;
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new LineBorder(Color.GRAY, 2));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(frame, "Invalid email format");
                return;
            }

            if (users.containsKey(username)) {
                JOptionPane.showMessageDialog(frame, "Username already taken");
            } else {
                users.put(username, new User(username, email, password));
                chats.put(username, new ArrayList<>());
                chatListModel.addElement(username);
                JOptionPane.showMessageDialog(frame, "Registration Successful");
                cardLayout.show(mainPanel, "Login");
            }
        });

        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Email"));
        panel.add(emailField);
        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(registerButton);

        return panel;
    }

    private JPanel createMainChatPanel() {
        JPanel mainChatPanel = new JPanel(new BorderLayout());
        JPanel chatListPanel = new JPanel(new BorderLayout());
        chatWindowPanel = new JPanel(new BorderLayout());

        chatListPanel.setBorder(new LineBorder(Color.GRAY, 2));
        chatWindowPanel.setBorder(new LineBorder(Color.GRAY, 2));

        // Chat List Section
        JList<String> chatList = new JList<>(chatListModel);
        JButton newChatButton = new JButton("New Chat");
        chatListPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
        chatListPanel.add(newChatButton, BorderLayout.SOUTH);

        // Placeholder for Chat Window
        JLabel placeholder = new JLabel("Open a chat", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.PLAIN, 18));
        chatWindowPanel.add(placeholder, BorderLayout.CENTER);

        // New Chat Button Action
        newChatButton.addActionListener(e -> {
            String recipient = JOptionPane.showInputDialog(frame, "Enter the username of the person you want to chat with:");
            if (recipient != null && !recipient.isEmpty()) {
                if (users.containsKey(recipient)) {
                    if (!chatListModel.contains(recipient)) {
                        chatListModel.addElement(recipient);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "User not found");
                }
            }
        });

        // Chat Selection Action
        chatList.addListSelectionListener(e -> {
            String selectedChat = chatList.getSelectedValue();
            if (selectedChat != null) {
                chatWindowPanel.removeAll();
                chatWindowPanel.add(createChatPanel(selectedChat), BorderLayout.CENTER);
                chatWindowPanel.revalidate();
                chatWindowPanel.repaint();
            }
        });

        mainChatPanel.add(chatListPanel, BorderLayout.WEST);
        mainChatPanel.add(chatWindowPanel, BorderLayout.CENTER);

        return mainChatPanel;
    }

    private JPanel createChatPanel(String recipient) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        JButton attachFileButton = new JButton("Attach File");

        chatArea.setText("");
        List<String> messages = chats.get(recipient);
        for (String msg : messages) {
            chatArea.append(msg + "\n");
        }

        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                messages.add("You: " + message);
                chatArea.append("You: " + message + "\n");
                messageField.setText("");
            }
        });

        attachFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                messages.add("You sent a file: " + file.getName());
                chatArea.append("You sent a file: " + file.getName() + "\n");
            }
        });

        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);
        panel.add(attachFileButton, BorderLayout.NORTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatApplication::new);
    }
}
