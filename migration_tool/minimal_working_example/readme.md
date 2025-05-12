###     

#### How to use migration tools

1. Go to your build.gradle file and add the following lines to include Mongock in your app:

Version **mongodbSpringDataVersion=5.5.1** for springboot 3.4.5

```
dependencies {
    ......
    implementation "io.mongock:mongodb-springdata-v4-driver:${mongodbSpringDataVersion}"
    implementation "io.mongock:mongock-springboot-v3:${mongodbSpringDataVersion}"
    ......
}
```

2. Go to your app's main or config class (e.g., SpringApplication.java) and enable Mongock

Add **@EnableMongock** annotation

```
import org.springframework.context.annotation.Configuration;
import io.mongock.runner.springboot.EnableMongock;


@EnableMongock
@Configuration
public class MongockConfiguration {}
```

4. Configure startup scan

You need to tell your application to scan the directory.
To do so, head to your **application.yaml** (or application.properties) file and add the following:

```
spring:
  application:
    name: minimal_working_example
    
mongock:
  migration-scan-package: com.malex.db.changelogs
```

5. Create a change log class

```
package: com.malex.db.changelogs


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

```
