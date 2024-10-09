package br.upe.ui;

import br.upe.controller.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;

import static br.upe.ui.Validation.*;

public class Interface {
    private static final Logger LOGGER = Logger.getLogger(Interface.class.getName());

    public static void main(String[] args) throws FileNotFoundException {
        LOGGER.info("Bem-Vindo ao Even4");
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
                        LOGGER.info("Saindo...");
                        break;
                    default:
                        LOGGER.info("Opção inválida. Tente novamente.");
                }
            } while (option != 0);
        }
    }

    private static void printMainMenu() {
        LOGGER.info("[1] - Login");
        LOGGER.info("[2] - Cadastrar");
        LOGGER.info("[0] - Sair");
        LOGGER.info("Escolha uma opção: ");
    }

    private static int getOption(Scanner sc) {
        if (sc.hasNextInt()) {
            int option = sc.nextInt();
            sc.nextLine();
            return option;
        } else {
            LOGGER.info("Entrada inválida. Por favor, insira um número.");
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
            Controller ac = new AttendeeController();
            Controller sub = new SubmitArticleController();

            switch (option) {
                case 1:
                    createFlow(sc, ec, sec, ses, userLogin);
                    break;
                case 2:
                    alterFlow(sc, ec, sec, ses, userLogin);
                    break;
                case 3:
                    enterFlow(sc, ses, userLogin, ac);
                    break;
                case 4:
                    if (setup(sc, userLogin)) {
                        option = 0;
                    }
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void printUserMenu() {
        LOGGER.info("[1] - Criar");
        LOGGER.info("[2] - Alterar");
        LOGGER.info("[3] - Acessar Evento");
        LOGGER.info("[4] - Perfil");
        LOGGER.info("[0] - Voltar");
        LOGGER.info("Escolha uma opção: ");
    }

    private static void createFlow(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) throws FileNotFoundException {
        int option;
        do {
            LOGGER.info("Escolha o que deseja criar:");
            LOGGER.info("[1] - Evento");
            LOGGER.info("[2] - SubEvento");
            LOGGER.info("[3] - Sessão");
            LOGGER.info("[0] - Voltar");
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
                    break; // Added missing break
                case 0:
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void alterFlow(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) throws FileNotFoundException {
        int option;
        do {
            LOGGER.info("Escolha o que deseja alterar:");
            LOGGER.info("[1] - Evento");
            LOGGER.info("[2] - SubEvento");
            LOGGER.info("[3] - Sessão");
            LOGGER.info("[0] - Voltar");
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
                    break; // Added missing break
                case 0:
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void enterFlow(Scanner sc, Controller ses, Controller userLogin, Controller ac) throws FileNotFoundException {
        SubmitArticleController submitArticleController = new SubmitArticleController();
        int option;
        do {
            LOGGER.info("Escolha a opção desejada:");
            LOGGER.info("[1] - Listar Inscrições");
            LOGGER.info("[2] - Inscrever-se");
            LOGGER.info("[3] - Submeter Artigo");
            LOGGER.info("[0] - Voltar");
            option = getOption(sc);

            switch (option) {
                case 1:
                    listEvents(sc, userLogin, ac);
                    break;
                case 2:
                    choiceEvent(sc, ses, userLogin, ac);
                    break;
                case 3:
                    articleMenu(sc, submitArticleController);
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }


    private static void listEvents(Scanner sc, Controller userLogin, Controller ac) throws FileNotFoundException {
        boolean isnull = ac.list(userLogin.getData("id"));
        if (isnull) {
            return;
        }
        int option;
        do {
            LOGGER.info("[1] - Atualizar Dados da Inscrição");
            LOGGER.info("[2] - Remover Inscrição");
            LOGGER.info("[0] - Voltar");
            option = getOption(sc);
            switch (option) {
                case 1:
                    alterAttendee(sc, ac);
                    break;
                case 2:
                    deleteAttendee(sc, ac, userLogin);
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
            }
        } while (option != 0);
    }

    private static void deleteAttendee(Scanner sc, Controller ac, Controller userLogin) {
        int option;
        do {
            LOGGER.info("[1] - Deletar Inscrição");
            LOGGER.info("[0] - Voltar");
            option = getOption(sc);
            switch (option) {
                case 1:
                    LOGGER.info("Digite o id da Sessão que você deseja sair");
                    String sessionId = sc.nextLine();
                    ac.delete(userLogin.getData("id"), "id", sessionId);
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
            }
        } while (option != 0);
    }

    private static void alterAttendee(Scanner sc, Controller ac) throws FileNotFoundException {
        int option;
        do {
            LOGGER.info("[1] - Alterar Nome");
            LOGGER.info("[0] - Voltar");
            option = getOption(sc);
            switch (option) {
                case 1:
                    LOGGER.info("Digite o id da Sessão que você deseja atualizar o seu nome");
                    String sessionId = sc.nextLine();
                    LOGGER.info("Digite o novo nome");
                    String name = sc.nextLine();
                    ac.update(name, sessionId);
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
            }
        } while (option != 0);
    }

    private static void choiceEvent(Scanner sc, Controller ses, Controller userLogin, Controller ac) throws FileNotFoundException {
        ses.show(userLogin.getData("id"), "userId");
        LOGGER.info("Digite o id da Sessão que você quer entrar:");
        String sessionId = sc.nextLine();
        enterEvent(sc, ses, sessionId, userLogin, ac);
    }

    private static void enterEvent(Scanner sc, Controller ses, String sessionId, Controller userLogin, Controller ac) throws FileNotFoundException {
        ses.show(sessionId, "sessionId");
        int option;
        do {
            LOGGER.info("[1] - Entrar na Sessão");
            LOGGER.info("[0] - Voltar");
            option = getOption(sc);
            switch (option) {
                case 1:
                    LOGGER.info("Digite seu nome para a emissão do certificado");
                    String name = sc.nextLine();
                    ac.create(name, sessionId, userLogin.getData("id"));
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
            }
        } while (option != 0);
    }

    private static void articleMenu(Scanner sc, SubmitArticleController submitArticleController) throws FileNotFoundException {
        int option;

        do {
            LOGGER.info("Escolha a opção desejada:");
            LOGGER.info("[1] - Submeter Artigo");
            LOGGER.info("[2] - Atualizar Artigo");
            LOGGER.info("[3] - Deletar Artigo");
            LOGGER.info("[4] - Listar Artigos");
            LOGGER.info("[0] - Voltar");
            option = getOption(sc);

            switch (option) {
                case 1:
                    LOGGER.info("Digite o nome do evento:");
                    String eventName = sc.next();
                    LOGGER.info("Digite o caminho do artigo:");
                    String filePath = sc.next();
                    submitArticleController.create(eventName, filePath);
                    break;
                case 2:
                    LOGGER.info("Digite o nome do evento do artigo a ser atualizado:");
                    String oldEventName = sc.nextLine();
                    LOGGER.info("Digite o novo caminho do artigo:");
                    String newFilePath = sc.nextLine();
                    submitArticleController.update(oldEventName, newFilePath);
                case 3:
                    LOGGER.info("Digite o nome do artigo a ser deletado:");
                    String deleteFilePath = sc.next();
                    submitArticleController.delete(deleteFilePath);
                    break;
                case 4:
                    LOGGER.info("Digite o nome do evento para listar os artigos:");
                    String eventNameToRead = sc.next();
                    submitArticleController.read(eventNameToRead);
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
    }

    private static void createEvent(Scanner sc, Controller ec, Controller userLogin) throws FileNotFoundException {
        LOGGER.info("Digite o nome do Evento: ");
        String nameEvent = sc.nextLine();
        LOGGER.info("Data do Evento: ");
        String dateEvent = sc.nextLine();
        LOGGER.info("Descrição do Evento: ");
        String descriptionEvent = sc.nextLine();
        LOGGER.info("Local do Evento: ");
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
            LOGGER.info("Selecione um Evento: ");
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
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (optionEvent != 0);
    }

    private static void printAlterEventMenu() {
        LOGGER.info("[1] - Apagar Evento ");
        LOGGER.info("[2] - Alterar Evento ");
        LOGGER.info("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private static void updateEvent(Scanner sc, Controller ec, String changed, String userId) throws FileNotFoundException {
        LOGGER.info("Digite o novo nome do Evento: ");
        String newName = sc.nextLine();
        LOGGER.info("Nova Data do Evento: ");
        String newDate = sc.nextLine();
        LOGGER.info("Nova Descrição do Evento: ");
        String newDescription = sc.nextLine();
        LOGGER.info("Novo Local do Evento: ");
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
        LOGGER.info("Nome do Evento Pai: ");
        String fatherEvent = sc.nextLine();
        LOGGER.info("Digite o nome do SubEvento: ");
        String nameSubEvent = sc.nextLine();
        LOGGER.info("Data do SubEvento: ");
        String dateSubEvent = sc.nextLine();
        LOGGER.info("Descrição do SubEvento: ");
        String descriptionSubEvent = sc.nextLine();
        LOGGER.info("Local do SubEvento: ");
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
            LOGGER.info("Selecione um SubEvento: ");
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
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (optionSubEvent != 0);
    }

    private static void printAlterSubEventMenu() {
        LOGGER.info("[1] - Apagar SubEvento ");
        LOGGER.info("[2] - Alterar SubEvento ");
        LOGGER.info("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private static void updateSubEvent(Scanner sc, Controller sec, String subChanged, String userId) throws FileNotFoundException {
        LOGGER.info("Digite o novo nome do SubEvento: ");
        String newName = sc.nextLine();
        LOGGER.info("Nova Data do SubEvento: ");
        String newDate = sc.nextLine();
        LOGGER.info("Nova Descrição do SubEvento: ");
        String newDescription = sc.nextLine();
        LOGGER.info("Novo Local do SubEvento: ");
        String newLocation = sc.nextLine();
        if (isValidDate(newDate)){
            sec.update(subChanged.trim(), newName.trim(), newDate, newDescription, newLocation, userId);
        }
    }



    private static void createSession(Scanner sc, Controller ec, Controller sec, Controller ses, Controller userLogin) throws FileNotFoundException {
        int optionSession;
        String type;
        do {
            LOGGER.info("[1] - Criar Sessão em um Evento");
            LOGGER.info("[2] - Criar Sessão em um SubEvento");
            LOGGER.info("[0] - Voltar");
            optionSession = getOption(sc);

            switch (optionSession) {
                case 1:
                    type = "Event";
                    LOGGER.info("Evento: ");
                    boolean isNull = ec.list(userLogin.getData("id"));
                    enterMenuSession(sc, ses, type, userLogin);
                    if (isNull) {
                        return;
                    }
                    break;
                case 2:
                    type = "SubEvent";
                    LOGGER.info("\nSubEvento: ");
                    boolean isNullSub = sec.list(userLogin.getData("id"));
                    enterMenuSession(sc, ses, type, userLogin);
                    if (isNullSub) {
                        return;
                    }
                    break;
                case 0:
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (optionSession != 0);
    }

    private static void enterMenuSession(Scanner sc, Controller ses, String type, Controller userLogin) throws FileNotFoundException {
        LOGGER.info("Nome do Evento Pai: ");
        String fatherEvent = sc.nextLine();
        LOGGER.info("Digite o nome da Sessão: ");
        String nameSession = sc.nextLine();
        LOGGER.info("Data da Sessão: ");
        String dateSession = sc.nextLine();
        LOGGER.info("Descrição da Sessão: ");
        String descriptionSession = sc.nextLine();
        LOGGER.info("Local da Sessão: ");
        String locationSession = sc.nextLine();
        LOGGER.info("Início da Sessão: ");
        String startTime = sc.nextLine();
        LOGGER.info("Término da Sessão: ");
        String endTime = sc.nextLine();
        if (isValidDate(dateSession) && areValidTimes(startTime, endTime)) {
            ses.create(fatherEvent.trim(), nameSession.trim(), dateSession, descriptionSession, locationSession, startTime, endTime, userLogin.getData("id"), type);
        }
    }

    private static void alterSession(Scanner sc, Controller ses, Controller userLogin) throws FileNotFoundException {
        boolean isNull = ses.list(userLogin.getData("id"));
        if (isNull) {
            return;
        }
        int optionSession;
        do {
            LOGGER.info("Selecione uma Sessão: ");
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
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (optionSession != 0);
    }

    private static void updateSession(Scanner sc, Controller ses, String subChanged, String userId) throws FileNotFoundException {
        LOGGER.info("Digite o novo nome da Sessão: ");
        String newName = sc.nextLine();
        LOGGER.info("Nova Data da Sessão: ");
        String newDate = sc.nextLine();
        LOGGER.info("Nova Descrição da Sessão: ");
        String newDescription = sc.nextLine();
        LOGGER.info("Novo Local da Sessão: ");
        String newLocation = sc.nextLine();
        LOGGER.info("Novo Início da Sessão: ");
        String newStartTime = sc.nextLine();
        LOGGER.info("Novo Término da Sessão: ");
        String newEndTime = sc.nextLine();
        if (isValidDate(newDate) && areValidTimes(newStartTime, newEndTime)) {
            ses.update(subChanged.trim(), newName.trim(), newDate, newDescription, newLocation, newStartTime, newEndTime, userId);
        }
    }

    private static void printAlterSessionMenu() {
        LOGGER.info("[1] - Apagar Sessão ");
        LOGGER.info("[2] - Alterar Sessão ");
        LOGGER.info("[0] - Voltar");
        System.out.print("Escolha uma opção: ");
    }

    public static Object[] login(Scanner sc) {
        Controller userController = new UserController();
        LOGGER.info("Digite seu email:");
        boolean isLog = false;
        if (sc.hasNextLine()) {
            String email = sc.nextLine();
            LOGGER.info("Digite seu cpf:");
            String cpf = sc.nextLine();
            if (userController.loginValidate(email, cpf)) {
                LOGGER.info("Login Realizado com Sucesso");
                isLog = true;
            } else {
                LOGGER.info("Login ou senha incorreto");
            }
        } else {
            LOGGER.info("Erro ao ler email.");
        }
        return new Object[]{isLog, userController};
    }

    public static void signup(Scanner sc) throws FileNotFoundException {
        Controller userController = new UserController();
        LOGGER.info("Cadastre seu email:");
        if (sc.hasNextLine()) {
            String email = sc.nextLine();
            if (!isValidEmail(email) || email.isEmpty()) {
                LOGGER.info("Email inválido. Tente novamente.");
                main(new String[]{"a", "b"});
                return;
            }
            LOGGER.info("Digite seu cpf:");
            if (sc.hasNextLine()) {
                String cpf = sc.nextLine();
                if (!isValidCPF(cpf) || cpf.isEmpty()) {
                    LOGGER.info("CPF inválido. Tente novamente");
                    main(new String[]{"a", "b"});
                    return;
                }
                userController.create(email.trim(), cpf.trim());
            } else {
                LOGGER.info("Erro ao ler cpf.");
            }
        } else {
            LOGGER.info("Erro ao ler email.");
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
                    LOGGER.info("Voltando...");
                    break;
                default:
                    LOGGER.info("Opção inválida. Tente novamente.");
            }
        } while (option != 0);
        return isRemoved;
    }

    private static void printUserProfile(Controller userLogin) {
        LOGGER.info("Email: " + userLogin.getData("email"));
        LOGGER.info("CPF: " + userLogin.getData("cpf"));
    }

    private static void printSetupMenu() {
        LOGGER.info("[1] - Atualizar conta");
        LOGGER.info("[2] - Deletar conta");
        LOGGER.info("[0] - Voltar");
        LOGGER.info("Escolha uma opção: ");
    }

    private static void updateUserAccount(Scanner sc, Controller userLogin) throws FileNotFoundException {
        int option;
        LOGGER.info("O que você deseja atualizar?");
        LOGGER.info("[1] - email");
        LOGGER.info("[2] - cpf");
        LOGGER.info("[0] - voltar");
        option = getOption(sc);

        switch (option) {
            case 1:
                LOGGER.info("Digite o novo email:");
                if (sc.hasNextLine()) {
                    String email = sc.nextLine();
                    userLogin.update(email, userLogin.getData("cpf"));
                } else {
                    LOGGER.info("Erro ao ler email.");
                }
                break;
            case 2:
                LOGGER.info("Digite o novo cpf:");
                if (sc.hasNextLine()) {
                    String cpf = sc.nextLine();
                    userLogin.update(userLogin.getData("email"), cpf);
                } else {
                    LOGGER.info("Erro ao ler cpf.");
                }
                break;
            case 0:
                LOGGER.info("Voltando...");
                break;
        }
    }

    private static boolean deleteUserAccount(Scanner sc, Controller userLogin) {
        LOGGER.info("Deletar Conta?");
        LOGGER.info("[1] - Sim");
        LOGGER.info("[2] - Não");
        int option = getOption(sc);

        if (option == 1) {
            userLogin.delete(userLogin.getData("id"), "id");
            return true;
        } else {
            LOGGER.info("Voltando...");
            return false;
        }
    }

}