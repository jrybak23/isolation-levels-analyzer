## Isolation level analyser

Tool that analyzes whether different phenomenas are reproducible on each isolation level.
So basically, the implementation of [such interface](https://github.com/jrybak23/isolation-levels-analyzer/blob/master/analyzer/src/main/java/com/example/isolationlevelsdemo/databases/PostgreSQL14.java):

```java
package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@Component
public class PostgreSQL14 implements DatabaseToAnalyze {
    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new PostgreSQLContainer<>("postgres:14");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return PostgreSQL95Dialect.class;
    }
}
```

generates such table:

![2022-04-06_15h08_54](https://user-images.githubusercontent.com/14634076/161971343-fbdc0ff9-8d0e-4776-a803-45d1b326eea5.png)

### **The results may not 100% correct!** 
So please double-check [analysis implementations](https://github.com/jrybak23/isolation-levels-analyzer/tree/master/analyzer/src/main/java/com/example/isolationlevelsdemo/analises) before making some conclusions. 

Based on changes in the master brunch changes to this all results are automatically deployed to [this page https://jrybak23.github.io/isolation-levels-analyzer/](https://jrybak23.github.io/isolation-levels-analyzer/).   