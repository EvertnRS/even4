package br.upe.persistence.repository;

import java.io.IOException;
import java.util.UUID;

public interface Persistence {
    Object[] create(Object... params);

    boolean delete(Object... params) throws IOException;

    boolean update(Object... params) throws IOException;

    Object getData(String dataToGet);

    Object getData(UUID id, String dataToGet);

    void setData(UUID eventId, String dataToSet, Object data);

    Object[] isExist(Object... params) throws IOException;
}
