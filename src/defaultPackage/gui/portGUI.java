package defaultPackage.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import defaultPackage.*;

import java.io.IOException;
import java.net.*;

/**
 * Created by leety on 2019/4/11.
 */
public class portGUI extends Application implements Observer{
    private static final double WINDOW_HEIGHT = 500.0;
    private static final double WINDOW_WIDTH = 800.0;
    private static double ZOOMFACTOR = 1;
    private Number mainStageHeight = WINDOW_HEIGHT, mainStageWidth = WINDOW_WIDTH;
    private String address = "";

    private TextField address_In = new TextField();
    private ImageButton startButton = new ImageButton(100, 80, "Start", .95, .82, .38, .8);
    private ImageButton clearButton = new ImageButton(100,80,"Clear");
    private static ShowPane showPane = new ShowPane(WINDOW_WIDTH,WINDOW_HEIGHT-150);
    private PortSniff sniff = new PortSniff();

    private final Group root = new Group();

    Socket s = new Socket();
    private final static int[] commonPort = {21,23,25,80,110,139,443,1433,1521,3389,8080};

    private void setZoomFactor(Number mainStageWidth, Number mainStageHeight) {
        double width = mainStageWidth.doubleValue() / WINDOW_WIDTH, height = mainStageHeight.doubleValue() / WINDOW_HEIGHT;
        if (width < height)
            ZOOMFACTOR = Math.round(width * 100.0) / 100.0;
        else
            ZOOMFACTOR = Math.round(height * 100.0) / 100.0;
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Port scanner");
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(720);
        Scene mainStage = new Scene(root, mainStageWidth.doubleValue(), mainStageHeight.doubleValue());
        sniff.reg(this);
        //-- resize listener --//
        mainStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            mainStageHeight = newValue;
            setZoomFactor(mainStageWidth, mainStageHeight);
            resize();
        });
        mainStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            mainStageWidth = newValue;
            setZoomFactor(mainStageWidth, mainStageHeight);
            resize();
        });
        //-- resize listener --//

        resize();
        address_In.setPromptText("web URL. e.g. www.foo.com or https://foo.com");
        sniff.setRunAll(true);
        sniff.setTimeout(10);
        sniff.setNoOfThread(100);

        startButton.setOnMouseEntered(event -> startButton.setOver(1.0));
        startButton.setOnMouseExited(event -> startButton.setOver(0.8));
        startButton.setOnMouseClicked(event -> {
            address = address_In.getText();
            System.out.println(address);
            showPane.setContext(address);

            sniff.setURL(address);
            //set the sniffer attributes.
            sniff.distributeWorker();
        });

        clearButton.setOnMouseEntered(event -> clearButton.setOver(1));
        clearButton.setOnMouseExited(event -> clearButton.setOver(0.8));
        clearButton.setOnMouseClicked(event ->{
            showPane.clear();
        });

        root.getChildren().add(startButton);
        root.getChildren().add(clearButton);
        root.getChildren().add(address_In);
        root.getChildren().add(showPane);
        primaryStage.setScene(mainStage);
        primaryStage.show();
    }

    private void resize() {
        address_In.setMinSize(200, 20);
        address_In.setPrefSize(300 * ZOOMFACTOR, 25 * ZOOMFACTOR);
        address_In.setLayoutX(mainStageWidth.doubleValue() / 2 - address_In.getPrefWidth() / 2 - 85);
        address_In.setLayoutY(20);

        startButton.setLocation(mainStageWidth.doubleValue() / 2 + address_In.getPrefWidth() / 2 + 50 - 85, 20);
        if (25 * ZOOMFACTOR <= 20) startButton.setSize(100, 20);
        else startButton.setSize(100, 25 * ZOOMFACTOR);
        clearButton.setLocation(mainStageWidth.doubleValue() / 2 + address_In.getPrefWidth() / 2 + 180 - 85,20);
        if (25 * ZOOMFACTOR <= 20) clearButton.setSize(100, 20);
        else clearButton.setSize(100, 25 * ZOOMFACTOR);

        showPane.setSize(mainStageWidth.doubleValue(), (mainStageHeight.doubleValue()-150)*ZOOMFACTOR) ;
        showPane.setLocation(0, 150);
    }

    public static void setContext(String showText){
        synchronized (portGUI.class){
            showPane.setContext(showText);
        }
        System.out.println(showText);
    }

    @Override
    public void update(String showText) {
//        synchronized (portGUI.class){
            showPane.setContext(showText);
        System.out.println(showText);
    }
}
//todo: remove the nested classes