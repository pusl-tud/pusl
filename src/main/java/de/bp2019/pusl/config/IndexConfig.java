package de.bp2019.pusl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Configuration class to set up MongoDB Indices
 * 
 * @author Leon Chemnitz
 */
@Configuration
public class IndexConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(IndexConfig.class);

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  MongoConverter mongoConverter;

  @EventListener(ApplicationReadyEvent.class)
  public void initIndicesAfterStartup() {
    LOGGER.info("Mongo InitIndicesAfterStartup init");
    var init = System.currentTimeMillis();

    var mappingContext = this.mongoConverter.getMappingContext();

    if (mappingContext instanceof MongoMappingContext) {
      var mongoMappingContext = (MongoMappingContext) mappingContext;

      for (BasicMongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
        var clazz = persistentEntity.getType();
        if (clazz.isAnnotationPresent(Document.class)) {
          var indexOps = mongoTemplate.indexOps(clazz);
          var resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
          resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
        }
      }
    }
    LOGGER.info("Mongo InitIndicesAfterStartup take: {}", (System.currentTimeMillis() - init));
  }
}