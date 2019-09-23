package com.hyke.portal.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class ActionsUtil {

    public final static Logger log = Logger.getLogger(ActionsUtil.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor executor;
    private Actions action;

    public ActionsUtil(WebDriver driver) {

        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 60);
        executor = (JavascriptExecutor) driver;
        action = new Actions(driver);
    }


    public void sendKeysChord(String keyChord) {
        action.sendKeys(keyChord).build().perform();
    }

    /**
     * Method description: Closes the current opened window
     */
    public void closeCurrentTab() {
        try {
            action.keyDown(Keys.CONTROL).sendKeys(Keys.chord("w")).build().perform();
            log.info("Closed Current tab by pressing Ctrl+w");

        } catch (NoSuchWindowException ns) {
            ns.printStackTrace();
            log.error("No window exist. " + ns.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while closing current window's tab. " + e.getMessage());
        }
    }

    /**
     * @param locator Method description: It performs mouse hover
     */
    public void performMouseHover(By locator) {
        try {
            action.moveToElement(driver.findElement(locator)).build().perform();
        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());
        } catch (ElementNotVisibleException env) {
            this.goToSleep(2000);
            action.moveToElement(driver.findElement(locator)).build().perform();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not perform mouse hover. " + e.getMessage());
        }
    }


    /**
     * @param locator            locator
     * @param elementTobeClicked Method description: It performs mouse hover and clicks on the element
     */
    public void performMouseHoverAndClick(By locator, String elementTobeClicked) {

        try {
            action.moveToElement(driver.findElement(locator)).build().perform();
            log.info("Mouse hover performed on locator: " + locator);
            driver.findElement(locator).click();
            log.info("Element clicked successfully");

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.moveToElement(driver.findElement(locator)).build().perform();
            driver.findElement(locator).click();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not perform mouse hover and click operation.");
        }
    }

    /**
     * Method Description: MoveSlider - Method to move slider from one position to other.
     *
     * @param locator locator
     * @param xAxis   x
     * @param yAxis   y
     */
    public void moveSlider(By locator, int xAxis, int yAxis) {

        try {
            action.dragAndDropBy(driver.findElement(locator), xAxis, yAxis).build().perform();
            log.info("Moved slider successfully");

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.dragAndDropBy(driver.findElement(locator), xAxis, yAxis).build().perform();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not move slider from one position to other.");
        }
    }

    /**
     * Method Description: Method clicks and holds the source webelement,
     * moves to destination webelement and release the element
     *
     * @param source      source
     * @param destination destination
     */
    public void dragAndDrop(String source, String destination) {

        WebElement src = driver.findElement(By.xpath(source));
        WebElement des = driver.findElement(By.id(destination));
        try {
            //For each action we need to build and Perform
            action.clickAndHold(src).build().perform();
            action.moveToElement(des).build().perform();
            action.release(des).build().perform();
            log.info("Performed drag and drop from source to destination");

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.clickAndHold(src).build().perform();
            action.moveToElement(des).build().perform();
            action.release(des).build().perform();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not drag and drop an element from source to destination.");
        }
    }

    /**
     * @param parentMenu xpath
     */
    public void rightClick(String parentMenu) {
        try {
            action.contextClick(driver.findElement(By.xpath(parentMenu))).build().perform(); //Context Click
            action.sendKeys(Keys.ARROW_RIGHT).build().perform();
            goToSleep(1000);
            action.sendKeys(Keys.ARROW_DOWN).build().perform();
            goToSleep(1000);
            action.sendKeys(Keys.ENTER).build().perform();
            log.info("Right click performed successfully");

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.contextClick(driver.findElement(By.xpath(parentMenu))).build().perform(); //Context Click
            action.sendKeys(Keys.ARROW_RIGHT).build().perform();
            goToSleep(1000);
            action.sendKeys(Keys.ARROW_DOWN).build().perform();
            goToSleep(1000);
            action.sendKeys(Keys.ENTER).build().perform();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not right click.");
        }
    }

    /**
     * Method Description: Select multiple values in Multiple Select dropdown
     *
     * @param xPath     xpath
     * @param inputdata input
     * @param splitVia  text
     */
    public void selectMulipleDropDownValues(String xPath, String inputdata, String splitVia) {

        int sleepTime = 4000;
        String[] input = inputdata.split(splitVia);
        Actions builder = new Actions(driver);
        for (String option : input) {
            builder.keyDown(Keys.CONTROL);
            try {
                new Select(driver.findElement(By.xpath(xPath))).selectByVisibleText(option);
            } catch (Exception e) {
                goToSleep(sleepTime);
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Method Description: Double clicks at the last known mouse coordinates
     */
    public void doubleClick() {

        try {
            action.doubleClick();
            log.info("Performed double click successfully");

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.doubleClick();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not double click.");
        }
    }

    /**
     * Method Description: Double clicks at the last known mouse coordinates
     *
     * @param locator locator
     */
    public void doubleClickTheLocator(String locator) {

        try {
            action.doubleClick(driver.findElement(By.xpath(locator)));
            log.info("Performed double click on locator: " + locator);

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.doubleClick(driver.findElement(By.xpath(locator)));
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not double click the locator.");
        }
    }

    /**
     * Method Description: Clicks at the last known mouse coordinates
     */
    public void click() {
        try {
            action.click();
            log.info("Performed click operation successfully");

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.click();
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not click the last known mouse coordinates.");
        }
    }

    /**
     * Method Description: Clicks at the last known mouse coordinates
     *
     * @param locator locator
     */
    public void clickTheLocator(By locator) {
        try {
            action.click(driver.findElement(locator));
            log.info("Performed click operation on locator: " + locator);

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.click(driver.findElement(locator));
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Couldn't click the locator.");
        }
    }

    public void navigateToUrl(String url) {
        driver.get(url);
    }

    /**
     * @param key key
     */
    public void pressKey(Keys key) {
        try {
            action.keyDown(key);
            log.info("Key is pressed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not press key.");
        }
    }

    /**
     * @param key key
     */
    public void releaseKey(Keys key) {
        try {
            action.keyUp(key);
            log.info("Key is pressed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not release key");
        }
    }

    /**
     * @param locator locator
     */
    public void clickandhold(By locator) {
        try {
            action.clickAndHold(driver.findElement(locator));
            log.info("Clicked and held on locator: " + locator);

        } catch (ElementNotVisibleException env) {
            goToSleep(2000);
            action.clickAndHold(driver.findElement(locator));
            env.printStackTrace();
            log.error("Element is present in DOM but not visible on page. " + env.getMessage());

        } catch (NoSuchElementException ne) {
            ne.printStackTrace();
            log.error("Element could not be located on page. " + ne.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Couldn't click and hold locator");
        }
    }

    public void goToSleep(int TimeInMillis) {
        try {
            Thread.sleep(TimeInMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
