package com.hyke.portal.utils;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

public class BaseUtil {

    public final static Logger log = Logger.getLogger(BaseUtil.class);
    public static String url;
    public static String environment;
    public static String reportFolderPath;
    protected static ExtentReports extent;
    static String os;
    static File directory = new File(".");
    protected Properties config = initPropFromFile("/src/main/test/com/hyke/portal/resources/config.properties");
    protected Properties cData = getTestData("commonData.properties");
    protected Properties country;
    protected String countryName;
    protected String browser;
    protected WebDriver driver;
    protected ExtentTest testReport;
    protected UIUtil util;

    String usrDirectory = System.getProperty("user.dir");


    public static Properties initPropFromFile(String filename) {
        Properties temp = new Properties();
        try {
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir") +
                    filename);
            temp.load(fs);

        } catch (Exception e) {
            log.error("Unable to load properties file: " + filename + e.getMessage());
        }
        return temp;
    }

    public static Properties getTestData(String filename) {
        Properties temp = new Properties();
        try {
            FileInputStream fs = new FileInputStream(directory.getCanonicalPath() + File.separator +
                    "src" + File.separator + "main" + File.separator + "test" + File.separator + "com" +
                    File.separator + "hyke" + File.separator +
                    "portal" + File.separator + "testdata" + File.separator + filename);
            temp.load(fs);

        } catch (Exception e) {
            log.error("Unable to load locator or testdata file: " + filename + e.getMessage());
        }
        return temp;
    }

    @BeforeSuite
    public void configure() {

        //Extent Report Setup in BaseLib
        extent = new ExtentReports(usrDirectory + File.separator + "report" + File.separator + "AutomationReport.html", true);
        extent.addSystemInfo("HostName", "Srikant");
        extent.addSystemInfo("Environment", "Web");
        extent.loadConfig(new File(usrDirectory + File.separator +
                "src" + File.separator + "main" + File.separator + "test" + File.separator + "com" +
                File.separator + "hyke" + File.separator +
                "portal" + File.separator + "resources" + File.separator + "extent-config.xml"));


        PropertyConfigurator.configure("src" + File.separator + "main" + File.separator + "test" + File.separator + "com" +
                File.separator + "hyke" + File.separator +
                "portal" + File.separator + "resources" + File.separator + "log4j.properties");

        os = (System.getProperty("os") != null) ?
                System.getProperty("os") : config.getProperty("os");
        log.info("OS set to: " + os);
        System.out.println("OS set to: " + os);

        environment = (System.getProperty("env") != null) ?
                System.getProperty("env") : config.getProperty("env");
        System.out.println("Environment set to: " + environment);
        log.info("Environment set to: " + environment);

        url = getEnvironmentURL(environment);
        log.info("URL set to: " + url);

    }

    @Parameters({"country"})
    @BeforeClass
    protected void setCountryName(@Optional String regionXML) {
        if (regionXML == null) {
            countryName = (System.getProperty("country") != null ?
                    System.getProperty("country") : config.getProperty("country")).toUpperCase();
        } else {
            countryName = regionXML;
        }
        country = getTestData(countryName + ".properties");
        log.info("Country set to: " + countryName + " for class " + this.getClass().getSimpleName());
    }

    @Parameters({"browser"})
    @BeforeMethod
    protected void setUpDriver(@Optional String browserXML) {
        if (browserXML == null) {
            browser = (System.getProperty("browser") != null) ?
                    System.getProperty("browser") : config.getProperty("browser");
        } else {
            browser = browserXML;
        }
        log.info("Browser set to: " + browser);

        driver = initDriver(browser);
        driver.manage().window().maximize();
        driver.get(url);
    }

    @BeforeMethod
    protected void startReporting(Method method) {
        String packageName, testName;

        packageName = this.getClass().getPackage().getName();
        testName = this.getClass().getSimpleName() + " : " + method.getName() + " : " + countryName;
        testReport = extent.startTest(testName, method.getAnnotation(Test.class).description())
                .assignCategory(countryName, packageName.substring(packageName.lastIndexOf(".") + 1));

        log.info("Extent report logging started for " + testName);
        System.out.println(">>>>> Execution started: " + testName);
        testReport.log(LogStatus.INFO, "Test execution started.");
    }

    @AfterMethod
    protected void reportFailure(ITestResult result, Method method) {
        String testName = this.getClass().getSimpleName() + " : " + method.getName() + " : " + countryName;
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                String screenshotName = util.takeScreenShot();
                testReport.log(LogStatus.FAIL, "<b>Test case failed with exception: </b><br>" +
                        result.getThrowable().toString().replace("\n", "<br>") +
                        "<br><b>Snapshot:</b><br>" + testReport.addScreenCapture(screenshotName));
            } catch (Exception e) {
                log.error("Unable to add screenshot to reports");
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            testReport.log(LogStatus.SKIP, "<b>Test case skipped with message: </b>" + result.getThrowable());
        }
        System.out.println(">>>>> Execution ended: " + testName);
        testReport.log(LogStatus.INFO, "Test execution completed.");
        extent.endTest(testReport);
    }

    @AfterMethod
    protected void tearDown() {
        driver.quit();
    }

    @AfterSuite(alwaysRun = true)
    protected void endReporting() {
        extent.flush();
        extent.close();
    }

    protected String initData(String countryData) {
        return country.getProperty(countryData);
    }

    private String getEnvironmentURL(String environment) {
        switch (environment.toUpperCase()) {

            case "DEV":
                return "https://www.google.com/";

            case "QA":
                return "https://www.google.com/";

            case "UAT":
                return "https://www.google.com/";

            default:
                return config.getProperty("url");
        }
    }

    private synchronized WebDriver initDriver(String browser) {
        String os = BaseUtil.os;
        String driversPath = "/lib/";
        try {
            driversPath = BaseUtil.directory.getCanonicalPath() + File.separator + "lib" + File.separator +
                    "webdrivers" + File.separator;
        } catch (IOException e) {
            log.info("Unable to find the workspace path" + e.getMessage());
        }

        System.out.println(">>>>> Initializing the webdriver: " + browser + " on OS: " + os);
        switch (browser.toUpperCase()) {

            case "CHROME":
                log.info("Initialize chrome driver on OS: " + os);
                switch (os.toUpperCase()) {
                    case "LINUX64":
                        System.setProperty("webdriver.chrome.driver", driversPath + "chromedriver");
                        break;
                    case "WIN":
                        System.setProperty("webdriver.chrome.driver", driversPath + "chromedriver.exe");
                        break;
                    case "MAC":
                        System.setProperty("webdriver.chrome.driver", driversPath + "chromedriver_mac");
                        break;
                }
                return new ChromeDriver();


            case "FIREFOX":
                log.info("Initialize chrome driver on OS: " + os);
                switch (os.toUpperCase()) {
                    case "LINUX64":
                        System.setProperty("webdriver.gecko.driver", driversPath + "geckodriver");
                        break;
                    case "WIN":
                        System.setProperty("webdriver.gecko.driver", driversPath + "geckodriver.exe");
                        break;
                    case "MAC":
                        System.setProperty("webdriver.gecko.driver", driversPath + "geckodriver_mac");
                        break;
                }
                return new FirefoxDriver();


            case "HEADLESS":
                log.info("Initialize phantom headless driver on OS: " + os);
                String[] phantomArgs = {
                        "--ignore-ssl-errors=true",
                        "--ssl-protocol=any",
                        "--webdriver-loglevel=NONE"
                };
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setJavascriptEnabled(true);
                caps.setCapability("takesScreenshot", true);
                caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);

                switch (os.toUpperCase()) {
                    case "LINUX64":
                        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                                driversPath + "phantomjs");
                        break;
                    case "WIN":
                        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                                driversPath + "phantomjs.exe");
                        break;
                    case "MAC":
                        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                                driversPath + "phantomjs_mac");
                        break;
                }
                return new PhantomJSDriver(caps);

            default:
                log.info("Unable to find browser. Initializing chromedriver on OS: Linux");
                System.setProperty("webdriver.chrome.driver", driversPath + "chromedriver");
                return new ChromeDriver();
        }
    }
}
