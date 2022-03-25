package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.model.TestModel;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransactionAndReturnValue;

@Component
public class NonRepeatableReadAnalysis implements Analysis {
    @Override
    public String getEffectName() {
        return "Non Repeatable Read";
    }

    @Override
    public boolean isReproducible(EntityManagerFactory entityManagerFactory) {
        return runInTransactionAndReturnValue(entityManagerFactory, entityManager1 -> {
            String initialValue = getValue(entityManager1);
            if (!initialValue.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                runInTransaction(entityManagerFactory, entityManager2 -> {
                    String value = getValue(entityManager2);
                    if (!value.equals(INITIAL_VALUE)) {
                        throw new RuntimeException();
                    }

                    TestModel testModel = new TestModel();
                    testModel.setId(1);
                    testModel.setValue("changed by 2nd transaction");
                    entityManager2.merge(testModel);
                });
            } catch (RollbackException e) {
                return false;
            }

            String value = getValue(entityManager1);
            if (value.equals("changed by 2nd transaction")) {
                return true;
            } else if (value.equals("initial value")) {
                return false;
            } else {
                throw new RuntimeException("Unexpected value " + value);
            }
        });
    }

    private String getValue(EntityManager entityManager) {
        entityManager.clear();
        TestModel model = entityManager.find(TestModel.class, 1);
        return model.getValue();
    }
}
