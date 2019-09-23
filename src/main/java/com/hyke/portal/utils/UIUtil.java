package com.hyke.portal.utils;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UIUtil {

    public final static Logger log = Logger.getLogger(UIUtil.class);
    private static UIUtil utilObject;
    private ExtentTest testReport;
    private WebDriver driver;
    private String winHandle;
    private WebDriverWait wait;
    private JavascriptExecutor executor;
    private RandomUtil randomUtil;
    private VerifyUtils verifyUtils;
    private WaitUtil waitUtil;
    private ActionsUtil actionsUtil;
    private EmailUtil emailUtil;

    public UIUtil(WebDriver driver, ExtentTest testReport) {
        this.driver = driver;
        this.testReport = testReport;
        actionsUtil = new ActionsUtil(driver);
        verifyUtils = new VerifyUtils(driver, testReport);
        waitUtil = new WaitUtil(driver);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        randomUtil = new RandomUtil();
        emailUtil = new EmailUtil();
        wait = new WebDriverWait(driver, 60);
        executor = (JavascriptExecutor) driver;
    }

    /**
     * @param driver      driver
     * @param element     element
     * @param Destination Destination
     * @throws Exception Exception
     */
    public static void takeScreenshotOfWebelement(WebDriver driver,
                                                  WebElement element, String Destination) throws Exception {
        File v = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage bi = ImageIO.read(v);
        org.openqa.selenium.Point p = element.getLocation();
        int n = element.getSize().getWidth();
        int m = element.getSize().getHeight();
        BufferedImage d = bi.getSubimage(p.getX(), p.getY(), n, m);
        ImageIO.write(d, "png", v);

        FileUtils.copyFile(v, new File(Destination));
    }

    public WaitUtil getWaitUtil() {
        return waitUtil;
    }

    public VerifyUtils getVerifyUtils() {
        return verifyUtils;
    }

    public RandomUtil getRandomUtil() {
        return randomUtil;
    }

    public ActionsUtil getActionsUtil() {
        return actionsUtil;
    }

    public EmailUtil getEmailUtil() {
        return emailUtil;
    }

    public void initExtentReport(ExtentTest testReport) {
        this.testReport = testReport;
        verifyUtils.initExtentReport(testReport);
    }

    private void reportTestStepFailure(String description, Exception e, boolean takeScreenshot) {
        if (takeScreenshot) {
            testReport.log(LogStatus.ERROR, description + "<br><b>Failed: </b>"
                    + e.getMessage().replace("\n", "<br>") + "<br><b>Snapshot:</b><br>"
                    + testReport.addScreenCapture(takeScreenShot()));
        } else {
            testReport.log(LogStatus.ERROR, description + "<br><b>Failed: </b>"
                    + e.getMessage().replace("\n", "<br>"));
        }
    }

    public String setImplicitWaitInMilliSeconds(int timeOut) {
        driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.MILLISECONDS);
        return "Timeout set to " + timeOut + " milli seconds.";
    }

    public void setWindowSize(int Dimension1, int dimension2) {
        driver.manage().window().setSize(new Dimension(Dimension1, dimension2));
    }

    /**
     * Method Description: Hits the provided url
     *
     * @param url url
     */
    public void launchUrl(String url) {
        driver.get(url);
        if (driver instanceof PhantomJSDriver) {
            ((JavascriptExecutor) driver).executeScript("window.confirm = function(msg){return true;};");
        }
    }

    public String getUrl() {
        System.out.println(driver.getCurrentUrl());
        return driver.getCurrentUrl();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    /**
     * Method Description: It applies a hard wait
     *
     * @param TimeInMillis time
     */
    public void goToSleep(int TimeInMillis) {
        try {
            Thread.sleep(TimeInMillis);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    /**
     * Waits for Page Load via Java Script Ready State
     */
    public boolean waitForPageLoad() {
        boolean isLoaded = false;
        int iTimeOut = 60; //Need to Give Global Timeout Value;

        try {
            Thread.sleep(2000);
            log.info("Waiting For Page load via JS");
            ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) driver).executeScript(
                            "return document.readyState").equals("complete");
                }
            };
            WebDriverWait wait = new WebDriverWait(driver, iTimeOut);
            wait.until(pageLoadCondition);
            isLoaded = true;
        } catch (Exception e) {
            log.error("Error Occured waiting for Page Load "
                    + driver.getCurrentUrl());
        }
        return isLoaded;
    }

    /**
     * Method Description: Clicks on a mentioned web-element Created Date:
     */
    public void click(By locator, String description) {
        try {
            driver.findElement(locator).click();
            log.info("Step: '" + description + "' in locator '" + locator.toString() + "' successful");
            testReport.log(LogStatus.PASS, description);

        } catch (ElementNotVisibleException env) {
            getWaitUtil().waitForElementToBeVisible(locator);
            driver.findElement(locator).click();
            log.info("Step: '" + description + "' in locator '" + locator.toString() + "' successful");
            testReport.log(LogStatus.PASS, description);

        } catch (NoSuchElementException ne) {
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure(description, ne, true);
            throw new NoSuchElementException("Locator '" + locator.toString() + "' search for step: "
                    + description + " failed. " + ne.getMessage());

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            reportTestStepFailure(description, se, true);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not perform mouse hover. " + e.getMessage());
            reportTestStepFailure(description, e, true);
        }
    }

    public void safeJavaScriptClick(By element) {
        try {
            if (driver.findElement(element).isEnabled() && driver.findElement(element).isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(element));
            } else {
                System.out.println("Unable to click on element");
            }
        } catch (StaleElementReferenceException e) {
            System.out.println("Element is not attached to the page document " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element was not found in DOM " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to click on element " + e.getMessage());
        }
    }

    public void clickOnLinkWithText(String text, String description) {
        try {
            driver.findElement(By.linkText(text)).click();
            log.info("Performed click operation on element with text: " + text);
            testReport.log(LogStatus.PASS, description);

        } catch (ElementNotVisibleException env) {
            this.goToSleep(1000);
            driver.findElement(By.linkText(text)).click();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. "
                    + env.getMessage());
            reportTestStepFailure(description, env, true);

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure(description, ne, true);

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            reportTestStepFailure(description, se, true);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not perform mouse hover. " + e.getMessage());
            reportTestStepFailure(description, e, true);
        }
    }

    public void clickHiddenElement(By locator) {
        WebElement elements = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elements);
    }

    /**
     * @param parent_locator locator
     * @param inputdata      input
     */
    public void clickElementInAMutliSelectDiv(By parent_locator, String inputdata) {
        try {
            String[] str = inputdata.split(";");
            for (int j = 0; j < str.length; j++) {
                driver.findElement(parent_locator).findElement(By.linkText(str[j].trim())).click();
                log.info("Element clicked.");
            }
        } catch (ElementNotVisibleException env) {
            this.goToSleep(1000);
            String[] str = inputdata.split(";");
            for (int j = 0; j < str.length; j++) {
                driver.findElement(parent_locator).findElement(By.linkText(str[j].trim())).click();
            }
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. "
                    + env.getMessage());
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in clicking an element in a multiple select divs. "
                    + e.getMessage());
        }
    }

    /**
     * Method Description: Clears the data from the mentioned web-element
     *
     * @param locator locator
     */
    public void clear(By locator) {
        try {
            driver.findElement(locator).clear();
            log.info("Performed clear operation for locator: " + locator.toString());
        } catch (InvalidElementStateException ie) {
            ie.printStackTrace();
            log.error("Element is either hidden or disabled. " + ie.getMessage());
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not perform clear operation. " + e.getMessage());
        }
    }

    /**
     * Method Description: Accepts string input and fill in the mentioned
     * web-element Created Date: 20-Jul-2016
     *
     * @param locator     locator
     * @param inputdata   input
     * @param description description
     */
    public void fill(By locator, String inputdata, String description) {
        try {
            driver.findElement(locator).clear();
            driver.findElement(locator).sendKeys(inputdata);
            log.info("Step: '" + description + "' with input '" + inputdata
                    + "' in locator '" + locator.toString() + "' successful");
            testReport.log(LogStatus.PASS, description + ", with input <b>"
                    + inputdata + "</b>");

        } catch (InvalidElementStateException ie) {
            ie.printStackTrace();
            log.error("Element is either hidden or disabled " + ie.getMessage());
            reportTestStepFailure(description, ie, true);

        } catch (NoSuchElementException ne) {
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure(description, ne, true);
            throw new NoSuchElementException("Locator '" + locator.toString() + "' search for step: "
                    + description + " failed. " + ne.getMessage());

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            reportTestStepFailure(description, se, true);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not perform fill operation. " + e.getMessage());
            reportTestStepFailure(description, e, true);
        }
    }

    /**
     * Method Description: It fetches and returns the text of a web-element
     *
     * @param locator locator
     */
    public String getText(By locator) {
        try {
            return driver.findElement(locator).getText();
        } catch (ElementNotVisibleException env) {
            getWaitUtil().waitForElementToBeVisible(locator);
            return driver.findElement(locator).getText();

        } catch (NoSuchElementException ne) {
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure("Element could not be located on page. ", ne, true);
            throw new NoSuchElementException("Locator '" + locator.toString() + "' search failed. "
                    + ne.getMessage());

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            return "Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in returning text of webelement!" + e.getMessage());
            return "Error in returning text of a webelement!" + e.getMessage();
        }
    }

    public String getAttribute(By locator, String attribute) {
        try {
            return driver.findElement(locator).getAttribute(attribute);
        } catch (ElementNotVisibleException env) {
            this.goToSleep(1000);
            String text = driver.findElement(locator).getAttribute(attribute);
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());
            return text;
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
            return "Element could not be located on page. " + ne.getMessage();
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            return "Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in returning the desired attribute of webelement! " + e.getMessage());
            return "Error in returning the desired attribute of webelement! " + e.getMessage();
        }
    }

    public String getAttribute(WebElement locator, String attribute) {
        try {
            return locator.getAttribute(attribute);
        } catch (ElementNotVisibleException env) {
            this.goToSleep(1000);
            String text = locator.getAttribute(attribute);
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());
            return text;
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
            return "Element could not be located on page. " + ne.getMessage();
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. " + se.getMessage());
            return "Either element has been deleted entirely or no longer attached to DOM. " + se.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in returning the desired attribute of webelement! " + e.getMessage());
            return "Error in returning the desired attribute of webelement! " + e.getMessage();
        }
    }

    /**
     * Method Description: It checks the presence of an element on the page of
     * given path
     *
     * @param locator locator
     */
    public Boolean isPresent(By locator) {
        try {
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            if (driver.findElements(locator).size() != 0) {
                log.info("Locator: " + locator.toString() + " is present on page");
                return true;
            } else {
                log.info("Locator: " + locator.toString() + " not present on page");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while checking the presence of an element on page"
                    + e.getMessage());
            return false;
        }
    }

    public Boolean isElementPresent(By locator) {
        try {
            if (driver.findElements(locator).size() != 0
                    && driver.findElement(locator).isDisplayed()) {
                log.info("Locator: " + locator.toString() + " is visible on page");
                return true;
            } else {
                log.info("Locator: " + locator.toString() + " not present on page");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while checking the presence of an element on page"
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Method Description: It checks the presence and visibility of an element
     * on page of given path
     *
     * @param locator locator
     */
    public Boolean isVisible(By locator) {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            if (driver.findElements(locator).size() != 0
                    && driver.findElement(locator).isDisplayed()) {
                log.info("Locator: " + locator.toString() + " is visible on page");
                return true;
            } else {
                log.info("Locator: " + locator.toString() + " is not visible on page");
                return false;
            }
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
            return false;
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Element is not visible on page. " + e.getMessage());
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        }
    }

    public Boolean isEnabled(By locator) {
        try {
            if (driver.findElements(locator).size() != 0
                    && driver.findElement(locator).isEnabled()) {
                driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                log.info("Locator: " + locator.toString() + " is visible on page");
                return true;
            } else {
                log.info("Locator: " + locator.toString() + " is not visible on page");
                return false;
            }
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
            return false;
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Element is not visible on page. " + e.getMessage());
            return false;
        }
    }

    public boolean isSelected(By locator) {
        WebElement element = driver.findElement(locator);
        return element.isSelected();
    }

    /**
     * Method Description: It selects a value from the dropdown basis text
     *
     * @param locator     locator
     * @param text        text
     * @param description description
     */
    public void selectByText(By locator, String text, String description) {
        try {
            new Select(driver.findElement(locator)).selectByVisibleText(text);
            testReport.log(LogStatus.PASS, description);

        } catch (ElementNotVisibleException env) {
            getWaitUtil().waitForElementToBeVisible(locator);
            new Select(driver.findElement(locator)).selectByVisibleText(text);
            testReport.log(LogStatus.PASS, description);

        } catch (NoSuchElementException ne) {
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure(description, ne, true);
            throw new NoSuchElementException("Locator '" + locator + "' search for step: "
                    + description + " failed. " + ne.getMessage());

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            reportTestStepFailure(description, se, true);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in selecting a value from dropdown of webelement! "
                    + e.getMessage());
            reportTestStepFailure(description, e, true);
        }
    }

    /**
     * Method Description: It selects a value from the dropdown basis index
     */
    public void selectByIndex(By locator, int index, String description) {
        try {
            new Select(driver.findElement(locator)).selectByIndex(index);
            testReport.log(LogStatus.PASS, description);

        } catch (ElementNotVisibleException env) {
            getWaitUtil().waitForElementToBeVisible(locator);
            new Select(driver.findElement(locator)).selectByIndex(index);
            testReport.log(LogStatus.PASS, description);

        } catch (NoSuchElementException ne) {
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure(description, ne, true);
            throw new NoSuchElementException("Locator '" + locator.toString() + "' search for step: "
                    + description + " failed. " + ne.getMessage());

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            reportTestStepFailure(description, se, true);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in selecting a value from dropdown of webelement! "
                    + e.getMessage());
            reportTestStepFailure(description, e, true);
        }
    }

    /**
     * Method Description: It selects a value from the dropdown basis Value
     */
    public void selectByValue(By locator, String value, String description) {
        try {
            new Select(driver.findElement(locator)).selectByValue(value);
            testReport.log(LogStatus.PASS, description);

        } catch (ElementNotVisibleException env) {
            getWaitUtil().waitForElementToBeVisible(locator);
            new Select(driver.findElement(locator)).selectByValue(value);
            testReport.log(LogStatus.PASS, description);

        } catch (NoSuchElementException ne) {
            log.error("Element could not be located on page. " + ne.getMessage());
            reportTestStepFailure(description, ne, true);
            throw new NoSuchElementException("Locator '" + locator.toString() + "' search for step: "
                    + description + " failed. " + ne.getMessage());

        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
            reportTestStepFailure(description, se, true);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in selecting a value from dropdown of webelement! "
                    + e.getMessage());
            reportTestStepFailure(description, e, true);
        }
    }

    public void deselectFromDropdown(By locator, String value) {
        try {
            WebElement dropdown = driver.findElement(locator);
            Select select = new Select(dropdown);
            select.deselectByVisibleText(value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public String getSelectedTextFromDropdown(By locator) {
        try {
            WebElement dropdown = driver.findElement(locator);
            Select select = new Select(dropdown);
            return select.getFirstSelectedOption().getText();
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    public String getSelectedIDFromDropdown(By locator) {
        try {
            WebElement dropdown = driver.findElement(locator);
            Select select = new Select(dropdown);
            return select.getFirstSelectedOption().getAttribute("value");
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    public boolean isChecked(By locator) {
        WebElement element = driver.findElement(locator);
        return element.isSelected();
    }

    public void checkRadiobutton(By locator) {
        try {
            if (!driver.findElement(locator).isSelected()) {
                driver.findElement(locator).click();
                log.info("Radio button is selected successfully");
            } else {
                log.info("Radio button is already selected");
            }
        } catch (ElementNotVisibleException env) {
            this.goToSleep(1000);
            if (!driver.findElement(locator).isSelected())
                driver.findElement(locator).click();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. "
                    + env.getMessage());
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in selecting radio button! " + e.getMessage());
        }
    }

    public void selectCheckbox(By locator) {
        try {
            if (!driver.findElement(locator).isSelected()) {
                driver.findElement(locator).click();
                log.info("Checkbox is selected successfully");
            } else {
                log.info("Checkbox is already selected");
            }
        } catch (ElementNotVisibleException env) {
            this.goToSleep(1000);
            if (!driver.findElement(locator).isSelected())
                driver.findElement(locator).click();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. "
                    + env.getMessage());
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
        } catch (StaleElementReferenceException se) {
            se.printStackTrace();
            log.error("Either element has been deleted entirely or no longer attached to DOM. "
                    + se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in selecting checkbox! " + e.getMessage());
        }
    }

    public List<WebElement> getList(By locator) {
        List<WebElement> list = driver.findElements(locator);
        return list;
    }

    public List<String> getListText(By locator) {
        List<String> listTexts = new ArrayList<>();
        List<WebElement> list = driver.findElements(locator);
        for (WebElement ele : list) {
            listTexts.add(ele.getText());
        }
        return listTexts;
    }

    /**
     * Method Description: Checks if an alert pop of browser is present
     */
    public boolean isAlertPresent() {
        try {
            Alert alert = new WebDriverWait(driver, 2)
                    .until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                driver.switchTo().alert();
                log.info("Alert is present");
                return true;
            } else {
                log.info("No alert present.");
                return false;
            }
        } catch (Exception e) {
            log.error("Error occured while verifying alert presence. " + e.getMessage());
            return false;
        }
    }

    /**
     * Method Description: It returns text present on Alert box
     */
    public String getAlertMessage() {
        String message = "";
        try {
            Alert alert = driver.switchTo().alert();
            message = alert.getText();
            log.info("Fetched message present on alert box: " + message);
            alert.accept();
            log.info("Pressed on OK/Yes button present to close the alert box.");
            return message;
        } catch (NoAlertPresentException na) {
            na.printStackTrace();
            log.error("No alert present. " + na.getMessage());
            return message;
        } catch (Exception e) {
            log.error("Error occured while fetching message present on alert box. "
                    + e.getMessage());
            return message;
        }
    }

    /**
     * Method Description: Clicks on OK present on the alert
     */
    public void acceptAlertBox() {

        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            log.info("Pressed on OK/Yes button present on the alert box.");
        } catch (NoAlertPresentException na) {
            log.error("No alert present. " + na.getMessage());
        } catch (Exception e) {
            log.error("Error occured while accepting alert box. " + e.getMessage());
        }
    }

    /**
     * Method Description: Clicks on Cancel/No present on the alert
     */
    public void dismissAlertBox() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            log.info("Pressed on Cancel/No button present on the alert box.");
        } catch (NoAlertPresentException na) {
            na.printStackTrace();
            log.error("No alert present. " + na.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occured while dismissing alert box. " + e.getMessage());
        }
    }

    /**
     * Method Description: Gets the handle for the current window
     */
    private void getWindowHandle() {
        try {
            winHandle = driver.getWindowHandle();
            log.info("Got the handle for the current window");
        } catch (NoSuchWindowException ns) {
            ns.printStackTrace();
            log.error("No window exist. " + ns.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occured while getting window handle. " + e.getMessage());
        }
    }

    /**
     * Method Description: Switches to the most recent window opened
     */
    public void switchtoNewWindow() {
        try {
            getWindowHandle();
            for (String windowsHandle : driver.getWindowHandles()) {
                driver.switchTo().window(windowsHandle);
                log.info("Switched to window: " + windowsHandle);
            }
        } catch (NoSuchWindowException ns) {
            ns.printStackTrace();
            log.error("No window exist. " + ns.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occuring while switching to most recent new window. "
                    + e.getMessage());
        }
    }

    /**
     * Method Description: Closes the Current Active Window
     */
    public void closeNewWindow() {
        try {
            driver.close();
            log.info("Current Active window has been closed");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Current Active Window could not be closed. " + e.getMessage());
        }
    }

    /**
     * Method Description: Switches back to original window
     */
    public void switchtoOriginalWindow() {
        try {
            driver.switchTo().window(winHandle);
            log.info("Switched back to original window");
        } catch (NoSuchWindowException ns) {
            ns.printStackTrace();
            log.error("No window exist. " + ns.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occuring while switching to most recent new window. "
                    + e.getMessage());
        }
    }

    public void switchToParentWindow() {
        try {
            driver.switchTo().defaultContent();
            log.info("Switched to Parent window");
        } catch (NoSuchWindowException ns) {
            ns.printStackTrace();
            log.error("No window exist. " + ns.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while switching to parent window. "
                    + e.getMessage());
        }
    }

    public void pressEnter(By locator) {
        driver.findElement(locator).sendKeys(Keys.ENTER);
    }

    public void pressKeys(Keys key, By element) {
        driver.findElement(element).sendKeys(key);
    }

    public void pressAnotherKeys(char c, By element) {
        driver.findElement(element).sendKeys(Character.toString(c));
    }

    /**
     * Method Description: MoveSlider - Function to move slider from one
     * position to other.
     *
     * @param eleToSlide locator
     * @param xAxis      x
     * @param yAxis      y
     */
    public void moveSlider(WebElement eleToSlide, int xAxis, int yAxis) {
        Actions act = new Actions(driver);
        act.dragAndDropBy(eleToSlide, xAxis, yAxis).build().perform();
    }

    /**
     * Method Description: Scroll page to bottom in slow motion
     */
    public void pageScrollToBottomInSlowMotion() {
        for (int count = 0; ; count++) {
            if (count >= 5) {
                // count value can be changed depending on number of times you want to scroll
                break;
            }
            // y value '800' can be changed
            ((JavascriptExecutor) driver).executeScript(
                    "window.scrollBy(0,800)", "");
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pageScrollToUpInSlowMotion() {
        for (int count = 0; ; count++) {
            if (count >= 5) {
                // count value can be changed depending on number of times you want to scroll
                break;
            }
            // y value '400' can be changed
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,-800)", "");
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void scrollBottomOnce() {
        for (int count = 0; ; count++) {
            if (count >= 1) {
                // count value can be changed depending on number of times you want to scroll
                break;
            }
            // y value '800' can be changed
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500)", "");
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method Description: Closes the browser completely
     */
    public void stopDriver() {
        try {
            driver.quit();
            log.info("Browser has been completely closed.");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Browser could not be closed. " + e.getMessage());

        }
    }

    /**
     * this method creates the screenshot for extent reports
     *
     * @return extent report test content
     */
    public String takeScreenShot() {
        String screenshotName = "image" + System.currentTimeMillis() + ".png";

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File(BaseUtil.reportFolderPath + File.separator + screenshotName));
            log.info("Screenshot " + screenshotName + " taken at " + BaseUtil.reportFolderPath);
        } catch (IOException e) {
            log.error("Fail to take screenshot");
        }
        return screenshotName;
    }

    /**
     * Method Description: Takes ScreenShot and returns the screenshot name
     */
    public String takeScreenshotOld() {
        String directory = System.getProperty("user.dir");
        directory = directory.replace("\\", "\\\\");

        String saveName = Calendar.getInstance().getTime().toString()
                .replace(":", "").replace(" ", "").trim();
        File scrFile = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(directory + "\\screenshots\\"
                    + saveName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveName + ".png";
    }

    public void uploadFile(String filepath) {
        StringSelection ss = new StringSelection(filepath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        goToSleep(2000);
    }

    public void fileUpload(String filepath, By element) {
        //String os = environment;
		/*if(environment.equals("win"))
		{
			filepath = filepath.replace("/", "\\");
		}*/
        System.out.println("Filepath----" + filepath);
        System.out.println("Element-----" + element);
        driver.findElement(element).sendKeys(filepath);
    }

    /**
     * Method Description: Tab Switching - Function to switch to another tab.
     */
    public void tabSwitch() {
        try {
            Robot rob = new Robot();
            rob.keyPress(KeyEvent.VK_CONTROL);
            rob.keyPress(KeyEvent.VK_TAB);
            rob.keyRelease(KeyEvent.VK_CONTROL);
            rob.keyRelease(KeyEvent.VK_TAB);
            // Focus on the tab
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}