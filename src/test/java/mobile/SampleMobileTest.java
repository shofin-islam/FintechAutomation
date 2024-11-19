package mobile;

import org.testng.annotations.Test;

import base.BaseTest;

public class SampleMobileTest extends BaseTest{
	 @Test
	    public void testMobileApp() throws InterruptedException {
	        System.out.println("Session Details: " + getMobileDriver().getSessionId());
	        Thread.sleep(3000);
	    }
}
