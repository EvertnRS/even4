package br.upe.controller.fx;
import br.upe.facade.FacadeInterface;

import java.io.IOException;

public interface FxController {
    void setFacade(FacadeInterface facade) throws IOException;
}
