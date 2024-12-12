package br.upe.controller.fx;

import br.upe.controller.fx.mediator.CertificateMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Attendee;
import br.upe.persistence.Session;
import br.upe.persistence.Event;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
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
import java.util.Objects;
import java.util.UUID;

public class CertificateScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String attendeeId;
    private CertificateMediator mediator;

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
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/images/certificate/DefaultCertificate.png")).toExternalForm());
        exampleCertificate.setImage(image);

        mediator = new CertificateMediator(this, facade, certificationPane, errorUpdtLabel);
        mediator.registerComponents();
    }

    public void createCertificate() throws IOException {
        String certificateAddress = addresTextField.getText();

            drawCertificate(certificateAddress);
            mediator.notify("handleBack");
    }

    private void drawCertificate(String certificateAddres) {
        try {
            BufferedImage certificate = ImageIO.read(new File(getClass().getResource("/images/certificate/EmptyCertificate.png").toURI()));

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

            EntityManager entityManager = JPAUtils.getEntityManagerFactory();

            Attendee attendee = entityManager.find(Attendee.class, attendeeId);
            if (attendee == null) {
                throw new EntityNotFoundException("Attendee não encontrado");
            }

            UUID sessionId = attendee.getSessionIds().iterator().next();
            Session session = entityManager.find(Session.class, sessionId);
            if (session == null) {
                throw new EntityNotFoundException("Sessão não encontrada");
            }

            Event event = entityManager.find(Event.class, session.getEventId());
            if (event == null) {
                throw new EntityNotFoundException("Evento não encontrado");
            }

            String attendeeName = attendee.getName();
            String eventName = event.getName();
            String startTime = String.valueOf(session.getStartTime());
            String endTime = String.valueOf(session.getEndTime());
            String workload = timeDifference(startTime, endTime);
            String eventDate = String.valueOf(session.getDate());

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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Download Completo");
            alert.setHeaderText(null);
            alert.setContentText("O certificado foi baixado com sucesso!");
            alert.showAndWait();

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

    public void openDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione uma Pasta");

        Stage stage = (Stage) certificationPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            addresTextField.setText(selectedDirectory.getAbsolutePath());
        } else {
            errorUpdtLabel.setText("Nenhuma pasta selecionada.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }

    public TextField getAddresTextField() {
        return addresTextField;
    }

    @Override
    public TextField getNameTextField() {
        return null;
    }

    @Override
    public TextField getLocationTextField() {
        return null;
    }

    @Override
    public TextField getDescriptionTextField() {
        return null;
    }

    @Override
    public DatePicker getDatePicker() {
        return null;
    }

}