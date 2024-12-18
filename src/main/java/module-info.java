module br.upe.even4 {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires org.postgresql.jdbc;
    requires io.github.cdimascio.dotenv.java;
    requires org.jetbrains.annotations;
    requires java.desktop;

    exports br.upe.ui;
    exports br.upe.controller;
    exports br.upe.persistence;

    opens br.upe.ui to javafx.fxml;
    opens br.upe.persistence to org.hibernate.orm.core;
}