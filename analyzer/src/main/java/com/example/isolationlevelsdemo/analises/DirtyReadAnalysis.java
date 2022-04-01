package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PessimisticLockException;
import javax.persistence.RollbackException;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransactionAndReturnValue;

@Component
@Slf4j
public class DirtyReadAnalysis implements Analysis {
    @Override
    public String getEffectName() {
        return "Dirty Read";
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
        return runInTransactionAndReturnValue(entityManagerFactory, entityManager1 -> {
            String initialValue = getValue(entityManager1);
            if (!initialValue.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }
            try {
                return runSecondTransaction(entityManagerFactory, entityManager1);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getEffectName() + " wasn't reproduced.", e);
                return false;
            }
        });
    }

    private boolean runSecondTransaction(EntityManagerFactory entityManagerFactory, EntityManager entityManager1) {
        return runInTransactionAndReturnValue(entityManagerFactory, entityManager2 -> {
            String initialValue2 = getValue(entityManager1);
            if (!initialValue2.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                TestModel testModel = new TestModel();
                testModel.setId(1);
                testModel.setValue("changed by 2nd transaction");
                entityManager2.merge(testModel);
                entityManager2.flush();
            } catch (PessimisticLockException e) {
                return false;
            }

            String value = getValue(entityManager1);
            return value.equals("changed by 2nd transaction");
        });
    }

    private String getValue(EntityManager entityManager) {
        entityManager.clear();
        TestModel model = entityManager.find(TestModel.class, 1);
        return model.getValue();
    }
}
