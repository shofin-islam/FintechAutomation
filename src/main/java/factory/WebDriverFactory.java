package factory;

//WebDriverFactory Class
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class WebDriverFactory implements Driver {
 private String browser;
 private WebDriver driver;

 public WebDriverFactory(String browser) {
     this.browser = browser;
 }

 @Override
 public WebDriver createDriver() {
     switch (browser.toLowerCase()) {
         case "chrome":
             driver = new ChromeDriver();
             return driver;
             
         case "firefox":
        	 driver = new FirefoxDriver();
             return driver;
             
         case "edge":
        	 driver = new EdgeDriver();
        	 return driver;
        	 
         default:
             throw new IllegalArgumentException("Unsupported browser: " + browser);
     }
 }
}
