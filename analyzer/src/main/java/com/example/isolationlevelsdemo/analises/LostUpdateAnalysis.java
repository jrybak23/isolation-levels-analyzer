package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.Result;
import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;
import java.util.Optional;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransactionAndReturnValue;

@Component
@Slf4j
public class LostUpdateAnalysis implements Analysis {
    @Override
    public String getEffectName() {
        return "Lost Update";
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

        return runInTransactionAndReturnValue(entityManagerFactory, entityManager -> {
            String value = getValue(entityManager);
            return value.equals("initial value updated by 1st transaction");
        });
    }

    private Optional<Result> runFirstTransaction(EntityManagerFactory entityManagerFactory) {
        return runInTransactionAndReturnValue(entityManagerFactory, entityManager1 -> {
            String initialValue = getValue(entityManager1);
            if (!initialValue.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                runSecondTransaction(entityManagerFactory);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getEffectName() + " wasn't reproduced.", e);
                return Optional.of(Result.NOT_REPRODUCED);
            }

            String value = getValue(entityManager1);
            TestModel testModel = new TestModel();
            testModel.setId(1);
            testModel.setValue(value + " updated by 1st transaction");
            entityManager1.merge(testModel);

            return Optional.empty();
        });
    }

    private void runSecondTransaction(EntityManagerFactory entityManagerFactory) {
        runInTransaction(entityManagerFactory, entityManager2 -> {
            String value = getValue(entityManager2);
            if (!value.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            TestModel testModel = new TestModel();
            testModel.setId(1);
            testModel.setValue(value + " updated by 2nd transaction");
            entityManager2.merge(testModel);
        });
    }

    private String getValue(EntityManager entityManager) {
        entityManager.clear();
        TestModel model = entityManager.find(TestModel.class, 1);
        return model.getValue();
    }
}
