package br.upe.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtils {
    private JPAUtils() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }
    private static final EntityManagerFactory entityManagerFactory;

    static {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", EnvConfig.get("DB_URL"));
        properties.put("jakarta.persistence.jdbc.user", EnvConfig.get("DB_USER"));
        properties.put("jakarta.persistence.jdbc.password", EnvConfig.get("DB_PASSWORD"));

        entityManagerFactory = Persistence.createEntityManagerFactory("even4", properties);
    }

    public static EntityManager getEntityManagerFactory() {
        return entityManagerFactory.createEntityManager();
    }
}
