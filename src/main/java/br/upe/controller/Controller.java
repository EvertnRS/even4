package br.upe.controller;

import java.io.IOException;

public interface Controller {
    void create(Object... params) throws IOException;
    void update(Object... params) throws IOException;
    void read() throws IOException;
    void delete(Object... params) throws IOException;
    boolean list(String idowner) throws IOException;
    void show(Object... params) throws IOException;
    boolean loginValidate(String email, String cpf);
    String getData(String dataToGet);
}
