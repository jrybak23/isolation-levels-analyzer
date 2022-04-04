package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.Result;
import com.example.isolationlevelsdemo.TransactionUtils;
import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Optional;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTheFirstTransactionAndReturnResult;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransactionAndReturnResult;

@Component
@Slf4j
public class SerializationAnomalyAnalysis implements Analysis {
    @Override
    public String getEffectName() {
        return "Serialization Anomaly";
    }

    @Override
    public boolean isReproducible(EntityManagerFactory entityManagerFactory) {
        Optional<Result> result;
        try {
            result = runFirstTransaction(entityManagerFactory);
        } catch (RollbackException e) {
            log.error("1st transaction is failed to commit. So " + getEffectName() + " wasn't reproduced.", e);
            return false;
        }

        if (result.isPresent()) {
            return result.get().equals(Result.REPRODUCED);
        }

        return runInTransactionAndReturnResult(entityManagerFactory, entityManager -> {
            return !entityManager.createQuery("from TestModel t where t.value = 'value inserted by 2-nd connection'")
                    .getResultList()
                    .isEmpty();
        });
    }

    private Optional<Result> runFirstTransaction(EntityManagerFactory entityManagerFactory) {
         return runInTheFirstTransactionAndReturnResult(entityManagerFactory, entityManager1 -> {
            String value1 = getValue(entityManager1);
            if (!value1.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                runSecondTransaction(entityManagerFactory);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getEffectName() + " wasn't reproduced.", e);
                return Optional.of(Result.NOT_REPRODUCED);
            }

            try {
                TestModel model = new TestModel();
                model.setId(2);
                model.setValue("value inserted by 1-nd connection");
                entityManager1.persist(model);
                entityManager1.flush();
            } catch (OptimisticLockException e) {
                log.info("Failed to insert using the first transaction. So " + getEffectName() + " is not reproduced.", e);
                return Optional.of(Result.NOT_REPRODUCED);
            } catch (PersistenceException e) { // for oracle support
                log.info("Failed to insert using the first transaction. So " + getEffectName() + " is not reproduced.", e);
                return Optional.of(Result.NOT_REPRODUCED);
            }

            return Optional.empty();
        });
    }

    private void runSecondTransaction(EntityManagerFactory entityManagerFactory) {
        TransactionUtils.runInTheSecondTransaction(entityManagerFactory, entityManager2 -> {
            String value2 = getValue(entityManager2);
            if (!value2.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            TestModel model = new TestModel();
            model.setId(3);
            model.setValue("value inserted by 2-nd connection");
            entityManager2.persist(model);
        });
    }

    private String getValue(EntityManager entityManager) {
        entityManager.clear();
        TestModel model = entityManager.find(TestModel.class, 1);
        return model.getValue();
    }
}
