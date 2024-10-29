package br.upe.controller.fx;
import br.upe.facade.Facade;
import java.io.IOException;

public interface FxController {
    void setFacade(Facade facade) throws IOException;
}
