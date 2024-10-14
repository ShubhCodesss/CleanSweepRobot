Clean Sweep Robotic Vacuum Cleaner & Sensor Simulator
This repository contains the development of the Clean Sweep Robotic Vacuum Cleaner Control System and its accompanying Sensor Simulator. The project simulates a robotic vacuum cleaner that navigates a home, detects and cleans dirt, manages power, and interacts with various surfaces. Our implementation follows Agile methodology with detailed sprint planning and retrospective reviews.

Project Overview
The Clean Sweep system is designed with two main modules:

Control System: Manages navigation, dirt detection, power management, and activity logging.
Sensor Simulator: Simulates real-world interactions between Clean Sweep and a defined floor plan, including sensor data on surfaces, obstacles, and dirt levels.
Sprint 0: Project Initialization

In Sprint 0, we focused on laying the groundwork for the project:
Project Decomposition: Defined core components like navigation, dirt detection, power management, and activity log.
Sensor Simulator & Floor Plan Representation: Designed a 2D grid layout with cells representing surface types, obstacles, and charging stations. Implemented a JSON layout file format to represent floor plans.
Product Backlog & Project Planning: Created and prioritized user stories in Trello, organizing tasks into epics such as Navigation, Power Management, and Sensor Simulator.
Initial Deliverables: Provided a foundational project plan with user stories and a layout file structure for upcoming sprints.

Sprint 1: Navigation and Sensor Enhancements
For Sprint 1, our focus was on implementing navigation and enhancing the sensor simulation:

Navigation System: Developed pathfinding logic for efficient grid traversal, implemented obstacle detection, and tested Clean Sweep’s ability to navigate around obstacles.
Sensor Simulation Updates: Enhanced the sensor simulator to detect and respond to various surface types (bare floor, low-pile carpet, high-pile carpet) and simulated real-time sensor feedback for obstacles and dirt detection.
Power Management: Introduced surface-based power usage, where each movement and cleaning action consumes power based on the surface type.
Testing and Refinement: Conducted unit testing for navigation and sensor functions, validating Clean Sweep’s responses across different environments.


Future Goals
Sprint 2 will focus on refining power management, implementing cleaning functionality, and introducing charging station interactions.

Repository Structure
/src: Contains source code for Control System and Sensor Simulator.
/docs: Documentation for project components, sprint plans, and retrospective insights.
/resources/floor_plan.json: Sample JSON file representing a dynamic floor plan for testing.
