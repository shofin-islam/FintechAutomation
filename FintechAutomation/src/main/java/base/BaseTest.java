package base;

import org.openqa.selenium.WebDriver;


import factory.Driver;
import factory.DriverFactory;
import io.appium.java_client.AppiumDriver;
import org.testng.annotations.*;

public abstract class BaseTest {
    protected ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    protected ThreadLocal<AppiumDriver> mobileDriver = new ThreadLocal<>();
    private Driver driverInstance;

    @Parameters({"type", "platformOrBrowser", "deviceName", "udid"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(String type, String platformOrBrowser, String deviceName, String udid) {
        driverInstance = DriverFactory.getDriver(type, platformOrBrowser, deviceName, udid);
        if (type.equalsIgnoreCase("web")) {
            webDriver.set((WebDriver) driverInstance.createDriver());
        } else if (type.equalsIgnoreCase("mobile")) {
            mobileDriver.set((AppiumDriver) driverInstance.createDriver());
        }
    }
    

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (webDriver.get() != null) {
            webDriver.get().quit();
            webDriver.remove();
        }
        if (mobileDriver.get() != null) {
            mobileDriver.get().quit();
            mobileDriver.remove();
        }
    }

    protected WebDriver getWebDriver() {
        return webDriver.get();
    }

    protected AppiumDriver getMobileDriver() {
        return mobileDriver.get();
    }
}

