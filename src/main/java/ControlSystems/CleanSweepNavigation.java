package ControlSystems;

import java.io.IOException;
import java.util.*;

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
    private Logger logger;           // Logger for recording actions

    // Battery threshold to trigger return-to-charge behavior
    private static final int LOW_BATTERY_THRESHOLD = 20;

    // Directions for movement: right, down, left, up
    private static final int[][] DIRECTIONS = { {1, 0}, {0, 1}, {-1, 0}, {0, -1} };

    /**
     * Constructor to initialize the robot's starting position and floor plan.
     * @param startX Starting X coordinate.
     * @param startY Starting Y coordinate.
     * @param floorPlan The floor plan to navigate.
     * @throws IOException If the logger cannot be initialized.
     */
    public CleanSweepNavigation(int startX, int startY, FloorPlan floorPlan) throws IOException {
        this.x = startX;
        this.y = startY;
        this.floorPlan = floorPlan;
        this.isActive = true;
        this.shutdown = false;
        this.visitedCells = new HashSet<>();
        this.batteryLevel = 100; // Assume battery starts at 100%
        this.logger = new Logger("clean_sweep_log.txt"); // Initialize the logger
    }

    // Existing methods (getX, getY, isShutDown, setPosition, setActive, isAtChargingStation) remain unchanged

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isShutDown() { return shutdown; }
    public int getBatteryLevel() { return batteryLevel; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        System.out.println("Robot position manually set to: (" + x + ", " + y + ")");
        logger.log("Robot position manually set to: (" + x + ", " + y + ")");
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            this.shutdown = false; // Reset shutdown flag on reactivation
            System.out.println("Robot reactivated.");
            logger.log("Robot reactivated.");
        } else {
            System.out.println("Robot deactivated.");
            logger.log("Robot deactivated.");
        }
    }

    public boolean isAtChargingStation() {
        return floorPlan.getCells()[y][x].isChargingStation();
    }

    /**
     * Navigates the robot based on sensor inputs and floor plan.
     */
    public void navigate() {
        if (!isActive || shutdown) {
            System.out.println("Robot is inactive or has shut down.");
            logger.log("Robot is inactive or has shut down.");
            return; // Stop navigating if the robot is inactive or shutdown
        }

        // Mark the current position as visited
        String currentPosition = x + "," + y;
        visitedCells.add(currentPosition);

        // Existing code to check battery level and move to charging station
        if (batteryLevel <= LOW_BATTERY_THRESHOLD && floorPlan.hasChargingStation() && !isAtChargingStation()) {
            moveToChargingStation();
            return;
        }

        // Existing movement logic (modified to integrate new pathfinding and obstacle detection)
        List<Cell> path = findNearestUnvisitedCell();
        if (path.isEmpty()) {
            System.out.println("No unvisited cells left. Cleaning complete.");
            logger.log("No unvisited cells left. Cleaning complete.");
            shutdown();
            return;
        }

        // Move along the path
        for (Cell cell : path) {
            moveToCell(cell);
            if (shutdown) {
                return;
            }
        }
    }

    /**
     * Moves the robot to the specified cell.
     * @param cell The cell to move to.
     */
    private void moveToCell(Cell cell) {
        x = cell.getX();
        y = cell.getY();
        String positionKey = x + "," + y;
        visitedCells.add(positionKey);

        System.out.println("Moved to position (" + x + ", " + y + ")");
        logger.log("Moved to position (" + x + ", " + y + ")");

        System.out.println("Surface Type: " + cell.getType());
        logger.log("Surface Type: " + cell.getType());

        // Update battery level based on floor type
        int energyConsumption = getEnergyConsumption(cell.getType());
        batteryLevel -= energyConsumption;

        System.out.println("Battery Level after move: " + batteryLevel + "%");
        logger.log("Battery Level after move: " + batteryLevel + "%");

        // Simulate cleaning the cell
        System.out.println("Cleaned cell at position (" + x + ", " + y + ")");
        logger.log("Cleaned cell at position (" + x + ", " + y + ")");

        if (batteryLevel <= 0) {
            System.out.println("Battery depleted! Shutting down.");
            logger.log("Battery depleted! Shutting down.");
            shutdown();
        }
    }

    /**
     * Finds the shortest path to the nearest unvisited cell using BFS.
     * @return List of cells representing the path.
     */
    private List<Cell> findNearestUnvisitedCell() {
        Queue<List<Cell>> queue = new LinkedList<>();
        Set<String> bfsVisited = new HashSet<>();

        Cell startCell = floorPlan.getCells()[y][x];
        List<Cell> startPath = new ArrayList<>();
        startPath.add(startCell);
        queue.add(startPath);
        bfsVisited.add(x + "," + y);

        while (!queue.isEmpty()) {
            List<Cell> path = queue.poll();
            Cell currentCell = path.get(path.size() - 1);
            int currX = currentCell.getX();
            int currY = currentCell.getY();

            String positionKey = currX + "," + currY;

            // Only consider cells that have not been visited by the robot
            if (!visitedCells.contains(positionKey)) {
                if (!isObstacle(currX, currY)) {
                    return path;
                }
            }

            // Explore neighbors in all four directions
            for (int[] dir : DIRECTIONS) {
                int newX = currX + dir[0];
                int newY = currY + dir[1];
                String newPositionKey = newX + "," + newY;

                if (isValidPosition(newX, newY) && !bfsVisited.contains(newPositionKey)) {
                    if (!isObstacle(newX, newY)) {
                        Cell neighbor = floorPlan.getCells()[newY][newX];
                        List<Cell> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(newPath);
                        bfsVisited.add(newPositionKey);
                    }
                }
            }
        }

        return new ArrayList<>(); // No unvisited cells found
    }

    /**
     * Checks if the given position is within the grid bounds.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if position is valid, false otherwise.
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < floorPlan.getGridSize() && y >= 0 && y < floorPlan.getGridSize();
    }

    /**
     * Moves the robot to the nearest charging station and recharges the battery.
     */
    private void moveToChargingStation() {
        System.out.println("Battery level low (" + batteryLevel + "%). Navigating to charging station...");
        logger.log("Battery level low (" + batteryLevel + "%). Navigating to charging station...");

        List<Cell> path = findPathToChargingStation();
        if (path.isEmpty()) {
            System.out.println("No path to charging station found. Shutting down.");
            logger.log("No path to charging station found. Shutting down.");
            shutdown();
            return;
        }

        for (Cell cell : path) {
            moveToCell(cell);
            if (shutdown) {
                return;
            }
        }

        if (isAtChargingStation()) {
            System.out.println("Reached charging station. Recharging...");
            logger.log("Reached charging station. Recharging...");
            batteryLevel = 100; // Recharge battery
            System.out.println("Battery fully recharged.");
            logger.log("Battery fully recharged.");
        } else {
            System.out.println("Failed to reach charging station. Shutting down.");
            logger.log("Failed to reach charging station. Shutting down.");
            shutdown();
        }
    }

    /**
     * Finds the shortest path to the nearest charging station using BFS.
     * @return List of cells representing the path.
     */
    private List<Cell> findPathToChargingStation() {
        Queue<List<Cell>> queue = new LinkedList<>();
        Set<String> bfsVisited = new HashSet<>();

        Cell startCell = floorPlan.getCells()[y][x];
        List<Cell> startPath = new ArrayList<>();
        startPath.add(startCell);
        queue.add(startPath);
        bfsVisited.add(x + "," + y);

        while (!queue.isEmpty()) {
            List<Cell> path = queue.poll();
            Cell currentCell = path.get(path.size() - 1);
            int currX = currentCell.getX();
            int currY = currentCell.getY();

            String positionKey = currX + "," + currY;

            if (floorPlan.getCells()[currY][currX].isChargingStation()) {
                return path;
            }

            // Explore neighbors
            for (int[] dir : DIRECTIONS) {
                int newX = currX + dir[0];
                int newY = currY + dir[1];
                String newPositionKey = newX + "," + newY;

                if (isValidPosition(newX, newY) && !bfsVisited.contains(newPositionKey)) {
                    if (!isObstacle(newX, newY)) {
                        Cell neighbor = floorPlan.getCells()[newY][newX];
                        List<Cell> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(newPath);
                        bfsVisited.add(newPositionKey);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path to charging station found
    }

    /**
     * Checks if the cell at the specified coordinates has an obstacle.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if there is an obstacle or out of bounds, false otherwise.
     */
    private boolean isObstacle(int x, int y) {
        if (!isValidPosition(x, y)) {
            return true; // Treat out-of-bounds as obstacles
        }
        Cell cell = floorPlan.getCells()[y][x];

        // Simulate dynamic obstacle detection
        if (cell.isObstacle()) {
            System.out.println("Obstacle detected at (" + x + ", " + y + ")");
            logger.log("Obstacle detected at (" + x + ", " + y + ")");
            return true;
        }

        // Additional logic for dynamic obstacles can be added here

        return false;
    }

    /**
     * Gets the floor type of the cell at the specified coordinates.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Floor type as a String, or "Unknown" if out of bounds.
     */
    private String getFloorType(int x, int y) {
        if (!isValidPosition(x, y)) {
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
        switch (floorType.toLowerCase()) {
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

    /**
     * Shuts down the robot.
     */
    private void shutdown() {
        isActive = false;
        shutdown = true;
        System.out.println("Clean Sweep has shut down.");
        logger.log("Clean Sweep has shut down.");
        logger.close(); // Close the logger
    }

    /**
     * Placeholder method to detect stairs at the current position.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if stairs are detected, false otherwise.
     */
    protected boolean isStairs(int x, int y) {
        if (!isValidPosition(x, y)) {
            return false; // Out-of-bounds, no stairs detected
        }
        return floorPlan.getCells()[y][x].isStairs(); // Check if the current cell has stairs
    }

    // Existing movement methods (moveRight, moveLeft, moveUp, moveDown) remain unchanged
    private void moveRight() { x++; }
    private void moveLeft() { x--; }
    private void moveUp() { y--; }
    private void moveDown() { y++; }
}
