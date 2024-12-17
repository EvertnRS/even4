package br.upe.utils;

public class EnvConfig {
    private EnvConfig() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    // Método para acessar variáveis de ambiente diretamente
    public static String get(String key) {
        return System.getenv(key);  // Acessa diretamente o ambiente do sistema
    }
}
