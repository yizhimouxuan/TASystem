package gui;

import model.Job;
import model.User;
import model.enums.JobStatus;
import model.enums.JobType;
import service.JobService;
import util.WorkTimeUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static gui.MainFrame.*;

/**
 * MO job posting panel with form to create and submit new job postings.
 * Work time requires selecting a specific date (year-month-day) + time slot.
 * Only future dates/times (after system time) are allowed.
 */
public class MOPostJobPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField titleField;
    private JComboBox<JobType> typeCombo;
    private JTextArea descArea;
    private JTextArea reqArea;
    private JComboBox<String> yearCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<String> dayCombo;
    private JComboBox<String> slotCombo;
    private JSpinner tasNeededSpinner;
    private DefaultListModel<String> slotListModel;
    private JList<String> slotJList;
    private List<WorkTimeUtil.WorkSlot> selectedSlots;

    public MOPostJobPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        selectedSlots = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== Title =====
        JLabel titleLabel = new JLabel("Post a New Job");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Create a new teaching assistant job posting");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(25, 30, 25, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Job Title ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel titleFieldLabel = new JLabel("Job Title");
        titleFieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleFieldLabel.setForeground(TEXT);
        formPanel.add(titleFieldLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        titleField = new JTextField(30);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        titleField.setBackground(new Color(250, 250, 252));
        formPanel.add(titleField, gbc);

        // --- Job Type ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Job Type");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(TEXT);
        formPanel.add(typeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        typeCombo = new JComboBox<>(JobType.values());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.setBackground(new Color(250, 250, 252));
        typeCombo.setBorder(new LineBorder(BORDER, 1, true));
        formPanel.add(typeCombo, gbc);

        // --- TAs Needed ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel tasLabel = new JLabel("TAs Needed");
        tasLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tasLabel.setForeground(TEXT);
        formPanel.add(tasLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
        tasNeededSpinner = new JSpinner(spinnerModel);
        tasNeededSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tasNeededSpinner.setBorder(new LineBorder(BORDER, 1, true));
        ((JSpinner.DefaultEditor) tasNeededSpinner.getEditor()).getTextField().setBackground(new Color(250, 250, 252));
        formPanel.add(tasNeededSpinner, gbc);

        // --- Work Time Selector (Row 3) ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel workLabel = new JLabel("Work Time");
        workLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        workLabel.setForeground(TEXT);
        formPanel.add(workLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel selectorPanel = buildSelectorPanel();
        formPanel.add(selectorPanel, gbc);

        // --- Added Slots List (Row 4) ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel listLabel = new JLabel("Added Slots");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listLabel.setForeground(TEXT);
        formPanel.add(listLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        JPanel slotListPanel = buildSlotListPanel();
        formPanel.add(slotListPanel, gbc);

        // --- Description ---
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(TEXT);
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 0.3; gbc.fill = GridBagConstraints.BOTH;
        descArea = new JTextArea(3, 30);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        descArea.setBackground(new Color(250, 250, 252));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(new LineBorder(BORDER, 1, true));
        formPanel.add(descScroll, gbc);

        // --- Requirements ---
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0; gbc.weighty = 0.3;
        JLabel reqLabel = new JLabel("Requirements");
        reqLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reqLabel.setForeground(TEXT);
        formPanel.add(reqLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 0.3; gbc.fill = GridBagConstraints.BOTH;
        reqArea = new JTextArea(3, 30);
        reqArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reqArea.setLineWrap(true);
        reqArea.setWrapStyleWord(true);
        reqArea.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        reqArea.setBackground(new Color(250, 250, 252));
        JScrollPane reqScroll = new JScrollPane(reqArea);
        reqScroll.setBorder(new LineBorder(BORDER, 1, true));
        formPanel.add(reqScroll, gbc);

        // --- Buttons ---
        gbc.gridx = 1; gbc.gridy = 7; gbc.weightx = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(15, 8, 8, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(PANEL_BG);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setBackground(TEXT_SECOND);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setPreferredSize(new Dimension(100, 42));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearFields());
        clearBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { clearBtn.setBackground(new Color(88, 97, 105)); }
            @Override public void mouseExited(MouseEvent e) { clearBtn.setBackground(TEXT_SECOND); }
        });

        JButton postBtn = new JButton("Post Job");
        postBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        postBtn.setBackground(PRIMARY);
        postBtn.setForeground(Color.WHITE);
        postBtn.setFocusPainted(false);
        postBtn.setPreferredSize(new Dimension(140, 42));
        postBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postBtn.addActionListener(e -> postJob());
        postBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { postBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { postBtn.setBackground(PRIMARY); }
        });

        btnPanel.add(clearBtn);
        btnPanel.add(postBtn);
        formPanel.add(btnPanel, gbc);

        // Wrap form in JScrollPane so Post Job button is always reachable
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
        scrollPane.getVerticalScrollBar().setBlockIncrement(128);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Build the date/time selector panel with ComboBoxes.
     * Uses a dedicated sub-panel with GridBagLayout for stable positioning.
     */
    private JPanel buildSelectorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.WEST;

        // Year
        c.gridx = 0; c.gridy = 0;
        JLabel yl = new JLabel("Year");
        yl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        yl.setForeground(TEXT_SECOND);
        panel.add(yl, c);

        c.gridy = 1;
        int currentYear = WorkTimeUtil.getCurrentYear();
        String[] years = {String.valueOf(currentYear), String.valueOf(currentYear + 1), String.valueOf(currentYear + 2)};
        yearCombo = new JComboBox<>(years);
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearCombo.setBackground(new Color(250, 250, 252));
        yearCombo.setBorder(new LineBorder(BORDER, 1, true));
        yearCombo.setPreferredSize(new Dimension(80, 32));
        panel.add(yearCombo, c);

        // Separator
        c.gridx = 1; c.gridy = 1;
        JLabel sep1 = new JLabel("-");
        sep1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sep1.setForeground(TEXT);
        panel.add(sep1, c);

        // Month
        c.gridx = 2; c.gridy = 0;
        JLabel ml = new JLabel("Month");
        ml.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ml.setForeground(TEXT_SECOND);
        panel.add(ml, c);

        c.gridy = 1;
        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        monthCombo.setBackground(new Color(250, 250, 252));
        monthCombo.setBorder(new LineBorder(BORDER, 1, true));
        monthCombo.setPreferredSize(new Dimension(60, 32));
        monthCombo.setSelectedIndex(WorkTimeUtil.getCurrentMonth() - 1);
        panel.add(monthCombo, c);

        // Separator
        c.gridx = 3; c.gridy = 1;
        JLabel sep2 = new JLabel("-");
        sep2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sep2.setForeground(TEXT);
        panel.add(sep2, c);

        // Day
        c.gridx = 4; c.gridy = 0;
        JLabel dl = new JLabel("Day");
        dl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dl.setForeground(TEXT_SECOND);
        panel.add(dl, c);

        c.gridy = 1;
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) days[i] = String.valueOf(i + 1);
        dayCombo = new JComboBox<>(days);
        dayCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dayCombo.setBackground(new Color(250, 250, 252));
        dayCombo.setBorder(new LineBorder(BORDER, 1, true));
        dayCombo.setPreferredSize(new Dimension(60, 32));
        dayCombo.setSelectedIndex(WorkTimeUtil.getCurrentDay() - 1);
        panel.add(dayCombo, c);

        // Time Slot
        c.gridx = 5; c.gridy = 0; c.insets = new Insets(2, 12, 2, 2);
        JLabel sl = new JLabel("Time Slot");
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sl.setForeground(TEXT_SECOND);
        panel.add(sl, c);

        c.gridy = 1; c.insets = new Insets(2, 12, 2, 2);
        slotCombo = new JComboBox<>(WorkTimeUtil.TIME_SLOTS);
        slotCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        slotCombo.setBackground(new Color(250, 250, 252));
        slotCombo.setBorder(new LineBorder(BORDER, 1, true));
        slotCombo.setPreferredSize(new Dimension(130, 32));
        // Pre-select slot after current hour
        int currentHour = WorkTimeUtil.getCurrentHour();
        for (int i = 0; i < WorkTimeUtil.TIME_SLOTS.length; i++) {
            String startH = WorkTimeUtil.TIME_SLOTS[i].substring(0, 2);
            if (Integer.parseInt(startH) > currentHour) {
                slotCombo.setSelectedIndex(i);
                break;
            }
        }
        panel.add(slotCombo, c);

        // Add button
        c.gridx = 6; c.gridy = 1; c.insets = new Insets(2, 12, 2, 2);
        JButton addSlotBtn = new JButton("+ Add");
        addSlotBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addSlotBtn.setBackground(PRIMARY);
        addSlotBtn.setForeground(Color.WHITE);
        addSlotBtn.setFocusPainted(false);
        addSlotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addSlotBtn.setPreferredSize(new Dimension(70, 32));
        addSlotBtn.addActionListener(e -> addSlot());
        addSlotBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { addSlotBtn.setBackground(PRIMARY_DARK); }
            @Override public void mouseExited(MouseEvent e) { addSlotBtn.setBackground(PRIMARY); }
        });
        panel.add(addSlotBtn, c);

        // Current time hint
        c.gridx = 0; c.gridy = 2; c.gridwidth = 7;
        c.insets = new Insets(6, 2, 0, 2);
        JLabel timeHint = new JLabel("System time: " + currentYear + "-"
                + WorkTimeUtil.getCurrentMonth() + "-" + WorkTimeUtil.getCurrentDay()
                + " " + WorkTimeUtil.getCurrentHour() + ":00  (only future slots allowed)");
        timeHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        timeHint.setForeground(TEXT_SECOND);
        panel.add(timeHint, c);

        return panel;
    }

    /**
     * Build the added slots list panel using JList (much more stable than dynamic JPanel).
     */
    private JPanel buildSlotListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(5, 5, 5, 5)));
        panel.setPreferredSize(new Dimension(0, 120));

        slotListModel = new DefaultListModel<>();
        slotJList = new JList<>(slotListModel);
        slotJList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        slotJList.setFixedCellHeight(26);
        slotJList.setBackground(PANEL_BG);
        slotJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(slotJList);
        scroll.setBorder(null);
        scroll.setBackground(PANEL_BG);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnPanel.setBackground(PANEL_BG);

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeBtn.setBackground(WARNING);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.setPreferredSize(new Dimension(140, 28));
        removeBtn.addActionListener(e -> removeSelectedSlot());

        JButton removeAllBtn = new JButton("Remove All");
        removeAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeAllBtn.setBackground(DANGER);
        removeAllBtn.setForeground(Color.WHITE);
        removeAllBtn.setFocusPainted(false);
        removeAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeAllBtn.setPreferredSize(new Dimension(100, 28));
        removeAllBtn.addActionListener(e -> removeAllSlots());

        btnPanel.add(removeBtn);
        btnPanel.add(removeAllBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addSlot() {
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        int month = Integer.parseInt((String) monthCombo.getSelectedItem());
        int day = Integer.parseInt((String) dayCombo.getSelectedItem());
        String slot = (String) slotCombo.getSelectedItem();
        if (slot == null) return;

        // Validate future time
        String error = WorkTimeUtil.validateSlot(year, month, day, slot);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Invalid Date/Time", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dateStr = String.format("%d-%02d-%02d", year, month, day);

        // Check duplicate
        for (WorkTimeUtil.WorkSlot s : selectedSlots) {
            if (s.date.equals(dateStr) && s.timeSlot.equals(slot)) {
                JOptionPane.showMessageDialog(this, "This time slot is already added.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        WorkTimeUtil.WorkSlot workSlot = new WorkTimeUtil.WorkSlot(dateStr, slot);
        selectedSlots.add(workSlot);
        refreshSlotList();
    }

    private void removeSelectedSlot() {
        int idx = slotJList.getSelectedIndex();
        if (idx >= 0 && idx < selectedSlots.size()) {
            selectedSlots.remove(idx);
            refreshSlotList();
        }
    }

    private void removeAllSlots() {
        if (selectedSlots.isEmpty()) return;
        selectedSlots.clear();
        refreshSlotList();
    }

    /**
     * Refresh JList from selectedSlots. JList is much more stable than dynamic JPanel.
     */
    private void refreshSlotList() {
        slotListModel.clear();
        if (selectedSlots.isEmpty()) {
            slotListModel.addElement("  (No slots added yet)");
        } else {
            for (int i = 0; i < selectedSlots.size(); i++) {
                WorkTimeUtil.WorkSlot s = selectedSlots.get(i);
                slotListModel.addElement("  " + (i + 1) + ".  " + s.date + "  " + s.timeSlot
                        + "  (" + WorkTimeUtil.getSlotHours(s.timeSlot) + "h)");
            }
        }
    }

    private void postJob() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        String title = titleField.getText().trim();
        JobType type = (JobType) typeCombo.getSelectedItem();
        String description = descArea.getText().trim();
        String requirements = reqArea.getText().trim();

        if (title.isEmpty() || description.isEmpty() || requirements.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedSlots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one work time slot.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int tasNeeded = (Integer) tasNeededSpinner.getValue();
        if (tasNeeded < 1 || tasNeeded > 15) {
            JOptionPane.showMessageDialog(this, "TAs Needed must be between 1 and 15.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String workTime = WorkTimeUtil.encodeSlots(selectedSlots);

        Job job = new Job();
        job.setMoAccount(user.getAccount());
        job.setTitle(title);
        job.setType(type);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setWorkTime(workTime);
        job.setTasNeeded((Integer) tasNeededSpinner.getValue());
        job.setStatus(JobStatus.PENDING_REVIEW);
        job.setHired(false);

        boolean success = JobService.postJob(job);
        if (success) {
            JOptionPane.showMessageDialog(this, "Job posted! Waiting for admin review.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to post job.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        titleField.setText("");
        typeCombo.setSelectedIndex(0);
        tasNeededSpinner.setValue(1);

        // Reset date/time combos to current
        yearCombo.setSelectedIndex(0);
        monthCombo.setSelectedIndex(WorkTimeUtil.getCurrentMonth() - 1);
        dayCombo.setSelectedIndex(WorkTimeUtil.getCurrentDay() - 1);
        int currentHour = WorkTimeUtil.getCurrentHour();
        for (int i = 0; i < WorkTimeUtil.TIME_SLOTS.length; i++) {
            String startH = WorkTimeUtil.TIME_SLOTS[i].substring(0, 2);
            if (Integer.parseInt(startH) > currentHour) {
                slotCombo.setSelectedIndex(i);
                break;
            }
        }

        descArea.setText("");
        reqArea.setText("");

        // Clear slots - JList model update is stable
        selectedSlots.clear();
        refreshSlotList();
    }
}
