package com.hyke.portal.utils;

import com.hyke.portal.utils.BaseUtil;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;


import java.util.Properties;

public class TestRetryAnalyzer extends BaseUtil implements IRetryAnalyzer {

    private Properties config = BaseUtil.initPropFromFile("/src/main/resources/config.properties");
    private int retryCount = 0;
    private String isRetry = config.getProperty("RetryTest");
    private int maxRetryCount = Integer.parseInt(config.getProperty("RetryCount"));


    /**
     * Below method returns 'true' if the tests method has to be retried else 'false'
     * and it takes the 'Result' as parameter of the tests method that just ran
     *
     * @return boolean value
     */
    public boolean retry(ITestResult result) {

        if (isRetry.equalsIgnoreCase("YES")) {
            if (retryCount < maxRetryCount) {
                System.out.println("Retrying tests " + result.getName() + " with status "
                        + getResultStatusName(result.getStatus()) + " for the " + (retryCount + 1) + " time(s).");
                retryCount++;
                return true;
            }
        }
        return false;
    }

    private String getResultStatusName(int status) {
        String resultName = null;
        if (status == 1)
            resultName = "SUCCESS";
        if (status == 2)
            resultName = "FAILURE";
        if (status == 3)
            resultName = "SKIP";
        return resultName;
    }
}