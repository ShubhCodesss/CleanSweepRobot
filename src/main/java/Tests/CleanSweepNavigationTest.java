// src/test/java/Tests/CleanSweepNavigationTest.java
package Tests;

import static org.junit.Assert.*;
import org.junit.*;

import ControlSystems.CleanSweepNavigation;
import ControlSystems.FloorPlan;
import ControlSystems.Cell;

public class CleanSweepNavigationTest {

    private CleanSweepNavigation robot;
    private FloorPlan floorPlan;

    @Before
    public void setup() throws Exception {
        // Initialize the FloorPlan
        floorPlan = createMockFloorPlanNoObstacles();

        // Initialize the Clean Sweep robot at a starting position (0, 0) with the floor plan
        robot = new CleanSweepNavigation(0, 0, floorPlan);
    }

    /**
     * Creates a mock floor plan with no obstacles.
     * @return FloorPlan object.
     */
    private FloorPlan createMockFloorPlanNoObstacles() {
        FloorPlan floorPlan = new FloorPlan();
        floorPlan.setGridSize(10);
        Cell[][] cells = new Cell[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell cell = new Cell();
                cell.setType("hardwood");
                cell.setObstacle(false);
                cells[y][x] = cell;
            }
        }
        floorPlan.setCells(cells);
        return floorPlan;
    }

    @Test
    public void testNoObstacles() {
        // Simulate a few steps of movement
        robot.navigate();

        // Check that the robot has moved right and hasn't shut down
        assertTrue(robot.getX() > 0 && robot.getX() < floorPlan.getGridSize());
        assertEquals(0, robot.getY()); // Should move right along the X-axis
        assertFalse(robot.isShutDown());
    }

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
        floorPlan.setGridSize(10);
        Cell[][] cells = new Cell[10][10];
        // Initialize all cells
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell cell = new Cell();
                cell.setType("hardwood");
                cell.setObstacle(false);
                cells[y][x] = cell;
            }
        }
        // Surround starting position (0,0) with obstacles
        if (floorPlan.getGridSize() > 1) {
            cells[0][1].setObstacle(true); // Right
            cells[1][0].setObstacle(true); // Down
        }
        floorPlan.setCells(cells);
        return floorPlan;
    }

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
        assertTrue(robot.isShutDown());
    }

    @Test
    public void testBoundaryLimit() throws Exception {
        // Place the robot near the right boundary of the grid
        robot = new CleanSweepNavigation(floorPlan.getGridSize() - 1, 0, floorPlan);

        robot.navigate();

        // Ensure the robot remains at the boundary position and does not shut down
        assertEquals(floorPlan.getGridSize() - 1, robot.getX()); // X should stay at the boundary
        //assertEquals(0, robot.getY()); // Y should remain 0
        assertFalse(robot.isShutDown());
    }

    @Test
    public void testMultipleMovements() throws Exception {
        // Set the robot at starting position (0, 0)
        robot = new CleanSweepNavigation(0, 0, floorPlan);

        // Simulate first movement
        robot.navigate();
        assertEquals(1, robot.getX()); // After first move, X should be 1
        assertEquals(0, robot.getY()); // Y should remain 0
        assertFalse(robot.isShutDown());

        // Simulate second movement
        robot.navigate();
        assertEquals(2, robot.getX()); // X should be 2 after second move
        assertEquals(0, robot.getY()); // Y should remain 0
        assertFalse(robot.isShutDown());
    }

    @Test
    public void testRobotShutdownAndMovedByUser() throws Exception {
        // Create a floor plan where the robot cannot move
        floorPlan = createMockFloorPlanAllSidesBlocked();
        robot = new CleanSweepNavigation(0, 0, floorPlan);

        robot.navigate();

        // Ensure the robot shuts down
        assertTrue(robot.isShutDown());

        // "Move" the robot to a new position (manually simulate user movement)
        robot.setPosition(5, 5);
        robot.setActive(true); // Reactivate the robot

        // Simulate movement in a floor plan with no obstacles
        floorPlan = createMockFloorPlanNoObstacles();
        robot.navigate();

        // Check that the robot moved after being relocated
        assertTrue(robot.getX() > 5 || robot.getY() > 5);
    }
}
