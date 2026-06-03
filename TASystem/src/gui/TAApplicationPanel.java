package gui;

import model.Application;
import model.Job;
import model.User;
import model.enums.ApplicationStatus;
import model.enums.JobStatus;
import service.ApplicationService;
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
 * TA applications panel showing all applications with status color coding.
 */
public class TAApplicationPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<ApplicationService.ApplicationDetail> currentDetails;

    public TAApplicationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Applications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Track the status of your job applications");
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

        // Applications table
        String[] columns = {"MO Account", "Job Title", "MO", "Apply Time", "Status", "Action"};
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
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Job Title
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // MO
        table.getColumnModel().getColumn(3).setPreferredWidth(130); // Apply Time
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Action
        table.getColumnModel().getColumn(5).setMaxWidth(90);

        // Status column color renderer
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

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadData();
    }

    private void loadData() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        currentDetails = ApplicationService.getMyApplicationDetails(user.getAccount());

        tableModel.setRowCount(0);
        if (currentDetails != null) {
            for (ApplicationService.ApplicationDetail detail : currentDetails) {
                long ts = detail.getApplication().getApplyTime();
                String dateStr = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                Job job = detail.getJob();
                String moAccount = (job != null && job.getMoAccount() != null) ? job.getMoAccount() : "-";

                tableModel.addRow(new Object[]{
                        moAccount,
                        detail.getJobTitle(),
                        detail.getMoName(),
                        dateStr,
                        detail.getApplication().getStatus().toString(),
                        "View"
                });
            }
        }
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
