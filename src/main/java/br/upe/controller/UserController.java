package br.upe.controller;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import br.upe.persistence.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserController implements Controller {
    private HashMap<String, Persistence> userHashMap;
    private Persistence userLog;

    public UserController() {
        this.read();
    }

    public HashMap<String, Persistence> getUserHashMap() {
        return userHashMap;
    }

    public void setUserHashMap(HashMap<String, Persistence> userHashMap) {
        this.userHashMap = userHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "email" -> data = this.userLog.getData("email");
                case "cpf" -> data = this.userLog.getData("cpf");
                case "id" -> data = this.userLog.getData("id");
                default -> throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Informação não existe ou é restrita");
        }
        return data;
    }


    @Override
    public void SubmitArticleController(String string) {

    }

    public void setUserLog(Persistence userLog) {
        this.userLog = userLog;
    }

    @Override
    public void create(Object... params) {
        if (params.length < 2) {
            System.out.println("Só pode ter 2 parametros");
        }

        String email = (String) params[0];
        String cpf = (String) params[1];
        Persistence userPersistence = new User();
        try {
            for (Map.Entry<String, Persistence> entry : this.userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                if (user.getData("email").equals(email)) {
                    throw new IOException();
                }
            }

            userPersistence.create(email, cpf);

        } catch (IOException exception) {
            System.out.println("Email já cadastrado");
        }
    }

    @Override
    public void update(Object... params) {
        if (params.length < 2) {
            System.out.println("Só pode ter 2 parametros");
        }
        Persistence userPersistence = new User();
        String email = (String) params[0];
        String cpf = (String) params[1];
        Persistence user = userHashMap.get(this.userLog.getData("id"));
        if (user == null) {
            System.out.println("Usuário não encontrado");
            return;
        }
        user.setData("email",email);
        user.setData("cpf", cpf);
        userHashMap.put(this.userLog.getData("id"), user);

        userPersistence.update(userHashMap);
    }

    @Override
    public void read() {
        Persistence userPersistence = new User();
        this.userHashMap = userPersistence.read();
    }

    @Override
    public void delete(Object... params) {
        if (((String) params[1]).equals("id")) {
            Iterator<Map.Entry<String, Persistence>> iterator = userHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence user = entry.getValue();
                if (user.getData("id").equals((String) params[0])) {
                    iterator.remove();
                }
            }
            Persistence userPersistence = new User();
            userPersistence.delete(userHashMap);
        }
    }

    @Override
    public boolean list(String idowner) {
        return false;
    }

    @Override
    public void show(Object... params) {

    }

    public boolean loginValidate(String email, String cpf) {
        for (Map.Entry<String, Persistence> entry : this.userHashMap.entrySet()) {
            Persistence user = entry.getValue();
            if (user.getData("email").equals(email) && user.getData("cpf").equals(cpf)) {
                setUserLog(user);
                return true;
            }
        }
        return false;
    }


}
