package replications;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class MisconfigurationDescriptionLoader {

    /**
     * Returns the description of a Kubernetes misconfiguration from a JSON file.
     *
     * @param jsonFilePath Path to the JSON file
     * @param misconfigurationName Name of the misconfiguration
     * @return Description if found, otherwise null
     * @throws IOException if file cannot be read or parsed
     */
    public static String getDescription(String jsonFilePath, String misconfigurationName)
            throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(jsonFilePath));

        if (!root.isArray()) {
            throw new IllegalArgumentException("JSON root must be an array");
        }

        Iterator<JsonNode> elements = root.elements();
        while (elements.hasNext()) {
            JsonNode node = elements.next();
            String name = node.path("misconfiguration").asText();

            if (name.equalsIgnoreCase(misconfigurationName)) {
                return node.path("description").asText();
            }
        }

        return null;
    }
}