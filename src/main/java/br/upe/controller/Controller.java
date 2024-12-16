package br.upe.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface Controller {
    Object[] create(Object... params) throws IOException;

    boolean update(Object... params) throws IOException;

    boolean delete(Object... params) throws IOException;

    <T> List<T> getAll();

    <T> List<T> list(Object... params) throws IOException;

    <T> List<T> getEventArticles(UUID eventId);

    Object[] isExist(Object... params) throws IOException;

    String getData(String dataToGet);

}
