package com.journal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages journal entries - handles storage and retrieval of activities.
 * Data is stored in JSON format, organized by date.
 */
public class JournalManager {
    private static final String DATA_DIR = "data";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Gson gson;

    public JournalManager() {
        // Configure Gson to handle LocalDateTime
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        builder.setPrettyPrinting();
        this.gson = builder.create();
        
        // Ensure data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Saves a journal entry for a specific date.
     */
    public void saveEntry(JournalEntry entry) throws IOException {
        LocalDate date = entry.getStartTime().toLocalDate();
        
        List<JournalEntry> entries = loadEntriesForDate(date);
        entries.add(entry);
        
        saveEntriesForDate(date, entries);
    }

    /**
     * Loads all entries for a specific date.
     */
    public List<JournalEntry> loadEntriesForDate(LocalDate date) {
        String filename = getFilenameForDate(date);
        File file = new File(filename);
        
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            List<JournalEntry> entries = gson.fromJson(reader, 
                new TypeToken<List<JournalEntry>>(){}.getType());
            return entries != null ? entries : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error loading entries: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Loads all entries for today.
     */
    public List<JournalEntry> loadTodayEntries() {
        return loadEntriesForDate(LocalDate.now());
    }

    /**
     * Saves all entries for a specific date.
     */
    private void saveEntriesForDate(LocalDate date, List<JournalEntry> entries) throws IOException {
        String filename = getFilenameForDate(date);
        
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(entries, writer);
        }
    }

    /**
     * Gets the filename for a specific date.
     */
    private String getFilenameForDate(LocalDate date) {
        return DATA_DIR + File.separator + date.format(DATE_FORMATTER) + ".json";
    }

    /**
     * Gets all available dates that have journal entries.
     */
    public List<LocalDate> getAvailableDates() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            return new ArrayList<>();
        }

        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<LocalDate> dates = new ArrayList<>();
        for (File file : files) {
            try {
                String dateStr = file.getName().replace(".json", "");
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                dates.add(date);
            } catch (Exception e) {
                // Skip invalid filenames
            }
        }
        return dates.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Loads all journal entries from all available dates.
     */
    public List<JournalEntry> loadAllEntries() {
        List<JournalEntry> allEntries = new ArrayList<>();
        List<LocalDate> dates = getAvailableDates();
        
        for (LocalDate date : dates) {
            allEntries.addAll(loadEntriesForDate(date));
        }
        
        return allEntries;
    }

    /**
     * Checks if a new entry would collide (overlap) with existing entries for the same date.
     * Returns a list of conflicting entries if collisions are found.
     */
    public List<JournalEntry> checkCollisions(JournalEntry newEntry) {
        List<JournalEntry> collisions = new ArrayList<>();
        
        if (newEntry.getStartTime() == null || newEntry.getEndTime() == null) {
            return collisions;
        }
        
        LocalDate date = newEntry.getStartTime().toLocalDate();
        List<JournalEntry> existingEntries = loadEntriesForDate(date);
        
        LocalDateTime newStart = newEntry.getStartTime();
        LocalDateTime newEnd = newEntry.getEndTime();
        
        for (JournalEntry existing : existingEntries) {
            if (existing.getStartTime() == null || existing.getEndTime() == null) {
                continue;
            }
            
            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd = existing.getEndTime();
            
            // Check for overlap: new activity overlaps if it starts before existing ends 
            // and ends after existing starts
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                collisions.add(existing);
            }
        }
        
        return collisions;
    }

    /**
     * Gets the end time of the last activity for a specific date.
     * Returns null if no activities exist for that date.
     */
    public LocalDateTime getLastActivityEndTime(LocalDate date) {
        List<JournalEntry> entries = loadEntriesForDate(date);
        
        if (entries.isEmpty()) {
            return null;
        }
        
        // Find the entry with the latest end time
        return entries.stream()
                .filter(e -> e.getEndTime() != null)
                .map(JournalEntry::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}
