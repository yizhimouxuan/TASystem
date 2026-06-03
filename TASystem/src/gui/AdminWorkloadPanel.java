package gui;

import service.AdminService;
import service.WorkloadReport;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

import static gui.MainFrame.*;

/**
 * Admin workload panel showing TA workload statistics table with CSV export.
 */
public class AdminWorkloadPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private List<WorkloadReport.Entry> currentEntries;

    public AdminWorkloadPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Workload Statistics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Completed work hours and job counts per TA");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titleLeftPanel = new JPanel(new GridLayout(2, 1));
        titleLeftPanel.setBackground(BG_MAIN);
        titleLeftPanel.add(titleLabel);
        titleLeftPanel.add(subtitleLabel);

        // Buttons
        JButton exportBtn = new JButton("Export CSV");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportBtn.setBackground(SUCCESS);
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setBorderPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e -> exportCSV());
        exportBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { exportBtn.setBackground(new Color(36, 176, 162)); }
            @Override public void mouseExited(MouseEvent e) { exportBtn.setBackground(SUCCESS); }
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(ACCENT);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setBackground(ACCENT); }
        });

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLeftPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_MAIN);
        btnPanel.add(refreshBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(exportBtn);
        titlePanel.add(btnPanel, BorderLayout.EAST);
        titlePanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Workload table
        String[] columns = {"TA Account", "TA Name", "Jobs Count", "Total Hours"};
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

        // Jobs Count column renderer - highlight high workload
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Integer) {
                    int count = (Integer) value;
                    if (count >= 3) {
                        label.setForeground(WARNING);
                        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        label.setForeground(TEXT);
                        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                return label;
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
        WorkloadReport report = AdminService.getWorkloadStats();
        currentEntries = (report != null) ? report.getEntries() : null;

        tableModel.setRowCount(0);
        if (currentEntries != null) {
            for (WorkloadReport.Entry entry : currentEntries) {
                tableModel.addRow(new Object[]{
                        entry.getTaAccount(),
                        entry.getTaName(),
                        entry.getJobCount(),
                        entry.getTotalHours()
                });
            }
        }
    }

    private void exportCSV() {
        if (currentEntries == null || currentEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("workload_report.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header
            writer.println("TA Account,TA Name,Jobs Count,Total Hours");

            // Data rows
            for (WorkloadReport.Entry entry : currentEntries) {
                writer.printf("%s,%s,%d,%d%n",
                        escapeCSV(entry.getTaAccount()),
                        escapeCSV(entry.getTaName()),
                        entry.getJobCount(),
                        entry.getTotalHours());
            }

            JOptionPane.showMessageDialog(this,
                    "CSV exported successfully to: " + file.getAbsolutePath(),
                    "Export Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export CSV: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Escape CSV field value by wrapping in quotes if contains special characters.
     */
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
