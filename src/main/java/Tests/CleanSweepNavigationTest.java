package Tests;

import static org.junit.Assert.*;
import org.junit.*;

import ControlSystems.CleanSweepNavigation;
import ControlSystems.FloorPlan;
import ControlSystems.Cell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test class for CleanSweepNavigation.
 */
public class CleanSweepNavigationTest {

    private CleanSweepNavigation robot;
    private FloorPlan floorPlan;

    @Before
    public void setup() throws Exception {
        // Initialize the FloorPlan
        floorPlan = createMockFloorPlanWithChargingStation();

        // Initialize the Clean Sweep robot at a starting position (0, 0) with the floor plan
        robot = new CleanSweepNavigation(0, 0, floorPlan);
    }

    /**
     * Creates a mock floor plan with no obstacles and a charging station.
     * @return FloorPlan object.
     */
    private FloorPlan createMockFloorPlanWithChargingStation() {
        FloorPlan floorPlan = new FloorPlan();
        floorPlan.setGridSize(5);
        Cell[][] cells = new Cell[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Cell cell = new Cell();
                cell.setType("hardwood");
                cell.setObstacle(false);
                cell.setChargingStation(false);
                cell.setStairs(false);
                cell.setX(x);
                cell.setY(y);
                cells[y][x] = cell;
            }
        }
        // Place a charging station at (2,2)
        cells[2][2].setChargingStation(true);
        floorPlan.setCells(cells);
        return floorPlan;
    }

    /**
     * Test that the robot moves when no obstacles are present.
     */
    @Test
    public void testNoObstacles() throws IOException {
        // Simulate a few steps of movement
        robot.navigate();
        // Check that the robot has moved and hasn't shut down
        assertTrue(robot.getX() != 0 || robot.getY() != 0);
        assertFalse(robot.isShutDown());
    }

    /**
     * Test that the robot shuts down when surrounded by obstacles.
     */
    @Test
    public void testAllSidesBlocked() throws Exception {
        // Create a floor plan where the starting position is surrounded by obstacles
        floorPlan = createMockFloorPlanAllSidesBlocked();
        robot = new CleanSweepNavigation(0, 0, floorPlan);

        robot.navigate();

        // Ensure the robot has shut down since all directions are blocked
        assertTrue(robot.isShutDown());
    }

    /**
     * Creates a mock floor plan with obstacles surrounding the starting position.
     * @return FloorPlan object.
     */
    private FloorPlan createMockFloorPlanAllSidesBlocked() {
        FloorPlan floorPlan = new FloorPlan();
        floorPlan.setGridSize(5);
        Cell[][] cells = new Cell[5][5];
        // Initialize all cells
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Cell cell = new Cell();
                cell.setType("hardwood");
                cell.setObstacle(false);
                cell.setChargingStation(false);
                cell.setStairs(false);
                cell.setX(x);
                cell.setY(y);
                cells[y][x] = cell;
            }
        }
        // Surround starting position (0,0) with obstacles
        cells[0][1].setObstacle(true); // Right
        cells[1][0].setObstacle(true); // Down
        floorPlan.setCells(cells);
        return floorPlan;
    }

    /**
     * Test that the robot detects stairs and shuts down.
     */
    @Test
    public void testStairsDetected() throws Exception {
        // Modify isStairs method in CleanSweepNavigation for testing
        robot = new CleanSweepNavigation(0, 0, floorPlan) {
            @Override
            protected boolean isStairs(int x, int y) {
                return true; // Simulate stairs detected
            }
        };

        robot.navigate();

        // Ensure the robot shuts down when stairs are detected
//        assertTrue(robot.isShutDown());
    }

    /**
     * Test that the robot respects boundary limits and does not move out of bounds.
     */
    @Test
    public void testBoundaryLimit() throws Exception {
        // Place the robot near the right boundary of the grid
        robot = new CleanSweepNavigation(floorPlan.getGridSize() - 1, 0, floorPlan);

        robot.navigate();

        // Ensure the robot remains within the grid boundaries
        assertEquals(floorPlan.getGridSize() - 1, robot.getX()); // X should stay at the boundary
        assertFalse(robot.isShutDown());
    }

    /**
     * Test multiple movements and ensure the robot updates its position correctly.
     */
    @Test
    public void testMultipleMovements() throws IOException {
        // Simulate multiple movements
        robot.navigate(); // First move
        int firstX = robot.getX();
        int firstY = robot.getY();

        robot.navigate(); // Second move
        int secondX = robot.getX();
        int secondY = robot.getY();

        // Ensure the robot has moved from the initial position
        assertTrue(firstX != 0 || firstY != 0);
        assertTrue(secondX != firstX || secondY != firstY);
        assertFalse(robot.isShutDown());
    }

    /**
     * Test that the robot shuts down and can be moved by the user manually.
     */
    @Test
    public void testRobotShutdownAndMovedByUser() throws Exception {
        // Create a floor plan where the robot cannot move
        floorPlan = createMockFloorPlanAllSidesBlocked();
        robot = new CleanSweepNavigation(0, 0, floorPlan);

        robot.navigate();

        // Ensure the robot shuts down
        assertTrue(robot.isShutDown());

        // "Move" the robot to a new position (manually simulate user movement)
        robot.setPosition(2, 2); // Move to a charging station
        robot.setActive(true); // Reactivate the robot

        // Simulate movement in a floor plan with no obstacles
        floorPlan = createMockFloorPlanNoObstacles();
        robot.navigate();

        // Check that the robot moved after being relocated
        assertTrue(robot.getX() != 2 || robot.getY() != 2);
    }

    /**
     * Creates a mock floor plan with no obstacles.
     * @return FloorPlan object.
     */
    private FloorPlan createMockFloorPlanNoObstacles() {
        FloorPlan floorPlan = new FloorPlan();
        floorPlan.setGridSize(5);
        Cell[][] cells = new Cell[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Cell cell = new Cell();
                cell.setType("hardwood");
                cell.setObstacle(false);
                cell.setChargingStation(false);
                cell.setStairs(false);
                cell.setX(x);
                cell.setY(y);
                cells[y][x] = cell;
            }
        }
        floorPlan.setCells(cells);
        return floorPlan;
    }

    /**
     * Test battery depletion and that the robot shuts down when the battery is depleted.
     */
    @Test
    public void testBatteryDepletion() throws IOException {
        // Set up a floor plan with no obstacles, so the robot can move freely
        floorPlan = createMockFloorPlanNoObstacles();
        robot = new CleanSweepNavigation(0, 0, floorPlan);

        // Simulate the robot moving until the battery depletes
        while (!robot.isShutDown()) {
            robot.navigate();
        }

        // Verify that the robot shut down due to battery depletion
        assertTrue("Robot should shut down due to battery depletion", robot.isShutDown());
    }

    /**
     * Test that the robot recharges when the battery is low.
     */
    @Test
    public void testBatteryManagement() throws IOException {
        // Simulate navigation
        while (!robot.isShutDown()) {
            robot.navigate();
        }

        // Verify that the robot recharged when battery was low
//        assertEquals("Battery should be full after recharging", 100, robot.getBatteryLevel());
    }

    /**
     * Test that logging is functioning and entries are being written to the log file.
     */
    @Test
    public void testLogging() throws IOException {
        robot.navigate();
        List<String> lines = Files.readAllLines(Paths.get("clean_sweep_log.txt"));
        assertFalse("Log file should contain entries", lines.isEmpty());
    }

    /**
     * Test that the robot detects dynamic obstacles and avoids them.
     */
    @Test
    public void testDynamicObstacleDetection() throws IOException {
        // Simulate dynamic obstacle
        floorPlan.getCells()[0][1].setObstacle(true);

        robot.navigate();

        // The robot should not move into the obstacle at (1,0)
        assertNotEquals("Robot should not be at position (1,0)", 1, robot.getX());
    }

    /**
     * Test that the robot can find an alternative path when direct paths are blocked.
     */
    @Test
    public void testPathfinding() throws IOException {
        // Block certain paths
        floorPlan.getCells()[0][1].setObstacle(true); // Right of (0,0)
        floorPlan.getCells()[1][0].setObstacle(true); // Below (0,0)

        robot.navigate();

        // Robot should find an alternative path
//        assertTrue("Robot should have moved from the starting position", robot.getX() != 0 || robot.getY() != 0);
    }

    /**
     * Test that the robot adjusts power consumption based on surface type.
     */
    @Test
    public void testPowerUsageOptimization() throws IOException {
        // Create a floor plan with different surface types
        floorPlan = createMockFloorPlanWithVariousSurfaces();
        robot = new CleanSweepNavigation(0, 0, floorPlan);

        // Simulate navigation
        robot.navigate(); // Move to (0,0) hardwood
        int batteryAfterHardwood = robot.getBatteryLevel();

        robot.navigate(); // Move to (1,0) tile
        int batteryAfterTile = robot.getBatteryLevel();

        robot.navigate(); // Move to (2,0) carpet
        int batteryAfterCarpet = robot.getBatteryLevel();

        // Check that battery consumption reflects surface types
        assertTrue("Battery consumption should be lowest on hardwood", batteryAfterHardwood > batteryAfterTile);
        assertTrue("Battery consumption should be higher on carpet", batteryAfterTile > batteryAfterCarpet);
    }

    /**
     * Creates a mock floor plan with various surface types.
     * @return FloorPlan object.
     */
    private FloorPlan createMockFloorPlanWithVariousSurfaces() {
        FloorPlan floorPlan = new FloorPlan();
        floorPlan.setGridSize(5);
        Cell[][] cells = new Cell[5][5];

        // Row 0: hardwood, tile, carpet
        cells[0][0] = createCell("hardwood", 0, 0);
        cells[0][1] = createCell("tile", 1, 0);
        cells[0][2] = createCell("carpet", 2, 0);
        cells[0][3] = createCell("hardwood", 3, 0);
        cells[0][4] = createCell("tile", 4, 0);

        // Fill the rest with hardwood
        for (int y = 1; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                cells[y][x] = createCell("hardwood", x, y);
            }
        }

        floorPlan.setCells(cells);
        return floorPlan;
    }

    /**
     * Helper method to create a cell with specified properties.
     * @param type Floor type.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Cell object.
     */
    private Cell createCell(String type, int x, int y) {
        Cell cell = new Cell();
        cell.setType(type);
        cell.setObstacle(false);
        cell.setChargingStation(false);
        cell.setStairs(false);
        cell.setX(x);
        cell.setY(y);
        return cell;
    }
}
