package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ReportsManager {
	static ExtentReports extentReports;
	static ExtentSparkReporter extentSparkReporter;
	static ExtentTest extentTest;

	public static void main(String[] args) {
		createHtmlReport();
		
		extentTest = extentReports.createTest("Demo-Test");
		extentTest.log(Status.INFO, "sample test log");
		extentTest.log(Status.PASS, "sample test got passed");
		
		flush();
	
	}
	
	public static void createHtmlReport() {
		extentReports = new ExtentReports();
		extentSparkReporter = new ExtentSparkReporter(System.getProperty("user.dir")+"/Fintech-Automation-Report.html");
		extentReports.attachReporter(extentSparkReporter);				
	}
	
	public static void flush() {
		extentReports.flush();		
	}
	

}
