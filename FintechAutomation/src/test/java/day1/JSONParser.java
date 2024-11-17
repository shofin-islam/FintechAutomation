package day1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utils.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONParser {

    private JsonNode rootNode;

    public JSONParser(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Load JSON file and parse it into a JsonNode
            rootNode = mapper.readTree(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to find value by key name (recursive)
    public List<JsonNode> findValuesByKey(String key) {
        List<JsonNode> results = new ArrayList<>();
        findValuesByKeyRecursive(rootNode, key, results);
        return results;
    }

    private void findValuesByKeyRecursive(JsonNode node, String key, List<JsonNode> results) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals(key)) {
                    results.add(field.getValue());
                }
                findValuesByKeyRecursive(field.getValue(), key, results);
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                findValuesByKeyRecursive(arrayElement, key, results);
            }
        }
    }

    // Method to find keys by value (recursive)
    public List<String> findKeysByValue(String value) {
        List<String> keys = new ArrayList<>();
        findKeysByValueRecursive(rootNode, value, keys, "");
        return keys;
    }

    private void findKeysByValueRecursive(JsonNode node, String targetValue, List<String> keys, String currentPath) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String newPath = currentPath.isEmpty() ? field.getKey() : currentPath + "." + field.getKey();
                if (field.getValue().isValueNode() && field.getValue().asText().equals(targetValue)) {
                    keys.add(newPath);
                }
                findKeysByValueRecursive(field.getValue(), targetValue, keys, newPath);
            }
        } else if (node.isArray()) {
            int index = 0;
            for (JsonNode arrayElement : node) {
                findKeysByValueRecursive(arrayElement, targetValue, keys, currentPath + "[" + index + "]");
                index++;
            }
        }
    }

    // Method to print the JSON structure (for verification/debugging)
    public void printJson() {
        System.out.println(rootNode.toPrettyString());
    }

    // Main method for testing
    public static void main(String[] args) {
        JSONParser parser = new JSONParser("src/main/resources/Configuration.json");

        // Print JSON (for debugging)
        parser.printJson();

        // Example: Find values by key
        String searchKey = "accountnumber";
        List<JsonNode> valuesByKey = parser.findValuesByKey(searchKey);
        System.out.println("Values for key '" + searchKey + "': " + valuesByKey);

        // Example: Find keys by value
        String searchValue = "someValue";
        List<String> keysByValue = parser.findKeysByValue(searchValue);
        System.out.println("Keys for value '" + searchValue + "': " + keysByValue);
        
        ConfigManager manager = new ConfigManager();
       
        System.out.println( "-----"+manager.getAuthTokenUrl());
    }
}
