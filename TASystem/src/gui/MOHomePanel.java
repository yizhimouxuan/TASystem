package gui;

import model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import static gui.MainFrame.*;

/**
 * MO home panel with left navigation bar and right content area using CardLayout.
 */
public class MOHomePanel extends JPanel {

    private MainFrame mainFrame;
    private CardLayout contentCardLayout;
    private JPanel contentPanel;
    private JLabel welcomeLabel;

    private JLabel[] menuLabels;
    private String[] menuNames = {"Dashboard", "Post Job", "Applications"};
    private String[] menuCards = {"DASHBOARD", "POST_JOB", "APPLICATIONS"};

    public MOHomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(NAV_BG);
        topBar.setPreferredSize(new Dimension(0, 50));
        topBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        User user = mainFrame.getCurrentUser();
        String name = (user != null) ? user.getName() : "MO";
        welcomeLabel = new JLabel("Welcome, " + name);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutBtn.setBackground(new Color(40, 80, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> doLogout());

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // Left navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(NAV_BG);
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        menuLabels = new JLabel[menuNames.length];
        for (int i = 0; i < menuNames.length; i++) {
            JLabel menuLabel = createMenuLabel(menuNames[i], i);
            menuLabels[i] = menuLabel;
            navPanel.add(menuLabel);
            navPanel.add(Box.createVerticalStrut(5));
        }

        // Content panel with CardLayout
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(BG_MAIN);

        contentPanel.add(new MODashboardPanel(mainFrame), "DASHBOARD");
        contentPanel.add(new MOPostJobPanel(mainFrame), "POST_JOB");
        contentPanel.add(new MOApplicationsPanel(mainFrame), "APPLICATIONS");

        // Add components
        add(topBar, BorderLayout.NORTH);
        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Select first menu by default
        selectMenu(0);
    }

    private JLabel createMenuLabel(String text, int index) {
        JLabel label = new JLabel("  " + text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setMaximumSize(new Dimension(200, 40));
        label.setPreferredSize(new Dimension(200, 40));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setOpaque(true);
        label.setBackground(NAV_BG);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMenu(index);
                contentCardLayout.show(contentPanel, menuCards[index]);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (label.getBackground() != HOVER_BG) {
                    label.setBackground(new Color(86, 62, 182));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (label.getBackground() != HOVER_BG) {
                    label.setBackground(NAV_BG);
                }
            }
        });

        return label;
    }

    private void selectMenu(int index) {
        for (int i = 0; i < menuLabels.length; i++) {
            if (i == index) {
                menuLabels[i].setBackground(HOVER_BG);
                menuLabels[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                menuLabels[i].setBackground(NAV_BG);
                menuLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        }
    }

    private void doLogout() {
        mainFrame.setCurrentUser(null);
        mainFrame.showPanel("LOGIN");
    }
}
