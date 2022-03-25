package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.model.TestModel;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;
import static com.example.isolationlevelsdemo.TransactionUtils.runInTransactionAndReturnValue;

@Component
public class SerializationAnomalyAnalysis implements Analysis {
    @Override
    public String getEffectName() {
        return "Serialization Anomaly";
    }

    @Override
    public boolean isReproducible(EntityManagerFactory entityManagerFactory) {
        try {
            return runInTransactionAndReturnValue(entityManagerFactory, entityManager1 -> {
                String value1 = getValue(entityManager1);
                if (!value1.equals(INITIAL_VALUE)) {
                    throw new RuntimeException();
                }

                try {
                    runInTransaction(entityManagerFactory, entityManager2 -> {
                        String value2 = getValue(entityManager2);
                        if (!value2.equals(INITIAL_VALUE)) {
                            throw new RuntimeException();
                        }

                        TestModel model = new TestModel();
                        model.setId(3);
                        model.setValue("value inserted by 2-nd connection");
                        entityManager2.persist(model);
                    });
                } catch (RollbackException e) {
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
        } catch (RollbackException e) {
            return false;
        }
    }

    private String getValue(EntityManager entityManager) {
        entityManager.clear();
        TestModel model = entityManager.find(TestModel.class, 1);
        return model.getValue();
    }
}
