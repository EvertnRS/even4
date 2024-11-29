package br.upe.ui;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseSmokeTest {

    private static EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("test-persistence-unit");
    }

    @Test
    public void testDatabaseConnection() {
        EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
        assertNotNull(entityManagerFactory, "EntityManagerFactory não deve ser nulo");

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            assertNotNull(entityManager, "EntityManager não deve ser nulo");

            entityManager.getTransaction().begin();
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            fail("Erro ao conectar ou realizar uma operação no banco: " + e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            if (entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        }
    }
}
