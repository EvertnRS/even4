package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.User;

import java.io.FileNotFoundException;

public interface Controller {
    void create(Object... params) throws FileNotFoundException;
    void update(Object... params) throws FileNotFoundException;
    void read();
    void delete(Object... params);
    boolean list(String idowner);
    void show(Object... params);
    boolean loginValidate(String email, String cpf);
    String getData(String dataToGet);


    void SubmitArticleController(String string);
}
