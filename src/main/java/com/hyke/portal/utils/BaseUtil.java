package com.hyke.portal.utils;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.ttn.framework.utils.FileOperation;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
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
    public static String fileDownloadPath;
    protected static ExtentReports extent;
    static String os;
    static File directory = new File(".");
    protected Properties config = initPropFromFile("/src/main/test/com/hyke/portal/resources/config.properties");
    protected Properties cData = getTestData("commonData.properties");
    protected Properties region;
    protected String regionName;
    protected String browser;
    protected FileOperation fileOperation;
    protected WebDriver driver;
    protected ExtentTest testReport;
    protected UIUtil util;


    //credential variables test data
    protected String username = "username";
    protected String password = "password";

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
        String extentConfigPath, reportPath;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Calendar.getInstance().getTime());

        PropertyConfigurator.configure("src/main/test/com/hyke/portal/resources/log4j.properties");

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

        try {
            fileDownloadPath = directory.getCanonicalPath() + File.separator + "src" + File.separator + "main" +
                    File.separator + "resources" + File.separator + "downloads";
            FileUtils.forceMkdir(new File(fileDownloadPath));
            fileOperation = new FileOperation(fileDownloadPath);
            log.info("File download path: " + fileDownloadPath);

            reportFolderPath = directory.getCanonicalPath() + File.separator +
                    "AutomationReport" + File.separator + "TestRun_" + timeStamp;
            FileUtils.forceMkdir(new File(reportFolderPath));
            reportPath = reportFolderPath + File.separator + "AutomationReport.html";
            log.info("Extent report folder path: " + reportPath);

            extentConfigPath = directory.getCanonicalPath() + File.separator + "src" + File.separator + "main" +
                    File.separator + "test" +
                    File.separator+ "com" +
                    File.separator+ "hyke" +
                    File.separator+"portal" +
                    File.separator+"resources" + File.separator + "extent-config.xml";
            log.info("Extent report config path: " + extentConfigPath);

            extent = new ExtentReports(reportPath, false);
            extent.loadConfig(new File(extentConfigPath));
        } catch (IOException e) {
            log.error("IO exception occur: " + e.getMessage());
        }
    }

    @Parameters({"region"})
    @BeforeClass
    protected void setRegionName(@Optional String regionXML) {
        if (regionXML == null) {
            regionName = (System.getProperty("region") != null ?
                    System.getProperty("region") : config.getProperty("region")).toUpperCase();
        } else {
            regionName = regionXML;
        }
        region = getTestData(regionName + ".properties");
        log.info("Region set to: " + regionName + " for class " + this.getClass().getSimpleName());
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
        testName = this.getClass().getSimpleName() + " : " + method.getName() + " : " + regionName;
        testReport = extent.startTest(testName, method.getAnnotation(Test.class).description())
                .assignCategory(regionName, packageName.substring(packageName.lastIndexOf(".") + 1));

        log.info("Extent report logging started for " + testName);
        System.out.println(">>>>> Execution started: " + testName);
        testReport.log(LogStatus.INFO, "Test execution started.");
    }

    @AfterMethod
    protected void reportFailure(ITestResult result,Method method) {
        String testName = this.getClass().getSimpleName() + " : " + method.getName() + " : " + regionName;
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

        try {
            //copy the extend reports to new folder dedicated for Jenkins integration
            File srcDir = new File(reportFolderPath);
            File destDir = new File(directory.getCanonicalPath() + File.separator +
                    "AutomationReport" + File.separator + "LatestRun");
            FileUtils.forceMkdir(destDir);
            FileUtils.cleanDirectory(destDir);
            FileUtils.copyDirectory(srcDir, destDir);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String initData(String regionData) {
        return region.getProperty(regionData);
    }

    private String getEnvironmentURL(String environment) {
        switch (environment.toUpperCase()) {
            case "IDT":
                return "http://vps-idt.westcon.com/";
            case "IAT":
                return "http://vps-iat.westcon.com/";
            case "AUTO":
                return "https://www.bfl-web-client.qa3.tothenew.net/en-ae/login";
            case "RUN":
                return "http://vps-run.westcon.com/";
            case "UAT":
                return "http://vps-uat.westcon.com/";

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
                return new ChromeDriver(setChromeCaps("chrome"));

            case "CHROMEHEADLESS":
                log.info("Initialize chrome headless driver on OS: " + os);
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
                return new ChromeDriver(setChromeCaps("chromeheadless"));

            case "FIREFOX":
                log.info("Initialize firefox driver on OS: " + os);
                DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                capabilities.setJavascriptEnabled(true);
                return new FirefoxDriver(setFirefoxProfile());

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
                return new ChromeDriver(setChromeCaps("chrome"));
        }
    }

    private FirefoxProfile setFirefoxProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", BaseUtil.fileDownloadPath);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel," +
                        "image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
        return profile;
    }

    private DesiredCapabilities setChromeCaps(String browserType) {

        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", BaseUtil.fileDownloadPath);
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromeOptionsMap = new HashMap<>();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--test-type");
        options.addArguments("--disable-extensions"); //to disable browser extension popup
        if (browserType.equalsIgnoreCase("chromeheadless")) {

            log.info("Running chrome in headless mode");
            options.addArguments("headless");
            options.addArguments("window-size=1200x600");

        }
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        return cap;

    }
}