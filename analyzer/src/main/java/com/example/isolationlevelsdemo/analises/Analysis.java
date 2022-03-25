package com.example.isolationlevelsdemo.analises;

import javax.persistence.EntityManagerFactory;

public interface Analysis {
    String getEffectName();

    boolean isReproducible(EntityManagerFactory entityManagerFactory);
}
