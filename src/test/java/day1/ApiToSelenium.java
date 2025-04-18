package day1;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.v130.network.Network;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApiToSelenium {

    private static String authToken = null;

    public static void main(String[] args) {
        // Step 1: Initialize WebDriver (ChromeDriver with DevTools)
        WebDriver driver = new ChromeDriver();
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        // Enable network monitoring
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Intercept network requests and capture the Authorization token
        devTools.addListener(Network.requestWillBeSent(), request -> {
            if (request.getRequest().getHeaders().containsKey("Authorization")) {
                authToken = request.getRequest().getHeaders().get("Authorization").toString();
                System.out.println("[INFO] Captured Auth Token: " + authToken);
            }
        });

        // Step 2: Perform login on the website
        driver = loginToWebsite(driver);

        if (authToken == null) {
            System.out.println("[ERROR] Failed to capture the token from network requests.");
            driver.quit();
            return;
        }

        // Step 3: Call the API with the captured token
        callApiWithRestAssured(authToken);

        // Step 4: Open a new tab in the same browser and navigate to the profile page
        openNewTab(driver, "https://railapp.railway.gov.bd/profile");

        // Step 5: Switch tabs and perform additional actions
        switchToTab(driver, 0); // Switch to the first tab
        System.out.println("[INFO] Switched back to the first tab. Current URL: " + driver.getCurrentUrl());

        switchToTab(driver, 1); // Switch to the second tab
        System.out.println("[INFO] Switched to the second tab. Current URL: " + driver.getCurrentUrl());

        // Close the browser
        driver.quit();
    }

    private static WebDriver loginToWebsite(WebDriver driver) {
        driver.get("https://railapp.railway.gov.bd/auth/login");

        try {
            // Wait for elements and perform login
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Login')]")));
            loginButton.click();

            WebElement mobileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Mobile Number']")));
            mobileInput.sendKeys("01737153148");

            WebElement passwordInput = driver.findElement(By.xpath("//input[@placeholder='Password']"));
            passwordInput.sendKeys("Sh0f1n2oi2");

            WebElement loginSubmit = driver.findElement(By.xpath("//div[@class='flex justify-center w-full']"));
            loginSubmit.click();

            try {
                WebElement agreeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='I AGREE']")));
                agreeButton.click();
            } catch (TimeoutException e) {
                System.out.println("[INFO] No modal appeared.");
            }

            System.out.println("[INFO] Login successful. Current URL: " + driver.getCurrentUrl());
            return driver;

        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
            return null;
        }
    }

    private static void callApiWithRestAssured(String token) {
        System.out.println("[INFO] Calling API with token using Rest Assured...");
        RestAssured.baseURI = "https://railspaapi.shohoz.com/v1.0/app/auth/profile";

        Response response = RestAssured.given()
            .header("Authorization", token)
            .when()
            .get();

        System.out.println("[INFO] API Response Status Code: " + response.getStatusCode());
        System.out.println("[INFO] API Response Body: " + response.getBody().asString());
    }

    private static void openNewTab(WebDriver driver, String url) {
        ((JavascriptExecutor) driver).executeScript("window.open();");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
        driver.get(url);
        System.out.println("[INFO] Opened new tab and navigated to: " + url);
    }

    private static void switchToTab(WebDriver driver, int tabIndex) {
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabIndex < tabs.size()) {
            driver.switchTo().window(tabs.get(tabIndex));
            System.out.println("[INFO] Switched to tab index: " + tabIndex);
        } else {
            System.out.println("[ERROR] Invalid tab index: " + tabIndex);
        }
    }
}
