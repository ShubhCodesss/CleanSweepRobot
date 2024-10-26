package ControlSystems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a single cell in the floor plan grid.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cell {
    private String type;            // Floor type (e.g., hardwood, carpet, tile)
    private boolean obstacle;       // Indicates if there is an obstacle in the cell
    private boolean chargingStation;// Indicates if the cell has a charging station
    private boolean stairs;         // Indicates if the cell has stairs

    // Default constructor
    public Cell() {
    }

    // Getters and setters

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
