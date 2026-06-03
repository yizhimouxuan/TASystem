package gui;

import model.Job;
import model.User;
import model.enums.JobStatus;
import model.enums.UserRole;
import service.AdminService;
import service.JobService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static gui.MainFrame.*;

/**
 * Admin Dashboard panel with summary statistics cards.
 */
public class AdminDashboardPanel extends JPanel {

    private MainFrame mainFrame;
    private JLabel totalUsersLabel;
    private JLabel tasLabel;
    private JLabel mosLabel;
    private JLabel pendingJobsLabel;

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("System overview and key metrics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Stats cards panel - 2x2 grid
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 18, 18));
        statsPanel.setBackground(BG_MAIN);

        totalUsersLabel = new JLabel("0", SwingConstants.CENTER);
        tasLabel = new JLabel("0", SwingConstants.CENTER);
        mosLabel = new JLabel("0", SwingConstants.CENTER);
        pendingJobsLabel = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Total Users", totalUsersLabel, PRIMARY, new Color(239, 241, 255)));
        statsPanel.add(createStatCard("TAs", tasLabel, ACCENT, new Color(232, 248, 255)));
        statsPanel.add(createStatCard("MOs", mosLabel, SUCCESS, new Color(232, 247, 245)));
        statsPanel.add(createStatCard("Pending Jobs", pendingJobsLabel, WARNING, new Color(255, 248, 235)));

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setBackground(PRIMARY); }
        });

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBackground(BG_MAIN);
        refreshPanel.add(refreshBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_MAIN);
        centerPanel.add(statsPanel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(refreshPanel, BorderLayout.SOUTH);

        // Load data
        loadData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color, Color bgTint) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(25, 30, 25, 30)));

        // Accent bar on left
        JPanel accentBar = new JPanel();
        accentBar.setBackground(color);
        accentBar.setPreferredSize(new Dimension(5, 0));
        accentBar.setOpaque(true);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(TEXT_SECOND);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
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
        // Get all users
        List<User> allUsers = AdminService.getAllUsers(null, null);
        int total = 0;
        int tas = 0;
        int mos = 0;

        if (allUsers != null) {
            for (User u : allUsers) {
                total++;
                if (u.getRole() == UserRole.TA) {
                    tas++;
                } else if (u.getRole() == UserRole.MO) {
                    mos++;
                }
            }
        }

        totalUsersLabel.setText(String.valueOf(total));
        tasLabel.setText(String.valueOf(tas));
        mosLabel.setText(String.valueOf(mos));

        // Get pending jobs
        List<Job> pendingJobs = JobService.getPendingJobs();
        int pendingCount = (pendingJobs != null) ? pendingJobs.size() : 0;
        pendingJobsLabel.setText(String.valueOf(pendingCount));
    }
}
