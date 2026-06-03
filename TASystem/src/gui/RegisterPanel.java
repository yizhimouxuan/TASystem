package gui;

import model.User;
import model.enums.UserRole;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static gui.MainFrame.*;

/**
 * Registration panel for new users to create an account.
 */
public class RegisterPanel extends JPanel {

    private MainFrame mainFrame;
    private JComboBox<String> roleCombo;
    private JTextField accountField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JTextField nameField;
    private JTextField emailField;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG_MAIN);

        // Center card panel
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setPreferredSize(new Dimension(460, 660));
        card.setBorder(new CompoundBorder(
                new EmptyBorder(0, 0, 8, 8),
                new CompoundBorder(
                    new LineBorder(new Color(230, 232, 235), 1, true),
                    new EmptyBorder(30, 45, 30, 45))));

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Join the TA Recruitment System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(PANEL_BG);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Role combo
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(TEXT);
        roleCombo = new JComboBox<>(new String[]{"TA", "MO", "ADMIN"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setBackground(new Color(250, 250, 252));
        roleCombo.setBorder(new LineBorder(BORDER, 1, true));

        // Account field
        JLabel accountLabel = new JLabel("Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accountLabel.setForeground(TEXT);
        accountField = new JTextField(20);
        accountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        accountField.setBackground(new Color(250, 250, 252));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(TEXT);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        passwordField.setBackground(new Color(250, 250, 252));

        // Confirm password field
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(TEXT);
        confirmField = new JPasswordField(20);
        confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        confirmField.setBackground(new Color(250, 250, 252));

        // Name field
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT);
        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        nameField.setBackground(new Color(250, 250, 252));

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(TEXT);
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        emailField.setBackground(new Color(250, 250, 252));

        // Register button
        JButton registerBtn = new JButton("Create Account");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setBackground(PRIMARY);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setPreferredSize(new Dimension(0, 42));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> doRegister());
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { registerBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { registerBtn.setBackground(PRIMARY); }
        });

        // Back link
        JLabel backLink = new JLabel("Already have an account? Sign in");
        backLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backLink.setForeground(PRIMARY);
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPanel("LOGIN");
            }
        });
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(PANEL_BG);
        backPanel.add(backLink);

        gbc.gridy = 0;
        formPanel.add(roleLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(roleCombo, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 0, 4, 0);
        formPanel.add(accountLabel, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 4, 0);
        formPanel.add(accountField, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(8, 0, 4, 0);
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 0, 4, 0);
        formPanel.add(passwordField, gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(8, 0, 4, 0);
        formPanel.add(confirmLabel, gbc);
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 0, 4, 0);
        formPanel.add(confirmField, gbc);
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 0, 4, 0);
        formPanel.add(nameLabel, gbc);
        gbc.gridy = 9;
        gbc.insets = new Insets(4, 0, 4, 0);
        formPanel.add(nameField, gbc);
        gbc.gridy = 10;
        gbc.insets = new Insets(8, 0, 4, 0);
        formPanel.add(emailLabel, gbc);
        gbc.gridy = 11;
        gbc.insets = new Insets(4, 0, 4, 0);
        formPanel.add(emailField, gbc);
        gbc.gridy = 12;
        gbc.insets = new Insets(18, 0, 5, 0);
        formPanel.add(registerBtn, gbc);
        gbc.gridy = 13;
        gbc.insets = new Insets(5, 0, 0, 0);
        formPanel.add(backPanel, gbc);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);
    }

    private void doRegister() {
        String roleStr = (String) roleCombo.getSelectedItem();
        UserRole role = UserRole.valueOf(roleStr);
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        // Validation
        if (account.isEmpty() || password.isEmpty() || confirm.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create concrete user subclass based on role
        User user;
        if (role == UserRole.TA) user = new model.TA();
        else if (role == UserRole.MO) user = new model.MO();
        else if (role == UserRole.ADMIN) user = new model.Admin();
        else user = new model.TA();
        user.setAccount(account);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        user.setFrozen(false);

        boolean success = AuthService.register(user);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            mainFrame.showPanel("LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "Account already exists or registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        accountField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        nameField.setText("");
        emailField.setText("");
        roleCombo.setSelectedIndex(0);
    }
}
