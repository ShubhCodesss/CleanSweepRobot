package Tests;
import static org.junit.Assert.*;
import org.junit.*;

import ControlSystems.CleanSweepNavigation;

public class CleanSweepNavigationTest {

    private CleanSweepNavigation robot;

    @Before
    public void setup() {
        // Initialize the Clean Sweep robot at a starting position (0, 0)
        robot = new CleanSweepNavigation(0, 0);
    }

    // Mock method to set sensor states manually (assuming such a method exists in the CleanSweepNavigation class)
    private void setSensors(boolean left, boolean right, boolean up, boolean down, boolean bottom) {
        robot.setSensors(left, right, up, down, bottom);
    }

    @Test
    public void testNoObstacles() {
        // No obstacles in any direction
        setSensors(false, false, false, false, false);

        // Simulate a few steps of movement
        robot.navigate();

        // Check that the robot has moved right (as there are no obstacles) and hasn't shut down
        assertTrue(robot.getX() > 0 && robot.getX() < CleanSweepNavigation.GRID_SIZE);
        assertEquals(0, robot.getY()); // Should move right along the X-axis
        assertFalse(robot.isShutDown());
    }

    @Test
    public void testAllSidesBlocked() {
        // All directional sensors detect obstacles
        setSensors(true, true, true, true, false);

        // Simulate movement (should shut down)
        robot.navigate();

        // Ensure the robot has shut down since all directions are blocked
        assertTrue(robot.isShutDown());
    }

    @Test
    public void testStairsDetected() {
        // Simulate stairs detected by the bottom sensor
        setSensors(false, false, false, false, true); // True for stairs

        // Simulate movement (should shut down)
        robot.navigate();

        // Ensure the robot shuts down when stairs are detected
        assertTrue(robot.isShutDown());
    }

    @Test
    public void testBoundaryLimit() {
        // Place the robot near the right boundary of the grid
        robot = new CleanSweepNavigation(CleanSweepNavigation.GRID_SIZE - 1, 0);

        // Simulate no obstacles to the right (should stop at boundary)
        setSensors(false, true, true, true, false); // Right is open

        robot.navigate();

        // Ensure the robot doesn't move past the grid boundary
        assertEquals(CleanSweepNavigation.GRID_SIZE - 1, robot.getX()); // Should stay at boundary
        assertFalse(robot.isShutDown()); // No shutdown as it's still able to move within the boundary
    }

    @Test
    public void testMultipleMovements() {
        // Starting at (0, 0)
        robot = new CleanSweepNavigation(0, 0);

        // First move: right is open
        setSensors(false, true, true, false, false); // Right open
        robot.navigate();

        // Second move: right blocked, down is open
        setSensors(true, true, true, false, false); // Down open
        robot.navigate();

        // Check that the robot moved correctly
        assertEquals(1, robot.getX()); // First move right
        assertEquals(1, robot.getY()); // Second move down
        assertFalse(robot.isShutDown());
    }

    @Test
    public void testRobotShutdownAndMovedByUser() {
        // First simulate all sides blocked at (0, 0)
        setSensors(true, true, true, true, false);
        robot.navigate();

        // Ensure the robot shuts down
        assertTrue(robot.isShutDown());

        // "Move" the robot to a new position (manually simulate user movement)
        robot.setPosition(5, 5);
        robot.setActive(true); // Reactivate the robot

        // Simulate no obstacles after manual movement
        setSensors(false, false, false, false, false);
        robot.navigate();

        // Check that the robot moved after being relocated
        assertTrue(robot.getX() > 5 || robot.getY() > 5);
    }
}

