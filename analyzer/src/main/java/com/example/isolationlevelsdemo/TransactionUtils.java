package com.example.isolationlevelsdemo;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class TransactionUtils {
    public static void runInTransaction(EntityManagerFactory entityManagerFactory, Consumer<EntityManager> function) {
        wrapWithTransactionAndReturnResult(entityManagerFactory, "", entityManager -> {
            function.accept(entityManager);
            return Optional.empty();
        });
    }

    public static void runInTheSecondTransaction(EntityManagerFactory entityManagerFactory, Consumer<EntityManager> function) {
        wrapWithTransactionAndReturnResult(entityManagerFactory, "1st", entityManager -> {
            function.accept(entityManager);
            return Optional.empty();
        });
    }

    public static <T> T runInTransactionAndReturnResult(EntityManagerFactory entityManagerFactory, Function<EntityManager, T> function) {
        return wrapWithTransactionAndReturnResult(entityManagerFactory, "", entityManager -> {
            T value = function.apply(entityManager);
            return Optional.of(value);
        }).orElseThrow();
    }

    public static <T> T runInTheFirstTransactionAndReturnResult(EntityManagerFactory entityManagerFactory, Function<EntityManager, T> function) {
        return wrapWithTransactionAndReturnResult(entityManagerFactory, "1st", entityManager -> {
            T value = function.apply(entityManager);
            return Optional.of(value);
        }).orElseThrow();
    }

    public static <T> T runInTheSecondTransactionAndReturnResult(EntityManagerFactory entityManagerFactory, Function<EntityManager, T> function) {
        return wrapWithTransactionAndReturnResult(entityManagerFactory, "2nd", entityManager -> {
            T value = function.apply(entityManager);
            return Optional.of(value);
        }).orElseThrow();
    }

    private static <T> Optional<T> wrapWithTransactionAndReturnResult(EntityManagerFactory entityManagerFactory,
                                                                      String transactionName,
                                                                      Function<EntityManager, Optional<T>> action) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        log.info("The {} transaction {} has just begun.", transactionName, transaction.hashCode());
        try {
            Optional<T> result = action.apply(entityManager);
            transaction.commit();
            log.info("The {} transaction {} has just committed.", transactionName, transaction.hashCode());
            return result;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private TransactionUtils() {
    }
}
