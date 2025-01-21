package factory;

//MobileDriverFactory Class
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;


public class MobileDriverFactory implements Driver {
 private String platform;
 private String deviceName;
 private String udid;

 public MobileDriverFactory(String platform, String deviceName, String udid) {
     this.platform = platform;
     this.deviceName = deviceName;
     this.udid = udid;
 }
 

 @Override
 public AppiumDriver createDriver() {
     

     try {
         switch (platform.toLowerCase()) {
             case "android":
            	UiAutomator2Options options = new UiAutomator2Options();
             	options.setDeviceName(deviceName);
             	options.setUdid(udid);
             	options.setPlatformName("Android");
             	options.setAutomationName("UiAutomator2");
             	options.setPlatformVersion("12");
             	options.setAppPackage("com.bs23.fintech.ib.abbl.uat");
             	options.setAppActivity("com.bs23.fintech.ib.abbl.MainActivity");
             	
             	System.out.println("Driver initialized started!");
			try {
				return new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), options);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             	
             //need to set based on new implementation
             case "ios":
            	 
            	 XCUITestOptions xcuiTestOptions = new XCUITestOptions();
            	 
            	 xcuiTestOptions.setPlatformName("iOS");
            	 xcuiTestOptions.setDeviceName("iPhone 11");
            	 xcuiTestOptions.setBundleId("your.bundle.id");
            	 
			try {
				return new IOSDriver(new URI("http://127.0.0.1:4723").toURL(), xcuiTestOptions);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
             default:
                 throw new IllegalArgumentException("Unsupported platform: " + platform);
         }
     } catch (MalformedURLException e) {
         throw new RuntimeException("Invalid Appium server URL", e);
     }
 }
}

