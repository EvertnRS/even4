package br.upe.persistence;

import java.util.HashMap;

public interface  Persistence {
    String getName();

    void setName(String email);

    void create(Object... params);
    void delete(Object... params);
    void update(Object... params);

    String getData(String dataToGet);
    void setData(String dataToSet, String data);
    HashMap<String, Persistence> read();
    HashMap<String, Persistence> read(Object... params);
}
