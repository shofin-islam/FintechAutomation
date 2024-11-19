package factory;

//DriverFactory Class
public class DriverFactory {
 public static Driver getDriver(String type, String platformOrBrowser, String deviceName, String udid) {
     if (type.equalsIgnoreCase("web")) {
         return new WebDriverFactory(platformOrBrowser);
     } else if (type.equalsIgnoreCase("mobile")) {
         return new MobileDriverFactory(platformOrBrowser, deviceName, udid);
     } else {
         throw new IllegalArgumentException("Invalid driver type: " + type);
     }
 }
}

