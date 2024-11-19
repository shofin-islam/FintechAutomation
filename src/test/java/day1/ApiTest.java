package day1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

public class ApiTest {

    public static void main(String[] args) {
        // Define the base URL of your API
        String baseUrl = "http://your-api-endpoint.com/api";  // Replace with actual URL

        // Create a map for request parameters
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("student_id", 121);
        requestParams.put("batch", 31);
        requestParams.put("session", "23-24");

        // Send the POST request with parameters in the body (JSON format)
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)  // Use JSON content type
                .body(requestParams)           // Send the map as the JSON request body
                .when()
                .post("/your-endpoint")        // Specify your endpoint
                .then()
                .extract().response();        // Extract the response

        // Print the response
        System.out.println("Response Body: " + response.getBody().asString());

        // You can assert the response status or other values
        
        /*Common Assertion Methods in TestNG:
        	Assert.assertEquals(actual, expected) — Asserts that two values are equal.
        	Assert.assertTrue(condition) — Asserts that a condition is true.
        	Assert.assertFalse(condition) — Asserts that a condition is false.
        	Assert.assertNull(object) — Asserts that an object is null.
        	Assert.assertNotNull(object) — Asserts that an object is not null.
        	Assert.assertNotEquals(actual, expected) — Asserts that two values are not equal.
        	*/
        Assert.assertEquals(200, response.getStatusCode()); // Example to check for successful response
    }
}

