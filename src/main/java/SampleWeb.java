

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class SampleWeb {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		WebDriver driver = new ChromeDriver();
		
		driver.get("http://www.google.com");
		
		System.out.println(driver.getTitle());
		
		driver.close();
		
		
		Thread.sleep(3000);
		
		WebDriver driverf = new FirefoxDriver();
		
		driverf.get("http://www.google.com");
		
		System.out.println(driverf.getTitle());
		Thread.sleep(3000);
		driverf.quit();
		
//		driverf.quit();

	}

}
