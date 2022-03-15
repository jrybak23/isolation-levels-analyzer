package com.example.isolationlevelsdemo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionUtils {
    public static void runInTransaction(EntityManagerFactory entityManagerFactory, Consumer<EntityManager> function) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            function.accept(entityManager);
            transaction.commit();
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
        try {
            T value = function.apply(entityManager);
            transaction.commit();
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
