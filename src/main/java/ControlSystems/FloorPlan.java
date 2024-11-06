package ControlSystems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the floor plan consisting of a grid of cells.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FloorPlan {
    private int gridSize;   // Size of the grid (e.g., 10 for a 10x10 grid)
    private Cell[][] cells; // 2D array representing the grid of cells

    // Default constructor
    public FloorPlan() {
    }

    /**
     * Checks if the floor plan has at least one charging station.
     * @return True if there is a charging station, false otherwise.
     */
    public boolean hasChargingStation() {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (cells[y][x].isChargingStation()) {
                    return true;
                }
            }
        }
        return false;
    }

    // Getters and setters

    /**
     * Gets the size of the grid.
     * @return Grid size as an integer.
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Sets the size of the grid.
     * @param gridSize Grid size to set.
     */
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * Gets the grid of cells.
     * @return 2D array of Cell objects.
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Sets the grid of cells.
     * @param cells 2D array of Cell objects to set.
     */
    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }
}
