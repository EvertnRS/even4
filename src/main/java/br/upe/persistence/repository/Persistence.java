package br.upe.persistence.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public interface Persistence {
    void create(Object... params);

    void delete(Object... params) throws IOException;

    void update(Object... params) throws IOException;

    void setData(String dataToSet, Object data);

    Object getData(String dataToGet);

    Object getData(UUID id, String dataToGet);

    void setData(UUID eventId, String dataToSet, Object data);

    HashMap<UUID, Persistence> read() throws IOException;

    HashMap<UUID, Persistence> read(Object... params);

    boolean loginValidate(String email, String password);
}
