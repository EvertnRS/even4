package br.upe.persistence.repository;

import java.io.IOException;
import java.util.UUID;

public interface Persistence {
    void create(Object... params);

    void delete(Object... params) throws IOException;

    void update(Object... params) throws IOException;

    Object getData(String dataToGet);

    Object getData(UUID id, String dataToGet);

    void setData(UUID eventId, String dataToSet, Object data);

    boolean loginValidate(String email, String password);
}
