package gui;

import service.AuthService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static gui.MainFrame.*;

/**
 * Password panel with two tabs: Change Password and Forgot Password.
 */
public class PasswordPanel extends JPanel {

    private MainFrame mainFrame;

    public PasswordPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG_MAIN);

        // Center card panel
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setPreferredSize(new Dimension(460, 540));
        card.setBorder(new CompoundBorder(
                new EmptyBorder(0, 0, 8, 8),
                new CompoundBorder(
                    new LineBorder(new Color(230, 232, 235), 1, true),
                    new EmptyBorder(25, 45, 25, 45))));

        // Title
        JLabel titleLabel = new JLabel("Password Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Tabbed pane with custom styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(TEXT);
        tabbedPane.setBorder(new EmptyBorder(5, 0, 0, 0));
        tabbedPane.addTab("Change Password", createChangePasswordPanel());
        tabbedPane.addTab("Forgot Password", createForgotPasswordPanel());

        // Back link
        JLabel backLink = new JLabel("Back to Login");
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

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(tabbedPane, BorderLayout.CENTER);
        card.add(backPanel, BorderLayout.SOUTH);

        add(card);
    }

    private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel accountLabel = new JLabel("Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accountLabel.setForeground(TEXT);
        JTextField accountField = new JTextField(20);
        accountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        accountField.setBackground(new Color(250, 250, 252));

        JLabel oldLabel = new JLabel("Old Password");
        oldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        oldLabel.setForeground(TEXT);
        JPasswordField oldField = new JPasswordField(20);
        oldField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        oldField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        oldField.setBackground(new Color(250, 250, 252));

        JLabel newLabel = new JLabel("New Password");
        newLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newLabel.setForeground(TEXT);
        JPasswordField newField = new JPasswordField(20);
        newField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        newField.setBackground(new Color(250, 250, 252));

        JLabel confirmLabel = new JLabel("Confirm New Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(TEXT);
        JPasswordField confirmField = new JPasswordField(20);
        confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        confirmField.setBackground(new Color(250, 250, 252));

        JButton changeBtn = new JButton("Change Password");
        changeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changeBtn.setBackground(PRIMARY);
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.setBorderPainted(false);
        changeBtn.setPreferredSize(new Dimension(0, 40));
        changeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { changeBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { changeBtn.setBackground(PRIMARY); }
        });
        changeBtn.addActionListener(e -> {
            String account = accountField.getText().trim();
            String oldPw = new String(oldField.getPassword());
            String newPw = new String(newField.getPassword());
            String confirmPw = new String(confirmField.getPassword());

            if (account.isEmpty() || oldPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPw.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = AuthService.changePassword(account, oldPw, newPw);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                accountField.setText("");
                oldField.setText("");
                newField.setText("");
                confirmField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to change password. Check account and old password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 0;
        panel.add(accountLabel, gbc);
        gbc.gridy = 1;
        panel.add(accountField, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 8, 5, 8);
        panel.add(oldLabel, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 8, 5, 8);
        panel.add(oldField, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 8, 5, 8);
        panel.add(newLabel, gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 8, 5, 8);
        panel.add(newField, gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 8, 5, 8);
        panel.add(confirmLabel, gbc);
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 8, 5, 8);
        panel.add(confirmField, gbc);
        gbc.gridy = 8;
        gbc.insets = new Insets(18, 8, 6, 8);
        panel.add(changeBtn, gbc);

        return panel;
    }

    private JPanel createForgotPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel accountLabel = new JLabel("Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accountLabel.setForeground(TEXT);
        JTextField accountField = new JTextField(20);
        accountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        accountField.setBackground(new Color(250, 250, 252));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(TEXT);
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        emailField.setBackground(new Color(250, 250, 252));

        JLabel newLabel = new JLabel("New Password");
        newLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newLabel.setForeground(TEXT);
        JPasswordField newField = new JPasswordField(20);
        newField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        newField.setBackground(new Color(250, 250, 252));

        JLabel confirmLabel = new JLabel("Confirm New Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(TEXT);
        JPasswordField confirmField = new JPasswordField(20);
        confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        confirmField.setBackground(new Color(250, 250, 252));

        JButton resetBtn = new JButton("Reset Password");
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.setBackground(PRIMARY);
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setPreferredSize(new Dimension(0, 40));
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { resetBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { resetBtn.setBackground(PRIMARY); }
        });
        resetBtn.addActionListener(e -> {
            String account = accountField.getText().trim();
            String email = emailField.getText().trim();
            String newPw = new String(newField.getPassword());
            String confirmPw = new String(confirmField.getPassword());

            if (account.isEmpty() || email.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPw.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = AuthService.resetPassword(account, email, newPw);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successfully! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                accountField.setText("");
                emailField.setText("");
                newField.setText("");
                confirmField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password. Check account and email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 0;
        panel.add(accountLabel, gbc);
        gbc.gridy = 1;
        panel.add(accountField, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 8, 5, 8);
        panel.add(emailLabel, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 8, 5, 8);
        panel.add(emailField, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 8, 5, 8);
        panel.add(newLabel, gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 8, 5, 8);
        panel.add(newField, gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 8, 5, 8);
        panel.add(confirmLabel, gbc);
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 8, 5, 8);
        panel.add(confirmField, gbc);
        gbc.gridy = 8;
        gbc.insets = new Insets(18, 8, 6, 8);
        panel.add(resetBtn, gbc);

        return panel;
    }
}
