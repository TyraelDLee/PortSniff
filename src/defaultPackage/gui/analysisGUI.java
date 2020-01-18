package defaultPackage.gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class analysisGUI extends Scene {
    private double height = 0;
    private double width = 0;

    public analysisGUI(Parent root) {
        super(root);
    }

    public analysisGUI(Parent root, double width, double height) {
        super(root, width, height);
    }

    void init(){

    }

    public void resize(double width, double height){

    }

    class test extends Application{

        @Override
        public void start(Stage primaryStage) throws Exception {

        }
    }
}
