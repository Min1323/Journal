package com.journal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Journal program.
 * Provides a command-line interface for adding and viewing journal entries.
 */
public class JournalApp {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private JournalManager journalManager;
    private Scanner scanner;

    public JournalApp() {
        this.journalManager = new JournalManager();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("=== Journal App ===");
        System.out.println("Track your daily activities for productivity analysis\n");

        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addActivity();
                    break;
                case "2":
                    viewTodayEntries();
                    break;
                case "3":
                    viewDateEntries();
                    break;
                case "4":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    private void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Add Activity");
        System.out.println("2. View Today's Entries");
        System.out.println("3. View Entries for Specific Date");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private void addActivity() {
        System.out.println("\n--- Add New Activity ---");

        // Get start time
        LocalDateTime startTime = getDateTimeInput("Enter start time (yyyy-MM-dd HH:mm): ");
        if (startTime == null) return;

        // Get end time
        LocalDateTime endTime = getDateTimeInput("Enter end time (yyyy-MM-dd HH:mm): ");
        if (endTime == null) return;

        // Validate time order
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            System.out.println("Error: End time must be after start time.\n");
            return;
        }

        // Get activity type
        System.out.print("Enter activity type (e.g., Work, Exercise, Sleep, Study): ");
        String activityType = scanner.nextLine().trim();
        if (activityType.isEmpty()) {
            System.out.println("Error: Activity type cannot be empty.\n");
            return;
        }

        // Get is_consuming flag
        boolean isConsuming = getBooleanInput("Is this activity consuming? (y/n): ");

        // Get is_productive flag
        boolean isProductive = getBooleanInput("Is this activity productive? (y/n): ");

        // Create and save entry
        JournalEntry entry = new JournalEntry(startTime, endTime, activityType, isConsuming, isProductive);
        
        try {
            journalManager.saveEntry(entry);
            System.out.println("\n✓ Activity saved successfully!");
            System.out.println(entry.toString() + "\n");
        } catch (Exception e) {
            System.out.println("Error saving entry: " + e.getMessage() + "\n");
        }
    }

    private LocalDateTime getDateTimeInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Using current time...");
                return LocalDateTime.now();
            }

            try {
                return LocalDateTime.parse(input, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use yyyy-MM-dd HH:mm (e.g., 2024-01-15 14:30)");
                System.out.print("Try again or press Enter to use current time: ");
            }
        }
    }

    private boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    private void viewTodayEntries() {
        System.out.println("\n--- Today's Activities ---");
        List<JournalEntry> entries = journalManager.loadTodayEntries();
        displayEntries(entries);
    }

    private void viewDateEntries() {
        System.out.print("\nEnter date to view (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr, 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<JournalEntry> entries = journalManager.loadEntriesForDate(date);
            System.out.println("\n--- Activities for " + dateStr + " ---");
            displayEntries(entries);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd (e.g., 2024-01-15)\n");
        }
    }

    private void displayEntries(List<JournalEntry> entries) {
        if (entries.isEmpty()) {
            System.out.println("No entries found.\n");
            return;
        }

        System.out.println("Total entries: " + entries.size() + "\n");
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i).toString());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        JournalApp app = new JournalApp();
        app.run();
    }
}
