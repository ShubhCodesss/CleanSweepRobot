package ControlSystems;
import java.util.Random;
/**
 * Research Notes: Navigation Simulation
 *
 * - Algorithms for virtual navigation:
 *   - A* Algorithm: A pathfinding algorithm that simulates movement through a virtual environment.
 *   - Dijkstra’s Algorithm: Used for shortest pathfinding in a graph-like floor map.
 *   - Potential Field Method: An algorithm where the robot is repelled from obstacles and attracted toward the goal.
 *
 * - Virtual sensors:
 *   - Simulate LiDAR using raycasting techniques to detect virtual obstacles.
 *   - Simulate Infrared by projecting a straight-line sensor detection.
 *
 * - Key challenges in software:
 *   - Efficient collision detection algorithms in a virtual grid.
 *   - Dynamic updates to the environment during movement simulation.
 */

public class CleanSweepNavigation {

    // Grid size (assuming a 10x10 home for this example)
    public static final int GRID_SIZE = 10;

    // Clean Sweep's current position on the grid
    int x, y;

    // Sensors (simulating the presence of obstacles around the robot)
    boolean sensorLeft, sensorRight, sensorUp, sensorDown;
    boolean sensorBottom; // Sensor for stairs

    // Indicates whether the robot is active or shut down
    boolean isActive;
    boolean shutdown;

    public CleanSweepNavigation(int startX, int startY) {
        // Initialize the robot at a given start position
        this.x = startX;
        this.y = startY;
        this.isActive = true;
        this.shutdown = false;
    }
    
    public void setSensors(boolean sensorLeft, boolean sensorRight, boolean sensorUp, boolean sensorDown, boolean sensorBottom) {
        this.sensorLeft = sensorLeft;
        this.sensorRight = sensorRight;
        this.sensorUp = sensorUp;
        this.sensorDown = sensorDown;
        this.sensorBottom = sensorBottom;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isShutDown() {
        return shutdown;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            this.shutdown = false;
        }
    }



    // Simulate the sensors detecting obstacles (for example purposes)
    public void checkSensors() {
        Random rand = new Random();

        // Simulate random obstacles detected by directional sensors
        sensorLeft = rand.nextBoolean();
        sensorRight = rand.nextBoolean();
        sensorUp = rand.nextBoolean();
        sensorDown = rand.nextBoolean();
        sensorBottom = rand.nextBoolean(); // Detect stairs or declines
    }

    // Method to move Clean Sweep based on sensor input
    public void navigate() {
        while (isActive) {
            checkSensors(); // Update sensor readings

            if (sensorBottom) {
                System.out.println("Stairs detected! Shutting down.");
                shutdown();
                return;
            }

            // Try moving based on sensor readings
            if (!sensorRight && x + 1 < GRID_SIZE) {
                moveRight();
            } else if (!sensorLeft && x - 1 >= 0) {
                moveLeft();
            } else if (!sensorUp && y + 1 < GRID_SIZE) {
                moveUp();
            } else if (!sensorDown && y - 1 >= 0) {
                moveDown();
            } else {
                // All directions blocked, shut down
                System.out.println("Unable to move! Shutting down.");
                shutdown();
                return;
            }

            // Simulate a short delay between movements
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Move right (x + 1, y)
    public void moveRight() {
        x++;
        System.out.println("Moved right to position: (" + x + ", " + y + ")");
    }

    // Move left (x - 1, y)
    public void moveLeft() {
        x--;
        System.out.println("Moved left to position: (" + x + ", " + y + ")");
    }

    // Move up (x, y + 1)
    public void moveUp() {
        y++;
        System.out.println("Moved up to position: (" + x + ", " + y + ")");
    }

    // Move down (x, y - 1)
    public void moveDown() {
        y--;
        System.out.println("Moved down to position: (" + x + ", " + y + ")");
    }

    // Shut down the Clean Sweep
    public void shutdown() {
        isActive = false;
        shutdown = true;
        System.out.println("Clean Sweep has shut down.");
    }

    public static void main(String[] args) {
        // Starting the Clean Sweep at position (0, 0)
        CleanSweepNavigation cleanSweep = new CleanSweepNavigation(0, 0);
        cleanSweep.navigate();
    }
}

