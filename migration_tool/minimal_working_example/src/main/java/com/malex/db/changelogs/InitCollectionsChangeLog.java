package com.malex.db.changelogs;

import com.mongodb.client.model.IndexOptions;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id = "init-customer", order = "001", author = "malex")
public class InitCollectionsChangeLog {

  @BeforeExecution
  public void beforeExecution(final MongoTemplate mongoTemplate) {
    mongoTemplate
        .createCollection(
            "customer",
            CollectionOptions.empty()
                .validator(
                    Validator.schema(
                        MongoJsonSchema.builder()
                            .required("firstName", "lastName", "email")
                            .properties(
                                JsonSchemaProperty.int64("id"),
                                JsonSchemaProperty.string("firstName"),
                                JsonSchemaProperty.string("lastName"),
                                JsonSchemaProperty.string("email"))
                            .build())))
        .createIndex(new Document("email", 1), new IndexOptions().name("email").unique(true));
  }

  @RollbackBeforeExecution
  public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
    mongoTemplate.dropCollection("customer");
  }

  @Execution
  public void execute(MongoTemplate mongoTemplate) {
    // No-op or insert some default data
  }

  @RollbackExecution
  public void rollbackExecution(MongoTemplate mongoTemplate) {
    // Remove inserted data if needed
  }
}
