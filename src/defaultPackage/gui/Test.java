package defaultPackage.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

public class Test extends Application {
    private static final double WINDOW_HEIGHT = 500.0;
    private static final double WINDOW_WIDTH = 800.0;
    private static double ZOOMFACTOR = 1;
    private Number mainStageHeight = WINDOW_HEIGHT, mainStageWidth = WINDOW_WIDTH;
    private final Group root = new Group();
    private MenuBar menuRoot = new MenuBar();
    private Menu fileM = new Menu("File");
    private Menu viewM = new Menu("View");
    private boolean currentTypeSN = false;
    analysisGUI ag = new analysisGUI();
    sniffGUI ds = new sniffGUI();

    private void setZoomFactor(Number mainStageWidth, Number mainStageHeight) {
        double width = mainStageWidth.doubleValue() / WINDOW_WIDTH, height = mainStageHeight.doubleValue() / WINDOW_HEIGHT;
        if (width < height) ZOOMFACTOR = Math.round(width * 100.0) / 100.0;
        else ZOOMFACTOR = Math.round(height * 100.0) / 100.0;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("test");
        primaryStage.setMinHeight(420);
        primaryStage.setMinWidth(720);
        Scene scene = new Scene(root,WINDOW_WIDTH,WINDOW_HEIGHT);
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            mainStageHeight = newValue;
            setZoomFactor(mainStageWidth, mainStageHeight);
            resize();
        });
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            mainStageWidth = newValue;
            setZoomFactor(mainStageWidth, mainStageHeight);
            resize();
        });

        ds.boundStage(primaryStage);
        ds.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        ag.boundStage(primaryStage);
        ag.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);

        //-- Menu selection --//
        menuRoot.setUseSystemMenuBar(true);
        MenuItem Setting = new MenuItem("Setting");
        Setting.setOnAction(event -> {
//            if (menuButton.onClick()) {
//                //root.getChildren().add(settingGroup);
//                settingInOutAnim(settingGroup, true);
//            } else {
//                settingInOutAnim(settingGroup, false);
//            }
        });
        MenuItem analyze = new MenuItem("Analyze");
        MenuItem portSniff = new MenuItem("Port Sniffer");
        analyze.setOnAction(event -> changeType("analysis"));
        portSniff.setOnAction(event -> changeType("sniff"));
        fileM.getItems().addAll(Setting);
        viewM.getItems().addAll(analyze, new SeparatorMenuItem(), portSniff);
        menuRoot.getMenus().addAll(fileM, viewM);

        root.getChildren().add(menuRoot);
        root.getChildren().add(ag);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void resize(){
        ag.resize(mainStageWidth,mainStageHeight,ZOOMFACTOR);
        ds.resize(mainStageWidth,mainStageHeight,ZOOMFACTOR);
    }

    private void changeType(String type){
        if(type.equals("analysis") && currentTypeSN){
            root.getChildren().remove(ds);
            root.getChildren().add(ag);
            currentTypeSN = false;
        }else if(type.equals("sniff") && !currentTypeSN){
            root.getChildren().remove(ag);
            root.getChildren().add(ds);
            currentTypeSN = true;
        }
    }
}
