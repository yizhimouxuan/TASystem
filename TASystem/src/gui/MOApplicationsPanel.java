package gui;

import data.DataStore;
import model.Application;
import model.CV;
import model.Job;
import model.TAProfile;
import model.User;
import service.ApplicationService;
import service.CVService;
import service.JobService;
import service.ProfileService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static gui.MainFrame.*;

/**
 * MO applications panel with job selector, application table,
 * and Hire/Reject/View Profile action buttons.
 */
public class MOApplicationsPanel extends JPanel {

    private MainFrame mainFrame;
    private JComboBox<JobItem> jobCombo;
    private DefaultTableModel tableModel;
    private List<Application> currentApplications;

    public MOApplicationsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Job Applications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Review and manage applications for your jobs");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titleLeftPanel = new JPanel(new GridLayout(2, 1));
        titleLeftPanel.setBackground(BG_MAIN);
        titleLeftPanel.add(titleLabel);
        titleLeftPanel.add(subtitleLabel);

        // Job selector panel
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(PANEL_BG);
        selectorPanel.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(10, 15, 10, 15)));

        JLabel selectLabel = new JLabel("Select Job:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jobCombo = new JComboBox<>();
        jobCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jobCombo.setBackground(new Color(250, 250, 252));
        jobCombo.setBorder(new LineBorder(BORDER, 1, true));
        jobCombo.setPreferredSize(new Dimension(350, 28));
        jobCombo.addActionListener(e -> loadApplications());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(ACCENT);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadJobs());
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setBackground(ACCENT); }
        });

        selectorPanel.add(selectLabel);
        selectorPanel.add(jobCombo);
        selectorPanel.add(Box.createHorizontalStrut(10));
        selectorPanel.add(refreshBtn);

        // Applications table
        String[] columns = {"TA Account", "TA Name", "Skills", "Apply Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setBackground(PANEL_BG);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(BG_MAIN);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton hireBtn = new JButton("Hire");
        hireBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hireBtn.setBackground(SUCCESS);
        hireBtn.setForeground(Color.WHITE);
        hireBtn.setFocusPainted(false);
        hireBtn.setBorderPainted(false);
        hireBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hireBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hireBtn.setBackground(new Color(36, 176, 162)); }
            @Override public void mouseExited(MouseEvent e) { hireBtn.setBackground(SUCCESS); }
        });
        hireBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && currentApplications != null && row < currentApplications.size()) {
                hireApplication(currentApplications.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select an application.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rejectBtn.setBackground(DANGER);
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFocusPainted(false);
        rejectBtn.setBorderPainted(false);
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rejectBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { rejectBtn.setBackground(new Color(219, 61, 101)); }
            @Override public void mouseExited(MouseEvent e) { rejectBtn.setBackground(DANGER); }
        });
        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && currentApplications != null && row < currentApplications.size()) {
                rejectApplication(currentApplications.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select an application.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton viewBtn = new JButton("View TA Profile");
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
            if (row >= 0 && currentApplications != null && row < currentApplications.size()) {
                viewTAProfile(currentApplications.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select an application.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        actionPanel.add(viewBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(hireBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(rejectBtn);

        // Combine
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BG_MAIN);
        northPanel.add(titleLeftPanel, BorderLayout.NORTH);
        northPanel.add(selectorPanel, BorderLayout.CENTER);
        northPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Load jobs
        loadJobs();
    }

    private void loadJobs() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        jobCombo.removeAllItems();
        jobCombo.addItem(new JobItem("", "All"));
        List<Job> jobs = JobService.getMOJobs(user.getAccount());
        if (jobs != null) {
            for (Job job : jobs) {
                jobCombo.addItem(new JobItem(job.getJobId(), job.getTitle()));
            }
        }
    }

    private void loadApplications() {
        JobItem selected = (JobItem) jobCombo.getSelectedItem();
        if (selected == null) {
            tableModel.setRowCount(0);
            return;
        }

        User user = mainFrame.getCurrentUser();
        if (selected.jobId.isEmpty() && user != null) {
            currentApplications = ApplicationService.getApplicationsForMO(user.getAccount());
        } else {
            currentApplications = ApplicationService.getApplicationsForJob(selected.jobId);
        }

        tableModel.setRowCount(0);
        if (currentApplications != null) {
            for (Application app : currentApplications) {
                // Get TA profile for skills display
                TAProfile profile = ProfileService.getProfile(app.getTaAccount());
                String skills = (profile != null && profile.getSkills() != null) ? profile.getSkills() : "-";
                // Truncate skills if too long
                if (skills.length() > 40) {
                    skills = skills.substring(0, 40) + "...";
                }

                long ts = app.getApplyTime();
                String dateStr = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                User taUser = DataStore.findUserByAccount(app.getTaAccount());
                String taName = (taUser != null) ? taUser.getName() : app.getTaAccount();

                tableModel.addRow(new Object[]{
                        app.getTaAccount(),
                        taName,
                        skills,
                        dateStr,
                        app.getStatus().toString()
                });
            }
        }
    }

    private void hireApplication(Application app) {
        int confirm = JOptionPane.showConfirmDialog(this, "Hire this TA?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = ApplicationService.hire(app.getApplicationId());
        if (success) {
            JOptionPane.showMessageDialog(this, "TA hired successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadApplications();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to hire TA.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectApplication(Application app) {
        String reason = JOptionPane.showInputDialog(this, "Enter rejection reason:", "Reject Application", JOptionPane.QUESTION_MESSAGE);
        if (reason == null || reason.trim().isEmpty()) {
            return;
        }

        boolean success = ApplicationService.reject(app.getApplicationId(), reason.trim());
        if (success) {
            JOptionPane.showMessageDialog(this, "Application rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadApplications();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject application.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewTAProfile(Application app) {
        User taUser = DataStore.findUserByAccount(app.getTaAccount());
        TAProfile profile = ProfileService.getProfile(app.getTaAccount());

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "TA Profile", true);
        dialog.setSize(520, profile != null ? 620 : 380);
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
        JLabel titleLabel = new JLabel("TA Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Account: " + app.getTaAccount());
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

        // Basic Info
        card.add(createDetailBlock("Account", app.getTaAccount(), PRIMARY));
        card.add(Box.createVerticalStrut(12));
        card.add(createDetailBlock("Name", taUser != null ? taUser.getName() : "-", ACCENT));
        card.add(Box.createVerticalStrut(12));
        card.add(createDetailBlock("Email", taUser != null ? taUser.getEmail() : "-", new Color(67, 97, 238)));
        card.add(Box.createVerticalStrut(12));
        card.add(createDetailBlock("Role", "TA", WARNING));
        card.add(Box.createVerticalStrut(16));

        if (profile != null) {
            JSeparator sep2 = new JSeparator();
            sep2.setForeground(BORDER);
            sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(sep2);
            card.add(Box.createVerticalStrut(16));

            JLabel profileTitle = new JLabel("Profile Information");
            profileTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            profileTitle.setForeground(TEXT);
            profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            profileTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
            card.add(profileTitle);

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
            card.add(Box.createVerticalStrut(16));

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

            CV cv = CVService.getCV(app.getTaAccount());
            if (cv != null) {
                card.add(createDetailBlock("File Name", cv.getFileName(), PRIMARY));
                card.add(Box.createVerticalStrut(10));
                card.add(createDetailBlock("Qualified", cv.isQualified() ? "Yes" : "No", cv.isQualified() ? SUCCESS : DANGER));
                card.add(Box.createVerticalStrut(10));
                card.add(createMultilineBlock("Comment", (cv.getRejectReason() != null && !cv.getRejectReason().isEmpty()) ? cv.getRejectReason() : "-"));
            } else {
                JLabel noCV = new JLabel("No CV uploaded.");
                noCV.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                noCV.setForeground(TEXT_SECOND);
                noCV.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(noCV);
            }
        } else {
            JLabel noProfile = new JLabel("No profile found for this TA.");
            noProfile.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            noProfile.setForeground(TEXT_SECOND);
            noProfile.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(noProfile);
        }

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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

    /**
     * Wrapper class for job combo box display.
     */
    private static class JobItem {
        String jobId;
        String title;

        JobItem(String jobId, String title) {
            this.jobId = jobId;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
