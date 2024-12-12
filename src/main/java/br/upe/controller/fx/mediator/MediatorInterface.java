package br.upe.controller.fx.mediator;

import java.io.IOException;

public interface MediatorInterface {
    Object notify(String event) throws IOException;

    void registerComponents();
}
