package br.upe.persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public interface  Persistence {
    void create(Object... params);
    void delete(Object... params) throws IOException;
    void update(Object... params) throws IOException;

    Object getData(String dataToGet);
    void setData(String dataToSet, Object data);
    HashMap<UUID, Persistence> read() throws IOException;
    HashMap<UUID, Persistence> read(Object... params);
}
