package gui;

import model.User;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static gui.MainFrame.*;

/**
 * Login panel with centered card layout containing account/password fields.
 */
public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField accountField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG_MAIN);

        // Center card panel with layered shadow effect
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setPreferredSize(new Dimension(420, 450));
        card.setBorder(new CompoundBorder(
                new EmptyBorder(0, 0, 8, 8),
                new CompoundBorder(
                    new LineBorder(new Color(230, 232, 235), 1, true),
                    new EmptyBorder(35, 45, 35, 45))));

        // Title with accent underline
        JLabel titleLabel = new JLabel("TA Recruitment System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(PANEL_BG);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Account field
        JLabel accountLabel = new JLabel("Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accountLabel.setForeground(TEXT);
        accountField = new JTextField(20);
        accountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountField.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)));
        accountField.setBackground(new Color(250, 250, 252));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(TEXT);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)));
        passwordField.setBackground(new Color(250, 250, 252));

        // Login button
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setBackground(PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setPreferredSize(new Dimension(0, 44));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(new EmptyBorder(0, 0, 0, 0));
        loginBtn.addActionListener(e -> doLogin());
        // Hover effect
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { loginBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { loginBtn.setBackground(PRIMARY); }
        });

        gbc.gridy = 0;
        formPanel.add(accountLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(accountField, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(6, 0, 6, 0);
        formPanel.add(passwordField, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(22, 0, 8, 0);
        formPanel.add(loginBtn, gbc);

        // Links panel
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        linksPanel.setBackground(PANEL_BG);

        JLabel registerLink = createLinkLabel("Create account");
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPanel("REGISTER");
            }
        });

        JLabel forgotLink = createLinkLabel("Forgot password?");
        forgotLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPanel("PASSWORD");
            }
        });

        linksPanel.add(registerLink);
        linksPanel.add(new JLabel("\u2022") {{ setForeground(TEXT_SECOND); }});
        linksPanel.add(forgotLink);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(linksPanel, gbc);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);

        // Enter key listener
        passwordField.addActionListener(e -> doLogin());
    }

    private JLabel createLinkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(PRIMARY);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    private void doLogin() {
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (account.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter account and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = AuthService.login(account, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid account or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (user.isFrozen()) {
            JOptionPane.showMessageDialog(this, "Your account has been frozen. Contact admin.", "Account Frozen", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainFrame.setCurrentUser(user);
        mainFrame.refreshHomePanels();

        // Route to appropriate home panel based on role
        switch (user.getRole()) {
            case TA:
                mainFrame.showPanel("TA_HOME");
                break;
            case MO:
                mainFrame.showPanel("MO_HOME");
                break;
            case ADMIN:
                mainFrame.showPanel("ADMIN_HOME");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown user role.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
