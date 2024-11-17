package day1;

import java.net.MalformedURLException;
import java.net.URI;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class Sample {

	public static AppiumDriver driver; // No need to specify type

	// Construct the Appium server URL without the /wd/hub endpoint
	private static String getAppiumServerURL() {
    	return String.format("http://%s:%s", "127.0.0.1", 4723); // URL without /wd/hub
	}

	public static void main(String[] args) {
    	try {
        	UiAutomator2Options options = new UiAutomator2Options();
        	options.setDeviceName("RFCR10AX2AJ");
        	options.setPlatformName("Android");
        	options.setAutomationName("UiAutomator2");
        	options.setPlatformVersion("12");
        	options.setAppPackage("com.bs23.fintech.ib.abbl.uat");
        	options.setAppActivity("com.bs23.fintech.ib.abbl.MainActivity");


        	/* this section with deprecated
         	* // Create the URL for the Appium server
        	URL appiumServer = new URL(getAppiumServerURL());
        	// Initialize the driver
        	driver = new AndroidDriver(appiumServer, options);
        	*/

        	/*
         	* Remove deprecation by using URI to URL
         	* */

        	driver = new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), options);
        	System.out.println("Driver initialized successfully!");
        	
        	Thread.sleep(3000);

        	// Your test logic here...

    	} catch (MalformedURLException e) {
        	System.err.println("Invalid Appium server URL: " + e.getMessage());
    	} catch (Exception e) {
        	System.err.println("Error during Appium Driver initialization: " + e.getMessage());
    	} finally {
        	if (driver != null) {
            	driver.quit();
            	System.out.println("Driver quit successfully.");
        	}
    	}
	}
}

