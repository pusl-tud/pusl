package de.bp2019.pusl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties only used for testing
 * 
 * @author Leon Chemnitz
 */
@ConfigurationProperties("pusl.test")
public class TestProperties {
    private boolean headlessUiTests;
    private String baseUrl;

    public static String chromedriverWin = "chromedriver_win.exe";
    public static String chromedriverLinux = "/usr/local/bin/chromedriver";
    public static String chromedriverMac = "chromedriver_mac";

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isHeadlessUiTests() {
        return this.headlessUiTests;
    }

    public void setHeadlessUiTests(boolean headlessUiTests) {
        this.headlessUiTests = headlessUiTests;
    }

}