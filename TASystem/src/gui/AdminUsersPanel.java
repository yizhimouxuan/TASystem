package gui;

import model.CV;
import model.TAProfile;
import model.User;
import model.enums.UserRole;
import service.AdminService;
import service.CVService;
import service.ProfileService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static gui.MainFrame.*;

/**
 * Admin users management panel with search, filter, view details,
 * and freeze/unfreeze functionality.
 */
public class AdminUsersPanel extends JPanel {

    private MainFrame mainFrame;
    private JComboBox<String> roleCombo;
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<User> currentUsers;

    public AdminUsersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Search, view and manage system users");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titleLeftPanel = new JPanel(new GridLayout(2, 1));
        titleLeftPanel.setBackground(BG_MAIN);
        titleLeftPanel.add(titleLabel);
        titleLeftPanel.add(subtitleLabel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(PANEL_BG);
        searchPanel.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(10, 15, 10, 15)));

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleCombo = new JComboBox<>(new String[]{"All", "TA", "MO", "ADMIN"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setBackground(new Color(250, 250, 252));
        roleCombo.setBorder(new LineBorder(BORDER, 1, true));

        JLabel kwLabel = new JLabel("Search:");
        kwLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        searchField.setBackground(new Color(250, 250, 252));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> loadData());
        searchBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { searchBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { searchBtn.setBackground(PRIMARY); }
        });

        searchPanel.add(roleLabel);
        searchPanel.add(roleCombo);
        searchPanel.add(Box.createHorizontalStrut(15));
        searchPanel.add(kwLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchBtn);

        // Users table
        String[] columns = {"Account", "Name", "Email", "Role", "Frozen"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(235, 238, 255));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PANEL_BG);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBorder(new EmptyBorder(0, 0, 0, 0));

        // Frozen column renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Boolean frozen = (Boolean) value;
                if (Boolean.TRUE.equals(frozen)) {
                    label.setText("YES");
                    label.setForeground(DANGER);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setText("NO");
                    label.setForeground(SUCCESS);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setBackground(PANEL_BG);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(BG_MAIN);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton viewBtn = new JButton("View Details");
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewBtn.setBackground(ACCENT);
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);
        viewBtn.setBorderPainted(false);
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { viewBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { viewBtn.setBackground(ACCENT); }
        });
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && currentUsers != null && row < currentUsers.size()) {
                viewUserDetails(currentUsers.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton freezeBtn = new JButton("Freeze / Unfreeze");
        freezeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        freezeBtn.setBackground(WARNING);
        freezeBtn.setForeground(Color.WHITE);
        freezeBtn.setFocusPainted(false);
        freezeBtn.setBorderPainted(false);
        freezeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        freezeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { freezeBtn.setBackground(new Color(245, 173, 67)); }
            @Override public void mouseExited(MouseEvent e) { freezeBtn.setBackground(WARNING); }
        });
        freezeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && currentUsers != null && row < currentUsers.size()) {
                toggleFreeze(currentUsers.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        actionPanel.add(viewBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(freezeBtn);

        // Combine
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BG_MAIN);
        northPanel.add(titleLeftPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Load data
        loadData();
    }

    private void loadData() {
        String roleStr = (String) roleCombo.getSelectedItem();
        String keyword = searchField.getText().trim();

        String roleFilter = null;
        if (!"All".equals(roleStr)) {
            roleFilter = roleStr;
        }

        currentUsers = AdminService.getAllUsers(roleFilter, keyword.isEmpty() ? null : keyword);

        tableModel.setRowCount(0);
        if (currentUsers != null) {
            for (User user : currentUsers) {
                tableModel.addRow(new Object[]{
                        user.getAccount(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().toString(),
                        user.isFrozen()
                });
            }
        }
    }

    private void viewUserDetails(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "User Details", true);
        dialog.setSize(520, user.getRole() == UserRole.TA ? 620 : 380);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(BG_MAIN);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_BG);
        card.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new CompoundBorder(
                new LineBorder(new Color(220, 222, 225), 1, true),
                new EmptyBorder(20, 24, 16, 24))));

        // Header
        JLabel titleLabel = new JLabel("User Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Account: " + user.getAccount());
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(TEXT_SECOND);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLabel.setBorder(new EmptyBorder(2, 0, 10, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(subLabel);
        card.add(sep);
        card.add(Box.createVerticalStrut(16));

        // Basic Info section
        card.add(createDetailBlock("Account", user.getAccount(), PRIMARY));
        card.add(Box.createVerticalStrut(12));
        card.add(createDetailBlock("Name", user.getName(), ACCENT));
        card.add(Box.createVerticalStrut(12));
        card.add(createDetailBlock("Email", user.getEmail(), new Color(67, 97, 238)));
        card.add(Box.createVerticalStrut(12));
        card.add(createDetailBlock("Role", user.getRole().toString(), WARNING));
        card.add(Box.createVerticalStrut(12));
        card.add(createStatusBlock("Frozen", user.isFrozen()));
        card.add(Box.createVerticalStrut(16));

        // TA-specific info
        if (user.getRole() == UserRole.TA) {
            JSeparator sep2 = new JSeparator();
            sep2.setForeground(BORDER);
            sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(sep2);
            card.add(Box.createVerticalStrut(16));

            JLabel taTitle = new JLabel("TA Information");
            taTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            taTitle.setForeground(TEXT);
            taTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            taTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
            card.add(taTitle);

            TAProfile profile = ProfileService.getProfile(user.getAccount());
            if (profile != null) {
                card.add(createDetailBlock("Education", profile.getEducation(), PRIMARY));
                card.add(Box.createVerticalStrut(10));
                card.add(createDetailBlock("Major", profile.getMajor(), ACCENT));
                card.add(Box.createVerticalStrut(10));
                card.add(createDetailBlock("GPA", profile.getGpa(), SUCCESS));
                card.add(Box.createVerticalStrut(10));
                card.add(createDetailBlock("Phone", profile.getPhone(), new Color(200, 170, 100)));
                card.add(Box.createVerticalStrut(10));
                card.add(createMultilineBlock("Skills", profile.getSkills()));
                card.add(Box.createVerticalStrut(10));
                card.add(createMultilineBlock("Experience", profile.getExperience()));
                card.add(Box.createVerticalStrut(10));
                card.add(createMultilineBlock("Availability", profile.getAvailability()));
                card.add(Box.createVerticalStrut(10));
                card.add(createDetailBlock("Preferred Types", profile.getPreferredTypes(), WARNING));
                card.add(Box.createVerticalStrut(14));
            } else {
                JLabel noProfile = new JLabel("No profile found.");
                noProfile.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                noProfile.setForeground(TEXT_SECOND);
                noProfile.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(noProfile);
                card.add(Box.createVerticalStrut(14));
            }

            // CV section
            JSeparator sep3 = new JSeparator();
            sep3.setForeground(BORDER);
            sep3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sep3.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(sep3);
            card.add(Box.createVerticalStrut(14));

            JLabel cvTitle = new JLabel("CV Information");
            cvTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            cvTitle.setForeground(TEXT);
            cvTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            cvTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
            card.add(cvTitle);

            CV cv = CVService.getCV(user.getAccount());
            if (cv != null) {
                card.add(createDetailBlock("File Name", cv.getFileName(), PRIMARY));
                card.add(Box.createVerticalStrut(10));
                card.add(createDetailBlock("Qualified", cv.isQualified() ? "Yes" : "No", cv.isQualified() ? SUCCESS : DANGER));
                card.add(Box.createVerticalStrut(10));
                card.add(createMultilineBlock("Comment", (cv.getRejectReason() != null && !cv.getRejectReason().isEmpty()) ? cv.getRejectReason() : "-"));
                card.add(Box.createVerticalStrut(14));

                // CV action buttons
                JPanel cvBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                cvBtnPanel.setBackground(PANEL_BG);
                cvBtnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JButton qualifyBtn = new JButton("Mark as Qualified");
                qualifyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                qualifyBtn.setBackground(SUCCESS);
                qualifyBtn.setForeground(Color.WHITE);
                qualifyBtn.setFocusPainted(false);
                qualifyBtn.setBorderPainted(false);
                qualifyBtn.setPreferredSize(new Dimension(150, 34));
                qualifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                qualifyBtn.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { qualifyBtn.setBackground(new Color(36, 176, 162)); }
                    @Override public void mouseExited(MouseEvent e) { qualifyBtn.setBackground(SUCCESS); }
                });

                JButton unqualifyBtn = new JButton("Mark as Unqualified");
                unqualifyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                unqualifyBtn.setBackground(DANGER);
                unqualifyBtn.setForeground(Color.WHITE);
                unqualifyBtn.setFocusPainted(false);
                unqualifyBtn.setBorderPainted(false);
                unqualifyBtn.setPreferredSize(new Dimension(150, 34));
                unqualifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                unqualifyBtn.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { unqualifyBtn.setBackground(new Color(219, 61, 101)); }
                    @Override public void mouseExited(MouseEvent e) { unqualifyBtn.setBackground(DANGER); }
                });

                qualifyBtn.addActionListener(e -> {
                    CVService.markQualified(user.getAccount(), true, "");
                    JOptionPane.showMessageDialog(dialog, "CV marked as qualified!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    viewUserDetails(user);
                });

                unqualifyBtn.addActionListener(e -> {
                    String reason = JOptionPane.showInputDialog(dialog, "Enter rejection reason:", "Reject CV", JOptionPane.QUESTION_MESSAGE);
                    if (reason != null && !reason.trim().isEmpty()) {
                        CVService.markQualified(user.getAccount(), false, reason.trim());
                        JOptionPane.showMessageDialog(dialog, "CV marked as unqualified.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        viewUserDetails(user);
                    }
                });

                if (cv.isQualified()) {
                    qualifyBtn.setEnabled(false);
                } else {
                    unqualifyBtn.setEnabled(false);
                }

                cvBtnPanel.add(qualifyBtn);
                cvBtnPanel.add(unqualifyBtn);
                card.add(cvBtnPanel);
            } else {
                JLabel noCV = new JLabel("No CV uploaded.");
                noCV.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                noCV.setForeground(TEXT_SECOND);
                noCV.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(noCV);
            }
        }

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom OK button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BG_MAIN);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 12, 0));

        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okBtn.setBackground(PRIMARY);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorderPainted(false);
        okBtn.setPreferredSize(new Dimension(120, 40));
        okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dialog.dispose());
        okBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { okBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { okBtn.setBackground(PRIMARY); }
        });

        bottomPanel.add(okBtn);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createDetailBlock(String label, String value, Color accentColor) {
        JPanel block = new JPanel(new BorderLayout(10, 0));
        block.setBackground(PANEL_BG);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(4, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel(new GridLayout(1, 2, 10, 0));
        content.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECOND);
        lbl.setPreferredSize(new Dimension(120, 22));

        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(TEXT);

        content.add(lbl);
        content.add(val);

        block.add(accent, BorderLayout.WEST);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JPanel createStatusBlock(String label, boolean frozen) {
        JPanel block = new JPanel(new BorderLayout(10, 0));
        block.setBackground(PANEL_BG);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel accent = new JPanel();
        accent.setBackground(frozen ? DANGER : SUCCESS);
        accent.setPreferredSize(new Dimension(4, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        content.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECOND);
        lbl.setPreferredSize(new Dimension(120, 22));

        JLabel badge = new JLabel(frozen ? "YES" : "NO");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 12, 3, 12));
        badge.setBackground(frozen ? new Color(255, 235, 238) : new Color(232, 247, 245));
        badge.setForeground(frozen ? DANGER : SUCCESS);

        content.add(lbl);
        content.add(badge);

        block.add(accent, BorderLayout.WEST);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JPanel createMultilineBlock(String label, String value) {
        JPanel block = new JPanel(new BorderLayout(10, 0));
        block.setBackground(PANEL_BG);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accent = new JPanel();
        accent.setBackground(new Color(200, 210, 225));
        accent.setPreferredSize(new Dimension(4, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel(new BorderLayout(0, 2));
        content.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECOND);

        JTextArea area = new JTextArea(value != null ? value : "-");
        area.setFont(new Font("Segoe UI", Font.BOLD, 14));
        area.setForeground(TEXT);
        area.setBackground(PANEL_BG);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBorder(null);

        content.add(lbl, BorderLayout.NORTH);
        content.add(area, BorderLayout.CENTER);

        block.add(accent, BorderLayout.WEST);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private void toggleFreeze(User user) {
        String action = user.isFrozen() ? "unfreeze" : "freeze";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action + " user: " + user.getAccount() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = AdminService.freezeUser(user.getAccount(), !user.isFrozen());
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "User " + action + "d successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to " + action + " user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text != null ? text : "-");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT);
        return label;
    }
}
