package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.Result;
import com.example.isolationlevelsdemo.config.AppProperties;
import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;

import java.util.Optional;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.*;

@Component
@Slf4j
public class NonRepeatableReadAnalysis implements Analysis {

    @Autowired
    private AppProperties appProperties;

    @Override
    public String getPhenomenaName() {
        return "Non Repeatable Read";
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
        return runInTheFirstTransactionAndReturnResult(entityManagerFactory, entityManager1 -> {
            String initialValue = getValue(entityManager1);
            if (!initialValue.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            Optional<Result> result;
            try {
                result = runSecondTransaction(entityManagerFactory);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getPhenomenaName() + " wasn't reproduced.", e);
                return false;
            }

            if (result.isPresent()) {
                return result.get().equals(Result.REPRODUCED);
            }

            String value = getValue(entityManager1);
            if (value.equals("initial value changed by 2nd transaction")) {
                return true;
            } else if (value.equals("initial value")) {
                return false;
            } else {
                throw new RuntimeException("Unexpected value " + value);
            }
        });
    }

    private Optional<Result> runSecondTransaction(EntityManagerFactory entityManagerFactory) {
        return runInTheSecondTransactionAndReturnResult(entityManagerFactory, entityManager2 -> {
            String value = getValue(entityManager2);
            if (!value.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                entityManager2.createQuery("update TestModel t set t.value = :value where t.id = 1")
                        .setParameter("value", value + " changed by 2nd transaction")
                        .setHint("jakarta.persistence.query.timeout", appProperties.getLockTimeout())
                        .executeUpdate();
            } catch (QueryTimeoutException e) {
                log.info("Lock timeout while updating using 2nd transaction to check " + getPhenomenaName() + ". So it's not reproduced.", e);
                return Optional.of(Result.NOT_REPRODUCED);
            }

            return Optional.empty();
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
