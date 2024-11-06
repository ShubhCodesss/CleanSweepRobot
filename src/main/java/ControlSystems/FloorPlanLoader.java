package ControlSystems;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to load the floor plan from a JSON file.
 */
public class FloorPlanLoader {

    /**
     * Loads the floor plan from the specified JSON file.
     * @param inputStream InputStream of the JSON file.
     * @return FloorPlan object representing the floor plan.
     * @throws IOException If there is an error reading the file.
     */
    public static FloorPlan loadFloorPlan(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FloorPlan floorPlan = objectMapper.readValue(inputStream, FloorPlan.class);

        // Set coordinates for each cell
        for (int y = 0; y < floorPlan.getGridSize(); y++) {
            for (int x = 0; x < floorPlan.getGridSize(); x++) {
                Cell cell = floorPlan.getCells()[y][x];
                cell.setX(x);
                cell.setY(y);
            }
        }

        return floorPlan;
    }
}
