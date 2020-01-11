package defaultPackage.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


public class SettingGroup extends StackPane {
    private Number mainStageHeight, mainStageWidth;
    private static final String Img_address = "defaultPackage/asset/^name.png";

    public ImageButton returnButton = new ImageButton(20,20,new Image(Img_address.replace("^name","reload")));
    public TextField startPort = new TextField();
    public TextField endPort = new TextField();
    private GridPane ports = new GridPane();
    public GridPane root = new GridPane();
    private GridPane mtfeild = new GridPane();
    private GridPane checkPort = new GridPane();
    public CheckBox commonPort = new CheckBox("Common port");
    public CheckBox mtcheck = new CheckBox("Enable MT");
    public TextField numOfMT = new TextField();
    public CheckBox runAll = new CheckBox("run all ports");
    private GridPane timeoutSet = new GridPane();
    public TextField timeout = new TextField();
    public RadioButton http = new RadioButton("http");
    public RadioButton https = new RadioButton("https");
    private GridPane requestmode = new GridPane();

    private VBox vBox = new VBox();
    private HBox hBox = new HBox();

    private void init(){
        returnButton.setLocation(30,30);
        returnButton.setMaxSize(20,20);
        commonPort.setSelected(true);
        startPort.setPromptText("start port, 1 ~ 65535");
        endPort.setPromptText("end port, 1 ~ 65535");

        numOfMT.setPromptText("Number of core to run.");
        numOfMT.setDisable(true);

        startPort.setDisable(true);
        endPort.setDisable(true);

        checkPort.setHgap(10);
        checkPort.setVgap(5);
        checkPort.add(commonPort,0,0);
        checkPort.add(runAll,1,0);

        ports.setHgap(5);
        ports.setVgap(5);
        ports.add(new Label("Setting ports range"), 0,0);
        ports.add(startPort,0,1);
        ports.add(new Label("-"),1,1);
        ports.add(endPort,2,1);

        mtfeild.setHgap(5);
        mtfeild.setVgap(5);
        mtfeild.add(new Label("This option will increase efficiency, \r\nbut highly depend on hardware."),0,0);
        mtfeild.add(numOfMT,0,1);

        timeout.setPromptText("default 1s, min 500ms");
        timeoutSet.add(new Label("Time out"),0,0);
        timeoutSet.add(timeout,0,1);
        timeoutSet.add(new Label(" ms"),1,1);

        ToggleGroup group = new ToggleGroup();
        http.setToggleGroup(group);
        http.setSelected(true);
        https.setToggleGroup(group);
        requestmode.setHgap(5);
        requestmode.setVgap(5);
        requestmode.add(http,0,1);
        requestmode.add(https,1,1);

        root.add(checkPort,0,0);
        root.add(ports,0,1);
        root.add(timeoutSet,0,2);
        root.add(mtcheck,1,0);
        root.add(mtfeild,1,1);
        root.add(requestmode,1,2);
        root.setVgap(10);
        root.setHgap(50);
    }

    public void resize(Number width, Number height){
        this.setHeight(height.doubleValue());
        this.setWidth(width.doubleValue());
        vBox.setMinHeight(height.doubleValue());
        vBox.setMaxWidth(0);
        hBox.setMinWidth(width.doubleValue());
        hBox.setMaxHeight(0);

        setMargin(returnButton,new Insets(0,width.doubleValue()-45,height.doubleValue()-45,0));
    }

    public SettingGroup(Number height, Number width){
        this.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255),null,null)));
        root.setMinSize(600,300);
        root.setMaxSize(600,300);
        this.setAlignment(Pos.CENTER);
        resize(width,height);
        init();
        this.getChildren().addAll(root,returnButton, vBox, hBox);
    }
}
