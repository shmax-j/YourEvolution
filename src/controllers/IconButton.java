package controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class IconButton extends Pane{
    public static final double IMAGE_SIZE = 40;

    private ImageView imageView = new ImageView();
    public IconButton(Image image){
        getChildren().add(imageView);
        imageView.setImage(image);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setFitWidth(IMAGE_SIZE);
        setMaxHeight(IMAGE_SIZE);
        setMaxWidth(IMAGE_SIZE);
        setOpacity(.7);
        this.setOnMouseEntered(event -> setOpacity(.9));
        setOnMouseExited(event -> setOpacity(.7));
    }

    public ImageView getImageView() {
        return imageView;
    }
}
