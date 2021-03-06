package defaultPackage.gui;

import defaultPackage.*;
import defaultPackage.Hackage.ARPDisconnect;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**************************************************************************
 *                                                                        *
 *                         PortSniffer v 1.0                              *
 *                         Main class for GUI                             *
 *                                                                        *
 *                  Backup class, now move to sniffGUI                    *
 *                       Copyright (c) 2020 LYL                           *
 *                            @author LYL                                 *
 *                            @version 1.0                                *
 **************************************************************************/
public class portGUI extends Application implements Observer {
    private static final double WINDOW_HEIGHT = 500.0;
    private static final double WINDOW_WIDTH = 800.0;
    private static double ZOOMFACTOR = 1;
    private static final String ints = "0123456789";
    private static final String title = "Port scanner";

    private PortSniff sniff = new PortSniff();
    private Number mainStageHeight = WINDOW_HEIGHT, mainStageWidth = WINDOW_WIDTH;
    private String address = "";
    private AtomicReference<String> ht = new AtomicReference<>("http://");
    private TextField address_In = new TextField();
    private ImageButton startButton = new ImageButton(100, 80, "Start", .95, .82, .38, .8);
    private ImageButton clearButton = new ImageButton(100, 80, "Cancel");
    private ShowPane showPane = new ShowPane(WINDOW_WIDTH, WINDOW_HEIGHT - 150);
    private ProgressBar progressBar = new ProgressBar(WINDOW_WIDTH - 100, 5, true);
    private MenuButton menuButton = new MenuButton();
    private MenuBar menuRoot = new MenuBar();
    private Menu fileM = new Menu("File");
    private Menu viewM = new Menu("View");

    private String currentType = "sniff";

    private final Group root = new Group();

    private SettingGroup settingGroup = new SettingGroup(mainStageHeight, mainStageWidth);
    private boolean showSetting = false;

    private void setZoomFactor(Number mainStageWidth, Number mainStageHeight) {
        double width = mainStageWidth.doubleValue() / WINDOW_WIDTH, height = mainStageHeight.doubleValue() / WINDOW_HEIGHT;
        if (width < height) ZOOMFACTOR = Math.round(width * 100.0) / 100.0;
        else ZOOMFACTOR = Math.round(height * 100.0) / 100.0;
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(title);
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(720);
        Scene mainStage = new Scene(root, mainStageWidth.doubleValue(), mainStageHeight.doubleValue());
        sniff.reg(this);

        menuRoot.setUseSystemMenuBar(true);
        //-- resize listener start  --//
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
        //-- resize listener end --//

        //-- component initial start --//
        resize();
        address_In.setPromptText("web URL or IP. e.g. www.foo.com or 127.0.0.1");
        //set style//
        address_In.setStyle("-fx-background-color: transparent;-fx-border-style: solid;-fx-border-width: 0 0 2 0;-fx-border-color: #999999;");
        sniff.setTimeout(1000);
        sniff.setNoOfThread(1);
        settingGroup.setLayoutY(mainStageHeight.doubleValue());
        //-- component initial end --//

        //-- component listener setting start --//
        address_In.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER)
                sniffRun(primaryStage);
        });
        startButton.setOnMouseEntered(event -> startButton.setOver(1.0));
        startButton.setOnMouseExited(event -> startButton.setOver(0.8));
        startButton.setOnMouseClicked(event -> sniffRun(primaryStage));
        menuButton.setOnMouseClicked(event -> {
            if (menuButton.onClick()) settingInOutAnim(settingGroup, true);
            else settingInOutAnim(settingGroup, false);
        });
        //-- Setting page component listener start --//
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
                sniff.setRunAll(false);
            }
        });
        settingGroup.mtcheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                settingGroup.numOfMT.setDisable(false);
                if (convertToInt(settingGroup.numOfMT.getText()))
                    sniff.setNoOfThread(Integer.parseInt(settingGroup.numOfMT.getText()));
            } else {
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

        settingGroup.http.selectedProperty().addListener((observable, oldValue, newValue) -> ht.set("http://"));
        settingGroup.https.selectedProperty().addListener((observable, oldValue, newValue) -> ht.set("https://"));
        //-- Setting page component listener end --//

        clearButton.setOnMouseEntered(event -> clearButton.setOver(1));
        clearButton.setOnMouseExited(event -> clearButton.setOver(0.8));
        clearButton.setOnMouseClicked(event -> {
            startButton.setDisable(false);
            for(PortSniff.SniffTask s : sniff.WORKERS){
                s.cancel();
            }
        });
        //-- component listener setting end --//


        //-- Menu selection --//
        MenuItem Setting = new MenuItem("Setting");
        Setting.setOnAction(event -> {
            if (menuButton.onClick()) {
                //root.getChildren().add(settingGroup);
                settingInOutAnim(settingGroup, true);
            } else {
                settingInOutAnim(settingGroup, false);
            }
        });
        MenuItem analyze = new MenuItem("Analyze");
        MenuItem portSniff = new MenuItem("Port Sniffer");
        //analyze.setOnAction(event -> changeType("analysis"));
        //portSniff.setOnAction(event -> changeType("sniff"));
        fileM.getItems().addAll(Setting);
        viewM.getItems().addAll(analyze, new SeparatorMenuItem(), portSniff);
        menuRoot.getMenus().addAll(fileM, viewM);
        //-- Menu selection --//
        //mainStage.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        root.getChildren().addAll(startButton, clearButton, address_In, showPane, progressBar, settingGroup, menuButton, menuRoot);
        primaryStage.setScene(mainStage);
        primaryStage.show();
    }

    //X: width, Y: height
    private void resize() {
        settingGroup.root.setLayoutY(mainStageHeight.doubleValue() * 0.3);
        settingGroup.root.setLayoutX((mainStageWidth.doubleValue() - settingGroup.root.getWidth()) / 2);
        settingGroup.resize(mainStageWidth, mainStageHeight.doubleValue());
        if (!showSetting)
            settingGroup.setLayoutY(mainStageHeight.doubleValue());

        address_In.setMinSize(200, 20);
        address_In.setPrefSize(300 * ZOOMFACTOR, 25 * ZOOMFACTOR);
        address_In.setLayoutX((mainStageWidth.doubleValue() - address_In.getPrefWidth()) / 2 - 100);
        address_In.setLayoutY(20);

        //settingButton.setLocation(50, 20);
        menuButton.setLocation(50, 25);
        startButton.setLocation((mainStageWidth.doubleValue() + address_In.getPrefWidth()) / 2 + 50 - 100, 20);
        if (25 * ZOOMFACTOR <= 20) startButton.setSize(100, 20);
        else startButton.setSize(100, 25 * ZOOMFACTOR);

        clearButton.setLocation((mainStageWidth.doubleValue() + address_In.getPrefWidth()) / 2 + 180 - 100, 20);
        if (25 * ZOOMFACTOR <= 20) clearButton.setSize(100, 20);
        else clearButton.setSize(100, 25 * ZOOMFACTOR);

        showPane.setSize(mainStageWidth.doubleValue() - 50, mainStageHeight.doubleValue() - 200);
        showPane.setLocation(25, 150);

        progressBar.setSize(mainStageWidth.doubleValue() - 100, 5);
        progressBar.setLocation(mainStageWidth.doubleValue() / 2 - progressBar.getComponentWidth() / 2, mainStageHeight.doubleValue() - 30);
    }

    @Override
    public void update(String showText) {
        showPane.setContext(showText);
    }

    //-- Animation section --//
    private void rotateAnim(Node node, boolean right) {
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

    private void settingInOutAnim(Node node, boolean in) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.8f);
        ft.setToValue(1.0f);
        ft.setAutoReverse(false);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        if (in) {
            settingGroup.setLayoutY(mainStageHeight.doubleValue());
            tt.setFromY(0);
            tt.setToY(-mainStageHeight.doubleValue());
            showSetting = true;
        } else {
            settingGroup.setLayoutY(0);
            tt.setFromY(0);
            tt.setToY(mainStageHeight.doubleValue());
            showSetting = false;
        }

        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(ft, tt);
        pt.setCycleCount(1);
        pt.play();
    }
    //-- Animation section --//

    private boolean convertToInt(String input) {
        ArrayList<Boolean> arr = new ArrayList<>();
        if (input.length() < 1 || input.equals("")) return false;
        for (int i = 0; i < input.length(); i++) arr.add(ints.contains(input.charAt(i) + ""));
        if (arr.contains(false)) return false;
        else return true;
    }

    private String getTime(double duration) {
        int h, m, s;
        h = (int) duration / 3600;
        m = ((int) duration / 60) % 60;
        s = (int) duration % 60;
        String time = "^h hours ^m minutes ^s seconds.";
        time = time.replace("^h", h + "").replace("^m", m + "").replace("^s", s + "");
        time = h == 1 ? time.replace("hours", "hour") : time;
        time = m == 1 ? time.replace("minutes", "minute") : time;
        time = s == 1 ? time.replace("seconds", "second") : time;
        return time;
    }

    private void sniffRun(Stage primaryStage) {
        if(!checkIllegal(address_In.getText())) {
            address_In.setStyle("-fx-background-color: transparent;-fx-border-style: solid;-fx-border-width: 0 0 2 0;-fx-border-color: #ee6666");
            address_In.setPromptText("Illegal input! Use IP either URL.");
            address_In.setText("");
        }
        else{
            address_In.setStyle("-fx-background-color: transparent;-fx-border-style: solid;-fx-border-width: 0 0 2 0;-fx-border-color: #999999");
            address_In.setPromptText("web URL or IP. e.g. www.foo.com or 127.0.0.1");
            primaryStage.setTitle(title+" running...");
            showPane.clear();
            progressBar.reset();
            startButton.setDisable(true);
            ArrayList<Boolean> allDone = new ArrayList<>();
            ArrayList<Integer> openPorts = new ArrayList<>();
            ArrayList<Boolean> allCancel = new ArrayList<>();
            address = ht + address_In.getText();
            System.out.println(address);
            showPane.setContext(address);
            sniff.setURL(address);
            final long startTimeStamp = System.currentTimeMillis();
            //set the sniffer attributes.
            sniff.distributeWorker();
            System.out.println("size: " + sniff.WORKERS.size());
            //-- Thread status listener start --//
            for (PortSniff.SniffTask t : sniff.WORKERS) {
                AtomicReference<Double> currentProgress = new AtomicReference<>((double) 0);
                t.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (t.getProgress() >= currentProgress.get() && allCancel.size()<1) {
                        currentProgress.set(t.getProgress());
                        progressBar.setUpdate(currentProgress.get());
                    }
                });
                t.messageProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue.equals("Cancelled")) allCancel.add(true);
                    if(allCancel.size()==sniff.getNumOfThread()) primaryStage.setTitle(title);
                    if (newValue.equals("ThreadFinished")) {
                        allDone.add(true);
                        System.out.println("Thread" + t.getThreadID() + " done!");
                        openPorts.addAll(t.getOpenPortOnThisThread());
                    }
                    if (allDone.size() == sniff.getNumOfThread() || allDone.size()==1 && settingGroup.commonPort.selectedProperty().get()) getReport(primaryStage,openPorts,startTimeStamp);
                });
            }
            //-- Thread status listener end --//
        }

    }

    private void getReport(Stage primaryStage, ArrayList<Integer> openPorts, long startTimeStamp){
        long endTimeStamp = System.currentTimeMillis();
        String openedPort = "\r\nPorts opened: \r\n";
        if (openPorts.size() < 1) openedPort += "none...";
        for (int port : openPorts) openedPort += port + " ";
        showPane.setContext(openedPort);
        showPane.setContext("Total use " + getTime((endTimeStamp - startTimeStamp) / 1000.0));
        startButton.setDisable(false);
        primaryStage.setTitle(title);
    }

    private boolean checkIllegal(String input){
        System.out.println(input);
        return input.length()>=1 && input.contains(".") || input.equals("localhost");
    }
}
//todo: add the pulse/restart feature for sub-threads. (temporally suspend)
//todo: get server information. (under development)
//todo: add a main UI class, move sniff class to Group, combine analysis and port sniff.