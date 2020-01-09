package defaultPackage.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import defaultPackage.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leety on 2019/4/11.
 */
public class portGUI extends Application implements Observer {
    private static final double WINDOW_HEIGHT = 500.0;
    private static final double WINDOW_WIDTH = 800.0;
    private static double ZOOMFACTOR = 1;
    private Number mainStageHeight = WINDOW_HEIGHT, mainStageWidth = WINDOW_WIDTH;
    private String address = "";
    private static final String Img_address = "defaultPackage/asset/^name.png";
    private static final String ints = "0123456789";

    private TextField address_In = new TextField();
    private ImageButton startButton = new ImageButton(100, 80, "Start", .95, .82, .38, .8);
    private ImageButton clearButton = new ImageButton(100, 80, "Clear");
    private ImageButton settingButton = new ImageButton(25, 25, new Image(Img_address.replace("^name", "setting")));
    private static ShowPane showPane = new ShowPane(WINDOW_WIDTH, WINDOW_HEIGHT - 150);
    private PortSniff sniff = new PortSniff();

    private final Group root = new Group();

    private SettingGroup settingGroup = new SettingGroup(mainStageHeight, mainStageWidth);

    private final static int[] commonPort = {21, 23, 25, 80, 110, 139, 443, 1433, 1521, 3389, 8080};

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

        sniff.setTimeout(1000);
        sniff.setNoOfThread(1);

        ArrayList<Boolean> allDone = new ArrayList<>();


        startButton.setOnMouseEntered(event -> startButton.setOver(1.0));
        startButton.setOnMouseExited(event -> startButton.setOver(0.8));
        startButton.setOnMouseClicked(event -> {
            address = address_In.getText();
            System.out.println(address);
            showPane.setContext(address);
            sniff.setURL(address);
            //set the sniffer attributes.
            sniff.distributeWorker();
            System.out.println("size: " + sniff.WORKERS.size());
            int threads = 0;
            for (Task t : sniff.WORKERS) {
                int finalThreads = threads;
                t.messageProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.equals("ThreadFinished")) {
                        allDone.add(true);
                        System.out.println("Thread " + finalThreads + " done!");
                    }
                    if (allDone.size() == sniff.getNumOfThread()) {
                        String openedPort = "\r\nPorts opened: \r\n";
                        for (int port : sniff.openPorts) {
                            openedPort += port + " ";
                        }
                        showPane.setContext(openedPort);
                    }
                });
                threads++;
            }
        });

        settingButton.setOnMouseExited(event -> rotateAnim(settingButton, false));
        settingButton.setOnMouseEntered(event -> rotateAnim(settingButton, true));
        settingButton.setOnMouseClicked(event -> {
            root.getChildren().add(settingGroup);
            settingInOutAnim(settingGroup, true);
        });
        settingGroup.returnButton.setOnMouseClicked(event -> {
            settingInOutAnim(settingGroup,false);
            //root.getChildren().remove(settingGroup);
        });
        settingGroup.returnButton.setOnMouseEntered(event -> rotateAnim(settingGroup.returnButton, true));
        settingGroup.commonPort.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                settingGroup.runAll.setSelected(false);
                settingGroup.startPort.setDisable(true);
                settingGroup.endPort.setDisable(true);
                sniff.setRunAll(false);
                sniff.clearPorts();
            } else {
                settingGroup.startPort.setDisable(false);
                settingGroup.endPort.setDisable(false);
            }

            if (!settingGroup.mtcheck.selectedProperty().get() && settingGroup.startPort.getText().equals("") && settingGroup.endPort.getText().equals("")) {
                sniff.setRunAll(false);
            }
        });
        settingGroup.runAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                settingGroup.commonPort.setSelected(false);
                settingGroup.startPort.setDisable(true);
                settingGroup.endPort.setDisable(true);
                sniff.setRunAll(true);
                sniff.setPort(-1, -1);
            } else {
                settingGroup.startPort.setDisable(false);
                settingGroup.endPort.setDisable(false);
            }
        });
        settingGroup.mtcheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                settingGroup.numOfMT.setDisable(false);
            else {
                settingGroup.numOfMT.setDisable(true);
                sniff.setNoOfThread(1);
            }

        });
        AtomicInteger startPort = new AtomicInteger(1);
        AtomicInteger endPort = new AtomicInteger(65535);
        settingGroup.startPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (convertToInt(newValue)) {
                startPort.set(Integer.parseInt(newValue));
                sniff.setPort(startPort.get(), endPort.get());
            }
        });
        settingGroup.endPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (convertToInt(newValue)) {
                endPort.set(Integer.parseInt(newValue));
                sniff.setPort(startPort.get(), endPort.get());
            }
        });
        settingGroup.numOfMT.textProperty().addListener((observable, oldValue, newValue) -> {
            if (convertToInt(newValue))
                sniff.setNoOfThread(Integer.parseInt(newValue));
        });
        settingGroup.timeout.textProperty().addListener((observable, oldValue, newValue) -> {
            if (convertToInt(newValue))
                sniff.setTimeout(Integer.parseInt(newValue));
        });


        clearButton.setOnMouseEntered(event -> clearButton.setOver(1));
        clearButton.setOnMouseExited(event -> clearButton.setOver(0.8));
        clearButton.setOnMouseClicked(event -> {
            showPane.clear();
        });

        root.getChildren().add(startButton);
        root.getChildren().add(clearButton);
        root.getChildren().add(address_In);
        root.getChildren().add(showPane);
        root.getChildren().add(settingButton);
        primaryStage.setScene(mainStage);
        primaryStage.show();
    }

    //X: width, Y: height
    private void resize() {
        settingGroup.root.setLayoutY(mainStageHeight.doubleValue() * 0.3);
        settingGroup.root.setLayoutX(mainStageWidth.doubleValue() / 2 - settingGroup.root.getWidth() / 2);
        settingGroup.resize(mainStageWidth, mainStageHeight);
        address_In.setMinSize(200, 20);
        address_In.setPrefSize(300 * ZOOMFACTOR, 25 * ZOOMFACTOR);
        address_In.setLayoutX(mainStageWidth.doubleValue() / 2 - address_In.getPrefWidth() / 2 - 100);
        address_In.setLayoutY(20);
        settingButton.setLocation(mainStageWidth.doubleValue() - 50, 20);

        startButton.setLocation(mainStageWidth.doubleValue() / 2 + address_In.getPrefWidth() / 2 + 50 - 100, 20);
        if (25 * ZOOMFACTOR <= 20) startButton.setSize(100, 20);
        else startButton.setSize(100, 25 * ZOOMFACTOR);
        clearButton.setLocation(mainStageWidth.doubleValue() / 2 + address_In.getPrefWidth() / 2 + 180 - 100, 20);
        if (25 * ZOOMFACTOR <= 20) clearButton.setSize(100, 20);
        else clearButton.setSize(100, 25 * ZOOMFACTOR);

        showPane.setSize(mainStageWidth.doubleValue(), (mainStageHeight.doubleValue() - 150) * ZOOMFACTOR);
        showPane.setLocation(0, 150);
    }

    public static void setContext(String showText) {
        synchronized (portGUI.class) {
            showPane.setContext(showText);
        }
        System.out.println(showText);
    }

    @Override
    public void update(String showText) {
//        synchronized (portGUI.class){
        showPane.setContext(showText);
        //System.out.println(showText);
    }

    public void rotateAnim(Node node, boolean right) {
        RotateTransition rt = new RotateTransition(Duration.millis(250), node);
        if (right) {
            rt.setFromAngle(0);
            rt.setToAngle(45);
        } else {
            rt.setFromAngle(0);
            rt.setToAngle(-45);
        }
        rt.setAutoReverse(false);
        rt.play();
    }

    private boolean convertToInt(String input) {
        ArrayList<Boolean> arr = new ArrayList<>();
        if (input.length() < 1 || input.equals("")) return false;

        for (int i = 0; i < input.length(); i++) {
            arr.add(ints.contains(input.charAt(i) + ""));
        }
        if (arr.contains(false)) return false;
        else return true;
    }

    private void settingInOutAnim(Node node, boolean in) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.5f);
        ft.setToValue(1.0f);
        ft.setAutoReverse(false);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        if (in) {
            tt.setFromY(50);
            tt.setToY(0);
        } else {
            tt.setFromY(0);
            tt.setToY(50);
        }

        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(ft, tt);
        pt.setCycleCount(1);
        pt.play();
        pt.setOnFinished(event -> {
            if (!in)root.getChildren().remove(settingGroup);
        });
    }
}
//todo: remove the nested classes