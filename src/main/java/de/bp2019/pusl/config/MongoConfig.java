package de.bp2019.pusl.config;

import com.mongodb.MongoClient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Class to handle all the MongoDB configuration
 * 
 * @author Leon Chemnitz
 */
@ConfigurationProperties(prefix = "mongo")
@EnableMongoRepositories(basePackages = AppConfig.BASE_PACKAGE + ".repository")
@SuppressWarnings("deprecation")
class MongoConfig extends AbstractMongoConfiguration {
  private String dbName;

  @Override
  protected String getDatabaseName() {
    return dbName;
  }

  @Override
  public MongoClient mongoClient() {
    return new MongoClient();
  }

  @Override
  protected String getMappingBasePackage() {
    return AppConfig.BASE_PACKAGE;
  }
}