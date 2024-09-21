package shmax.food;

import shmax.controllers.Main;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NanoFoodPiece extends Pane {

    private boolean eaten = false;
    private Point2D position;
    private float foodValue = (float) Math.random()+.5f;

    public NanoFoodPiece() {
        Rectangle graphics = new Rectangle(11, 11, Color.rgb(150, 144, 107));
        double scale = foodValue;
        graphics.setScaleX(scale);
        graphics.setScaleY(scale);
        getChildren().add(graphics);
        setTranslateX(Math.random()*1200-600+(Main.BTarget!=null? Main.BTarget.getTranslateX(): Main.MCPTarget!=null? Main.MCPTarget.getTranslateX():0));
        setTranslateY(Math.random()*1200-600);
        position = new Point2D(getTranslateX()+graphics.getWidth(),getTranslateY()+graphics.getHeight());
    }

    public double getFoodValue() {
        return foodValue;
    }
    public Point2D getPosition() {
        return position;
    }
    public boolean isEaten() {
        return eaten;
    }

    public void destroy(){
        eaten = true;
    }
}
