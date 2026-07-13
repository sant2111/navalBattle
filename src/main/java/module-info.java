module com.example.navalbattle {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.navalbattle.controller to javafx.fxml;

    exports com.example.navalbattle;
    exports com.example.navalbattle.view;
    exports com.example.navalbattle.controller;
    exports com.example.navalbattle.model;
    exports com.example.navalbattle.persistence;
    exports com.example.navalbattle.exceptions;
}