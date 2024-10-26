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
    private Set<String> visitedCells;// Set of visited cells to avoid revisiting
    private int batteryLevel;        // Battery level of the robot



    /**
     * Placeholder method to detect stairs at the current position.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if stairs are detected, false otherwise.
     */
    protected boolean isStairs(int x, int y) {
        if (x < 0 || x >= floorPlan.getGridSize() || y < 0 || y >= floorPlan.getGridSize()) {
            return false; // Out-of-bounds, no stairs detected
        }
        return floorPlan.getCells()[y][x].isStairs(); // Check if the current cell has stairs
    }

    /**
     * Manually sets the robot's position (used for testing or user intervention).
     * @param x X coordinate to set.
     * @param y Y coordinate to set.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        System.out.println("Robot position manually set to: (" + x + ", " + y + ")");
    }
    public boolean isAtChargingStation() {
        return floorPlan.getCells()[y][x].isChargingStation();
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
        } else {
            System.out.println("Robot deactivated.");
        }
    }

    // Battery threshold to trigger return-to-charge behavior

    private static final int LOW_BATTERY_THRESHOLD = 20;

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

    public int getX() { return x; }

    public int getY() { return y; }

    public boolean isShutDown() { return shutdown; }

    /**
     * Navigates the robot based on sensor inputs and floor plan.
     */
    public void navigate() {
        if (!isActive || shutdown) {
            System.out.println("Robot is inactive or has shut down.");
            return; // Stop navigating if the robot is inactive or shutdown
        }
        // Mark the current position as visited
        String currentPosition = x + "," + y;
        visitedCells.add(currentPosition);

        // Example of a simple movement logic that only moves right if possible
        if (!isObstacle(x + 1, y) && x + 1 < floorPlan.getGridSize() && !visitedCells.contains((x + 1) + "," + y)) {
            moveRight();
        } else if (!isObstacle(x, y + 1) && y + 1 < floorPlan.getGridSize() && !visitedCells.contains(x + "," + (y + 1))) {
            moveDown();
        } else if (!isObstacle(x - 1, y) && x - 1 >= 0 && !visitedCells.contains((x - 1) + "," + y)) {
            moveLeft();
        } else if (!isObstacle(x, y - 1) && y - 1 >= 0 && !visitedCells.contains(x + "," + (y - 1))) {
            moveUp();
        }

        // Update battery level based on floor type
        batteryLevel -= getEnergyConsumption(getFloorType(x, y));
        System.out.println("Battery Level after move: " + batteryLevel + "%");

        // Check if battery is depleted
        if (batteryLevel <= 0) {
            shutdown = true;
            System.out.println("Battery depleted! Shutting down.");
        }

        // Gather possible moves
        List<String> possibleMoves = new ArrayList<>();

        if (!isObstacle(x + 1, y)) possibleMoves.add("RIGHT");
        if (!isObstacle(x - 1, y)) possibleMoves.add("LEFT");
        if (!isObstacle(x, y + 1)) possibleMoves.add("DOWN");
        if (!isObstacle(x, y - 1)) possibleMoves.add("UP");

        // Shutdown if no moves are possible
        if (possibleMoves.isEmpty()) {
            shutdown = true;
            System.out.println("All sides blocked. Robot shutting down.");
            return;
        }



        // Check if battery is low and only navigate to charging station if one exists
        if (batteryLevel <= LOW_BATTERY_THRESHOLD && floorPlan.hasChargingStation() && !isAtChargingStation()) {
            System.out.println("Battery level low (20%). Navigating to charging station...");
            moveToChargingStation();
        } else if (isAtChargingStation()) {
            System.out.println("Reached charging station. Recharging...");
            batteryLevel = 100; // Reset battery to full
            System.out.println("Battery fully recharged.");
        } else {
            // Regular movement logic (move to the right as a placeholder)
            moveRight();
            batteryLevel -= getEnergyConsumption(getFloorType(x, y)); // Decrease battery based on floor type
            System.out.println("Battery Level after move: " + batteryLevel + "%");

            // Check if battery is depleted
            if (batteryLevel <= 0) {
                shutdown = true;
                System.out.println("Battery depleted! Shutting down.");
            }
        }
    // Get sensor data from adjacent cells
        boolean sensorLeft = isObstacle(x - 1, y);
        boolean sensorRight = isObstacle(x + 1, y);
        boolean sensorUp = isObstacle(x, y - 1);
        boolean sensorDown = isObstacle(x, y + 1);
        boolean sensorBottom = isStairs(x, y); // Implement isStairs if necessary

        // Print current position and sensor states for debugging
        System.out.println("Current Position: (" + x + ", " + y + ")");
        System.out.println("Current Floor Type: " + getFloorType(x, y));
        System.out.println("Battery Level: " + batteryLevel + "%");

        // Check for shutdown due to stairs
        if (sensorBottom) {
            System.out.println("Stairs detected! Shutting down.");
            shutdown();
            return;
        }

        // Determine possible moves to unvisited cells
        List<String> possibleMovesCells = new ArrayList<>();
        if (!sensorRight && x + 1 < floorPlan.getGridSize() && !visitedCells.contains((x + 1) + "," + y)) {
            possibleMovesCells.add("RIGHT");
        }
        if (!sensorDown && y + 1 < floorPlan.getGridSize() && !visitedCells.contains(x + "," + (y + 1))) {
            possibleMovesCells.add("DOWN");
        }
        if (!sensorLeft && x - 1 >= 0 && !visitedCells.contains((x - 1) + "," + y)) {
            possibleMovesCells.add("LEFT");
        }
        if (!sensorUp && y - 1 >= 0 && !visitedCells.contains(x + "," + (y - 1))) {
            possibleMovesCells.add("UP");
        }

        // Move the robot if possible
        if (!possibleMovesCells.isEmpty()) {
            String move = possibleMovesCells.get(0);
            switch (move) {
                case "RIGHT": moveRight(); break;
                case "DOWN": moveDown(); break;
                case "LEFT": moveLeft(); break;
                case "UP": moveUp(); break;
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
            System.out.println("No unvisited adjacent cells available.");
            shutdown();
        }
    }

    /**
     * Moves the robot to the nearest charging station and recharges the battery.
     */
    private void moveToChargingStation() {
        System.out.println("Battery level low (20%). Navigating to charging station...");

        // Assume charging station is at (2, 2) for this example
        int chargingStationX = 2;
        int chargingStationY = 2;

        if (x < chargingStationX) {
            x++;
        } else if (x > chargingStationX) {
            x--;
        } else if (y < chargingStationY) {
            y++;
        } else if (y > chargingStationY) {
            y--;
        }

        System.out.println("Current Position: (" + x + ", " + y + ")");

        // Check if at charging station after each move
        if (isAtChargingStation()) {
            System.out.println("Reached charging station. Recharging...");
            batteryLevel = 100; // Reset battery to full
            System.out.println("Battery fully recharged.");
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
     * Calculates the energy consumption based on the floor type.
     * @param floorType Type of the floor.
     * @return Energy consumption as an integer.
     */
    private int getEnergyConsumption(String floorType) {
        switch (floorType) {
            case "hardwood":
                return 1; // Lower energy consumption
            case "tile":
                return 2; // Moderate energy consumption
            case "carpet":
                return 5; // Higher energy consumption for carpet
            default:
                return 1; // Default consumption
        }
    }


    private void moveRight() { x++; }

    private void moveLeft() { x--; }

    private void moveUp() { y--; }

    private void moveDown() { y++; }

    private void shutdown() {
        isActive = false;
        shutdown = true;
        System.out.println("Clean Sweep has shut down.");
    }
}
