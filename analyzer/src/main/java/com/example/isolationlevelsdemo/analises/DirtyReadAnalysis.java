package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.config.AppProperties;
import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTheFirstTransactionAndReturnResult;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTheSecondTransactionAndReturnResult;

@Component
@Slf4j
public class DirtyReadAnalysis implements Analysis {

    @Autowired
    private AppProperties appProperties;

    @Override
    public String getPhenomenaName() {
        return "Dirty Read";
    }

    @Override
    public boolean isReproducible(EntityManagerFactory entityManagerFactory) {
        try {
            return runFirstTransaction(entityManagerFactory);
        } catch (RollbackException e) {
            log.error("1st transaction is failed to commit. So " + getPhenomenaName() + " wasn't reproduced.", e);
            return false;
        }

    }

    private boolean runFirstTransaction(EntityManagerFactory entityManagerFactory) {
        log.info("Starting the 1st transaction to perform " + getPhenomenaName() + " analysis.");
        return runInTheFirstTransactionAndReturnResult(entityManagerFactory, entityManager1 -> {
            String initialValue = getValue(entityManager1);
            if (!initialValue.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }
            try {
                log.info("Starting the 2nd transaction to perform {} analysis.", getPhenomenaName());
                return runSecondTransaction(entityManagerFactory, entityManager1);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getPhenomenaName() + " wasn't reproduced.", e);
                return false;
            }
        });
    }

    private boolean runSecondTransaction(EntityManagerFactory entityManagerFactory, EntityManager entityManager1) {
        return runInTheSecondTransactionAndReturnResult(entityManagerFactory, entityManager2 -> {
            String initialValue2 = getValue(entityManager1);
            if (!initialValue2.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                entityManager2.createQuery("update TestModel t set t.value = :value where t.id = 1")
                        .setParameter("value", "changed by 2nd transaction")
                        .setHint("jakarta.persistence.query.timeout", appProperties.getLockTimeout())
                        .executeUpdate();
            } catch (QueryTimeoutException e) {
                log.info("Lock timeout while updating using 2nd transaction to check " + getPhenomenaName() + ". So it's not reproduced.", e);
                return false;
            } catch (PessimisticLockException e) {
                return false;
            }

            String value;
            try {
                value = getValue(entityManager1);
            } catch (QueryTimeoutException e) {
                log.info("Lock timeout while reading using 1st transaction to check " + getPhenomenaName() + ". So it's not reproduced.", e);
                return false;
            }
            return value.equals("changed by 2nd transaction");
        });
    }

    private String getValue(EntityManager entityManager) {
        entityManager.clear();
        TestModel model = (TestModel) entityManager.createQuery("from TestModel t where t.id = 1")
                .setHint("jakarta.persistence.query.timeout", appProperties.getLockTimeout())
                .getResultList().get(0);
        return model.getValue();
    }
}
