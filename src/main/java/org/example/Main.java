package org.example;

public class Main {
    public static void main(String[] args) {
        // Initialize a 2x2 grid
        Cell[][] floorPlan = new Cell[2][2];

        // Set up the grid cells with properties
        floorPlan[0][0] = new Cell("bare floor", false, true, 0);  // Charging station
        floorPlan[0][1] = new Cell("low-pile carpet", false, false, 2);  // Dirt present
        floorPlan[1][0] = new Cell("high-pile carpet", true, false, 0);  // Obstacle
        floorPlan[1][1] = new Cell("bare floor", false, false, 1);  // Dirt present

        // Initialize the Sensor Simulator with the grid and starting position
        SensorSimulator simulator = new SensorSimulator(floorPlan, 0, 0);

        // Simulate movement and display sensor feedback
        simulator.move("right");  // Now moving to (0, 1) instead of (1, 0)
        simulator.printSensorFeedback();

//        // Simulate movement and display sensor feedback
//        simulator.move("right");
//        simulator.printSensorFeedback();

        simulator.move("down");  // Blocked by obstacle
        simulator.move("right");  // Valid move
        simulator.printSensorFeedback();
    }
}
