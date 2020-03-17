package defaultPackage.gui;

import defaultPackage.PortSniff;
import javafx.scene.Group;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;
/**************************************************************************
 *                                                                        *
 *                         PortSniffer v 2.0                              *
 *                    Main class for server analysis                      *
 *                                                                        *
 *                       Copyright (c) 2020 LYL                           *
 *                            @author LYL                                 *
 *                            @version 2.0                                *
 **************************************************************************/
class analysisGUI extends Group {
    private double WINDOW_HEIGHT, WINDOW_WIDTH;

    private PortSniff sniff = new PortSniff();
    private String address = "";
    private AtomicReference<String> ht = new AtomicReference<>("http://");
    private TextField address_In = new TextField();
    private ShowPane contextHolder = new ShowPane();
    private ImageButton startButton = new ImageButton(100, 80, "Start", .1, .3, .9, .8);
    private ImageButton clearButton = new ImageButton(100, 80, "Clear");
    private MenuButton menuButton = new MenuButton();
    private Stage primaryStage;

    public analysisGUI(double width, double height) {
        this.WINDOW_HEIGHT = height;
        this.WINDOW_WIDTH = width;
        contextHolder.setSize(width, height);
        init();
        listeners();
    }

    public analysisGUI(double width, double height, Stage primaryStage) {
        this.WINDOW_HEIGHT = height;
        this.WINDOW_WIDTH = width;
        this.primaryStage = primaryStage;
        contextHolder.setSize(width, height);
        init();
        listeners();
    }

    public analysisGUI(){
        listeners();
    }

    public void boundStage(Stage stage){
        this.primaryStage = stage;
    }

    public void setSize(double width, double height) {
        this.WINDOW_HEIGHT = height;
        this.WINDOW_WIDTH = width;
        init();
    }

    private void init() {
        contextHolder.setSize(this.WINDOW_WIDTH,this.WINDOW_HEIGHT-150);
        address_In.setPromptText("web URL or IP. e.g. www.foo.com or 127.0.0.1");
        address_In.setStyle("-fx-background-color: transparent;-fx-border-style: solid;-fx-border-width: 0 0 2 0;-fx-border-color: #999999;");
        resize(this.WINDOW_WIDTH,this.WINDOW_HEIGHT,1);

        contextHolder.setTheme(ShowPane.TRANSPARENT);
        contextHolder.setContext("test");

        this.getChildren().addAll(contextHolder, address_In, startButton, clearButton);
        startButton.requestFocus();
    }

    public void resize(Number width, Number height, double ZOOMFACTOR) {
        this.WINDOW_HEIGHT = height.doubleValue();
        this.WINDOW_WIDTH = width.doubleValue();

        //System.out.println(width.doubleValue()+" "+height.doubleValue());
        contextHolder.setSize(width.doubleValue()-100, height.doubleValue() - 150);
        contextHolder.setLocation(50,150);

        address_In.setMinSize(200, 20);
        address_In.setPrefSize(300 * ZOOMFACTOR, 25 * ZOOMFACTOR);
        address_In.setLayoutX((width.doubleValue() - address_In.getPrefWidth()) / 2 - 100);
        address_In.setLayoutY(20);

        startButton.setLocation((width.doubleValue()+ address_In.getPrefWidth()) / 2 + 50 - 100, 20);
        if (25 * ZOOMFACTOR <= 20) startButton.setSize(100, 20);
        else startButton.setSize(100, 25 * ZOOMFACTOR);

        clearButton.setLocation((width.doubleValue() + address_In.getPrefWidth()) / 2 + 180 - 100, 20);
        if (25 * ZOOMFACTOR <= 20) clearButton.setSize(100, 20);
        else clearButton.setSize(100, 25 * ZOOMFACTOR);
    }

    private void listeners(){
        startButton.setOnMouseEntered(event -> startButton.setOver(1.0));
        startButton.setOnMouseExited(event -> startButton.setOver(0.8));
        startButton.setOnMouseClicked(event -> {
            startButton.requestFocus();
        });
        clearButton.setOnMouseEntered(event -> clearButton.setOver(1));
        clearButton.setOnMouseExited(event -> clearButton.setOver(0.8));
        clearButton.setOnMouseClicked(event -> {
            startButton.setDisable(false);
            for(PortSniff.SniffTask s : sniff.WORKERS){
                s.cancel();
            }
        });
    }

}

