package com.example.isolationlevelsdemo;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class TransactionUtils {
    public static void runInTransaction(EntityManagerFactory entityManagerFactory, Consumer<EntityManager> function) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        log.info("The transaction " + transaction.hashCode() +" has begun.");
        transaction.begin();
        try {
            function.accept(entityManager);
            transaction.commit();
            log.info("The transaction " + transaction.hashCode() +" has committed.");
        }  finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public static <T> T runInTransactionAndReturnValue(EntityManagerFactory entityManagerFactory, Function<EntityManager, T> function) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        log.info("The transaction " + transaction.hashCode() +" has begun.");
        try {
            T value = function.apply(entityManager);
            transaction.commit();
            log.info("The transaction " + transaction.hashCode() +" has committed.");
            return value;
        }  finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private TransactionUtils() {
    }
}
