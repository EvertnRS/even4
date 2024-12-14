package br.upe.controller.fx.screen;

import br.upe.facade.FacadeInterface;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;

public interface FxController {
    void setFacade(FacadeInterface facade) throws IOException;

    TextField getNameTextField();

    TextField getLocationTextField();

    TextField getDescriptionTextField();

    DatePicker getDatePicker();
}
