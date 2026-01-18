package com.journal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a hierarchical category structure for activities.
 * Categories are organized in a tree structure based on tokenized activity names.
 */
public class Category {
    private String activityTitle;
    private Category parent;
    private Map<String, Category> children; // Key is the child's activity title
    private int numberOfChildren;
    private double totalTimeHours; // Cumulative time spent in this category

    public Category(String activityTitle, Category parent) {
        this.activityTitle = activityTitle;
        this.parent = parent;
        this.children = new HashMap<>();
        this.numberOfChildren = 0;
        this.totalTimeHours = 0.0;
    }

    // Getters and Setters
    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Map<String, Category> getChildren() {
        return children;
    }

    public List<Category> getChildrenList() {
        return new ArrayList<>(children.values());
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public double getTotalTimeHours() {
        return totalTimeHours;
    }

    /**
     * Adds time to this category and propagates to parent categories.
     */
    public void addTime(double hours) {
        this.totalTimeHours += hours;
        if (parent != null) {
            parent.addTime(hours);
        }
    }

    /**
     * Gets or creates a child category with the given title.
     */
    public Category getOrCreateChild(String childTitle) {
        if (!children.containsKey(childTitle)) {
            Category child = new Category(childTitle, this);
            children.put(childTitle, child);
            numberOfChildren = children.size();
        }
        return children.get(childTitle);
    }

    /**
     * Gets the full path from root to this category.
     */
    public String getFullPath() {
        if (parent == null) {
            return activityTitle;
        }
        return parent.getFullPath() + " > " + activityTitle;
    }

    /**
     * Gets the depth level of this category (0 for root).
     */
    public int getDepth() {
        if (parent == null) {
            return 0;
        }
        return parent.getDepth() + 1;
    }

    @Override
    public String toString() {
        return String.format("Category: %s | Children: %d | Time: %.2f hours", 
                activityTitle, numberOfChildren, totalTimeHours);
    }
}
