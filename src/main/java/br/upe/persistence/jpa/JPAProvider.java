package br.upe.persistence.jpa;

import jakarta.persistence.EntityManager;

public interface JPAProvider {
    EntityManager getEntityManager();
}