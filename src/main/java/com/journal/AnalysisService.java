package com.journal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides analysis functionality for journal entries.
 * Calculates statistics like consuming/productive percentages and cumulative hours.
 */
public class AnalysisService {
    private JournalManager journalManager;
    private CategoryManager categoryManager;

    public AnalysisService(JournalManager journalManager, CategoryManager categoryManager) {
        this.journalManager = journalManager;
        this.categoryManager = categoryManager;
    }

    /**
     * Analysis result for consuming/productive breakdown.
     */
    public static class ConsumingProductiveStats {
        private double consumingHours;
        private double productiveHours;
        private double totalHours;
        private double consumingPercentage;
        private double productivePercentage;

        public ConsumingProductiveStats(double consumingHours, double productiveHours) {
            this.consumingHours = consumingHours;
            this.productiveHours = productiveHours;
            this.totalHours = consumingHours + productiveHours;
            
            if (totalHours > 0) {
                this.consumingPercentage = (consumingHours / totalHours) * 100.0;
                this.productivePercentage = (productiveHours / totalHours) * 100.0;
            } else {
                this.consumingPercentage = 0.0;
                this.productivePercentage = 0.0;
            }
        }

        public double getConsumingHours() { return consumingHours; }
        public double getProductiveHours() { return productiveHours; }
        public double getTotalHours() { return totalHours; }
        public double getConsumingPercentage() { return consumingPercentage; }
        public double getProductivePercentage() { return productivePercentage; }
    }

    /**
     * Calculates consuming/productive statistics for a specific date.
     */
    public ConsumingProductiveStats getStatsForDate(LocalDate date) {
        List<JournalEntry> entries = journalManager.loadEntriesForDate(date);
        return calculateStats(entries);
    }

    /**
     * Calculates consuming/productive statistics for today.
     */
    public ConsumingProductiveStats getStatsForToday() {
        return getStatsForDate(LocalDate.now());
    }

    /**
     * Calculates lifetime consuming/productive statistics (all entries).
     */
    public ConsumingProductiveStats getLifetimeStats() {
        List<LocalDate> dates = journalManager.getAvailableDates();
        List<JournalEntry> allEntries = new ArrayList<>();
        
        for (LocalDate date : dates) {
            allEntries.addAll(journalManager.loadEntriesForDate(date));
        }
        
        return calculateStats(allEntries);
    }

    /**
     * Calculates statistics from a list of entries.
     */
    private ConsumingProductiveStats calculateStats(List<JournalEntry> entries) {
        double consumingHours = 0.0;
        double productiveHours = 0.0;

        for (JournalEntry entry : entries) {
            double duration = entry.getDurationHours();
            
            if (entry.isConsuming()) {
                consumingHours += duration;
            }
            if (entry.isProductive()) {
                productiveHours += duration;
            }
        }

        return new ConsumingProductiveStats(consumingHours, productiveHours);
    }

    /**
     * Gets cumulative hours for each main activity category.
     */
    public Map<String, Double> getMainActivityHours() {
        // Rebuild category tree from all entries
        categoryManager.reset();
        List<LocalDate> dates = journalManager.getAvailableDates();
        
        for (LocalDate date : dates) {
            List<JournalEntry> entries = journalManager.loadEntriesForDate(date);
            categoryManager.processEntries(entries);
        }

        // Get main categories (first level)
        List<Category> mainCategories = categoryManager.getMainCategories();
        Map<String, Double> result = new HashMap<>();
        
        for (Category category : mainCategories) {
            result.put(category.getActivityTitle(), category.getTotalTimeHours());
        }
        
        return result;
    }

    /**
     * Gets cumulative hours for each main activity category for a specific date.
     */
    public Map<String, Double> getMainActivityHoursForDate(LocalDate date) {
        // Rebuild category tree for this date only
        categoryManager.reset();
        List<JournalEntry> entries = journalManager.loadEntriesForDate(date);
        categoryManager.processEntries(entries);

        // Get main categories
        List<Category> mainCategories = categoryManager.getMainCategories();
        Map<String, Double> result = new HashMap<>();
        
        for (Category category : mainCategories) {
            result.put(category.getActivityTitle(), category.getTotalTimeHours());
        }
        
        return result;
    }

    /**
     * Gets the full category tree for visualization.
     */
    public Category getCategoryTree() {
        // Rebuild category tree from all entries
        categoryManager.reset();
        List<LocalDate> dates = journalManager.getAvailableDates();
        
        for (LocalDate date : dates) {
            List<JournalEntry> entries = journalManager.loadEntriesForDate(date);
            categoryManager.processEntries(entries);
        }
        
        return categoryManager.getRootCategory();
    }
}
