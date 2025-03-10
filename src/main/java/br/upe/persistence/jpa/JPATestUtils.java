// src/main/java/br/upe/persistence/jpa/JPATestUtils.java
package br.upe.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPATestUtils implements JPAProvider {
    private static final EntityManagerFactory entityManagerFactory;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", EnvConfig.get("DB_TEST_URL"));
        properties.put("jakarta.persistence.jdbc.user", EnvConfig.get("DB_TEST_USER"));
        properties.put("jakarta.persistence.jdbc.password", EnvConfig.get("DB_TEST_PASSWORD"));

        entityManagerFactory = Persistence.createEntityManagerFactory("even4-test", properties);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}