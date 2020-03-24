package de.bp2019.pusl.config;

import java.util.Arrays;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Class to handle all the MongoDB configuration
 * 
 * @author Leon Chemnitz
 */
@Configuration
@EnableMongoRepositories(basePackages = PuslProperties.BASE_PACKAGE + ".repository")
class MongoConfig extends AbstractMongoClientConfiguration {

  @Autowired
  MongoProperties mongoProperties;

  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDbName();
  }

  @Override
  public MongoClient mongoClient() {
    MongoClientSettings settings = MongoClientSettings.builder()
        .applyToClusterSettings(builder -> builder.hosts(
            Arrays.asList(new ServerAddress(mongoProperties.getAddress(), Integer.valueOf(mongoProperties.getPort())))))
        .build();

    return MongoClients.create(settings);
  }

  @Override
  protected String getMappingBasePackage() {
    return PuslProperties.BASE_PACKAGE + ".model";
  }

}