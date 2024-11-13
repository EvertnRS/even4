package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.repository.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CertificateScreenController extends BaseController implements FxController {
    private static final String SESSION_ID = "sessionId";
    private FacadeInterface facade;
    private String attendeeId;

    @FXML
    private AnchorPane certificationPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField addresTextField;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private ImageView exampleCertificate;

    public void setFacade(FacadeInterface facade) {
        this.facade = facade;
        initial();
    }

    public void setEventName(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    private void initial() {
        userEmail.setText(facade.getEventData("email"));
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/images/DefaultCertificate.png")).toExternalForm());
        exampleCertificate.setImage(image);

    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", certificationPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", certificationPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", certificationPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", certificationPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", certificationPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", certificationPane, facade, null);
    }

    public void handleInscriptionSession() throws IOException {
        genericButton("/fxml/enterSessionScreen.fxml", certificationPane, facade, null);
    }

    @FXML
    private void createArticle()throws IOException {
        String certificateAddres = addresTextField.getText();
        drawCertificate(certificateAddres);
        handleInscriptionSession();
    }

    private void drawCertificate(String certificateAddres) {
        try {
            BufferedImage certificate = ImageIO.read(new File(getClass().getResource("/images/EmptyCertificate.png").toURI()));

            BufferedImage newCertificate = new BufferedImage(
                    certificate.getWidth(),
                    certificate.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2d = newCertificate.createGraphics();
            Graphics2D g2dName = newCertificate.createGraphics();
            g2d.drawImage(certificate, 0, 0, null);

            g2d.setFont(new Font("Arial", Font.BOLD, 45));
            g2d.setColor(Color.BLACK);
            g2dName.setFont(new Font("Arial", Font.BOLD, 60));
            g2dName.setColor(Color.BLACK);

            Map<UUID, Persistence> attendeeMap = facade.getAttendeeHashMap();
            Map<UUID, Persistence> sessionMap = facade.getSessionHashMap();
            String attendeeName = (String) attendeeMap.get(attendeeId).getData("name");

            String eventName = (String) sessionMap.get(attendeeMap.get(attendeeId).getData(SESSION_ID)).getData("name");

            String startTime = (String) sessionMap.get(attendeeMap.get(attendeeId).getData(SESSION_ID)).getData("startTime");
            String endTime = (String) sessionMap.get(attendeeMap.get(attendeeId).getData(SESSION_ID)).getData("endTime");
            String workload = timeDifference(startTime, endTime);

            String eventDate = (String) sessionMap.get(attendeeMap.get(attendeeId).getData(SESSION_ID)).getData("date");

            int xName = 130;
            int yName = 400;
            int xEvent = 630;
            int yEvent = 500;
            int xWorkload = 650;
            int yWorkload = 587;
            int xData = 130;
            int yData = 950;

            g2dName.drawString(attendeeName, xName, yName);
            g2d.drawString(eventName, xEvent, yEvent);
            g2d.drawString(workload, xWorkload, yWorkload);
            g2d.drawString(eventDate, xData, yData);

            g2d.dispose();

            File directory = new File(certificateAddres);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File outputFile = new File(directory, "certificate.png");

            ImageIO.write(newCertificate, "png", outputFile);
        } catch (IOException e) {
            errorUpdtLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String timeDifference(String startTimeStr, String endTimeStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
        LocalTime endTime = LocalTime.parse(endTimeStr, formatter);

        Duration duration;

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            duration = Duration.between(startTime, endTime).plusHours(24);
        } else {
            duration = Duration.between(startTime, endTime);
        }

        long hours = duration.toHours();
        return String.format("%d hora(s)", hours);
    }


    @FXML
    private void openDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione uma Pasta");

        Stage stage = (Stage) certificationPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            addresTextField.setText(selectedDirectory.getAbsolutePath());
        } else {
            errorUpdtLabel.setText("Nenhuma pasta selecionada.");
        }
    }
}