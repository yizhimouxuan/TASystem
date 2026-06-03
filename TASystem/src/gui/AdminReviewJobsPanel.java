package gui;

import data.DataStore;
import model.Job;
import model.enums.JobStatus;
import service.ApplicationService;
import service.JobService;
import util.WorkTimeUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static gui.MainFrame.*;

/**
 * Admin panel for reviewing all job postings.
 * Allows approving or rejecting jobs with a reason.
 */
public class AdminReviewJobsPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Job> currentJobs;

    public AdminReviewJobsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Review Jobs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Approve or reject job postings");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titleLeftPanel = new JPanel(new GridLayout(2, 1));
        titleLeftPanel.setBackground(BG_MAIN);
        titleLeftPanel.add(titleLabel);
        titleLeftPanel.add(subtitleLabel);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(ACCENT);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setPreferredSize(new Dimension(80, 32));
        refreshBtn.addActionListener(e -> loadData());
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setBackground(ACCENT); }
        });

        JPanel refreshWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        refreshWrapper.setBackground(BG_MAIN);
        refreshWrapper.add(refreshBtn);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLeftPanel, BorderLayout.WEST);
        titlePanel.add(refreshWrapper, BorderLayout.EAST);
        titlePanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Jobs table
        String[] columns = {"MO Account", "Title", "Type", "TAs Needed", "Status", "Action"};
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
        table.getColumnModel().getColumn(0).setPreferredWidth(110); // MO Account
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Type
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // TAs Needed
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Action
        table.getColumnModel().getColumn(5).setMaxWidth(90);

        // Status column renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                String status = (value != null) ? value.toString() : "";
                if ("PUBLISHED".equals(status)) {
                    label.setForeground(PRIMARY);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("FULL".equals(status)) {
                    label.setForeground(SUCCESS);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("ENDED".equals(status)) {
                    label.setForeground(TEXT_SECOND);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("PENDING_REVIEW".equals(status)) {
                    label.setForeground(WARNING);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("REJECTED".equals(status)) {
                    label.setForeground(DANGER);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(TEXT);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return label;
            }
        });

        // View button renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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
                if (col == 5 && row >= 0 && currentJobs != null && row < currentJobs.size()) {
                    showJobDetails(currentJobs.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setBackground(PANEL_BG);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(BG_MAIN);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton approveBtn = new JButton("Approve");
        approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        approveBtn.setBackground(SUCCESS);
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);
        approveBtn.setBorderPainted(false);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        approveBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { approveBtn.setBackground(new Color(36, 176, 162)); }
            @Override public void mouseExited(MouseEvent e) { approveBtn.setBackground(SUCCESS); }
        });
        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && currentJobs != null && row < currentJobs.size()) {
                approveJob(currentJobs.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a job.", "Error", JOptionPane.WARNING_MESSAGE);
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
            if (row >= 0 && currentJobs != null && row < currentJobs.size()) {
                rejectJob(currentJobs.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a job.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        actionPanel.add(approveBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(rejectBtn);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Load data
        loadData();
    }

    private void loadData() {
        currentJobs = DataStore.getAllJobs();

        tableModel.setRowCount(0);
        if (currentJobs != null) {
            for (Job job : currentJobs) {
                tableModel.addRow(new Object[]{
                        job.getMoAccount(),
                        job.getTitle(),
                        job.getType(),
                        job.getTasNeeded(),
                        job.getStatus().toString(),
                        "View"
                });
            }
        }
    }

    private void approveJob(Job job) {
        if (job.getStatus() == JobStatus.ENDED) {
            JOptionPane.showMessageDialog(this, "Ended jobs cannot be approved.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Approve job: " + job.getTitle() + "?",
                "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = JobService.reviewJob(job.getJobId(), true, null);
        if (success) {
            JOptionPane.showMessageDialog(this, "Job approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to approve job.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectJob(Job job) {
        if (job.getStatus() == JobStatus.ENDED) {
            JOptionPane.showMessageDialog(this, "Ended jobs cannot be rejected.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String reason = JOptionPane.showInputDialog(this,
                "Enter rejection reason for: " + job.getTitle(),
                "Reject Job", JOptionPane.QUESTION_MESSAGE);
        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rejection reason is required.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Reject job: " + job.getTitle() + "?",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = JobService.reviewJob(job.getJobId(), false, reason.trim());
        if (success) {
            JOptionPane.showMessageDialog(this, "Job rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject job.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Job Details Dialog (matching MODashboard style) =====
    private void showJobDetails(Job job) {
        int hiredCount = ApplicationService.getHiredCount(job.getJobId());

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Job Details", true);
        dialog.setSize(480, 520);
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

        JLabel subLabel = new JLabel("Detailed information about this job posting");
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

        card.add(createDetailBlock("Job Title", job.getTitle(), PRIMARY));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("Type", job.getType().toString(), ACCENT));
        card.add(Box.createVerticalStrut(14));
        card.add(createStatusBlock("Status", job.getStatus()));
        card.add(Box.createVerticalStrut(18));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(BORDER);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep2);
        card.add(Box.createVerticalStrut(18));

        card.add(createDetailBlock("TAs Needed", String.valueOf(job.getTasNeeded()), WARNING));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("TAs Hired", String.valueOf(hiredCount),
                hiredCount >= job.getTasNeeded() ? SUCCESS : TEXT));
        card.add(Box.createVerticalStrut(18));

        JSeparator sep3 = new JSeparator();
        sep3.setForeground(BORDER);
        sep3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep3.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep3);
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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BG_MAIN);
        bottomPanel.setBorder(new EmptyBorder(5, 0, 12, 0));

        JButton bottomClose = new JButton("Close");
        bottomClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bottomClose.setBackground(PRIMARY);
        bottomClose.setForeground(Color.WHITE);
        bottomClose.setFocusPainted(false);
        bottomClose.setBorderPainted(false);
        bottomClose.setPreferredSize(new Dimension(120, 40));
        bottomClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomClose.addActionListener(e -> dialog.dispose());
        bottomClose.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { bottomClose.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { bottomClose.setBackground(PRIMARY); }
        });
        bottomPanel.add(bottomClose);

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
}
