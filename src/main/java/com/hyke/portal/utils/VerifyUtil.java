package com.hyke.portal.utils;

import java.io.File;
import java.io.IOException;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class VerifyUtil {

    private ExtentTest testReport;
    private WebDriver driver;
    private Logger log = Logger.getLogger(VerifyUtil.class);
    private AssertionError error;
    private boolean passStatus;

    public VerifyUtil(WebDriver driver, ExtentTest testReport) {
        this.driver = driver;
        this.testReport = testReport;
    }

    private VerifyUtil(AssertionError error) {
        this.passStatus = false;
        this.error = error;
    }

    private VerifyUtil() {
        this.passStatus = true;
    }

    public void initExtentReport(ExtentTest testReport) {
        this.testReport = testReport;
    }

    public boolean isPassStatus() {
        return passStatus;
    }

    public void exitOnFailure() {
        log.info("Checking for Assertion errors to exit the test");
        if (error != null) {
            log.info("Exiting the test on Assertion failure as exitOnFail flag is set to True");
            testReport.log(LogStatus.FAIL, "Exiting the test after Assertion failure");
            throw error;
        }
    }

    private void reportFailure(boolean screenshotOnFailure, String description,
                               AssertionError e) {
        log.info(description + ": Assertion FAILED with exception " + e.getMessage());
        System.out.println(description + ": Fail");

        if (screenshotOnFailure) {
            String screenshotName = "image" + System.currentTimeMillis() + ".png";
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(src, new File(BaseUtil.reportFolderPath + File.separator + screenshotName));
                log.info("Screenshot " + screenshotName + " taken at " + BaseUtil.reportFolderPath);

            } catch (IOException io) {
                log.error("Fail to take screenshot");
            }
            testReport.log(LogStatus.FAIL, description + "<br><b>Failed: </b>"
                    + e.getMessage().replace("\n", "<br>") + "<br><b>Snapshot:</b><br>"
                    + testReport.addScreenCapture(screenshotName));
        } else {
            testReport.log(LogStatus.FAIL, description + "<br><b>Failed: </b>"
                    + e.getMessage().replace("\n", "<br>"));
        }
    }

    /**
     * @param actual              actual object value
     * @param expected            expected object value
     * @param description         description for extent report
     * @param screenshotOnFailure take screenshot
     */
    public VerifyUtil verifyEquals(Object actual, Object expected, String description,
                                   boolean screenshotOnFailure) {
        try {
            Assert.assertEquals(actual, expected);
            log.info(description + ": Assertion is successful");
            System.out.println(description + ": Success");
            testReport.log(LogStatus.PASS, description + ": <b>Success</b>");
            return new VerifyUtil();

        } catch (AssertionError e) {
            reportFailure(screenshotOnFailure, description, e);
            return new VerifyUtil(e);
        }
    }

    public VerifyUtil verifyNotEquals(Object actual, Object expected, String description,
                                      boolean screenshotOnFailure) {
        try {
            Assert.assertNotEquals(actual, expected);
            log.info(description + ": Assertion is successful");
            System.out.println(description + ": Success");
            testReport.log(LogStatus.PASS, description + ": <b>Success</b>");
            return new VerifyUtil();

        } catch (AssertionError e) {
            reportFailure(screenshotOnFailure, description, e);
            return new VerifyUtil(e);
        }
    }

    /**
     * @param condition           condition to be verified
     * @param message             message shown on fail
     * @param description         description for extent report
     * @param screenshotOnFailure take screenshot
     */
    public VerifyUtil verifyTrue(boolean condition, String message, String description,
                                 boolean screenshotOnFailure) {
        try {
            Assert.assertTrue(condition, message);
            log.info(description + ": Assertion is successful");
            System.out.println(description + ": Success");
            testReport.log(LogStatus.PASS, description + ": <b>Success</b>");
            return new VerifyUtil();

        } catch (AssertionError e) {
            reportFailure(screenshotOnFailure, description, e);
            return new VerifyUtil(e);
        }
    }

    public VerifyUtil verifyFalse(boolean condition, String message, String description,
                                  boolean screenshotOnFailure) {
        try {
            Assert.assertFalse(condition, message);
            log.info(description + ": Assertion is successful");
            System.out.println(description + ": Success");
            testReport.log(LogStatus.PASS, description + ": <b>Success</b>");
            return new VerifyUtil();

        } catch (AssertionError e) {
            reportFailure(screenshotOnFailure, description, e);
            return new VerifyUtil(e);
        }
    }
}