package de.bp2019.pusl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import de.bp2019.pusl.model.Grade;

@Configuration
public class IndexConfig {
    @Autowired
    MongoTemplate mongoTemplate;
  
    @Autowired
    MongoMappingContext mongoMappingContext;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
  
      IndexOperations indexOps = mongoTemplate.indexOps(Grade.class);
  
      IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
      resolver.resolveIndexFor(Grade.class).forEach(indexOps::ensureIndex);
    }
}