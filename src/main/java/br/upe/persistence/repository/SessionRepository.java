package br.upe.persistence.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class SessionRepository implements Persistence{
    private static SessionRepository instance;

    public SessionRepository() {
    }

    public static SessionRepository getInstance() {
        if (instance == null) {
            synchronized (SessionRepository.class) {
                if (instance == null) {
                    instance = new SessionRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void create(Object... params) {

    }

    @Override
    public void delete(Object... params) throws IOException {

    }

    @Override
    public void update(Object... params) throws IOException {

    }

    @Override
    public void setData(String dataToSet, Object data) {

    }

    @Override
    public Object getData(String dataToGet) {
        return null;
    }

    @Override
    public Object getData(UUID id, String dataToGet) {
        return null;
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {

    }

    @Override
    public HashMap<UUID, Persistence> read() throws IOException {
        return null;
    }

    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        return null;
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return false;
    }
}
