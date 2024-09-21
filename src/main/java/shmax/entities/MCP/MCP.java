package shmax.entities.MCP;

import shmax.entities.bacteria.Bacteria;
import shmax.food.NanoFoodPiece;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

import static shmax.entities.MCP.Cell.HEIGHT;
import static shmax.entities.MCP.Cell.WIDTH;
import static shmax.entities.MCP.MCPIntentions.feeding;
import static shmax.entities.MCP.MCPIntentions.walking;
import static shmax.entities.MCP.MCPStates.idle;
import static shmax.entities.MCP.MCPStates.isMoving;
import static shmax.controllers.Main.*;

public class MCP extends Pane{
    private int t, r, b, l;//Contains top, right, left and bottom part joined to this
    private ArrayList<CO> body;
    private MCPStates state = idle;

    private Point2D movingPoint;
    private Point2D org_position = new Point2D(0,0);
    private Point2D l_center;
    private Point2D g_center;
    private MCPIntentions intention = walking;
    private double currentSpeed = .6;
    private double maxSpeed = 1;
    private double idleSpeed = .3;
    private double defSpeed = .6;
    private Point2D velocity;

    private float satiety;
    private float maxSatiety = 150;
    private NanoFoodPiece tgFood;


    public MCP() {
        body = new ArrayList<>();
        body.add(new Cell(0,0));//Center
        body.add(new Cell(1,0));//Right
        state = idle;
        render();
    }

    private void moveToPoint(Point2D point){
        this.movingPoint = point;
        Point2D direction = point.subtract(g_center.getX(),g_center.getY()).normalize();
        switch (intention){
            case feeding:
                currentSpeed = maxSpeed;
                break;
            case walking:
                currentSpeed = idleSpeed;
                break;
        }
        velocity = direction.multiply(currentSpeed);
        rotateToPoint(direction.getX(),direction.getY());
        state = isMoving;
    }

    private void rotateToPoint(double vecX, double vecY){
        double angle = new Point2D(vecX, vecY).angle(0,-1);
        double completeAngle = vecX>0?angle:-angle;
        getTransforms().clear();
        Rotate rotator = new Rotate(completeAngle, l_center.getX(), l_center.getY());
        getTransforms().add(rotator);
    }

//    Every tick event
public void update(){
    org_position = new Point2D(getTranslateX(), getTranslateY());
    g_center = org_position.add(l_center);
    switch (state) {
        case idle:
            moveToPoint(new Point2D(getTranslateX()+Math.random()*1400-700, getTranslateY()+Math.random()*1400-700));
            break;
        case isMoving:
            setTranslateX(org_position.getX()+velocity.getX());
            setTranslateY(org_position.getY()+velocity.getY());
            if (org_position.add(l_center).distance(movingPoint)<3){
                switch (intention){
                    case walking:
                        state = idle;
                        break;
                    case feeding:
                        satiety += 10;
                        tgFood.destroy();
                        state = idle;
                }
            }
            break;
        case isRotating:
            break;
        case isWait:
            break;
    }
}

    private void render(){
        getChildren().clear();
        body.forEach(any -> {
            switch (any.type){
                case 2:
                    t=r=b=l=0;
                    body.forEach(n -> {
                        if (n.x == any.x && n.y == any.y-1)t = n.type;
                        if (n.x == any.x+1 && n.y == any.y)r = n.type;
                        if (n.x == any.x && n.y == any.y+1)b = n.type;
                        if (n.x == any.x-1 && n.y == any.y)l = n.type;
                    });
                    any.graphic.setImage(Cell.images.get(""+t+r+b+l));
                    any.graphic.setFitWidth(WIDTH);
                    any.graphic.setFitHeight(HEIGHT);
                    any.graphic.setOnMouseClicked(event -> {
                        if (BTarget !=null){
                            BTarget.getChildren().remove(Bacteria.activeCircle);
                            BTarget = null;
                        }
                        MCPTarget = this;
                    });
                    getChildren().add(any);
                    any.setTranslateX(any.graphic.getFitWidth() * any.x);
                    any.setTranslateY(any.graphic.getFitHeight()  * any.y);
                    break;
            }
        });


//        Center finding
        int maxX = 0;
        int maxY = 0;
        int minY = 0;
        int minX = 0;
//        max values initialization
        for (CO co:body){
            if (co.x>maxX)maxX=co.x;
            if (co.x<minX)minX=co.x;
            if (co.y>maxY)maxY=co.y;
            if (co.y<minY)minY=co.y;
        }
//        top_left point alignment
        l_center = new Point2D(minX * WIDTH,minY * HEIGHT);
//        finding height and width of shmax.entities.MCP (in cells)
        int hCellCount = Math.abs(maxX)+Math.abs(minX);
        int wCellCount = Math.abs(maxY)+Math.abs(minY);
//        compensation of zero cell
        hCellCount++;
        wCellCount++;
//        displacement l_center to center of shmax.entities.MCP
        l_center = new Point2D(l_center.getX()+(hCellCount*(WIDTH/2)),l_center.getY()+(wCellCount*(HEIGHT/2)));
    }

    public void eat(){
//        Searching
        short searchDistance = 1000;
        for (NanoFoodPiece next: foodList){
            if (next.getPosition().distance(this.org_position)<searchDistance){
                tgFood = next;
                searchDistance = (short) next.getPosition().distance(this.org_position);
            }
        }
//        Checking
        try {
            moveToPoint(tgFood.getPosition());
            intention = feeding;
        }catch (NullPointerException e){
            printMessage("Food not founded in search zone");
        }
    }

    @Override
    public String toString() {
        String beforeMods = gL("target","Target")+":\n"+gL("satiety", "Satiety")+" - "+satiety;
        StringBuilder mods = new StringBuilder();
        mods.append("");
        return beforeMods+mods;
    }
}
