// src/main/java/ControlSystems/CleanSweepNavigation.java
package ControlSystems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the Clean Sweep robot's navigation system.
 */
public class CleanSweepNavigation {
    private int x, y;                // Current position on the grid
    private boolean isActive;        // Indicates if the robot is active
    private boolean shutdown;        // Indicates if the robot has shut down
    private FloorPlan floorPlan;     // The floor plan the robot navigates
    private Set<String> visitedCells; // Set of visited cells to avoid revisiting
    private int batteryLevel;        // Battery level of the robot

    /**
     * Constructor to initialize the robot's starting position and floor plan.
     * @param startX Starting X coordinate.
     * @param startY Starting Y coordinate.
     * @param floorPlan The floor plan to navigate.
     */
    public CleanSweepNavigation(int startX, int startY, FloorPlan floorPlan) {
        this.x = startX;
        this.y = startY;
        this.floorPlan = floorPlan;
        this.isActive = true;
        this.shutdown = false;
        this.visitedCells = new HashSet<>();
        this.batteryLevel = 100; // Assume battery starts at 100%
    }

    // Getters

    /**
     * Gets the current X coordinate.
     * @return Current X coordinate.
     */
    public int getX() { return x; }

    /**
     * Gets the current Y coordinate.
     * @return Current Y coordinate.
     */
    public int getY() { return y; }

    /**
     * Checks if the robot has shut down.
     * @return True if shut down, false otherwise.
     */
    public boolean isShutDown() { return shutdown; }

    /**
     * Navigates the robot based on sensor inputs and floor plan.
     */
    public void navigate() {
        if (!isActive || shutdown) {
            System.out.println("Robot is inactive or has shut down.");
            return; // Stop navigating if the robot is inactive or shutdown
        }

        // Mark current position as visited
        String currentPosition = x + "," + y;
        visitedCells.add(currentPosition);

        // Get sensor data from adjacent cells
        boolean sensorLeft = isObstacle(x - 1, y);
        boolean sensorRight = isObstacle(x + 1, y);
        boolean sensorUp = isObstacle(x, y - 1);
        boolean sensorDown = isObstacle(x, y + 1);
        boolean sensorBottom = isStairs(x, y); // Implement isStairs if necessary

        // Get floor types of adjacent cells
        String floorLeft = getFloorType(x - 1, y);
        String floorRight = getFloorType(x + 1, y);
        String floorUp = getFloorType(x, y - 1);
        String floorDown = getFloorType(x, y + 1);

        // Print current position and sensor states for debugging
        System.out.println("Current Position: (" + x + ", " + y + ")");
        System.out.println("Current Floor Type: " + getFloorType(x, y));
        System.out.println("Battery Level: " + batteryLevel + "%");
        System.out.println("Sensors:");
        System.out.println("  Left - Obstacle: " + sensorLeft + ", Floor Type: " + floorLeft);
        System.out.println("  Right - Obstacle: " + sensorRight + ", Floor Type: " + floorRight);
        System.out.println("  Up - Obstacle: " + sensorUp + ", Floor Type: " + floorUp);
        System.out.println("  Down - Obstacle: " + sensorDown + ", Floor Type: " + floorDown);

        // Check for shutdown due to stairs
        if (sensorBottom) {
            System.out.println("Stairs detected! Shutting down.");
            shutdown();
            return;
        }

        // Determine possible moves to unvisited cells
        List<String> possibleMoves = new ArrayList<>();

        if (!sensorRight && x + 1 < floorPlan.getGridSize() && !visitedCells.contains((x + 1) + "," + y)) {
            possibleMoves.add("RIGHT");
        }
        if (!sensorDown && y + 1 < floorPlan.getGridSize() && !visitedCells.contains(x + "," + (y + 1))) {
            possibleMoves.add("DOWN");
        }
        if (!sensorLeft && x - 1 >= 0 && !visitedCells.contains((x - 1) + "," + y)) {
            possibleMoves.add("LEFT");
        }
        if (!sensorUp && y - 1 >= 0 && !visitedCells.contains(x + "," + (y - 1))) {
            possibleMoves.add("UP");
        }

        if (!possibleMoves.isEmpty()) {
            // Choose the first available move (could be enhanced with better logic)
            String move = possibleMoves.get(0);
            switch (move) {
                case "RIGHT":
                    moveRight();
                    break;
                case "DOWN":
                    moveDown();
                    break;
                case "LEFT":
                    moveLeft();
                    break;
                case "UP":
                    moveUp();
                    break;
            }
            System.out.println("Moved " + move + " to position: (" + x + ", " + y + ")");

            // Update battery level based on floor type
            Cell currentCell = floorPlan.getCells()[y][x];
            int energyConsumption = getEnergyConsumption(currentCell.getType());
            batteryLevel -= energyConsumption;
            System.out.println("Battery Level after move: " + batteryLevel + "%");

            if (batteryLevel <= 0) {
                System.out.println("Battery depleted! Shutting down.");
                shutdown();
            }
        } else {
            // No unvisited adjacent cells, robot may choose to backtrack or shut down
            System.out.println("No unvisited adjacent cells available.");
            shutdown();
        }
    }

    /**
     * Checks if the cell at the specified coordinates has an obstacle.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if there is an obstacle or out of bounds, false otherwise.
     */
    private boolean isObstacle(int x, int y) {
        if (x < 0 || x >= floorPlan.getGridSize() || y < 0 || y >= floorPlan.getGridSize()) {
            return true; // Treat out-of-bounds as obstacles
        }
        return floorPlan.getCells()[y][x].isObstacle();
    }

    /**
     * Gets the floor type of the cell at the specified coordinates.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Floor type as a String, or "Unknown" if out of bounds.
     */
    private String getFloorType(int x, int y) {
        if (x < 0 || x >= floorPlan.getGridSize() || y < 0 || y >= floorPlan.getGridSize()) {
            return "Unknown"; // Out-of-bounds
        }
        return floorPlan.getCells()[y][x].getType();
    }

    /**
     * Placeholder method to detect stairs at the current position.
     * Can be overridden by subclasses for testing purposes.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if stairs are detected, false otherwise.
     */
    protected boolean isStairs(int x, int y) {
        // Implement logic to detect stairs if applicable
        return false;
    }

    /**
     * Calculates the energy consumption based on the floor type.
     * @param floorType Type of the floor.
     * @return Energy consumption as an integer.
     */
    private int getEnergyConsumption(String floorType) {
        switch (floorType) {
            case "hardwood":
                return 1;
            case "carpet":
                return 2;
            default:
                return 1; // Default consumption
        }
    }

    // Movement methods

    /**
     * Moves the robot one cell to the right.
     */
    private void moveRight() {
        x++;
    }

    /**
     * Moves the robot one cell to the left.
     */
    private void moveLeft() {
        x--;
    }

    /**
     * Moves the robot one cell upwards.
     */
    private void moveUp() {
        y--;
    }

    /**
     * Moves the robot one cell downwards.
     */
    private void moveDown() {
        y++;
    }

    // Additional methods

    /**
     * Manually sets the robot's position (used for testing or user intervention).
     * @param x X coordinate to set.
     * @param y Y coordinate to set.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Activates or deactivates the robot.
     * @param active True to activate, false to deactivate.
     */
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            this.shutdown = false; // Reset shutdown flag on reactivation
            System.out.println("Robot reactivated.");
        }
    }

    /**
     * Shuts down the robot.
     */
    private void shutdown() {
        isActive = false;
        shutdown = true;
        System.out.println("Clean Sweep has shut down.");
    }
}
