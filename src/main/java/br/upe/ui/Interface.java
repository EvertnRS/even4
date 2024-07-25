package br.upe.ui;

import br.upe.controller.*;

import java.io.FileNotFoundException;
import java.util.Scanner;

import static br.upe.ui.Validation.*;

public class Interface {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Bem-Vindo ao Even4");
        try (Scanner sc = new Scanner(System.in)) {
            int option;
            do {
                printMainMenu();
                option = getOption(sc);

                switch (option) {
                    case 1:
                        loginFlow(sc);
                        break;
                    case 2:
                        signup(sc);
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } while (option != 0);
        }
    }

    private static void printMainMenu() {
        System.out.println("[1] - Login");
        System.out.println("[2] - Cadastrar");
        System.out.println("[0] - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int getOption(Scanner sc) {
        if (sc.hasNextInt()) {
            int option = sc.nextInt();
            sc.nextLine();
            return option;
        } else {
            System.out.println("Entrada inválida. Por favor, insira um número.");
            sc.nextLine();
            return -1;
        }
    }

    private static void loginFlow(Scanner sc) throws FileNotFoundException {
        Object[] results = login(sc);
        boolean isLog = (boolean) results[0];
        Controller userLogin = (Controller) results[1];

        if (isLog) {
            userMenu(sc, userLogin);
        }
    }

    private static void userMenu(Scanner sc, Controller userLogin) throws FileNotFoundException {
        int option;
        do {
            printUserMenu();
            option = getOption(sc);

            Controller ec = new EventController();
            Controller sec = new SubEventController();
            Controller ses = new SessionController();

            switch (option) {
                case 1:
                    createFlow(sc, ec, sec, ses, userLogin);
                    break;
                case 2:
                    alterFlow(sc, ec, sec, ses, userLogin);
                    break;
                case 3:
                    enterFlow(sc, ec, sec, ses, userLogin);
                    break;
                case 4:
                    if (setup(sc, userLogin)) {
                        option = 0;
                    }
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void printUserMenu() {
        System.out.println("[1] - Criar");
        System.out.println("[2] - Alterar");
        System.out.println("[3] - Acessar Evento");
        System.out.println("[4] - Perfil");
        System.out.println("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private static void createFlow(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) throws FileNotFoundException {
        int option;
        do {
            System.out.println("Escolha o que deseja criar:");
            System.out.println("[1] - Evento");
            System.out.println("[2] - SubEvento");
            System.out.println("[3] - Sessão");
            System.out.println("[0] - Voltar");
            option = getOption(sc);

            switch (option) {
                case 1:
                    createEvent(sc, ec, userLogin);
                    break;
                case 2:
                    createSubEvent(sc, ec, sec, userLogin);
                    break;
                case 3:
                    createSession(sc, ec, sec, ses, userLogin);
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void alterFlow(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) throws FileNotFoundException {
        int option;
        do {
            System.out.println("Escolha o que deseja alterar:");
            System.out.println("[1] - Evento");
            System.out.println("[2] - SubEvento");
            System.out.println("[3] - Sessão");
            System.out.println("[0] - Voltar");
            option = getOption(sc);

            switch (option) {
                case 1:
                    alterEvent(sc, ec, userLogin);
                    break;
                case 2:
                    alterSubEvent(sc, sec, userLogin);
                    break;
                case 3:
                    alterSession(sc, ses, userLogin);
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void enterFlow(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) {
        int option;
        do {
            System.out.println("Escolha a opção desejada:");
            System.out.println("[1] - Listar Inscrições");
            System.out.println("[2] - Inscrever-se");
            System.out.println("[3] - Submeter Artigo");
            System.out.println("[0] - Voltar");
            option = getOption(sc);

            switch (option) {
                case 1:

                    break;
                case 2:
                    EventInscription(sc, ec, sec, ses, userLogin);
                    break;
                case 3:

                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void EventInscription(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) {
        ec.show(userLogin.getData("id"));
        sec.show(userLogin.getData("id"));
        System.out.println("Digite o evento que você deseja entrar: ");
        String event = sc.nextLine();
        enterEvent(sc, event, ses, userLogin);
    }

    private static void enterEvent(Scanner sc, String event, Controller ses, Controller userLogin) {
        ses.show(userLogin.getData("id"), event);
        int option;
        do {
            System.out.println("[1] - Entrar no evento");
            System.out.println("[0] - Voltar");
            option = getOption(sc);
            switch (option) {
                case 1:

                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
            }
        } while (option != 0);


    }

    private static void createEvent(Scanner sc, Controller ec, Controller userLogin) throws FileNotFoundException {
        System.out.println("Digite o nome do Evento: ");
        String nameEvent = sc.nextLine();
        System.out.println("Data do Evento: ");
        String dateEvent = sc.nextLine();
        System.out.println("Descrição do Evento: ");
        String descriptionEvent = sc.nextLine();
        System.out.println("Local do Evento: ");
        String locationEvent = sc.nextLine();
        if (isValidDate(dateEvent)){
            ec.create(nameEvent.trim(), dateEvent, descriptionEvent, locationEvent, userLogin.getData("id"));
        }
    }

    private static void alterEvent(Scanner sc, Controller ec, Controller userLogin) throws FileNotFoundException {
        boolean isNull = ec.list(userLogin.getData("id"));
        if (isNull) {
            return;
        }
        int optionEvent;
        do {
            System.out.println("Selecione um Evento: ");
            String changed = sc.nextLine();
            printAlterEventMenu();

            optionEvent = getOption(sc);
            switch (optionEvent) {
                case 1:
                    ec.delete(changed, "name", userLogin.getData("id"));
                    optionEvent = 0;
                    break;
                case 2:
                    updateEvent(sc, ec, changed, userLogin.getData("id"));
                    optionEvent = 0;
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (optionEvent != 0);
    }

    private static void printAlterEventMenu() {
        System.out.println("[1] - Apagar Evento ");
        System.out.println("[2] - Alterar Evento ");
        System.out.println("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private static void updateEvent(Scanner sc, Controller ec, String changed, String userId) throws FileNotFoundException {
        System.out.println("Digite o novo nome do Evento: ");
        String newName = sc.nextLine();
        System.out.println("Nova Data do Evento: ");
        String newDate = sc.nextLine();
        System.out.println("Nova Descrição do Evento: ");
        String newDescription = sc.nextLine();
        System.out.println("Novo Local do Evento: ");
        String newLocation = sc.nextLine();
        if (isValidDate(newDate)){
            ec.update(changed.trim(), newName.trim(), newDate, newDescription, newLocation, userId);
        }
    }

    private static void createSubEvent(Scanner sc, Controller ec, Controller sec, Controller userLogin) throws FileNotFoundException {
        boolean isNull = ec.list(userLogin.getData("id"));
        if (isNull) {
            return;
        }
        System.out.println("Nome do Evento Pai: ");
        String fatherEvent = sc.nextLine();
        System.out.println("Digite o nome do SubEvento: ");
        String nameSubEvent = sc.nextLine();
        System.out.println("Data do SubEvento: ");
        String dateSubEvent = sc.nextLine();
        System.out.println("Descrição do SubEvento: ");
        String descriptionSubEvent = sc.nextLine();
        System.out.println("Local do SubEvento: ");
        String locationSubEvent = sc.nextLine();
        if (isValidDate(dateSubEvent)){
            sec.create(fatherEvent.trim(), nameSubEvent.trim(), dateSubEvent, descriptionSubEvent, locationSubEvent, userLogin.getData("id"));
        }
    }

    private static void alterSubEvent(Scanner sc, Controller sec, Controller userLogin) throws FileNotFoundException {
        boolean isNull = sec.list(userLogin.getData("id"));
        if (isNull) {
            return;
        }
        int optionSubEvent;
        do {
            System.out.println("Selecione um SubEvento: ");
            String subChanged = sc.nextLine();
            printAlterSubEventMenu();

            optionSubEvent = getOption(sc);
            switch (optionSubEvent) {
                case 1:
                    sec.delete(subChanged, "name", userLogin.getData("id"));
                    optionSubEvent = 0;
                    break;
                case 2:
                    updateSubEvent(sc, sec, subChanged, userLogin.getData("id"));
                    optionSubEvent = 0;
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (optionSubEvent != 0);
    }

    private static void printAlterSubEventMenu() {
        System.out.println("[1] - Apagar SubEvento ");
        System.out.println("[2] - Alterar SubEvento ");
        System.out.println("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private static void updateSubEvent(Scanner sc, Controller sec, String subChanged, String userId) throws FileNotFoundException {
        System.out.println("Digite o novo nome do SubEvento: ");
        String newName = sc.nextLine();
        System.out.println("Nova Data do SubEvento: ");
        String newDate = sc.nextLine();
        System.out.println("Nova Descrição do SubEvento: ");
        String newDescription = sc.nextLine();
        System.out.println("Novo Local do SubEvento: ");
        String newLocation = sc.nextLine();
        if (isValidDate(newDate)){
            sec.update(subChanged.trim(), newName.trim(), newDate, newDescription, newLocation, userId);
        }
    }

    private static void createSession(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) throws FileNotFoundException {
        boolean isNull = ec.list(userLogin.getData("id"));
        if (isNull) {
            return;
        }
        System.out.println("Nome do Evento Pai: ");
        String fatherEvent = sc.nextLine();
        System.out.println("Digite o nome da Sessão: ");
        String nameSession = sc.nextLine();
        System.out.println("Data da Sessão: ");
        String dateSession = sc.nextLine();
        System.out.println("Descrição da Sessão: ");
        String descriptionSession = sc.nextLine();
        System.out.println("Local da Sessão: ");
        String locationSession = sc.nextLine();
        System.out.println("Início da Sessão: ");
        String startTime = sc.nextLine();
        System.out.println("Término da Sessão: ");
        String endTime = sc.nextLine();
        if (isValidDate(dateSession) && areValidTimes(startTime, endTime)){
            ses.create(fatherEvent.trim(), nameSession.trim(), dateSession, descriptionSession, locationSession, startTime, endTime, userLogin.getData("id"));
        }
    }

    private static void alterSession(Scanner sc, Controller ses, Controller userLogin) throws FileNotFoundException {
        boolean isNull = ses.list(userLogin.getData("id"));
        if (isNull) {
            return;
        }
        int optionSession;
        do {
            System.out.println("Selecione uma Sessão: ");
            String sesChanged = sc.nextLine();
            printAlterSessionMenu();

            optionSession = getOption(sc);
            switch (optionSession) {
                case 1:
                    ses.delete(sesChanged, "name", userLogin.getData("id"));
                    optionSession = 0;
                    break;
                case 2:
                    updateSession(sc, ses, sesChanged, userLogin.getData("id"));
                    optionSession = 0;
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (optionSession != 0);
    }

    private static void updateSession(Scanner sc, Controller ses, String subChanged, String userId) throws FileNotFoundException {
        System.out.println("Digite o novo nome da Sessão: ");
        String newName = sc.nextLine();
        System.out.println("Nova Data da Sessão: ");
        String newDate = sc.nextLine();
        System.out.println("Nova Descrição da Sessão: ");
        String newDescription = sc.nextLine();
        System.out.println("Novo Local da Sessão: ");
        String newLocation = sc.nextLine();
        System.out.println("Novo Início da Sessão: ");
        String newStartTime = sc.nextLine();
        System.out.println("Novo Término da Sessão: ");
        String newEndTime = sc.nextLine();
        if (isValidDate(newDate) && areValidTimes(newStartTime, newEndTime)){
            ses.update(subChanged.trim(), newName.trim(), newDate, newDescription, newLocation, newStartTime, newEndTime, userId);
        }
    }

    private static void printAlterSessionMenu() {
        System.out.println("[1] - Apagar Sessão ");
        System.out.println("[2] - Alterar Sessão ");
        System.out.println("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    public static Object[] login(Scanner sc) {
        Controller userController = new UserController();
        System.out.println("Digite seu email:");
        boolean isLog = false;
        if (sc.hasNextLine()) {
            String email = sc.nextLine();
            System.out.println("Digite seu cpf:");
            String cpf = sc.nextLine();
            if (userController.loginValidate(email, cpf)) {
                System.out.println("Login Realizado com Sucesso");
                isLog = true;
            } else {
                System.out.println("Login ou senha incorreto");
            }
        } else {
            System.out.println("Erro ao ler email.");
        }
        return new Object[]{isLog, userController};
    }

    public static void signup(Scanner sc) throws FileNotFoundException {
        Controller userController = new UserController();
        System.out.println("Cadastre seu email:");
        if (sc.hasNextLine()) {
            String email = sc.nextLine();
            if (!isValidEmail(email) || email.isEmpty() ) {
                System.out.println("Email inválido. Tente novamente.");
                main(new String[]{"a", "b"});
                return;
            }
            System.out.println("Digite seu cpf:");
            if (sc.hasNextLine()) {
                String cpf = sc.nextLine();
                if (!isValidCPF(cpf) || cpf.isEmpty()) {
                    System.out.println("CPF inválido. Tente novamente");
                    main(new String[]{"a", "b"});
                    return;
                }
                userController.create(email.trim(), cpf.trim());
            } else {
                System.out.println("Erro ao ler cpf.");
            }
        } else {
            System.out.println("Erro ao ler email.");
        }
    }

    public static boolean setup(Scanner sc, Controller userLogin) throws FileNotFoundException {
        int option;
        boolean isRemoved = false;
        do {
            printUserProfile(userLogin);
            printSetupMenu();

            option = getOption(sc);
            switch (option) {
                case 1:
                    updateUserAccount(sc, userLogin);
                    break;
                case 2:
                    isRemoved = deleteUserAccount(sc, userLogin);
                    if (isRemoved) {
                        String[] args = {"a", "b"};
                        main(args);
                    }
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
        return isRemoved;
    }

    private static void printUserProfile(Controller userLogin) {
        System.out.println("Email: " + userLogin.getData("email"));
        System.out.println("CPF: " + userLogin.getData("cpf"));
    }

    private static void printSetupMenu() {
        System.out.println("[1] - Atualizar conta");
        System.out.println("[2] - Deletar conta");
        System.out.println("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private static void updateUserAccount(Scanner sc, Controller userLogin) throws FileNotFoundException {
        int option;
        System.out.println("O que você deseja atualizar?");
        System.out.println("[1] - email");
        System.out.println("[2] - cpf");
        System.out.println("[0] - voltar");
        option = getOption(sc);

        switch (option) {
            case 1:
                System.out.println("Digite o novo email:");
                if (sc.hasNextLine()) {
                    String email = sc.nextLine();
                    userLogin.update(email, userLogin.getData("cpf"));
                } else {
                    System.out.println("Erro ao ler email.");
                }
                break;
            case 2:
                System.out.println("Digite o novo cpf:");
                if (sc.hasNextLine()) {
                    String cpf = sc.nextLine();
                    userLogin.update(userLogin.getData("email"), cpf);
                } else {
                    System.out.println("Erro ao ler cpf.");
                }
                break;
            case 0:
                System.out.println("Voltando...");
                break;
        }
    }

    private static boolean deleteUserAccount(Scanner sc, Controller userLogin) {
        System.out.println("Deletar Conta?");
        System.out.println("[1] - Sim");
        System.out.println("[2] - Não");
        int option = getOption(sc);

        if (option == 1) {
            userLogin.delete(userLogin.getData("id"), "id");
            return true;
        } else {
            System.out.println("Voltando...");
            return false;
        }
    }
}
