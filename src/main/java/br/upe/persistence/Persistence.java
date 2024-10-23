package br.upe.persistence;

import java.io.IOException;
import java.util.HashMap;

public interface  Persistence {
    String getName();

    void setName(String email);

    void create(Object... params);
    void delete(Object... params) throws IOException;
    void update(Object... params) throws IOException;

    String getData(String dataToGet);
    void setData(String dataToSet, String data);
    HashMap<String, Persistence> read() throws IOException;
    HashMap<String, Persistence> read(Object... params);
}
