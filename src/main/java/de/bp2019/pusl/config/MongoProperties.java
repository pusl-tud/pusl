package de.bp2019.pusl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pusl.mongo")
public class MongoProperties {    
    private String address;
    private String port;
    private String dbName;

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