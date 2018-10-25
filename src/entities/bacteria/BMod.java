package entities.bacteria;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

  public class BMod extends ImageView{
//     Классовая система модификаций
    final String name;
    float x, y;
    public int price;
    boolean isOutside;

    static BMod None = new BMod("None", new Image("res/BMods/None.png"),0,0,0,0, false);
    static BMod Flagellum = new BMod("Flagellum", new Image("res/BMods/Flagellum.png"),0, -374, -50,.25, true);
    static BMod Nucleus = new BMod("Nucleus", new Image("res/BMods/Nucleus.png"),40,0,0,.25,false);

    private BMod(String name, Image iv, int price, float x, float y, double scale, boolean isOutside) {
        this.name = name;
        this.price = price;
        setImage(iv);
        setTranslateX(x);
        setTranslateY(y);
        setScaleX(scale);
        setScaleY(scale);
        this.isOutside = isOutside;
    }
}
