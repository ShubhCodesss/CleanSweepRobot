package ControlSystems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a single cell in the floor plan grid.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cell {
    private int x;                // X coordinate in the grid
    private int y;                // Y coordinate in the grid
    private String type;          // Floor type (e.g., hardwood, carpet, tile)
    private boolean obstacle;     // Indicates if there is an obstacle in the cell
    private boolean chargingStation; // Indicates if the cell has a charging station
    private boolean stairs;       // Indicates if the cell has stairs

    // Default constructor
    public Cell() {
    }

    // Getters and setters for x and y coordinates
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Getters and setters for cell properties

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public boolean isChargingStation() {
        return chargingStation;
    }

    public void setChargingStation(boolean chargingStation) {
        this.chargingStation = chargingStation;
    }

    public boolean isStairs() {
        return stairs;
    }

    public void setStairs(boolean stairs) {
        this.stairs = stairs;
    }
}
