package shmax.controllers;

import shmax.entities.MCP.MCP;
import shmax.entities.bacteria.Bacteria;
import shmax.food.NanoFoodPiece;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Properties;

import static shmax.ErrorWindowKt.throwError;
import static shmax.component.ComponentKt.iconButton;
import static shmax.util.UtilKt.*;
import static shmax.controllers.AddModificationModalKt.*;


public class Main {

    /*
      Plans
      help under the cursor
      shmax.food searching mechanism (circle - radar) graphical
     */
    private static AnimationTimer loop;
    private static boolean isPaused = false;
    private static Properties startProps = new Properties();
    private static Properties langsList = new Properties();
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

    public void start(Stage primaryStage) throws URISyntaxException, FileNotFoundException {
        try{
            FileInputStream fis = new FileInputStream(new File(resource("start.properties")));;
            startProps.load(fis);
            fis.close();
        }catch (IOException e){
            throwError("Start properties not found");
        }

        try {
            FileInputStream fis = fisResource("languages/language.properties");
            langsList.load(fis);
            fis.close();
        } catch (IOException e) {
            throwError("Languages list not found in \"localization/language.properties\"" +
                    "\nlanguage reset to English");
        }

        localizationInit();

        VBox bTools = new VBox(0);
        VBox MCPTools = new VBox(0);
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
        Image ico_multiply = new Image(fisResource("icons/bacteria/multiply.png"));
        Image ico_cameraFocus = new Image(fisResource("icons/cameraFocus.png"));
        Image ico_eat = new Image(fisResource("icons/eat.png"));
        Image ico_addModification = new Image(fisResource("icons/bacteria/addModification.png"));
        Image ico_makeMCP = new Image(fisResource("icons/bacteria/makeMCP.png"));
        Image ico_deselect = new Image(fisResource("icons/main/deselect.png"));

//        Bacteria controls
        var mtp = iconButton(ico_multiply, "Multiply");
        var food = iconButton(ico_eat, "Eat (E)");
        var amd = iconButton(ico_addModification, "Add modification (Tab)");
        var mcp = iconButton(ico_makeMCP, "Make MCP");
        var focus = iconButton(ico_cameraFocus, "Camera focus (F)");
        var deselect = iconButton(ico_deselect, "Deselect");

//        shmax.entities.MCP controls
        var MCPFocus = iconButton(ico_cameraFocus);
        var MCPEats = iconButton(ico_eat);
        var MCPDeselect = iconButton(ico_deselect);

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
                        showAddModificationModal();
                    }catch (NullPointerException e){
                        break;
                    }
                    break;
                case "N":
                    try {
                        save();
                    } catch (ParserConfigurationException | TransformerException e) {
                        e.printStackTrace();
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
        food.setOnAction(event -> BTarget.searchForFood(foodList));
        amd.setOnAction(event -> showAddModificationModal());
        mcp.setOnAction(event -> BTarget.makeMCP(BTarget, BTarget.searchForPartner(bMap)));
        focus.setOnAction(event -> targetFocusMode = !targetFocusMode);
        deselect.setOnAction(event -> BTarget = null);

//        shmax.entities.MCP button-controls
        MCPFocus.setOnAction(event -> targetFocusMode = !targetFocusMode);
        MCPEats.setOnAction(event -> MCPTarget.eat());
        MCPDeselect.setOnAction(event -> MCPTarget = null);

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
                    if (next.getEaten())root.getChildren().remove(next);
                });
//                from list
                foodList.removeIf(NanoFoodPiece::getEaten);

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
        primaryStage.setTitle(gL("title", "YE"));
        messageBox.getChildren().add(message);
        loop.start();
        primaryStage.show();
    }

    private static void save() throws ParserConfigurationException, TransformerException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("SaveFile");
        document.appendChild(root);
        Element bacterias = document.createElement("bacterias");
        root.appendChild(bacterias);

        bMap.forEach(b -> {
            Element bacteria = document.createElement("sprites/bacteria");
            bacterias.appendChild(bacteria);
            bacteria.setAttribute("position", b.getTranslateX()+b.getTranslateY()+"");
            bacteria.setAttribute("satiety", b.getSatiety()+"");
        });

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        StreamResult result =  new StreamResult(new File("save.xml"));
        transformer.transform(source, result);
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
        try {
            FileInputStream langInput = new FileInputStream(new File(resource(langsList.getProperty(startProps.getProperty("language")))));
            language.load(langInput);
            langInput.close();
        } catch (IOException e) {
            throwError("File not founded or was corrupted "+langsList.getProperty(startProps.getProperty("language")));
        }
    }

    public static String gL(String k, String d){
        return language.getProperty(k,d);
    }
}
