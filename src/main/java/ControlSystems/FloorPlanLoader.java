// src/main/java/ControlSystems/FloorPlanLoader.java
package ControlSystems;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to load the floor plan from a JSON file.
 */
public class FloorPlanLoader {

    /**
     * Loads the floor plan from the specified JSON file.
     * @param filePath Path to the JSON file.
     * @return FloorPlan object representing the floor plan.
     * @throws IOException If there is an error reading the file.
     */
    public static FloorPlan loadFloorPlan(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, FloorPlan.class);
    }

}
