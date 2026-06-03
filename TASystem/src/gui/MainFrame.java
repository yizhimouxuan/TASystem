package gui;

import model.User;
import javax.swing.*;
import java.awt.*;

/**
 * Main application window using CardLayout to switch between different panels.
 * Contains static color constants used across all GUI panels.
 */
public class MainFrame extends JFrame {

    // Modern color palette — soft, professional, accessible
    public static final Color BG_MAIN      = new Color(240, 242, 245);
    public static final Color PRIMARY      = new Color(67, 97, 238);
    public static final Color PRIMARY_DARK = new Color(58, 12, 163);
    public static final Color ACCENT       = new Color(76, 201, 240);
    public static final Color SUCCESS      = new Color(46, 196, 182);
    public static final Color WARNING      = new Color(255, 183, 77);
    public static final Color DANGER       = new Color(239, 71, 111);
    public static final Color TEXT         = new Color(33, 37, 41);
    public static final Color TEXT_SECOND  = new Color(108, 117, 125);
    public static final Color BORDER       = new Color(222, 226, 230);
    public static final Color PANEL_BG     = new Color(255, 255, 255);
    public static final Color NAV_BG       = new Color(58, 12, 163);
    public static final Color HOVER_BG     = new Color(76, 52, 162);

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;

    public MainFrame() {
        // Force English button text on all JOptionPane dialogs regardless of system locale
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");

        setTitle("TA Recruitment System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_MAIN);

        // Add all panels to the card layout
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(new RegisterPanel(this), "REGISTER");
        mainPanel.add(new PasswordPanel(this), "PASSWORD");
        mainPanel.add(new TAHomePanel(this), "TA_HOME");
        mainPanel.add(new MOHomePanel(this), "MO_HOME");
        mainPanel.add(new AdminHomePanel(this), "ADMIN_HOME");

        add(mainPanel);
        showPanel("LOGIN");
    }

    /**
     * Switch to the specified panel by name.
     */
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    /**
     * Get the currently logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the currently logged-in user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Refresh the home panels after login.
     */
    public void refreshHomePanels() {
        // Recreate home panels to reflect current user
        Component[] comps = mainPanel.getComponents();
        for (Component comp : comps) {
            if (comp instanceof TAHomePanel || comp instanceof MOHomePanel || comp instanceof AdminHomePanel) {
                mainPanel.remove(comp);
            }
        }
        mainPanel.add(new TAHomePanel(this), "TA_HOME");
        mainPanel.add(new MOHomePanel(this), "MO_HOME");
        mainPanel.add(new AdminHomePanel(this), "ADMIN_HOME");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}
