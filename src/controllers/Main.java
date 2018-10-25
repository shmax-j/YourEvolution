package controllers;

import entities.MCP.MCP;
import entities.bacteria.Bacteria;
import food.NanoFoodPiece;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import static controllers.ErrorWindow.throwError;


public class Main extends Application {

    /*
      Plans
      help under the cursor
      food searching mechanism (circle - radar) graphical
     */
    private static AnimationTimer loop;
    private static boolean isPaused = false;
    private static Properties startProps = new Properties();
    private static Properties language = new Properties();
    private static BorderPane UIRoot = new BorderPane();
    public static Pane root = new Pane();
    private double interval_food = 50;
    private static boolean targetFocusMode = false;
    public static HashSet<NanoFoodPiece> foodList = new HashSet<>();
    public static HashSet<Bacteria> bMap = new HashSet<>();
    public static HashSet<MCP> mcpMap = new HashSet<>();
    public static Bacteria BTarget;
    public static MCP MCPTarget;
    private boolean cameraMoveF, cameraMoveB, cameraMoveR, cameraMoveL;
    private byte cameraSpeed = 2;

    private static Label message = new Label("");
    private static HBox messageBox = new HBox();
    private static float messageTimer = 20;

    @Override
    public void start(Stage primaryStage) {
        try{
            FileInputStream fis = new FileInputStream("controllers/start.properties");
            startProps.load(fis);
            fis.close();
        }catch (IOException e){
            throwError("Start properties not founded in /controllers/start.properties(\"start.properties\")");
        }

        localizationInit();

        VBox bTools = new VBox(40);
        VBox MCPTools = new VBox(40);
        VBox bInfoTable = new VBox(0);
        VBox center = new VBox(5);

//        Alignment
        bTools.setAlignment(Pos.TOP_RIGHT);
        bTools.setMaxHeight(150);
        bInfoTable.setAlignment(Pos.TOP_LEFT);
        center.setAlignment(Pos.CENTER);
        messageBox.setAlignment(Pos.CENTER);

        MCPTools.setAlignment(Pos.TOP_RIGHT);
        MCPTools.setMaxHeight(50);

//        icons init
        Image ico_multiply = new Image("controllers/icons/bacteria/multiply.png");
        Image ico_cameraFocus = new Image("controllers/icons/cameraFocus.png");
        Image ico_eat = new Image("controllers/icons/eat.png");
        Image ico_addModification = new Image("controllers/icons/bacteria/addModification.png");
        Image ico_makeMCP = new Image("controllers/icons/bacteria/makeMCP.png");
        Image ico_deselect = new Image("controllers/icons/main/deselect.png");

//        Bacteria controls
        IconButton mtp = new IconButton(ico_multiply);
        IconButton food = new IconButton(ico_eat);
        IconButton amd = new IconButton(ico_addModification);
        IconButton mcp = new IconButton(ico_makeMCP);
        IconButton focus = new IconButton(ico_cameraFocus);
        IconButton deselect = new IconButton(ico_deselect);

//        entities.MCP controls
        IconButton MCPFocus = new IconButton(ico_cameraFocus);
        IconButton MCPEats = new IconButton(ico_eat);
        IconButton MCPDeselect = new IconButton(ico_deselect);

        Label targetInfo = new Label("Choose the target.");

        UIRoot.setLeft(bInfoTable);
        UIRoot.getChildren().add(root);
        UIRoot.setBackground(Background.EMPTY);

        Scene scene = new Scene(UIRoot,600,600, Color.rgb(202, 239, 242));
        primaryStage.setScene(scene);

        //First item initialization
        Bacteria bt = new Bacteria(scene.getWidth()/2, scene.getHeight()/2);
        NanoFoodPiece ft = new NanoFoodPiece();
        bMap.add(bt);
        foodList.add(ft);

//        Keyboard controls
        scene.setOnKeyPressed(event -> {
            switch (event.getCode().getName()){
                case "W":
                    targetFocusMode = false;
                    cameraMoveF = true;
                    break;
                case "S":
                    targetFocusMode = false;
                    cameraMoveB = true;
                    break;
                case "D":
                    targetFocusMode = false;
                    cameraMoveR = true;
                    break;
                case "A":
                    targetFocusMode = false;
                    cameraMoveL = true;
                    break;
                case "Shift":
                    cameraSpeed = 5;
                    break;
                case "P":
                    if (isPaused){
                        resume();
                    }else pause();
                    break;
                case "E":
                    try{
                        BTarget.searchForFood(foodList);
                    }catch (NullPointerException e){
                        break;
                    }
                    break;
                case "Tab":
                    try {
                        BTarget.showModPane();
                    }catch (NullPointerException e){
                        break;
                    }
                    break;
                default:
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode().getName()){
                case "W":
                    cameraMoveF = false;
                    break;
                case "S":
                    cameraMoveB = false;
                    break;
                case "D":
                    cameraMoveR = false;
                    break;
                case "A":
                    cameraMoveL = false;
                    break;
                case "Shift":
                    cameraSpeed = 2;
                    break;
                default:
                    break;
            }
        });

//        Bacteria button-controls
        mtp.setOnMouseClicked(event -> {
            if (BTarget.canMultiply()){
                BTarget.updateInhInfo();
                BTarget.setSatiety(BTarget.getSatiety()/2);
                Bacteria nowTarget = new Bacteria(BTarget.getTranslateX(), BTarget.getTranslateY(), BTarget);
                bMap.add(nowTarget);
                root.getChildren().add(nowTarget);
            }
        });
        food.setOnMouseClicked(event -> BTarget.searchForFood(foodList));
        amd.setOnMouseClicked(event -> BTarget.showModPane());
        mcp.setOnMouseClicked(event -> BTarget.makeMCP(BTarget, BTarget.searchForPartner(bMap)));
        focus.setOnMouseClicked(event -> targetFocusMode = !targetFocusMode);
        deselect.setOnMouseClicked(event -> BTarget = null);

//        entities.MCP button-controls
        MCPFocus.setOnMouseClicked(event -> targetFocusMode = !targetFocusMode);
        MCPEats.setOnMouseClicked(event -> MCPTarget.eat());
        MCPDeselect.setOnMouseClicked(event -> MCPTarget = null);

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                bMap.removeIf(r->r.remove);

//                Updates
                bMap.forEach(tag ->tag.update(foodList));
                mcpMap.forEach(MCP::update);

                UIRoot.setRight(BTarget !=null?bTools: MCPTarget !=null?MCPTools:null);

//                Target focus
                if (targetFocusMode){
                    if (BTarget !=null){
                        root.setTranslateX(-BTarget.getTranslateX()+scene.getWidth()/2);
                        root.setTranslateY(-BTarget.getTranslateY()+scene.getHeight()/2);
                    }else if (MCPTarget!=null){
                        root.setTranslateX(-MCPTarget.getTranslateX()+300);
                        root.setTranslateY(-MCPTarget.getTranslateY()+300);
                    }

                }

                targetInfo.setText(BTarget == null?MCPTarget==null?gL("choose_target", "Choose target."): MCPTarget.toString():BTarget.toString());

                if (interval_food==0){
                    interval_food=200;
                    NanoFoodPiece beenAdded = new NanoFoodPiece();
                    foodList.add(beenAdded);
                    root.getChildren().add(beenAdded);
                }
                interval_food--;

                messageTimer--;
                if (messageTimer<0){
                    UIRoot.setBottom(null);
                }

//                Removing from root
                foodList.forEach(next -> {
                    next.toBack();
                    if (next.isEaten())root.getChildren().remove(next);
                });
//                from list
                foodList.removeIf(nfp -> nfp.isEaten());

//                Camera moving
                if (cameraMoveF)root.setTranslateY(root.getTranslateY()+cameraSpeed);
                if (cameraMoveB)root.setTranslateY(root.getTranslateY()-cameraSpeed);
                if (cameraMoveR)root.setTranslateX(root.getTranslateX()-cameraSpeed);
                if (cameraMoveL)root.setTranslateX(root.getTranslateX()+cameraSpeed);

//                Debug


            }
        };
        root.getChildren().addAll(bt,ft);
        bInfoTable.getChildren().add(targetInfo);
        bTools.getChildren().addAll(food,mtp,amd,mcp,focus,deselect);
        MCPTools.getChildren().addAll(MCPFocus,MCPEats,MCPDeselect);
        primaryStage.setTitle(gL("title", "Game"));
        messageBox.getChildren().add(message);
        loop.start();
        primaryStage.show();
    }

    public static void printMessage(String message) {
        Main.message.setText(message);
        UIRoot.setBottom(messageBox);
        messageTimer = 100;
    }

    static void pause(){
        isPaused = true;
        printMessage("Paused");
        loop.stop();
    }

    static void resume(){
        isPaused = false;
        loop.start();
        printMessage("");
    }

    private static void localizationInit() {
        FileInputStream languagesFIS;
        try{
            languagesFIS = new FileInputStream("localization/languages.properties");
        }catch (FileNotFoundException l){
            throwError("File \"languages\" not founded in /localization/");
            languagesFIS = null;
        }
        Properties langsList = new Properties();
        try {
            langsList.load(languagesFIS);
        }catch (IOException e){
            throwError("Cant load localizations list from file \"languages.properties\" in /localization/");
        }
        try{
            language.clear();
            language.load(new FileInputStream(langsList.getProperty(startProps.getProperty("language"))));
        } catch (FileNotFoundException e) {
            throwError("Some localization files don`t founded in /localization/");
        }catch (IOException e){
            throwError("Cant load file with selected in start properties localization");
        }
    }

    public static String gL(String k, String d){
        return language.getProperty(k,d);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
