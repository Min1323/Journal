package com.journal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single journal entry/activity.
 * Each entry tracks an activity with start time, end time, type, and productivity flags.
 */
public class JournalEntry {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String activityType;
    private boolean isConsuming;
    private boolean isProductive;

    // Default constructor for JSON deserialization
    public JournalEntry() {
    }

    public JournalEntry(LocalDateTime startTime, LocalDateTime endTime, 
                       String activityType, boolean isConsuming, boolean isProductive) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityType = activityType;
        this.isConsuming = isConsuming;
        this.isProductive = isProductive;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public boolean isConsuming() {
        return isConsuming;
    }

    public void setConsuming(boolean consuming) {
        isConsuming = consuming;
    }

    public boolean isProductive() {
        return isProductive;
    }

    public void setProductive(boolean productive) {
        isProductive = productive;
    }

    /**
     * Calculates the duration of this activity in hours.
     */
    public double getDurationHours() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return minutes / 60.0;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Activity: %s | %s - %s | Consuming: %s | Productive: %s | Duration: %.2f hours",
                activityType,
                startTime != null ? startTime.format(formatter) : "N/A",
                endTime != null ? endTime.format(formatter) : "N/A",
                isConsuming,
                isProductive,
                getDurationHours());
    }
}
