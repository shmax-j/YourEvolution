package entities.MCP;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.Util;

import java.util.HashMap;

class Cell extends CO {//entities.MCP.Cell - basic part of entities.MCP.entities.MCP
    final static double WIDTH = 46;
    final static double HEIGHT = 68;
    static HashMap<String, Image> images = new HashMap<>();
    private final static Image CENTER = new Image(Util.fisResource("sprites/bacteria/Bacteria_Center.png"));
    private final static Image TOP = new Image(Util.fisResource("sprites/bacteria/Bacteria_Top.png"));
    private final static Image BOTTOM = new Image(Util.fisResource("sprites/bacteria/Bacteria_Bottom.png"));
    private final static Image RIGHT = new Image(Util.fisResource("sprites/bacteria/Bacteria_Right.png"));
    private final static Image LEFT = new Image(Util.fisResource("sprites/bacteria/Bacteria_Left.png"));
    private final static Image TOP_RIGHT = new Image(Util.fisResource("sprites/bacteria/Bacteria_TopRight.png"));
    private final static Image TOP_LEFT = new Image(Util.fisResource("sprites/bacteria/Bacteria_TopLeft.png"));
    private final static Image BOTTOM_RIGHT = new Image(Util.fisResource("sprites/bacteria/Bacteria_BottomRight.png"));
    private final static Image BOTTOM_LEFT = new Image(Util.fisResource("sprites/bacteria/Bacteria_BottomLeft.png"));
    private final static Image TOP_TOP = new Image(Util.fisResource("sprites/bacteria/Bacteria_TopTop.png"));
    private final static Image BOTTOM_BOTTOM = new Image(Util.fisResource("sprites/bacteria/Bacteria_BottomBottom.png"));
    private final static Image RIGHT_RIGHT = new Image(Util.fisResource("sprites/bacteria/Bacteria_RightRight.png"));
    private final static Image LEFT_LEFT = new Image(Util.fisResource("sprites/bacteria/Bacteria_LeftLeft.png"));
    private final static Image TOP_BOTTOM = new Image(Util.fisResource("sprites/bacteria/Bacteria_TopBottom.png"));
    private final static Image RIGHT_LEFT = new Image(Util.fisResource("sprites/bacteria/Bacteria_RightLeft.png"));

    Cell(int x, int y) {
        super(x,y);
        //TRBL
        images.put("0000", CENTER);
        images.put("2222", CENTER);
        images.put("0222", TOP);
        images.put("2202", BOTTOM);
        images.put("2022", RIGHT);
        images.put("2220", LEFT);
        images.put("0022", TOP_RIGHT);
        images.put("0220", TOP_LEFT);
        images.put("2002", BOTTOM_RIGHT);
        images.put("2200", BOTTOM_LEFT);
        images.put("0020", TOP_TOP);
        images.put("2000", BOTTOM_BOTTOM);
        images.put("0002", RIGHT_RIGHT);
        images.put("0200", LEFT_LEFT);
        images.put("0202", RIGHT_LEFT);
        images.put("2020", TOP_BOTTOM);

        graphic = new ImageView(CENTER);
        type = 2;
        getChildren().add(graphic);
    }
}
