package gui;

import model.CV;
import model.User;
import service.CVService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static gui.MainFrame.*;

/**
 * CV upload panel for TAs to upload, view, download and delete their CV.
 */
public class TAUploadCVPanel extends JPanel {

    private MainFrame mainFrame;
    private JPanel statusCard;
    private JPanel emptyCard;
    private JLabel fileNameLabel;
    private JLabel qualifiedLabel;
    private JLabel reasonLabel;
    private JLabel selectedFileLabel;
    private File selectedFile;

    public TAUploadCVPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My CV");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Upload and manage your curriculum vitae");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECOND);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_MAIN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // ===== Upload Card =====
        JPanel uploadCard = new JPanel();
        uploadCard.setLayout(new BoxLayout(uploadCard, BoxLayout.Y_AXIS));
        uploadCard.setBackground(PANEL_BG);
        uploadCard.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(30, 30, 30, 30)));

        // Upload icon (custom drawn)
        DocIcon iconLabel = new DocIcon(56);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel uploadHint = new JLabel("Select a file to upload your CV");
        uploadHint.setFont(new Font("Segoe UI", Font.BOLD, 16));
        uploadHint.setForeground(TEXT);
        uploadHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadHint.setBorder(new EmptyBorder(10, 0, 4, 0));

        JLabel formatHint = new JLabel("Supported formats: PDF, DOC, DOCX");
        formatHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formatHint.setForeground(TEXT_SECOND);
        formatHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        formatHint.setBorder(new EmptyBorder(0, 0, 18, 0));

        // Buttons row
        JPanel btnRow = new JPanel();
        btnRow.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(PANEL_BG);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton selectBtn = new JButton("Select File");
        selectBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectBtn.setBackground(ACCENT);
        selectBtn.setForeground(Color.WHITE);
        selectBtn.setFocusPainted(false);
        selectBtn.setBorderPainted(false);
        selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectBtn.setPreferredSize(new Dimension(120, 38));
        selectBtn.addActionListener(e -> selectFile());
        selectBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { selectBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { selectBtn.setBackground(ACCENT); }
        });

        JButton uploadBtn = new JButton("Upload CV");
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uploadBtn.setBackground(SUCCESS);
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.setBorderPainted(false);
        uploadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadBtn.setPreferredSize(new Dimension(120, 38));
        uploadBtn.addActionListener(e -> uploadFile());
        uploadBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { uploadBtn.setBackground(new Color(36, 176, 162)); }
            @Override public void mouseExited(MouseEvent e) { uploadBtn.setBackground(SUCCESS); }
        });

        btnRow.add(selectBtn);
        btnRow.add(uploadBtn);

        selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        selectedFileLabel.setForeground(TEXT_SECOND);
        selectedFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedFileLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        uploadCard.add(iconLabel);
        uploadCard.add(uploadHint);
        uploadCard.add(formatHint);
        uploadCard.add(btnRow);
        uploadCard.add(selectedFileLabel);

        // ===== Status Card (CV exists) =====
        statusCard = new JPanel(new BorderLayout());
        statusCard.setBackground(PANEL_BG);
        statusCard.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(22, 22, 18, 22)));

        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 14));
        detailsPanel.setBackground(PANEL_BG);

        JLabel fnLabel = new JLabel("File Name:");
        fnLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fnLabel.setForeground(TEXT);
        fileNameLabel = new JLabel("-");
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileNameLabel.setForeground(TEXT);

        JLabel qLabel = new JLabel("Qualified:");
        qLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qLabel.setForeground(TEXT);
        qualifiedLabel = new JLabel("-");
        qualifiedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel rLabel = new JLabel("Review Comment:");
        rLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rLabel.setForeground(TEXT);
        reasonLabel = new JLabel("-");
        reasonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reasonLabel.setForeground(TEXT_SECOND);

        detailsPanel.add(fnLabel);
        detailsPanel.add(fileNameLabel);
        detailsPanel.add(qLabel);
        detailsPanel.add(qualifiedLabel);
        detailsPanel.add(rLabel);
        detailsPanel.add(reasonLabel);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(PANEL_BG);

        JButton downloadBtn = new JButton("Download");
        downloadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        downloadBtn.setBackground(ACCENT);
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setFocusPainted(false);
        downloadBtn.setBorderPainted(false);
        downloadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        downloadBtn.addActionListener(e -> downloadCV());
        downloadBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { downloadBtn.setBackground(new Color(56, 181, 220)); }
            @Override public void mouseExited(MouseEvent e) { downloadBtn.setBackground(ACCENT); }
        });

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setBackground(DANGER);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteCV());
        deleteBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { deleteBtn.setBackground(new Color(219, 61, 101)); }
            @Override public void mouseExited(MouseEvent e) { deleteBtn.setBackground(DANGER); }
        });

        actionPanel.add(downloadBtn);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(deleteBtn);

        statusCard.add(detailsPanel, BorderLayout.CENTER);
        statusCard.add(actionPanel, BorderLayout.SOUTH);

        // ===== Empty Card (no CV) =====
        emptyCard = new JPanel();
        emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
        emptyCard.setBackground(PANEL_BG);
        emptyCard.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(0, 4, 4, 0), new LineBorder(new Color(230, 232, 235), 1, true)),
            new EmptyBorder(30, 30, 30, 30)));

        DocIcon emptyIcon = new DocIcon(48);
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyText = new JLabel("No CV uploaded yet");
        emptyText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        emptyText.setForeground(TEXT_SECOND);
        emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyText.setBorder(new EmptyBorder(8, 0, 4, 0));

        JLabel emptySub = new JLabel("Upload your CV to apply for teaching assistant positions");
        emptySub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emptySub.setForeground(new Color(160, 170, 185));
        emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyCard.add(emptyIcon);
        emptyCard.add(emptyText);
        emptyCard.add(emptySub);

        // ===== Main Content (vertical stack) =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_MAIN);
        contentPanel.add(uploadCard);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(statusCard);
        contentPanel.add(emptyCard);

        // Combine
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(BG_MAIN);
        northWrapper.add(titlePanel, BorderLayout.NORTH);

        add(northWrapper, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Load CV info
        loadCV();
    }

    private void loadCV() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        if (CVService.hasCV(user.getAccount())) {
            CV cv = CVService.getCV(user.getAccount());
            if (cv != null) {
                fileNameLabel.setText(cv.getFileName());
                if (cv.isQualified()) {
                    qualifiedLabel.setText("Yes");
                    qualifiedLabel.setForeground(SUCCESS);
                } else {
                    qualifiedLabel.setText("No");
                    qualifiedLabel.setForeground(DANGER);
                }
                reasonLabel.setText(cv.getRejectReason() != null && !cv.getRejectReason().isEmpty() ? cv.getRejectReason() : "-");
                statusCard.setVisible(true);
                emptyCard.setVisible(false);
                return;
            }
        }
        statusCard.setVisible(false);
        emptyCard.setVisible(true);
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF & Word Files", "pdf", "doc", "docx"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            selectedFileLabel.setText("Selected: " + selectedFile.getName());
            selectedFileLabel.setForeground(TEXT);
        }
    }

    private void uploadFile() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = CVService.uploadCV(user.getAccount(), selectedFile);
        if (success) {
            JOptionPane.showMessageDialog(this, "CV uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            selectedFile = null;
            selectedFileLabel.setText("No file selected");
            selectedFileLabel.setForeground(TEXT_SECOND);
            loadCV();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to upload CV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void downloadCV() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        CV cv = CVService.getCV(user.getAccount());
        if (cv != null && cv.getFilePath() != null) {
            File file = new File(cv.getFilePath());
            if (file.exists()) {
                JOptionPane.showMessageDialog(this, "CV located at: " + cv.getFilePath(), "Download", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "File not found on disk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCV() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your CV?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        boolean success = CVService.deleteCV(user.getAccount());
        if (success) {
            JOptionPane.showMessageDialog(this, "CV deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCV();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete CV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Custom-drawn document icon that works on any platform without emoji font support. */
    private static class DocIcon extends JPanel {
        private int sz;
        DocIcon(int sz) {
            this.sz = sz;
            int h = (int)(sz * 1.28);
            setPreferredSize(new Dimension(sz, h));
            setMaximumSize(new Dimension(sz, h));
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = sz;
            int h = (int)(sz * 1.28);
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;
            int fold = Math.max(12, w / 3);

            // File body polygon with folded corner
            int[] xp = {x, x + w - fold, x + w, x + w, x, x};
            int[] yp = {y, y, y + fold, y + h, y + h, y};

            g2.setColor(new Color(239, 241, 255));
            g2.fillPolygon(xp, yp, 6);
            g2.setColor(PRIMARY);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawPolygon(xp, yp, 6);

            // Fold lines
            g2.drawLine(x + w - fold, y, x + w - fold, y + fold);
            g2.drawLine(x + w - fold, y + fold, x + w, y + fold);

            // Text lines inside file
            g2.setColor(new Color(180, 190, 210));
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int lx = x + (int)(w * 0.18);
            int lw = (int)(w * 0.64);
            int ly = y + fold + (int)(h * 0.22);
            int gap = (int)(h * 0.12);
            g2.drawLine(lx, ly, lx + lw, ly);
            g2.drawLine(lx, ly + gap, lx + lw, ly + gap);
            g2.drawLine(lx, ly + gap * 2, lx + lw - (int)(w * 0.1), ly + gap * 2);

            g2.dispose();
        }
    }
}
