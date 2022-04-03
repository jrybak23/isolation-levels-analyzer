package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.TransactionUtils;
import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.*;

@Component
@Slf4j
public class SerializationAnomalyAnalysis implements Analysis {
    @Override
    public String getEffectName() {
        return "Serialization Anomaly";
    }

    @Override
    public boolean isReproducible(EntityManagerFactory entityManagerFactory) {
        try {
            return runFirstTransaction(entityManagerFactory);
        } catch (RollbackException e) {
            log.error("1st transaction is failed to commit. So " + getEffectName() + " wasn't reproduced.", e);
            return false;
        }
    }

    private boolean runFirstTransaction(EntityManagerFactory entityManagerFactory) {
        return runInTheFirstTransactionAndReturnResult(entityManagerFactory, entityManager1 -> {
            String value1 = getValue(entityManager1);
            if (!value1.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                runSecondTransaction(entityManagerFactory);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getEffectName() + " wasn't reproduced.", e);
                return false;
            }

            TestModel model = new TestModel();
            model.setId(2);
            model.setValue("value inserted by 1-nd connection");
            entityManager1.persist(model);

            try {
                return !entityManager1.createQuery("from TestModel t where t.value = 'value inserted by 2-nd connection'")
                        .getResultList()
                        .isEmpty();
            } catch (RollbackException | OptimisticLockException e) {
                return false;
            }
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
