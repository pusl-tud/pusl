package de.bp2019.pusl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Handle for all the MongoDB configuration in application.properties
 * 
 * @author Leon Chemnitz
 */
@ConfigurationProperties("pusl.mongo")
public class MongoProperties {
    
    private MongoProperties(){}
    
    private String address;
    private String port;
    private String dbName;
    private String username;
    private String password;
    private String adminDb;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminDb() {
        return this.adminDb;
    }

    public void setAdminDb(String adminDb) {
        this.adminDb = adminDb;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

}