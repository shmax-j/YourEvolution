package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
    private static final double IMAGE_SIZE = 40;

    private ImageView imageView = new ImageView();
    public IconButton(Image image){
        this(image, null);
    }
    public IconButton(Image image, String tooltipText){
        setGraphic(imageView);
        imageView.setImage(image);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setFitWidth(IMAGE_SIZE);
        setPrefHeight(IMAGE_SIZE);
        setPrefWidth(IMAGE_SIZE);
        setOpacity(.7);
        setStyle("-fx-background-color: transparent;" +
                "-fx-padding: 0");
        setOnMouseEntered(event -> setOpacity(.9));
        setOnMouseExited(event -> setOpacity(.7));

        if (tooltipText!=null) {
            Tooltip tooltip = new Tooltip(tooltipText);
            setTooltip(tooltip);
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}
