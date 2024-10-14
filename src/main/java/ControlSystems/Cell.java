// src/main/java/ControlSystems/Cell.java
package ControlSystems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a single cell in the floor plan grid.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cell {
    private String type;      // Floor type (e.g., hardwood, carpet)
    private boolean obstacle; // Indicates if there is an obstacle in the cell

    // Default constructor
    public Cell() {
    }

    // Getters and setters

    /**
     * Gets the floor type of the cell.
     * @return Floor type as a String.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the floor type of the cell.
     * @param type Floor type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Checks if the cell has an obstacle.
     * @return True if there is an obstacle, false otherwise.
     */
    public boolean isObstacle() {
        return obstacle;
    }

    /**
     * Sets whether the cell has an obstacle.
     * @param obstacle True to set an obstacle, false otherwise.
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }
}
