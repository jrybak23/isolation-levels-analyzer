package com.example.isolationlevelsdemo.analises;

import javax.persistence.EntityManagerFactory;

public interface Analysis {
    String getPhenomenaName();

    boolean isReproducible(EntityManagerFactory entityManagerFactory);
}
