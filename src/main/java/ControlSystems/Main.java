package ControlSystems;

import java.io.IOException;
import java.io.InputStream;

/**
 * Main class to run the Clean Sweep Navigation program.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Load the floor plan from the JSON file
            InputStream inputStream = Main.class.getResourceAsStream("/floorplan2.json");
            if (inputStream == null) {
                System.err.println("Could not find floorplan1.json in resources.");
                return;
            }
            FloorPlan floorPlan = FloorPlanLoader.loadFloorPlan(inputStream);

            // Initialize the robot at position (0, 0)
            CleanSweepNavigation robot = new CleanSweepNavigation(0, 0, floorPlan);

            // Simulate the robot navigating until it shuts down
            while (!robot.isShutDown()) {
                robot.navigate();
            }
        } catch (IOException e) {
            System.err.println("Error during simulation: " + e.getMessage());
        }
    }
}
