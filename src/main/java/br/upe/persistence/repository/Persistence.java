package br.upe.persistence.repository;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface  Persistence {
    Object[] create(Object... params);
    boolean delete(Object... params) throws IOException;
    boolean update(Object... params) throws IOException;

    void setData(String dataToSet, Object data);
    Object getData(String dataToGet);

    Object getData(UUID id, String dataToGet);

    void setData(UUID eventId, String dataToSet, Object data);
    HashMap<UUID, Persistence> read() throws IOException;
    HashMap<UUID, Persistence> read(Object... params);
    Object[] isExist(Object... params) throws IOException;
}
