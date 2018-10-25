package entities.bacteria;

import controllers.Main;
import controllers.ModificationsPane;
import entities.MCP.MCP;
import food.NanoFoodPiece;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.util.HashSet;

import static controllers.Main.gL;

public class Bacteria extends Pane{
    public static ImageView activeCircle = new ImageView(new Image("res/Active.png"));

    public HashSet<BMod> modifications = new HashSet<>();

    private ModificationsPane mp = new ModificationsPane();
    private Point2D position;
    private String caller = "idle";
    private Point2D direction = new Point2D(0, 0);
    private double currentVelocity = 1;
    private double velocity = 1;
    private float hungerThreshold = 10;
    private float satiety = 10;
    private float maxSatiety = 100;
    private NanoFoodPiece choose = new NanoFoodPiece();
    private StateMachine state = StateMachine.idle;
    public boolean remove = false;
    private Point2D nextPos;
    private ImageView sprite = new ImageView(new Image("res/Bacteria1.png"));

    private Bacteria partner;
    private boolean isWaitingForPartner;

    private float waitingT = 1;
    private float modInhChance = 20;
    private float[] nucleus = new float[4];

    public Bacteria(double x, double y, Bacteria parent) {
        sprite.setFitHeight(48);
        sprite.setFitWidth(69);
        sprite.setX(-(sprite.getFitWidth() / 2));
        sprite.setY(-(sprite.getFitHeight() / 2));

        modifications.add(BMod.None);

        activeCircle.setFitHeight(75);
        activeCircle.setFitWidth(75);
        activeCircle.setX(-activeCircle.getFitWidth() / 2);
        activeCircle.setY(-activeCircle.getFitWidth() / 2);
        setTranslateY(y);
        setTranslateX(x);

        this.setPrefHeight(sprite.getFitHeight());
        this.setPrefWidth(sprite.getFitWidth());
        getChildren().addAll(sprite);
        currentVelocity = velocity;

        inherit(parent);

        sprite.setOnMouseClicked(event -> {
            if (Main.BTarget != this) {
                Main.BTarget = this;
                Main.bMap.forEach(next -> {
                    if (next.getChildren().contains(activeCircle)) {
                        next.getChildren().remove(activeCircle);
                    }
                });
                getChildren().add(activeCircle);
            } else {
                Main.BTarget = null;
                getChildren().remove(activeCircle);
            }
        });
    }

    //     Every tick event
    public void update(HashSet<NanoFoodPiece> foodPeaces) {
        if (Main.BTarget != this && getChildren().contains(activeCircle)) getChildren().remove(activeCircle);
        satiety -= .006;
        Point2D front = new Point2D(getTranslateX(), getTranslateY()).add(direction.multiply(sprite.getFitWidth() / 2));
        switch (state) {
            case isWait:
                waitingT--;
                if (waitingT == 0) {
                    switch (caller) {
                        case "idle":
                            changeState("idle");
                    }
                }
                break;
            case isRotating:
                break;
            case isMoving:
                if (caller.equals("fs") && !Main.root.getChildren().contains(choose)) searchForFood(Main.foodList);
                if (front.distance(nextPos) < front.distance(front.add(direction.multiply(currentVelocity)))) {
                    switch (caller) {
                        case "fs":
                            eats();
                            caller = "idle";
                            break;
                        case "idle":
                            changeState("idle");
                            break;
                        case "mcp":
                            if (partner.isWaitingForPartner) {
                                bornMcp();
                            } else isWaitingForPartner = true;
                            break;
                    }
                    setTranslateY(getTranslateY() + (nextPos.getY() - front.getY()));
                    setTranslateX(getTranslateX() + (nextPos.getX() - front.getX()));
                } else {
                    setTranslateY(getTranslateY() + direction.getY() * currentVelocity);
                    setTranslateX(getTranslateX() + direction.getX() * currentVelocity);
                }

                break;
            case idle:
                currentVelocity = velocity / 4;
                if (satiety < hungerThreshold) {
                    searchForFood(foodPeaces);
                }
                moveToPoint(new Point2D(getTranslateX() + Math.random() * 1400 - 700, getTranslateY() + Math.random() * 1400 - 700));
                break;
        }
        position = new Point2D(getTranslateX(), getTranslateY());
    }

    private void moveToPoint(Point2D pos) {
        //nextPos is using on the method outside(usable)
        nextPos = pos;
        direction = pos.subtract(getTranslateX(), getTranslateY()).normalize();
        double angle = calculateAngle(direction.getX(), direction.getY());
        getTransforms().clear();
        Rotate rotator = new Rotate(angle);
        getTransforms().add(rotator);
        changeState("isMoving");
    }

    public boolean canMultiply() {
        return satiety >= 20;
    }

    public void updateInhInfo() {
        nucleus[0] = satiety;
        nucleus[1] = maxSatiety;
        nucleus[2] = hungerThreshold;
        nucleus[3] = modInhChance;
    }

    private void inherit(Bacteria parent) {
        parent.modifications.forEach(next -> {
            float[] params = parent.nucleus;
            if (Math.random() * 100 <= params[3]) {
                addModification(next);
            }
            satiety = (float) (Math.random() * 100 <= 40 ? params[0] + Math.random() * 2 - 1 : params[0]);
            maxSatiety = (float) (Math.random() * 100 <= 40 ? params[1] + Math.random() * 2 - 1 : params[1]);
            hungerThreshold = (float) (Math.random() * 100 <= 40 ? params[2] + Math.random() * 2 - 1 : params[2]);
            modInhChance = (float) (Math.random() * 100 <= 40 ? params[3] + Math.random() * 2 - 1 : params[3]);
        });
    }

    public void addModification(BMod mod) {
        if (!mod.equals(BMod.None)) {
            modifications.add(mod);
            getChildren().add(mod);
        }
        mod.setX(mod.x);
        mod.setY(mod.y);
        modifications.remove(BMod.None);
        satiety -= mod.price;
        if (mod.isOutside) mod.toBack();
    }

    public void showModPane() {
        mp.reinitializeAndShow();
    }


    //    angle between x axis of bacteria and point with signature declared coordinates
    private double calculateAngle(double vexX, double vecY) {
        double angle = new Point2D(vexX, vecY).angle(1, 0);
        return vecY > 0 ? angle : -angle;
    }

    private void changeState(String state) {
        this.state = StateMachine.valueOf(state);
    }

    public void searchForFood(HashSet<NanoFoodPiece> foodList) {
        if (satiety >= maxSatiety - 1) {
            Main.printMessage("entities.bacteria.Bacteria is full");
            return;
        }
        double previous = 1000;
//        Choosing
        Point2D selfPosition = new Point2D(getTranslateX(), getTranslateY());
        for (NanoFoodPiece next : foodList) {
            if (new Point2D(next.getTranslateX(), next.getTranslateY()).distance(selfPosition) < previous) {
                previous = selfPosition.distance(new Point2D(next.getTranslateX(), next.getTranslateY()));
                choose = next;
            }
        }
//        Checking
        if (choose == null) {
            Main.printMessage("food not founded");
        } else {
            caller = "fs";
            currentVelocity = velocity;
            moveToPoint(choose.getPosition());
        }
    }

    private void eats() {
        satiety = (float) (satiety + choose.getFoodValue() > maxSatiety ? maxSatiety : satiety + choose.getFoodValue() * 10);
        choose.destroy();
        waitingT = 120;
        changeState("isWait");
        caller = "idle";
    }

    public Bacteria searchForPartner(HashSet<Bacteria> bList) {
        Bacteria relatePartner = null;
        double previous = 1000;
//        searching
        for (Bacteria next : bList) {
            if (next.position.distance(position) < previous && next != this) {
                previous = position.distance(next.position);
                relatePartner = next;
            }
        }
//        checking
        currentVelocity = velocity;
        return relatePartner;
    }

    public void makeMCP(Bacteria right, Bacteria left) {
        try {
            partner = left;
            partner.partner = right;
            if (right.position.distance(left.position) > 50) {
                moveToPoint(right.position.midpoint(left.position));
                left.moveToPoint(right.position.midpoint(left.position));
                partner.caller = "mcp";
                caller = "mcp";
            } else bornMcp();
        } catch (NullPointerException e) {
            Main.printMessage("Partner not founded");
        }
    }

    private void bornMcp() {
        MCP nmcp = new MCP();
        nmcp.setTranslateX(Main.BTarget.getTranslateX());
        nmcp.setTranslateY(Main.BTarget.getTranslateY());
        Main.mcpMap.add(nmcp);
        Main.root.getChildren().add(nmcp);
        if (Main.BTarget == this || Main.BTarget == partner) {
            destroy();
            partner.destroy();
            Main.BTarget = null;
            Main.MCPTarget = nmcp;
        } else {
            destroy();
            partner.destroy();
        }
    }

    public Bacteria(double x, double y) {
        sprite.setFitHeight(191 * .25);
        sprite.setFitWidth(276 * .25);
        sprite.setX(-(sprite.getFitWidth() / 2));
        sprite.setY(-(sprite.getFitHeight() / 2));
        modifications.add(BMod.None);
        setTranslateX(x);
        setTranslateY(y);

        activeCircle.setFitHeight(75);
        activeCircle.setFitWidth(75);
        activeCircle.setX(-activeCircle.getFitWidth() / 2);
        activeCircle.setY(-activeCircle.getFitWidth() / 2);

        this.setPrefHeight(sprite.getFitHeight());
        this.setPrefWidth(sprite.getFitWidth());
        getChildren().addAll(sprite);
        currentVelocity = velocity;
        sprite.setOnMouseClicked(event -> {
            if (Main.BTarget != this) {
                Main.BTarget = this;
                Main.bMap.forEach(next -> {
                    if (next.getChildren().contains(activeCircle)) {
                        next.getChildren().remove(activeCircle);
                    }
                });
                getChildren().add(activeCircle);
            } else {
                Main.BTarget = null;
                getChildren().remove(activeCircle);
            }
        });
    }

    private void destroy() {
        Main.root.getChildren().remove(this);
        remove = true;
    }

    @Override
    public String toString() {
        String beforeMods = gL("target", "Target") + ":\n" + gL("satiety", "Satiety") + " - " + Math.round(satiety);
        StringBuilder mods = new StringBuilder("\n" + gL("modifications", "Modifications"));
        modifications.forEach(mod -> mods.append("\n-").append(mod.name));
        return beforeMods + mods;
    }

    public float getSatiety() {
        return satiety;
    }

    public void setSatiety(float satiety) {
        this.satiety = satiety;
    }
}
