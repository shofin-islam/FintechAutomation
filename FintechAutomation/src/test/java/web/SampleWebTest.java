package web;

import org.testng.annotations.Test;

import base.BaseTest;

public class SampleWebTest extends BaseTest{
	@Test
    public void testGoogleHomePage() {
        getWebDriver().get("https://www.google.com");
        System.out.println("Title: " + getWebDriver().getTitle());
        
        try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Test
    public void testBingHomePage() {
        getWebDriver().get("https://www.bing.com");
        System.out.println("Title: " + getWebDriver().getTitle());
        try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
