package com.example.isolationlevelsdemo.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

@Component
public class EntityManagerFactoryFactory {

    public EntityManagerFactory getEntityManagerFactory(DataSource dataSource, Class<? extends Dialect> dialect) {
        Properties properties = getProperties(dialect);
        HibernatePersistenceUnitInfo unitInfo = new HibernatePersistenceUnitInfo(properties, dataSource);
        PersistenceUnitInfoDescriptor descriptor = new PersistenceUnitInfoDescriptor(unitInfo);
        return new EntityManagerFactoryBuilderImpl(descriptor, new HashMap<>())
                .build();
    }

    protected Properties getProperties(Class<? extends Dialect> dialect) {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect.getName());
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("javax.persistence.schema-generation.database.action", "update");
        properties.put("hibernate.id.new_generator_mappings", false);
        return properties;
    }
}
