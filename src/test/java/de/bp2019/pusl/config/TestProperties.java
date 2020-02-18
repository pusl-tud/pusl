package de.bp2019.pusl.config;

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

    private String superadminUsername;
    private String superadminPassword;
    private String adminUsername;
    private String adminPassword;
    private String wimiUsername;
    private String wimiPassword;
    private String hiwiUsername;
    private String hiwiPassword;

    private String chromedriverWin;
    private String chromedriverLinux;
    private String chromedriverMac;


    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getChromedriverLinux() {
        return this.chromedriverLinux;
    }

    public void setChromedriverLinux(String chromedriverLinux) {
        this.chromedriverLinux = chromedriverLinux;
    }

    public String getChromedriverMac() {
        return this.chromedriverMac;
    }

    public void setChromedriverMac(String chromedriverMac) {
        this.chromedriverMac = chromedriverMac;
    }

    public String getChromedriverWin() {
        return this.chromedriverWin;
    }

    public void setChromedriverWin(String chromedriverWin) {
        this.chromedriverWin = chromedriverWin;
    }

    public String getSuperadminUsername() {
        return this.superadminUsername;
    }

    public void setSuperadminUsername(String superadminUsername) {
        this.superadminUsername = superadminUsername;
    }

    public String getSuperadminPassword() {
        return this.superadminPassword;
    }

    public void setSuperadminPassword(String superadminPassword) {
        this.superadminPassword = superadminPassword;
    }

    public String getAdminUsername() {
        return this.adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return this.adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getWimiUsername() {
        return this.wimiUsername;
    }

    public void setWimiUsername(String wimiUsername) {
        this.wimiUsername = wimiUsername;
    }

    public String getWimiPassword() {
        return this.wimiPassword;
    }

    public void setWimiPassword(String wimiPassword) {
        this.wimiPassword = wimiPassword;
    }

    public String getHiwiUsername() {
        return this.hiwiUsername;
    }

    public void setHiwiUsername(String hiwiUsername) {
        this.hiwiUsername = hiwiUsername;
    }

    public String getHiwiPassword() {
        return this.hiwiPassword;
    }

    public void setHiwiPassword(String hiwiPassword) {
        this.hiwiPassword = hiwiPassword;
    }

    public boolean isHeadlessUiTests() {
        return this.headlessUiTests;
    }

    public void setHeadlessUiTests(boolean headlessUiTests) {
        this.headlessUiTests = headlessUiTests;
    }

}