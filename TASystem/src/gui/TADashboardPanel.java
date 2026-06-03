package gui;

import model.Application;
import model.Job;
import model.User;
import model.enums.ApplicationStatus;
import service.ApplicationService;
import data.DataStore;
import util.WorkTimeUtil;

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
 * TA Dashboard panel with summary statistics cards and applications table.
 */
public class TADashboardPanel extends JPanel {

    private MainFrame mainFrame;
    private JLabel totalLabel;
    private JLabel acceptedLabel;
    private JLabel pendingLabel;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<ApplicationService.ApplicationDetail> currentDetails;

    public TADashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Overview of your applications");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Stats cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(BG_MAIN);

        totalLabel = new JLabel("0", SwingConstants.CENTER);
        acceptedLabel = new JLabel("0", SwingConstants.CENTER);
        pendingLabel = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Applications", totalLabel, PRIMARY, new Color(239, 241, 255)));
        statsPanel.add(createStatCard("Accepted", acceptedLabel, SUCCESS, new Color(232, 247, 245)));
        statsPanel.add(createStatCard("Pending", pendingLabel, WARNING, new Color(255, 248, 235)));

        // ===== My Applications Section =====
        JLabel jobsLabel = new JLabel("My Applications");
        jobsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jobsLabel.setForeground(TEXT);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(ACCENT);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setPreferredSize(new Dimension(80, 28));
        refreshBtn.addActionListener(e -> loadData());
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setBackground(ACCENT); }
        });

        JPanel jobsHeaderPanel = new JPanel(new BorderLayout());
        jobsHeaderPanel.setBackground(BG_MAIN);
        jobsHeaderPanel.add(jobsLabel, BorderLayout.WEST);
        jobsHeaderPanel.add(refreshBtn, BorderLayout.EAST);
        jobsHeaderPanel.setBorder(new EmptyBorder(15, 0, 8, 0));

        // Table
        String[] columns = {"Job Title", "Type", "MO", "Apply Time", "Status", "Action"};
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
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Job Title
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // MO
        table.getColumnModel().getColumn(3).setPreferredWidth(130); // Apply Time
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Action
        table.getColumnModel().getColumn(5).setMaxWidth(90);

        // Status column renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (value != null) ? value.toString() : "";
                if ("HIRED".equals(status)) {
                    label.setForeground(SUCCESS);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("REJECTED".equals(status)) {
                    label.setForeground(DANGER);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(WARNING);
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
                if (col == 5 && row >= 0 && currentDetails != null && row < currentDetails.size()) {
                    showJobDetails(currentDetails.get(row).getJob());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setBackground(PANEL_BG);
        scrollPane.setPreferredSize(new Dimension(0, 220));
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);

        // ===== Center Panel: Jobs Header + Table =====
        JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.setBackground(BG_MAIN);
        centerPanel.add(jobsHeaderPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== North Panel: Title + Stats =====
        JPanel northPanel = new JPanel(new BorderLayout(0, 0));
        northPanel.setBackground(BG_MAIN);
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(statsPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // Load data
        loadData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color, Color bgTint) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(18, 22, 18, 22)));

        // Accent bar on left
        JPanel accentBar = new JPanel();
        accentBar.setBackground(color);
        accentBar.setPreferredSize(new Dimension(4, 0));
        accentBar.setOpaque(true);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECOND);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        valueLabel.setForeground(color);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(PANEL_BG);
        inner.add(titleLabel, BorderLayout.NORTH);
        inner.add(valueLabel, BorderLayout.CENTER);

        card.add(accentBar, BorderLayout.WEST);
        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    private void loadData() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        List<ApplicationService.ApplicationDetail> details =
                ApplicationService.getMyApplicationDetails(user.getAccount());
        currentDetails = details;

        int total = 0;
        int accepted = 0;
        int pending = 0;

        tableModel.setRowCount(0);

        if (details != null) {
            for (ApplicationService.ApplicationDetail detail : details) {
                total++;
                Application app = detail.getApplication();
                if (app.getStatus() == ApplicationStatus.HIRED) {
                    accepted++;
                } else if (app.getStatus() == ApplicationStatus.PENDING) {
                    pending++;
                }

                long ts = app.getApplyTime();
                String dateStr = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                Job job = detail.getJob();
                String jobTitle = job != null ? job.getTitle() : detail.getJobTitle();
                String jobType = job != null ? job.getType().toString() : "-";

                tableModel.addRow(new Object[]{
                        jobTitle,
                        jobType,
                        detail.getMoName(),
                        dateStr,
                        app.getStatus().toString(),
                        "View"
                });
            }
        }

        totalLabel.setText(String.valueOf(total));
        acceptedLabel.setText(String.valueOf(accepted));
        pendingLabel.setText(String.valueOf(pending));
    }

    private void showJobDetails(Job job) {
        if (job == null) {
            JOptionPane.showMessageDialog(this, "Job details not available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Job Details", true);
        dialog.setSize(480, 440);
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

        card.add(createDetailBlock("Job Title", job.getTitle(), PRIMARY));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("Type", job.getType().toString(), ACCENT));
        card.add(Box.createVerticalStrut(14));
        card.add(createStatusBlock("Status", job.getStatus()));
        card.add(Box.createVerticalStrut(14));
        card.add(createDetailBlock("TAs Needed", String.valueOf(job.getTasNeeded()), WARNING));
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

    private JPanel createStatusBlock(String label, model.enums.JobStatus status) {
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

    private Color getStatusColor(model.enums.JobStatus status) {
        switch (status) {
            case PUBLISHED: return PRIMARY;
            case FULL: return SUCCESS;
            case ENDED: return TEXT_SECOND;
            case PENDING_REVIEW: return WARNING;
            case REJECTED: return DANGER;
            default: return TEXT;
        }
    }

    private Color getStatusBgColor(model.enums.JobStatus status) {
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
