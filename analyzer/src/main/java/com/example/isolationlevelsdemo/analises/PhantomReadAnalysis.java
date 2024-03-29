package com.example.isolationlevelsdemo.analises;

import com.example.isolationlevelsdemo.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.isolationlevelsdemo.Constants.INITIAL_VALUE;
import static com.example.isolationlevelsdemo.TransactionUtils.*;

@Component
@Slf4j
public class PhantomReadAnalysis implements Analysis {
    @Override
    public String getPhenomenaName() {
        return "Phantom Read";
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

    private Boolean runFirstTransaction(EntityManagerFactory entityManagerFactory) {
        return runInTheFirstTransactionAndReturnResult(entityManagerFactory, entityManager1 -> {
            String value1 = getValue(entityManager1);
            if (!value1.equals(INITIAL_VALUE)) {
                throw new RuntimeException();
            }

            try {
                runSecondTransaction(entityManagerFactory);
            } catch (RollbackException e) {
                log.error("2nd transaction is failed to commit. So " + getPhenomenaName() + " wasn't reproduced.", e);
                return false;
            }

            Set<String> values = getValues(entityManager1);
            if (values.equals(Set.of(INITIAL_VALUE, "value inserted by 2-nd connection"))) {
                return true;
            } else if (values.equals(Set.of(INITIAL_VALUE))) {
                return false;
            } else {
                throw new RuntimeException("Unexpected values " + values);
            }
        });
    }

    private void runSecondTransaction(EntityManagerFactory entityManagerFactory) {
        runInTheSecondTransaction(entityManagerFactory, entityManager2 -> {
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

    private Set<String> getValues(EntityManager entityManager) {
        @SuppressWarnings("unchecked")
        List<TestModel> models = (List<TestModel>) entityManager.createQuery("from TestModel t")
                .getResultList();
        return models.stream()
                .map(TestModel::getValue)
                .collect(Collectors.toSet());
    }
}
