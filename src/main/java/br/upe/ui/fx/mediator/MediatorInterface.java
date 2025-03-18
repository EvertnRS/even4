package br.upe.ui.fx.mediator;

import java.io.IOException;

public interface MediatorInterface {
    Object notify(String event) throws IOException;

    void registerComponents();
}
