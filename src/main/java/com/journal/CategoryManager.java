package com.journal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Manages the categorization of activities by tokenizing activity names
 * and building a hierarchical category tree.
 */
public class CategoryManager {
    private Category rootCategory;
    private Map<String, Category> categoryCache; // Cache for quick lookup

    public CategoryManager() {
        this.rootCategory = new Category("ROOT", null);
        this.categoryCache = new HashMap<>();
    }

    /**
     * Tokenizes an activity name into words.
     * Example: "listening to audio book" -> ["listening", "to", "audio", "book"]
     */
    public List<String> tokenizeActivity(String activityType) {
        if (activityType == null || activityType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Convert to lowercase and split by spaces
        String normalized = activityType.toLowerCase().trim();
        String[] tokens = normalized.split("\\s+");
        return Arrays.asList(tokens);
    }

    /**
     * Processes a journal entry and adds it to the category tree.
     */
    public void processEntry(JournalEntry entry) {
        if (entry == null || entry.getActivityType() == null) {
            return;
        }

        List<String> tokens = tokenizeActivity(entry.getActivityType());
        if (tokens.isEmpty()) {
            return;
        }

        // Navigate/create the category path
        Category currentCategory = rootCategory;
        for (String token : tokens) {
            currentCategory = currentCategory.getOrCreateChild(token);
        }

        // Add the time to the final category and all its parents
        double duration = entry.getDurationHours();
        currentCategory.addTime(duration);
    }

    /**
     * Processes multiple entries and builds the category tree.
     */
    public void processEntries(List<JournalEntry> entries) {
        for (JournalEntry entry : entries) {
            processEntry(entry);
        }
    }

    /**
     * Gets the root category.
     */
    public Category getRootCategory() {
        return rootCategory;
    }

    /**
     * Gets all categories at a specific depth level.
     */
    public List<Category> getCategoriesAtDepth(int depth) {
        List<Category> result = new ArrayList<>();
        collectCategoriesAtDepth(rootCategory, depth, 0, result);
        return result;
    }

    /**
     * Recursively collects categories at a specific depth.
     */
    private void collectCategoriesAtDepth(Category category, int targetDepth, int currentDepth, List<Category> result) {
        if (currentDepth == targetDepth) {
            result.add(category);
            return;
        }

        for (Category child : category.getChildrenList()) {
            collectCategoriesAtDepth(child, targetDepth, currentDepth + 1, result);
        }
    }

    /**
     * Gets all main categories (first level categories, children of root).
     */
    public List<Category> getMainCategories() {
        return rootCategory.getChildrenList();
    }

    /**
     * Finds a category by its full path.
     */
    public Category findCategoryByPath(List<String> path) {
        Category current = rootCategory;
        for (String token : path) {
            Map<String, Category> children = current.getChildren();
            if (!children.containsKey(token.toLowerCase())) {
                return null;
            }
            current = children.get(token.toLowerCase());
        }
        return current;
    }

    /**
     * Resets the category tree (useful for rebuilding).
     */
    public void reset() {
        this.rootCategory = new Category("ROOT", null);
        this.categoryCache.clear();
    }
}
