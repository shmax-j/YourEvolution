package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorWindow {
    //    standard
    public static void throwError(String title, String text) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        Label message = new Label(text);
        Button ok = new Button("Ok");
        VBox vBox = new VBox(5, message, ok);

        ok.setOnAction(event -> window.close());
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(0, 0, 0, 10));

        BorderPane root = new BorderPane();
        root.setCenter(vBox);
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setTitle(title);
        window.showAndWait();
    }

    //    default
    public static void throwError() {
        throwError("Error", "Error");
    }

//    with addictive functionality
    public static void throwError(String title, String messedge, Button... addictiveFunctionality) {
        throwError("Not realized");
    }

    public static void throwError(String text) {
        throwError("Error", text);
    }
}

