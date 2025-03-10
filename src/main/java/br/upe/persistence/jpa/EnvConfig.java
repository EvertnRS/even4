package br.upe.persistence.jpa;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private EnvConfig() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    private static final Dotenv dotenv;

    static {
        String environment = System.getProperty("ENVIRONMENT");
        if ("test".equalsIgnoreCase(environment)) {
            dotenv = Dotenv.configure().filename("test.env").load();
        } else {
            dotenv = Dotenv.configure().filename(".env").load();
        }
    }

    public static String get(String key) {
        return dotenv.get(key);
    }
}