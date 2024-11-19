package day1;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import configurations.Web;
import utils.ConfigManager;


public class Demo {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		ConfigManager configManager = new ConfigManager();
		System.out.println("Browsers: " + configManager.getBrowsers());
		System.out.println("Web Base URL: " + configManager.getWebBaseUrl());
		System.out.println("Web Config: " + configManager.getWebConfig().toString());


		Web web = configManager.getWebConfig();
		
		System.out.println(web.getBrowsers().toString());

		
		try {
            ObjectMapper mapper = new ObjectMapper();

//            String jsonString = "{\"browsers\":[\"chrome\",\"firefox\",\"edge\"],\"baseurl\":\"http://google.com\",\"help\":\"some others if needed\"}";
            
            // Convert configManager.getWebConfig() to a JSON string
            String jsonString = mapper.writeValueAsString(configManager.getWebConfig());
            System.out.println("JSON String: " + jsonString);

            // Parse JSON string into JsonNode
            JsonNode rootNode = mapper.readTree(jsonString);

            // Access "baseurl" and "help"
            String baseUrl = rootNode.get("baseurl").asText();
            String help = rootNode.get("help").asText();

            // Access the "browsers" array
            JsonNode browsersNode = rootNode.get("browsers");
            List<String> browsers = new ArrayList<>();

            if (browsersNode.isArray()) {
                for (JsonNode browserNode : browsersNode) {
                    browsers.add(browserNode.asText());
                }
            }

            // Print the extracted values
            System.out.println("Base URL: " + baseUrl);
            System.out.println("Help: " + help);
            System.out.println("Browsers: " + browsers);

        } catch (Exception e) {
            e.printStackTrace();
        }
		
		/*
		WebDriver chromeDriver = new ChromeDriver();
		chromeDriver.get("http://www.google.com");
		System.out.println(chromeDriver.getTitle());
		chromeDriver.close();


		Thread.sleep(3000);

		WebDriver firefoxDriver = new FirefoxDriver();
		firefoxDriver.get("http://www.google.com");
		System.out.println(firefoxDriver.getTitle());
		Thread.sleep(3000);
		firefoxDriver.quit();
		*/


	}

}
