# Journal App

A productivity journal application to track your daily activities throughout the 24 hours of each day. This program helps you analyze your life by tracking activities with minimal clicks.

## Features

- **Easy Activity Tracking**: Add activities with just a few inputs:
  - Start time
  - End time
  - Activity type
  - Is consuming flag (y/n)
  - Is productive flag (y/n)
- **Permanent Storage**: All data is stored in JSON format, organized by date
- **View Entries**: View today's activities or activities for any specific date
- **Automatic Categorization**: Activities are automatically tokenized and organized into hierarchical categories
  - Example: "listening to audio book" and "listening to music" are both categorized under "listening" > "to", then branch into "audio" vs "music"
- **Analysis & Statistics**: 
  - View consuming vs productive percentages (by day or lifetime)
  - See cumulative hours for each main activity category
  - Visual GUI for easy data exploration

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building and Running

### Build the project:
```bash
mvn clean compile
```

### Run the CLI application:
```bash
mvn exec:java -Dexec.mainClass="com.journal.JournalApp"
```

### Run the GUI application:
```bash
mvn exec:java -Dexec.mainClass="com.journal.JournalGUI"
```

Or compile and run manually:
```bash
mvn package
# GUI
java -cp target/journal-app-1.0.0.jar:target/dependency/* com.journal.JournalGUI
```

## Usage

### CLI Mode (JournalApp)

1. **Add Activity**: Choose option 1 and enter:
   - Start time in format: `yyyy-MM-dd HH:mm` (e.g., `2024-01-15 09:00`)
   - End time in the same format
   - Activity type (e.g., "listening to audio book", "listening to music", "working on project")
   - Whether it's consuming (y/n)
   - Whether it's productive (y/n)

2. **View Today's Entries**: Choose option 2 to see all activities for today

3. **View Specific Date**: Choose option 3 and enter a date to view activities for that day

4. **Exit**: Choose option 4 to exit the program

### GUI Mode (JournalGUI)

The GUI provides a visual interface for analyzing your journal data:

- **Date Selection**: Choose a specific date from the dropdown or select "Lifetime View" to see all-time statistics
- **Statistics Panel**: View consuming vs productive percentages and total hours
- **Activities Panel**: See cumulative hours for each main activity category, sorted by time spent
- **Details Panel**: View detailed breakdown of statistics and activities
- **Refresh**: Click "Refresh Data" to reload all data from files

### Activity Categorization

Activities are automatically tokenized and categorized:
- "listening to audio book" → categorized as: listening > to > audio > book
- "listening to music" → categorized as: listening > to > music
- Both share the same parent categories ("listening" and "to") and branch at the third level

## Data Storage

Journal entries are stored in the `data/` directory as JSON files, one file per date (format: `yyyy-MM-dd.json`).

## Project Structure

```
Journal/
├── src/main/java/com/journal/
│   ├── JournalApp.java          # Main CLI application
│   ├── JournalGUI.java           # GUI application for analysis
│   ├── JournalEntry.java        # Activity/entry model
│   ├── JournalManager.java      # Data storage and retrieval
│   ├── Category.java            # Hierarchical category structure
│   ├── CategoryManager.java     # Tokenization and category tree building
│   ├── AnalysisService.java     # Statistics and analysis calculations
│   └── LocalDateTimeAdapter.java # JSON serialization helper
├── data/                         # Journal data storage (created at runtime)
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

## How It Works

### Categorization System

When you enter an activity like "listening to audio book", the system:
1. Tokenizes it into words: ["listening", "to", "audio", "book"]
2. Builds a hierarchical category tree:
   - Level 1: "listening"
   - Level 2: "to" (child of "listening")
   - Level 3: "audio" (child of "to")
   - Level 4: "book" (child of "audio")
3. Tracks cumulative time at each level
4. Similar activities share parent categories

### Analysis Features

- **Consuming vs Productive**: Calculates percentage breakdown based on cumulative hours
- **Main Activities**: Shows top-level categories (first word of activity) with total time
- **Day View**: Statistics for a specific date
- **Lifetime View**: Statistics across all recorded data
