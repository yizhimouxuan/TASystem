package gui;

import data.DataStore;
import model.Job;
import model.User;
import model.enums.JobStatus;
import service.ApplicationService;
import service.CVService;
import service.JobService;
import service.ProfileService;
import util.WorkTimeUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static gui.MainFrame.*;

/**
 * TA job search panel with keyword/type filtering and job listing table.
 */
public class TASearchJobPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField keywordField;
    private JComboBox<String> typeCombo;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Job> currentJobs;

    public TASearchJobPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Search Jobs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Find and apply for teaching assistant positions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(PANEL_BG);
        searchPanel.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(12, 18, 12, 18)));

        JLabel kwLabel = new JLabel("Keyword:");
        kwLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        keywordField = new JTextField(20);
        keywordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        keywordField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        keywordField.setBackground(new Color(250, 250, 252));

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typeCombo = new JComboBox<>(new String[]{"All", "ASSISTANT", "INVIGILATOR"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.setBackground(new Color(250, 250, 252));
        typeCombo.setBorder(new LineBorder(BORDER, 1, true));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> doSearch());
        searchBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { searchBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { searchBtn.setBackground(PRIMARY); }
        });

        searchPanel.add(kwLabel);
        searchPanel.add(keywordField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(typeLabel);
        searchPanel.add(typeCombo);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchBtn);

        // Job table
        String[] columns = {"Job Title", "Type", "MO", "TAs Needed", "TAs Hired", "Status", "Action"};
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

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(180); // Job Title
        table.getColumnModel().getColumn(1).setPreferredWidth(110); // Type
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // MO
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // TAs Needed
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // TAs Hired
        table.getColumnModel().getColumn(5).setPreferredWidth(90);  // Status
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Action
        table.getColumnModel().getColumn(6).setMaxWidth(90);

        // Status column renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                String status = (value != null) ? value.toString() : "";
                if ("FULL".equals(status)) {
                    label.setForeground(SUCCESS);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("ENDED".equals(status)) {
                    label.setForeground(TEXT_SECOND);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(PRIMARY);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return label;
            }
        });

        // View button renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, r, c);
                label.setText("View");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.WHITE);
                label.setBackground(ACCENT);
                label.setOpaque(true);
                return label;
            }
        });

        // Click to view job details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (col == 6 && row >= 0 && currentJobs != null && row < currentJobs.size()) {
                    showJobDetail(currentJobs.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setBackground(PANEL_BG);

        // Combine
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BG_MAIN);
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initial load
        doSearch();
    }

    private void doSearch() {
        String keyword = keywordField.getText().trim();
        String typeStr = (String) typeCombo.getSelectedItem();

        if ("All".equals(typeStr)) {
            typeStr = null;
        }

        currentJobs = JobService.searchJobs(keyword.isEmpty() ? null : keyword, typeStr);

        tableModel.setRowCount(0);
        if (currentJobs != null) {
            for (Job job : currentJobs) {
                User mo = DataStore.findUserByAccount(job.getMoAccount());
                String moName = (mo != null && mo.getName() != null && !mo.getName().isEmpty()) ? mo.getName() : job.getMoAccount();
                int hired = ApplicationService.getHiredCount(job.getJobId());

                tableModel.addRow(new Object[]{
                        job.getTitle(),
                        job.getType(),
                        moName,
                        job.getTasNeeded(),
                        hired,
                        job.getStatus().toString(),
                        "View"
                });
            }
        }
    }

    private void showJobDetail(Job job) {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Job Details", true);
        dialog.setSize(480, 480);
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
                new EmptyBorder(24, 28, 20, 28))));

        JLabel titleLabel = new JLabel("Job Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Information about this job posting");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(TEXT_SECOND);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLabel.setBorder(new EmptyBorder(4, 0, 12, 0));

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(BORDER);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep1.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(subLabel);
        card.add(sep1);
        card.add(Box.createVerticalStrut(16));

        User mo = DataStore.findUserByAccount(job.getMoAccount());
        String moName = (mo != null && mo.getName() != null && !mo.getName().isEmpty()) ? mo.getName() : job.getMoAccount();
        int hired = ApplicationService.getHiredCount(job.getJobId());

        card.add(createDetailBlock("Job Title", job.getTitle(), PRIMARY));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("Type", job.getType().toString(), ACCENT));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("MO", moName, new Color(200, 170, 100)));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("TAs Needed", String.valueOf(job.getTasNeeded()), WARNING));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("TAs Hired", String.valueOf(hired), SUCCESS));
        card.add(Box.createVerticalStrut(14));
        card.add(createStatusBlock("Status", job.getStatus()));
        card.add(Box.createVerticalStrut(18));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(BORDER);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep2);
        card.add(Box.createVerticalStrut(18));

        card.add(createMultilineBlock("Work Time", WorkTimeUtil.formatForDisplay(job.getWorkTime())));
        card.add(Box.createVerticalStrut(18));
        card.add(createMultilineBlock("Description", job.getDescription()));
        card.add(Box.createVerticalStrut(18));
        card.add(createMultilineBlock("Requirements", job.getRequirements()));
        card.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom panel with Apply + Close buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setBackground(BG_MAIN);
        bottomPanel.setBorder(new EmptyBorder(5, 0, 12, 0));

        boolean alreadyApplied = ApplicationService.hasApplied(user.getAccount(), job.getJobId());

        JButton applyBtn = new JButton(alreadyApplied ? "Already Applied" : "Apply Now");
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        applyBtn.setBackground(alreadyApplied ? TEXT_SECOND : SUCCESS);
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.setBorderPainted(false);
        applyBtn.setPreferredSize(new Dimension(140, 40));
        applyBtn.setCursor(alreadyApplied ? Cursor.getDefaultCursor() : new Cursor(Cursor.HAND_CURSOR));
        applyBtn.setEnabled(!alreadyApplied);
        if (!alreadyApplied) {
            applyBtn.addActionListener(e -> {
                doApply(job, user);
                dialog.dispose();
            });
            applyBtn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { applyBtn.setBackground(new Color(36, 176, 162)); }
                @Override public void mouseExited(MouseEvent e) { applyBtn.setBackground(SUCCESS); }
            });
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(PRIMARY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { closeBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { closeBtn.setBackground(PRIMARY); }
        });

        bottomPanel.add(applyBtn);
        bottomPanel.add(closeBtn);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createDetailBlock(String label, String value, Color accentColor) {
        JPanel block = new JPanel(new BorderLayout(12, 0));
        block.setBackground(PANEL_BG);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(4, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 2));
        content.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECOND);

        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(new Font("Segoe UI", Font.BOLD, 15));
        val.setForeground(TEXT);

        content.add(lbl);
        content.add(val);

        block.add(accent, BorderLayout.WEST);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JPanel createStatusBlock(String label, JobStatus status) {
        JPanel block = new JPanel(new BorderLayout(12, 0));
        block.setBackground(PANEL_BG);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel accent = new JPanel();
        accent.setBackground(getStatusColor(status));
        accent.setPreferredSize(new Dimension(4, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        content.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECOND);
        lbl.setPreferredSize(new Dimension(100, 22));

        JLabel badge = new JLabel(status.toString());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 14, 4, 14));
        badge.setBackground(getStatusBgColor(status));
        badge.setForeground(getStatusColor(status));

        content.add(lbl);
        content.add(badge);

        block.add(accent, BorderLayout.WEST);
        block.add(content, BorderLayout.CENTER);
        return block;
    }

    private JPanel createMultilineBlock(String label, String value) {
        JPanel block = new JPanel(new BorderLayout(12, 0));
        block.setBackground(PANEL_BG);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accent = new JPanel();
        accent.setBackground(new Color(200, 210, 225));
        accent.setPreferredSize(new Dimension(4, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel(new BorderLayout(0, 4));
        content.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECOND);

        JTextArea area = new JTextArea(value != null ? value : "-");
        area.setFont(new Font("Segoe UI", Font.BOLD, 15));
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

    private Color getStatusColor(JobStatus status) {
        switch (status) {
            case PUBLISHED: return PRIMARY;
            case FULL: return SUCCESS;
            case ENDED: return TEXT_SECOND;
            case PENDING_REVIEW: return WARNING;
            case REJECTED: return DANGER;
            default: return TEXT;
        }
    }

    private Color getStatusBgColor(JobStatus status) {
        switch (status) {
            case PUBLISHED: return new Color(239, 241, 255);
            case FULL: return new Color(232, 247, 245);
            case ENDED: return new Color(240, 242, 245);
            case PENDING_REVIEW: return new Color(255, 248, 235);
            case REJECTED: return new Color(255, 235, 238);
            default: return new Color(240, 242, 245);
        }
    }

    private void doApply(Job job, User user) {
        // Check profile completion
        if (!ProfileService.isProfileComplete(user.getAccount())) {
            JOptionPane.showMessageDialog(this, "Please complete your profile before applying.", "Profile Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check CV
        if (!CVService.hasCV(user.getAccount())) {
            JOptionPane.showMessageDialog(this, "Please upload your CV before applying.", "CV Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = ApplicationService.apply(user.getAccount(), job.getJobId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Application submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit application. You may have already applied or the job is no longer available.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
