package br.upe.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private EnvConfig() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}
