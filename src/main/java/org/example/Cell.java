package org.example;

public class Cell {
    private String surfaceType;  // e.g., "bare floor", "low-pile carpet", "high-pile carpet"
    private boolean obstacle;    // True if the cell contains an obstacle (e.g., wall or furniture)
    private boolean chargingStation;  // True if the cell contains a charging station
    private int dirt;  // Amount of dirt in the cell (0 means clean, positive means dirty)

    // Constructor to initialize the cell properties
    public Cell(String surfaceType, boolean obstacle, boolean chargingStation, int dirt) {
        this.surfaceType = surfaceType;
        this.obstacle = obstacle;
        this.chargingStation = chargingStation;
        this.dirt = dirt;
    }

    // Getter methods for cell properties
    public String getSurfaceType() { return surfaceType; }
    public boolean isObstacle() { return obstacle; }
    public boolean hasChargingStation() { return chargingStation; }
    public int getDirt() { return dirt; }

    // Method to clean the cell by reducing dirt level
    public void cleanDirt() {
        if (dirt > 0) {
            dirt--;  // Decrease dirt by 1 unit for each cleaning action
        }
    }
}
