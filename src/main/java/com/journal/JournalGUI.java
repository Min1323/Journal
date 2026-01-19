package com.journal;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * GUI application for the Journal program.
 * Provides visualization of consuming/productive statistics and activity hours,
 * plus a form to add new activities.
 */
public class JournalGUI extends JFrame {
    private JournalManager journalManager;
    private CategoryManager categoryManager;
    private AnalysisService analysisService;
    
    // Analysis tab components
    private JComboBox<String> dateComboBox;
    private JRadioButton dayViewRadio;
    private JRadioButton lifetimeViewRadio;
    private JPanel statsPanel;
    private JPanel activitiesPanel;
    private JTextArea detailsTextArea;
    
    // Add Activity tab components
    private JTextField startDateField;
    private JTextField startTimeField;
    private JTextField endDateField;
    private JTextField endTimeField;
    private JTextField activityTypeField;
    private JTextArea noteTextArea;
    private JCheckBox isConsumingCheckBox;
    private JCheckBox isProductiveCheckBox;
    private JTextArea collisionWarningArea;

    public JournalGUI() {
        this.journalManager = new JournalManager();
        this.categoryManager = new CategoryManager();
        this.analysisService = new AnalysisService(journalManager, categoryManager);
        
        initializeGUI();
        loadAvailableDates();
        updateDisplay();
    }

    private void initializeGUI() {
        setTitle("Journal - Activity Tracker & Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Analysis
        JPanel analysisPanel = createAnalysisPanel();
        tabbedPane.addTab("Analysis", analysisPanel);
        
        // Tab 2: Add Activity
        JPanel addActivityPanel = createAddActivityPanel();
        tabbedPane.addTab("Add Activity", addActivityPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Top panel - Date selection and view mode
        JPanel topPanel = createTopPanel();
        panel.add(topPanel, BorderLayout.NORTH);

        // Center panel - Statistics and activities
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Left: Statistics panel
        statsPanel = createStatsPanel();
        centerPanel.add(statsPanel);
        
        // Right: Activities panel
        activitiesPanel = createActivitiesPanel();
        centerPanel.add(activitiesPanel);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Details text area
        detailsTextArea = new JTextArea(8, 50);
        detailsTextArea.setEditable(false);
        detailsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(detailsTextArea);
        scrollPane.setBorder(new TitledBorder("Details"));
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> {
            loadAvailableDates();
            updateDisplay();
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createAddActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Start Date
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(15);
        startDateField.addActionListener(e -> checkCollisions());
        startDateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
        });
        formPanel.add(startDateField, gbc);
        
        // Start Time
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Start Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        startTimeField = new JTextField(15);
        startTimeField.addActionListener(e -> checkCollisions());
        startTimeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
        });
        formPanel.add(startTimeField, gbc);
        
        // End Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(15);
        endDateField.addActionListener(e -> checkCollisions());
        endDateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
        });
        formPanel.add(endDateField, gbc);
        
        // End Time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("End Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        endTimeField = new JTextField(15);
        endTimeField.addActionListener(e -> checkCollisions());
        endTimeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkCollisions(); }
        });
        formPanel.add(endTimeField, gbc);
        
        // Activity Type
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Activity Type:"), gbc);
        gbc.gridx = 1;
        activityTypeField = new JTextField(15);
        formPanel.add(activityTypeField, gbc);
        
        // Note
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Note:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        noteTextArea = new JTextArea(3, 15);
        noteTextArea.setLineWrap(true);
        noteTextArea.setWrapStyleWord(true);
        noteTextArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JScrollPane noteScroll = new JScrollPane(noteTextArea);
        formPanel.add(noteScroll, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        
        // Is Consuming
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Flags:"), gbc);
        gbc.gridx = 1;
        isConsumingCheckBox = new JCheckBox("Is Consuming");
        formPanel.add(isConsumingCheckBox, gbc);
        
        // Is Productive
        gbc.gridx = 1; gbc.gridy = 7;
        isProductiveCheckBox = new JCheckBox("Is Productive");
        formPanel.add(isProductiveCheckBox, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Activity");
        addButton.addActionListener(e -> addActivity());
        buttonPanel.add(addButton);
        
        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearAddActivityForm());
        buttonPanel.add(clearButton);
        
        JButton refreshButton = new JButton("Refresh Date/Time");
        refreshButton.addActionListener(e -> refreshDateTime());
        buttonPanel.add(refreshButton);
        
        formPanel.add(buttonPanel, gbc);
        
        // Collision warning area
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        collisionWarningArea = new JTextArea(5, 30);
        collisionWarningArea.setEditable(false);
        collisionWarningArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        collisionWarningArea.setForeground(Color.RED);
        collisionWarningArea.setBorder(new TitledBorder("Collision Warnings"));
        JScrollPane collisionScroll = new JScrollPane(collisionWarningArea);
        formPanel.add(collisionScroll, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Initialize with current date/time
        refreshDateTime();
        
        return panel;
    }

    private void refreshDateTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // Determine which date to use - try to parse from startDateField, otherwise use today
        LocalDate targetDate;
        try {
            String dateText = startDateField.getText().trim();
            if (!dateText.isEmpty()) {
                targetDate = LocalDate.parse(dateText, dateFormatter);
            } else {
                targetDate = LocalDate.now();
            }
        } catch (Exception e) {
            targetDate = LocalDate.now();
        }
        
        // Get the end time of the last activity for the target date, or use 00:00 if no activities exist
        LocalDateTime startTime;
        LocalDateTime lastActivityEndTime = journalManager.getLastActivityEndTime(targetDate);
        
        if (lastActivityEndTime != null) {
            // Use the end time of the last activity
            startTime = lastActivityEndTime;
        } else {
            // No activities for this date, use midnight (00:00)
            startTime = targetDate.atStartOfDay();
        }
        
        // Set start date/time
        startDateField.setText(startTime.format(dateFormatter));
        startTimeField.setText(startTime.format(timeFormatter));
        
        // Set end date/time to current time (or same as start if no activities)
        LocalDateTime endTime = LocalDateTime.now();
        // If start time is in the future or if start date is not today, use start time + 1 hour
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime) || 
            !startTime.toLocalDate().equals(LocalDate.now())) {
            endTime = startTime.plusHours(1); // Default to 1 hour duration
        }
        
        endDateField.setText(endTime.format(dateFormatter));
        endTimeField.setText(endTime.format(timeFormatter));
        
        // Check for collisions when date/time changes
        checkCollisions();
    }

    private void checkCollisions() {
        collisionWarningArea.setText("");
        
        try {
            LocalDateTime startTime = parseDateTime(startDateField.getText(), startTimeField.getText());
            LocalDateTime endTime = parseDateTime(endDateField.getText(), endTimeField.getText());
            
            if (startTime == null || endTime == null) {
                return;
            }
            
            if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
                collisionWarningArea.setText("Error: End time must be after start time.");
                return;
            }
            
            // Create a temporary entry to check collisions
            JournalEntry tempEntry = new JournalEntry(startTime, endTime, "", false, false);
            List<JournalEntry> collisions = journalManager.checkCollisions(tempEntry);
            
            if (!collisions.isEmpty()) {
                StringBuilder warning = new StringBuilder();
                warning.append("WARNING: This activity overlaps with existing entries:\n\n");
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (int i = 0; i < collisions.size(); i++) {
                    JournalEntry collision = collisions.get(i);
                    warning.append(String.format("%d. %s\n", i + 1, collision.getActivityType()));
                    warning.append(String.format("   Time: %s - %s\n", 
                            collision.getStartTime().format(formatter),
                            collision.getEndTime().format(formatter)));
                    warning.append(String.format("   Duration: %.2f hours\n\n", 
                            collision.getDurationHours()));
                }
                
                collisionWarningArea.setForeground(Color.RED);
                collisionWarningArea.setText(warning.toString());
            } else {
                collisionWarningArea.setForeground(new Color(0, 150, 0)); // Dark green
                collisionWarningArea.setText("No collisions detected. ✓");
            }
        } catch (Exception e) {
            // Invalid input, don't show collision warning
        }
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {
            String dateTimeStr = dateStr.trim() + " " + timeStr.trim();
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void addActivity() {
        try {
            // Parse date/time
            LocalDateTime startTime = parseDateTime(startDateField.getText(), startTimeField.getText());
            LocalDateTime endTime = parseDateTime(endDateField.getText(), endTimeField.getText());
            
            if (startTime == null || endTime == null) {
                JOptionPane.showMessageDialog(this, 
                        "Invalid date/time format. Please use yyyy-MM-dd for dates and HH:mm for times.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate time order
            if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
                JOptionPane.showMessageDialog(this, 
                        "End time must be after start time.",
                        "Invalid Time Range", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get activity type
            String activityType = activityTypeField.getText().trim();
            if (activityType.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Activity type cannot be empty.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get flags
            boolean isConsuming = isConsumingCheckBox.isSelected();
            boolean isProductive = isProductiveCheckBox.isSelected();
            
            // Get note
            String note = noteTextArea.getText().trim();
            
            // Check for collisions
            JournalEntry newEntry = new JournalEntry(startTime, endTime, activityType, isConsuming, isProductive, note);
            List<JournalEntry> collisions = journalManager.checkCollisions(newEntry);
            
            if (!collisions.isEmpty()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "This activity overlaps with " + collisions.size() + " existing entry/entries.\n" +
                        "Do you want to add it anyway?",
                        "Collision Detected", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Save entry
            journalManager.saveEntry(newEntry);
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Activity added successfully!\n" +
                    "Duration: " + String.format("%.2f", newEntry.getDurationHours()) + " hours",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form and refresh
            clearAddActivityForm();
            loadAvailableDates();
            updateDisplay();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding activity: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAddActivityForm() {
        refreshDateTime();
        activityTypeField.setText("");
        noteTextArea.setText("");
        isConsumingCheckBox.setSelected(false);
        isProductiveCheckBox.setSelected(false);
        collisionWarningArea.setText("");
        collisionWarningArea.setForeground(Color.RED);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        panel.add(new JLabel("Date:"));
        dateComboBox = new JComboBox<>();
        dateComboBox.addActionListener(e -> updateDisplay());
        panel.add(dateComboBox);
        
        panel.add(Box.createHorizontalStrut(20));
        
        dayViewRadio = new JRadioButton("Day View", true);
        lifetimeViewRadio = new JRadioButton("Lifetime View", false);
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(dayViewRadio);
        viewGroup.add(lifetimeViewRadio);
        
        dayViewRadio.addActionListener(e -> updateDisplay());
        lifetimeViewRadio.addActionListener(e -> updateDisplay());
        
        panel.add(dayViewRadio);
        panel.add(lifetimeViewRadio);
        
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Consuming vs Productive Statistics"));
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        // Labels for stats
        JLabel consumingLabel = new JLabel("Consuming:");
        JLabel consumingValue = new JLabel("0.0 hours (0.0%)");
        consumingValue.setName("consumingValue");
        consumingValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        JLabel productiveLabel = new JLabel("Productive:");
        JLabel productiveValue = new JLabel("0.0 hours (0.0%)");
        productiveValue.setName("productiveValue");
        productiveValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        JLabel totalLabel = new JLabel("Total:");
        JLabel totalValue = new JLabel("0.0 hours");
        totalValue.setName("totalValue");
        totalValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        contentPanel.add(consumingLabel);
        contentPanel.add(consumingValue);
        contentPanel.add(productiveLabel);
        contentPanel.add(productiveValue);
        contentPanel.add(totalLabel);
        contentPanel.add(totalValue);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Main Activities (Cumulative Hours)"));
        
        JList<String> activitiesList = new JList<>();
        activitiesList.setName("activitiesList");
        activitiesList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        activitiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activitiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = activitiesList.getSelectedValue();
                if (selected != null) {
                    showActivityDetails(selected);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(activitiesList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void loadAvailableDates() {
        List<LocalDate> dates = journalManager.getAvailableDates();
        dateComboBox.removeAllItems();
        
        if (dates.isEmpty()) {
            dateComboBox.addItem("No data available");
            dateComboBox.setEnabled(false);
        } else {
            dateComboBox.setEnabled(true);
            for (LocalDate date : dates) {
                dateComboBox.addItem(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            // Select today if available, otherwise select the most recent
            String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (dates.contains(LocalDate.now())) {
                dateComboBox.setSelectedItem(todayStr);
            } else if (!dates.isEmpty()) {
                dateComboBox.setSelectedIndex(dates.size() - 1);
            }
        }
    }

    private void updateDisplay() {
        if (lifetimeViewRadio.isSelected()) {
            updateLifetimeDisplay();
        } else {
            updateDayDisplay();
        }
    }

    private void updateDayDisplay() {
        String selectedDate = (String) dateComboBox.getSelectedItem();
        if (selectedDate == null || selectedDate.equals("No data available")) {
            clearDisplay();
            return;
        }

        try {
            LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            AnalysisService.ConsumingProductiveStats stats = analysisService.getStatsForDate(date);
            Map<String, Double> activities = analysisService.getMainActivityHoursForDate(date);
            
            updateStatsPanel(stats);
            updateActivitiesPanel(activities);
            updateDetailsTextArea(date, stats, activities);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLifetimeDisplay() {
        AnalysisService.ConsumingProductiveStats stats = analysisService.getLifetimeStats();
        Map<String, Double> activities = analysisService.getMainActivityHours();
        
        updateStatsPanel(stats);
        updateActivitiesPanel(activities);
        updateDetailsTextArea(null, stats, activities);
    }

    private void updateStatsPanel(AnalysisService.ConsumingProductiveStats stats) {
        Component[] components = statsPanel.getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel contentPanel = (JPanel) components[0];
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String name = label.getName();
                    if (name != null) {
                        switch (name) {
                            case "consumingValue":
                                label.setText(String.format("%.2f hours (%.1f%%)", 
                                        stats.getConsumingHours(), stats.getConsumingPercentage()));
                                break;
                            case "productiveValue":
                                label.setText(String.format("%.2f hours (%.1f%%)", 
                                        stats.getProductiveHours(), stats.getProductivePercentage()));
                                break;
                            case "totalValue":
                                label.setText(String.format("%.2f hours", stats.getTotalHours()));
                                break;
                        }
                    }
                }
            }
        }
    }

    private void updateActivitiesPanel(Map<String, Double> activities) {
        Component[] components = activitiesPanel.getComponents();
        if (components.length > 0 && components[0] instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) components[0];
            JList<?> list = (JList<?>) scrollPane.getViewport().getView();
            
            if (list != null && list.getName() != null && list.getName().equals("activitiesList")) {
                @SuppressWarnings("unchecked")
                JList<String> activitiesList = (JList<String>) list;
                
                if (activities.isEmpty()) {
                    activitiesList.setListData(new String[]{"No activities recorded"});
                } else {
                    // Sort by hours (descending) and format
                    String[] items = activities.entrySet().stream()
                            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                            .map(e -> String.format("%-20s %8.2f hours", e.getKey(), e.getValue()))
                            .toArray(String[]::new);
                    activitiesList.setListData(items);
                }
            }
        }
    }

    private void updateDetailsTextArea(LocalDate date, AnalysisService.ConsumingProductiveStats stats, 
                                       Map<String, Double> activities) {
        StringBuilder sb = new StringBuilder();
        
        if (date != null) {
            sb.append("Date: ").append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        } else {
            sb.append("View: Lifetime Statistics\n");
        }
        
        sb.append("\n=== Consuming vs Productive Breakdown ===\n");
        sb.append(String.format("Consuming:  %.2f hours (%.1f%%)\n", 
                stats.getConsumingHours(), stats.getConsumingPercentage()));
        sb.append(String.format("Productive: %.2f hours (%.1f%%)\n", 
                stats.getProductiveHours(), stats.getProductivePercentage()));
        sb.append(String.format("Total:      %.2f hours\n", stats.getTotalHours()));
        
        sb.append("\n=== Main Activities (sorted by time) ===\n");
        if (activities.isEmpty()) {
            sb.append("No activities recorded.\n");
        } else {
            activities.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .forEach(e -> sb.append(String.format("%-25s %8.2f hours\n", e.getKey(), e.getValue())));
        }
        
        detailsTextArea.setText(sb.toString());
    }

    private void showActivityDetails(String selected) {
        // Extract activity name from the formatted string
        String activityName = selected.trim().split("\\s+")[0];
        detailsTextArea.append("\n\nSelected Activity: " + activityName + "\n");
        // Could add more detailed breakdown here in the future
    }

    private void clearDisplay() {
        updateStatsPanel(new AnalysisService.ConsumingProductiveStats(0, 0));
        updateActivitiesPanel(Map.of());
        detailsTextArea.setText("No data available.");
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new JournalGUI().setVisible(true);
        });
    }
}
