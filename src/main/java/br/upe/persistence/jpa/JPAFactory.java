package br.upe.persistence.jpa;

import java.util.logging.Logger;

public class JPAFactory {
    private static final JPAProvider jpaProvider;
    private static final Logger LOGGER = Logger.getLogger(JPAFactory.class.getName());

    private JPAFactory(){
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada.");
    }

    static {
        String environment = EnvConfig.get("ENVIRONMENT");
        LOGGER.warning(environment);
        if ("test".equalsIgnoreCase(environment)) {
            jpaProvider = new JPATestUtils();
        } else {
            jpaProvider = new JPAUtils();
        }
    }

    public static JPAProvider getJPAProvider() {
        return jpaProvider;
    }
}