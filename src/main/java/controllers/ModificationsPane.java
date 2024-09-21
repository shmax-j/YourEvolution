package controllers;

import entities.bacteria.BacteriaModifications;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ModificationsPane extends Stage {
    private VBox root = new VBox();
    private Map<String, Button> map = new HashMap<>();
    public ModificationsPane(){
        root.setOnKeyPressed(event -> {
            switch (event.getCode().getName()){
                case "Esc":
                    Main.resume();
                    close();
                case "Tab":
                    Main.resume();
                    close();
            }
        });
        initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Add modification");
        root.setSpacing(5);
        root.setAlignment(Pos.CENTER);
        setOnCloseRequest(event -> Main.resume());
        root.setPadding(new Insets(5,0,5,0));

        for (BacteriaModifications next: BacteriaModifications.values()){
            map.put(next.name(), new Button(next.name()));
        }
        map.forEach((name, tag)->{
            root.getChildren().add(tag);
            tag.setOnAction(event -> {
                Main.BTarget.addModification(BacteriaModifications.valueOf(name).mod);
                close();
            });
        });
    }

    public void reinitializeAndShow(){
        map.forEach((next,btm)->{
            boolean c = Main.BTarget.getSatiety()<BacteriaModifications.valueOf(next).mod.price;
                if (Main.BTarget.modifications.contains(BacteriaModifications.valueOf(next).mod)||c){
                    btm.setDisable(true);

                }else btm.setDisable(false);
        });
        Main.pause();
        showAndWait();
    }
}
