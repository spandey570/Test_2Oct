package com.hyke.portal.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class WaitUtil {

    private static WaitUtil Wait;
    private WebDriver driver;
    private Logger log = Logger.getLogger(WaitUtil.class);
    private int timeoutPageLoad;

    WaitUtil(WebDriver driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public WaitUtil getWait() {
        if (Wait == null) {
            Wait = new WaitUtil(this.driver);
        }
        return Wait;
    }


    /**
     * Waits and Switches to the Frame
     *
     * @param locator          locator
     * @param timeOutInSeconds time
     */
    public void waitAndSwitchToFrame(By locator, int timeOutInSeconds) {
        try {
            waitForPageLoad(timeoutPageLoad);
            log.info("Switching to Frame" + locator.toString());
            WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));

        } catch (Exception e) {
            log.error("Exception found in waitAndSwitchToFrame: "
                    + e.getMessage());
        }
    }


    /**
     * Waits and Switched to the Frame found by its Id or Name
     *
     * @param sFrameName       frame
     * @param timeOutInSeconds time
     */
    public void waitAndSwitchToFrame(String sFrameName, int timeOutInSeconds) {
        try {
            WebDriverWait wait;
            waitForPageLoad(timeoutPageLoad);
            log.info("Waiting and Switching to Frame by its Name "
                    + sFrameName);
            wait = new WebDriverWait(driver, timeOutInSeconds);
            wait.until(ExpectedConditions
                    .frameToBeAvailableAndSwitchToIt(sFrameName));
            log.info("Switched to Frame : " + sFrameName);
            driver.switchTo().frame(sFrameName);

        } catch (Exception e) {
            log.error("Exception Occured while switching to frame "
                    + sFrameName + ": " + e.getMessage());
        }
    }


    public boolean waitForElementToBeDisplay(
            WebElement element, int maxSecondTimeout, boolean... isFailOnExcaption) {
        try {
            log.info("INTO METHOD waitForElementToBeDisplay");
            maxSecondTimeout = maxSecondTimeout * 20;
            while (!element.isDisplayed() && maxSecondTimeout > 0) {
                Thread.sleep(50L);
                maxSecondTimeout--;
            }
            if (maxSecondTimeout == 0 && isFailOnExcaption.length != 0) {
                if (isFailOnExcaption[0]) {
                    log.error("Element is not display within "
                            + (maxSecondTimeout / 20) + "Sec.");
                }
            }
            log.info("OUT OF METHOD waitForElementToBeDisplay");
            return true;

        } catch (Exception e) {
            log.error("Exception in waitForElementToBeDisplay: "
                    + e.getMessage());
            return false;
        }
    }


    /**
     * Wait for html element to be hidden
     *
     * @param element           element
     * @param maxSecondTimeout  time
     * @param isFailOnExcaption ( optional parameter true if fail on exception)
     */
    public boolean waitForElementToBeHidden(
            WebElement element, int maxSecondTimeout, boolean... isFailOnExcaption) {
        try {
            log.info("INTO waitForElementToBeHidden METHOD");
            maxSecondTimeout = maxSecondTimeout * 20;
            while (element.isDisplayed() && maxSecondTimeout > 0) {
                Thread.sleep(50L);
                maxSecondTimeout--;
            }
            if (maxSecondTimeout == 0 && isFailOnExcaption.length != 0) {
                if (isFailOnExcaption[0]) {
                    log.error("Element is not hidden within "
                            + (maxSecondTimeout / 20) + "Sec.");
                }
            }
            log.info("OUT OF METHOD waitForElementToBeHidden");
            return true;

        } catch (Exception e) {
            log.error("Exception in waitForElementToBeHidden: "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * waits for specified duration and checks that an element is present on
     * DOM. Visibility means that the element is not only displayed but also has
     * a height and width that is greater than 0.
     *
     * @param locator locator
     **/
    public void waitForElementToBeVisible(By locator) {
        try {
            log.info("Waiting for element to be visible using String locator: "
                    + locator.toString());
            WebDriverWait wait = new WebDriverWait(driver, 120);
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Thread.sleep(1000);
        } catch (Exception e) {
            log.info("Exception while waiting for visibility. "
                    + e.getMessage());
        }
    }


    public void waitForElementToBeVisible(By locator, long timeOutInSeconds) {
        try {
            log.info("Waiting for an element to be visible using String locator: "
                    + locator.toString());
            WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        } catch (Exception e) {
            log.info("Exception while waiting for visibility. "
                    + e.getMessage());
        }
    }


    /**
     * Wait For Element to Disable
     *
     * @param element          element
     * @param maxSecondTimeout time
     */
    public boolean waitForElementToDisable(WebElement element, int maxSecondTimeout) {
        try {
            log.info("INTO waitForElementToDisable METHOD");
            maxSecondTimeout = maxSecondTimeout * 20;
            while (element.isEnabled() && (maxSecondTimeout > 0)) {
                Thread.sleep(50L);
                maxSecondTimeout--;
            }
            if (maxSecondTimeout == 0) {
                log.error("Element is not disabled within "
                        + (maxSecondTimeout / 20) + "Sec.");
            }
            log.info("OUT OF METHOD waitForElementToDisable");
            return true;

        } catch (Exception e) {
            log.info("Exception found in waitForElementToDisable: "
                    + e.getMessage());
            return false;
        }
    }


    /**
     * Waits for an Element to DisAppear using Wait
     *
     * @param element               element
     * @param timeOutInSeconds      time
     * @param pollingInMilliSeconds time
     */
    public boolean waitForElementToDisAppear(
            final WebElement element, int timeOutInSeconds, int pollingInMilliSeconds) {
        try {
            log.info("Waiting for element to disappear using wait until element is not Displayed");
            return (new WebDriverWait(driver, timeOutInSeconds,
                    pollingInMilliSeconds))
                    .until(new ExpectedCondition<Boolean>() {
                        public Boolean apply(WebDriver driver) {

                            log.info("Waiting for element to be disappear ");
                            return !element.isDisplayed();
                        }
                    });

        } catch (Exception e) {
            log.info("Exception found in waitForElementToDisAppear: "
                    + e.getMessage());
            return false;
        }
    }


    public void waitForElementToDisappear(By locator) {
        try {
            log.info("Waiting for an element to be invisible using String locator "
                    + locator.toString());
            WebDriverWait wait = new WebDriverWait(driver, 120);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));

        } catch (Exception e) {
            log.info("Exception while waiting for invisibility: "
                    + e.getMessage());
        }
    }


    /**
     * Wait For Element to Enable
     *
     * @param element          element
     * @param maxSecondTimeout time
     */
    public boolean waitForElementToEnable(WebElement element, int maxSecondTimeout) {
        try {
            log.info("INTO waitForElementToEnable METHOD");
            maxSecondTimeout = maxSecondTimeout * 20;
            while (!element.isEnabled() && maxSecondTimeout > 0) {
                Thread.sleep(50L);
                maxSecondTimeout--;
            }
            if (maxSecondTimeout == 0) {
                log.error("Element is not enabled within "
                        + (maxSecondTimeout / 20) + "Sec.");
            }
            log.info("OUT OF METHOD waitForElementToEnable");
            return true;

        } catch (Exception e) {
            log.info("Exception found in waitForElementToEnable: "
                    + e.getMessage());
            return false;
        }
    }


    public void waitForElementToEnable(By locator) {
        try {
            log.info("Waiting for an element to be enabled using String locator "
                    + locator.toString());
            WebDriverWait wait = new WebDriverWait(driver, 120);
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            Thread.sleep(1000);

        } catch (Exception e) {
            log.info("Exception while waiting for visibility. "
                    + e.getMessage());
        }
    }


    /**
     * Waits for an Element to get Stale or deleted from DOM
     *
     * @param iTimeOutInSeconds time
     * @param by                locator
     */
    public boolean waitForElementToStale(int iTimeOutInSeconds, By by) {
        boolean isStale = true;
        int iAttempt = 0;

        try {
            iTimeOutInSeconds = iTimeOutInSeconds * 20;
            while (iTimeOutInSeconds > 0) {
                iAttempt++;
                log.info("Waiting for Element to Stale Attempt Number :"
                        + iAttempt);
                driver.manage().timeouts()
                        .implicitlyWait(100, TimeUnit.MILLISECONDS);
                log.info("Element :" + driver.findElement(by).isDisplayed());
                if (driver.findElements(by).size() == 0) {
                    isStale = false;
                    break;
                }
                Thread.sleep(30L);
                iTimeOutInSeconds--;
            }
        } catch (NoSuchElementException e) {
            log.error("No Element Found.This Means Loader is no more in HTML. Moving out of waitForElementToStale!!!");
            isStale = false;
        } catch (StaleElementReferenceException s) {
            log.error("Given Element is stale from DOM Moving out of waitForElementToStale!!!");
            isStale = false;
        } catch (Exception e) {
            log.error("Some Exception ocurred Please check code!!!");
        } finally {
            driver.manage().timeouts().implicitlyWait(
                    Integer.parseInt(("browser.implicitwait")),
                    //Need to Give Global implicit Timeout Value
                    TimeUnit.SECONDS);
        }
        return isStale;
    }


    /**
     * Waits for Page Load via Java Script Ready State
     *
     * @param iTimeOut time
     */
    public boolean waitForPageLoad(int iTimeOut) {
        try {
            Thread.sleep(1000);
            log.info("Waiting For Page load via JS");
            ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) driver).executeScript(
                            "return document.readyState").equals("complete");
                }
            };
            WebDriverWait wait = new WebDriverWait(driver, iTimeOut);
            wait.until(pageLoadCondition);
            return true;

        } catch (Exception e) {
            log.error("Error Occured waiting for Page Load "
                    + driver.getCurrentUrl());
        }
        return false;
    }


    /**
     * An expectation for checking if the given text is present in the specified
     * element
     *
     * @param element element
     * @param sText   text
     */
    public void waitFortextToBePresentInElement(
            final WebElement element, final String sText) {
        WebDriverWait wait = new WebDriverWait(driver, 120);
        wait.until(ExpectedConditions.textToBePresentInElement(element, sText));
    }


    /**
     * An expectation for checking if the given text is present in the element
     * that matches the given locator.
     *
     * @param locator locator
     * @param sText   text
     */
    public void waitFortextToBePresentInElementLocated(
            By locator, final String sText) {
        WebDriverWait wait = new WebDriverWait(driver, 120);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, sText));
    }


    /**
     * wait till tests present on element
     *
     * @param element           element
     * @param maxSecondTimeout  time
     * @param isFailOnExcaption ( optional parameter true if fail on exception)
     */
    public boolean waitForTextToBePresentOnElement(
            WebElement element, int maxSecondTimeout, String matchText,
            boolean... isFailOnExcaption) {
        try {
            log.info("INTO METHOD waitForTextToBePresentOnElement");
            maxSecondTimeout = maxSecondTimeout * 20;
            while ((!element.isDisplayed() && (maxSecondTimeout > 0) && (element
                    .getText().toLowerCase().equalsIgnoreCase(matchText
                            .toLowerCase().trim())))) {
                log.info("Loading...CountDown=" + maxSecondTimeout);
                Thread.sleep(50L);
                maxSecondTimeout--;
            }
            if ((maxSecondTimeout == 0) && (isFailOnExcaption.length != 0)) {
                if (isFailOnExcaption[0]) {
                    log.error("Element is not display within "
                            + (maxSecondTimeout / 20) + "Sec.");
                }
            }
            log.info("OUT OF METHOD waitForTextToBePresentOnElement");
            return true;

        } catch (Exception e) {
            log.info("Exception found in waitForTextToBePresentOnElement: "
                    + e.getMessage());
            return false;
        }
    }


    /**
     * Waits Until the Attribute of Element got Changed.
     *
     * @param webElement       element
     * @param attribute        attribute
     * @param value            value
     * @param maxSecondTimeout time
     */
    public void waitTillElementAttributeChange(
            WebElement webElement, String attribute, String value, int maxSecondTimeout) {
        try {
            log.info("INTO METHOD waitTillElementAttributeChange");
            maxSecondTimeout = maxSecondTimeout * 20;
            while (webElement.getAttribute(attribute) != null) {
                if ((!webElement.getAttribute(attribute.trim()).toLowerCase()
                        .contains(value.trim().toLowerCase()))
                        && (maxSecondTimeout > 0)) {
                    log.info("Loading...CountDown=" + maxSecondTimeout);
                    Thread.sleep(50L);
                    maxSecondTimeout--;
                }
            }
            log.info("OUT OF METHOD waitTillElementAttributeChange");
        } catch (Exception e) {
            log.error("SOME ERROR CAME IN METHOD->waitTillElementAttributeChange->"
                    + e.getMessage());
        }
    }


    public WebElement customWaitToStale(int timeOut, By byObj) {
        int waitCounter = 0;
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.MILLISECONDS);

        while (waitCounter <= timeOut) {
            try {
                Thread.sleep(1000);
                return driver.findElement(byObj);

            } catch (Exception e) {
                e.printStackTrace();
            }
            waitCounter = waitCounter + 1;
        }
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.MILLISECONDS);
        return null;
    }
}