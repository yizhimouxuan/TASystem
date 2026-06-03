package gui;

import data.DataStore;
import model.TAProfile;
import model.User;
import service.ProfileService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import static gui.MainFrame.*;

/**
 * TA Profile panel for viewing and editing profile information.
 */
public class TAProfilePanel extends JPanel {

    private MainFrame mainFrame;

    // Fields
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> educationCombo;
    private JTextField majorField;
    private JTextField gpaField;
    private JTextArea skillsArea;
    private JTextArea experienceArea;
    private JTextArea availabilityArea;
    private JComboBox<String> preferredCombo;
    private JLabel statusBadge;
    private JProgressBar progressBar;

    public TAProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== Title area =====
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel("Complete your profile to improve your chances of being hired");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);

        statusBadge = new JLabel("Incomplete");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(5, 14, 5, 14));
        statusBadge.setBackground(new Color(255, 248, 235));
        statusBadge.setForeground(WARNING);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        progressBar.setForeground(PRIMARY);
        progressBar.setBackground(new Color(235, 238, 245));
        progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        progressBar.setPreferredSize(new Dimension(200, 18));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statusPanel.setBackground(BG_MAIN);
        statusPanel.add(statusBadge);
        statusPanel.add(progressBar);
        statusPanel.setBorder(new EmptyBorder(8, 0, 18, 0));

        JPanel titlePanel = new JPanel(new GridLayout(3, 1));
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        titlePanel.add(statusPanel);

        // ===== Content scroll =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_MAIN);

        // Card 1: Personal Information
        JPanel card1 = createCard("Personal Information", PRIMARY, new FieldRow[]{
            new FieldRow("Full Name", nameField = createTextField()),
            new FieldRow("Email", emailField = createTextField()),
            new FieldRow("Phone", phoneField = createTextField())
        });

        // Card 2: Academic Background
        JPanel card2 = createCard("Academic Background", ACCENT, new FieldRow[]{
            new FieldRow("Education", educationCombo = createCombo(new String[]{"Bachelor", "Master", "PhD"})),
            new FieldRow("Major", majorField = createTextField()),
            new FieldRow("GPA", gpaField = createTextField("e.g. 3.8/4.0"))
        });

        // Card 3: Professional Info
        JPanel card3 = createCard("Professional Info", SUCCESS, new FieldRow[]{
            new FieldRow("Skills", new JScrollPane(skillsArea = createTextArea(3, "e.g. Python, Java, MATLAB..."))),
            new FieldRow("Experience", new JScrollPane(experienceArea = createTextArea(3, "e.g. TA for CS101 in 2024 Fall...")))
        });

        // Card 4: Preferences
        JPanel card4 = createCard("Preferences", WARNING, new FieldRow[]{
            new FieldRow("Availability", new JScrollPane(availabilityArea = createTextArea(3, "e.g. Mon/Wed/Fri mornings..."))),
            new FieldRow("Preferred Job Type", preferredCombo = createCombo(new String[]{"Both", "ASSISTANT only", "INVIGILATOR only"}))
        });

        contentPanel.add(card1);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(card2);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(card3);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(card4);
        contentPanel.add(Box.createVerticalStrut(18));

        // Save button
        JButton saveBtn = new JButton("Save Profile");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(PRIMARY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setPreferredSize(new Dimension(160, 42));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveProfile());
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { saveBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { saveBtn.setBackground(PRIMARY); }
        });

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        savePanel.setBackground(BG_MAIN);
        savePanel.add(saveBtn);
        contentPanel.add(savePanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Live progress listeners
        attachLiveListener(nameField);
        attachLiveListener(emailField);
        attachLiveListener(phoneField);
        attachLiveListener(majorField);
        attachLiveListener(gpaField);
        attachLiveListener(skillsArea);
        attachLiveListener(experienceArea);
        attachLiveListener(availabilityArea);
        educationCombo.addActionListener(e -> updateProgress());
        preferredCombo.addActionListener(e -> updateProgress());

        loadProfile();
    }

    // ===== Helper: create a card panel =====
    private JPanel createCard(String title, Color accentColor, FieldRow[] rows) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(18, 22, 22, 22)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(5, 0));
        accent.setOpaque(true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PANEL_BG);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 14, 0));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLabel);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL_BG);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < rows.length; i++) {
            FieldRow row = rows[i];
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;

            JLabel lbl = new JLabel(row.label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(TEXT);
            lbl.setPreferredSize(new Dimension(140, 28));
            form.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            form.add(row.component, gbc);
        }

        content.add(form);
        card.add(accent, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private static class FieldRow {
        String label;
        JComponent component;
        FieldRow(String label, JComponent component) {
            this.label = label;
            this.component = component;
        }
    }

    private JTextField createTextField() {
        return createTextField(null);
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField(25);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        f.setBackground(new Color(250, 250, 252));
        if (placeholder != null) {
            f.setToolTipText(placeholder);
        }
        return f;
    }

    private JComboBox<String> createCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(new Color(250, 250, 252));
        c.setBorder(new LineBorder(BORDER, 1, true));
        return c;
    }

    private JTextArea createTextArea(int rows, String placeholder) {
        JTextArea a = new JTextArea(rows, 25);
        a.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        a.setBackground(new Color(250, 250, 252));
        a.setToolTipText(placeholder);
        return a;
    }

    private void attachLiveListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateProgress(); }
            public void removeUpdate(DocumentEvent e) { updateProgress(); }
            public void changedUpdate(DocumentEvent e) { updateProgress(); }
        });
    }

    private void attachLiveListener(JTextArea area) {
        area.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateProgress(); }
            public void removeUpdate(DocumentEvent e) { updateProgress(); }
            public void changedUpdate(DocumentEvent e) { updateProgress(); }
        });
    }

    // ===== Load & Save =====
    private void loadProfile() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        TAProfile profile = ProfileService.getProfile(user.getAccount());
        if (profile != null) {
            nameField.setText(orDefault(profile.getName(), user.getName()));
            emailField.setText(orDefault(profile.getEmail(), user.getEmail()));
            phoneField.setText(str(profile.getPhone()));
            educationCombo.setSelectedItem(str(profile.getEducation()));
            majorField.setText(str(profile.getMajor()));
            gpaField.setText(str(profile.getGpa()));
            skillsArea.setText(str(profile.getSkills()));
            experienceArea.setText(str(profile.getExperience()));
            availabilityArea.setText(str(profile.getAvailability()));
            preferredCombo.setSelectedItem(parsePreferred(profile.getPreferredTypes()));
        } else {
            // First time: pre-fill from User registration
            nameField.setText(str(user.getName()));
            emailField.setText(str(user.getEmail()));
        }
        updateProgress();
    }

    private void saveProfile() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String education = (String) educationCombo.getSelectedItem();
        String major = majorField.getText().trim();
        String gpa = gpaField.getText().trim();
        String skills = skillsArea.getText().trim();
        String experience = experienceArea.getText().trim();
        String availability = availabilityArea.getText().trim();
        String preferred = (String) preferredCombo.getSelectedItem();

        TAProfile profile = new TAProfile();
        profile.setAccount(user.getAccount());
        profile.setName(name);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setEducation(education);
        profile.setMajor(major);
        profile.setGpa(gpa);
        profile.setSkills(skills);
        profile.setExperience(experience);
        profile.setAvailability(availability);
        profile.setPreferredTypes(preferredTypesToString(preferred));

        TAProfile existing = ProfileService.getProfile(user.getAccount());
        boolean success;
        if (existing == null) {
            success = ProfileService.createProfile(profile);
        } else {
            success = ProfileService.updateProfile(profile);
        }

        if (success) {
            // Sync name/email back to User model
            boolean userChanged = false;
            if (!name.equals(user.getName())) {
                user.setName(name);
                userChanged = true;
            }
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
                userChanged = true;
            }
            if (userChanged) {
                DataStore.updateUser(user);
                mainFrame.refreshHomePanels();
            }
            updateProgress();
            JOptionPane.showMessageDialog(this, "Profile saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProgress() {
        int pct = computeCompletionPercent();
        boolean complete = pct == 100;
        if (complete) {
            statusBadge.setText("Complete");
            statusBadge.setBackground(new Color(232, 247, 245));
            statusBadge.setForeground(SUCCESS);
            progressBar.setValue(100);
            progressBar.setString("100% Complete");
            progressBar.setForeground(SUCCESS);
        } else {
            statusBadge.setText("Incomplete");
            statusBadge.setBackground(new Color(255, 248, 235));
            statusBadge.setForeground(WARNING);
            progressBar.setValue(pct);
            progressBar.setString(pct + "% Complete");
            progressBar.setForeground(PRIMARY);
        }
    }

    private int computeCompletionPercent() {
        int filled = 0;
        int total = 10;
        if (!nameField.getText().trim().isEmpty()) filled++;
        if (!emailField.getText().trim().isEmpty()) filled++;
        if (!phoneField.getText().trim().isEmpty()) filled++;
        if (educationCombo.getSelectedItem() != null && !((String) educationCombo.getSelectedItem()).isEmpty()) filled++;
        if (!majorField.getText().trim().isEmpty()) filled++;
        if (!gpaField.getText().trim().isEmpty()) filled++;
        if (!skillsArea.getText().trim().isEmpty()) filled++;
        if (!experienceArea.getText().trim().isEmpty()) filled++;
        if (!availabilityArea.getText().trim().isEmpty()) filled++;
        if (preferredCombo.getSelectedItem() != null) filled++;
        return (filled * 100) / total;
    }

    private String str(String s) {
        return s != null ? s : "";
    }

    private String orDefault(String value, String fallback) {
        return (value != null && !value.trim().isEmpty()) ? value : fallback;
    }

    private String parsePreferred(String stored) {
        if (stored == null) return "Both";
        if (stored.contains("ASSISTANT") && !stored.contains("INVIGILATOR")) return "ASSISTANT only";
        if (stored.contains("INVIGILATOR") && !stored.contains("ASSISTANT")) return "INVIGILATOR only";
        return "Both";
    }

    private String preferredTypesToString(String selected) {
        if ("ASSISTANT only".equals(selected)) return "ASSISTANT";
        if ("INVIGILATOR only".equals(selected)) return "INVIGILATOR";
        return "ASSISTANT, INVIGILATOR";
    }
}
