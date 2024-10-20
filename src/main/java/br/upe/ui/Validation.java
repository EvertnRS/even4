package br.upe.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Validation {
    private static final Logger LOGGER = Logger.getLogger(Validation.class.getName());
    private Validation() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada.");
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidCPF(String cpf) {
        String cpfRegex = "^((\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})|(\\d{11}))$";
        Pattern pattern = Pattern.compile(cpfRegex);
        Matcher matcher = pattern.matcher(cpf);
        return matcher.matches();
    }

    public static boolean isValidDate(String dt) {
        String dateRegex = "^\\d{4}-((0\\d)|(1[0-2]))-(([0-2]\\d)|(3[01]))$";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(dt);

        if (matcher.matches()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                LocalDate dateValidate = LocalDate.parse(dt, formatter);
                LocalDate dateNow = LocalDate.now();

                if (dateValidate.isBefore(dateNow)) {
                    LOGGER.warning("A data '%s' é anterior à data de hoje.".formatted(dt));
                    return false;
                }

                return true;
            } catch (DateTimeParseException e) {
                LOGGER.warning("Erro ao validar data: " + e.getMessage());
                return false;
            }
        }
        LOGGER.warning("Formato de data inválido. Use o formato yyyy-MM-dd.");
        return false;
    }

    public static boolean areValidTimes(String startTime, String endTime) {
        return isValidTime(startTime) && (isValidTime(endTime));
    }

    public static boolean isValidTime(String hr) {
        String timeRegex = "^([01]\\d|2[0-3]):([0-5]\\d)$";
        Pattern pattern = Pattern.compile(timeRegex);
        Matcher matcher = pattern.matcher(hr);
        if (matcher.matches()) {
            String[] time = hr.split(":");

            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            if (hour < 0 || hour > 23) {
                LOGGER.warning("Horário inválido");
                return false;
            }

            if (minute < 0 || minute > 59) {
                LOGGER.warning("Horário inválido");
                return false;
            }
            return true;
        }
        LOGGER.warning("Formato de hora inválido. Use o formato hh:mm");
    return false;
    }
}

