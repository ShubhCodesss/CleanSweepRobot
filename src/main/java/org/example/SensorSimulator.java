package org.example;

public class SensorSimulator {
    private Cell[][] floorPlan;  // 2D array representing the grid of cells
    private int currentX, currentY;  // Current position of the Clean Sweep

    // Constructor to initialize the grid and starting position
    public SensorSimulator(Cell[][] floorPlan, int startX, int startY) {
        this.floorPlan = floorPlan;
        this.currentX = startX;
        this.currentY = startY;
    }

    // Method to move the Clean Sweep in a specific direction
    public boolean move(String direction) {
        int newX = currentX;
        int newY = currentY;

        // Determine new coordinates based on the direction
        switch (direction.toLowerCase()) {
            case "up": newY -= 1; break;
            case "down": newY += 1; break;
            case "left": newX -= 1; break;
            case "right": newX += 1; break;
            default: System.out.println("Invalid direction"); return false;
        }

        // Check if the new coordinates are within the grid bounds
        if (newX >= 0 && newX < floorPlan[0].length && newY >= 0 && newY < floorPlan.length) {
            Cell newCell = floorPlan[newY][newX];

            // Check if the new cell contains an obstacle
            if (newCell.isObstacle()) {
                System.out.println("Movement blocked by obstacle.");
                return false;
            } else {
                // Update position if no obstacle is found
                currentX = newX;
                currentY = newY;
                System.out.println("Moved to (" + currentX + ", " + currentY + ")");
                return true;
            }
        } else {
            System.out.println("Movement out of bounds.");
            return false;
        }
    }

    // Method to display sensor feedback for the current cell
    public void printSensorFeedback() {
        Cell currentCell = floorPlan[currentY][currentX];
        System.out.println("Current Surface: " + currentCell.getSurfaceType());
        System.out.println("Dirt Level: " + currentCell.getDirt() + " units");
        System.out.println("Charging Station: " + (currentCell.hasChargingStation() ? "Yes" : "No"));
    }
}
