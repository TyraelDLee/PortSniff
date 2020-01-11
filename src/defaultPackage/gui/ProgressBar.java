package defaultPackage.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class ProgressBar extends GridPane {
    private double width = 0;
    private double height = 0;
    private double progress = 0;
    private boolean showText = false;
    private StackPane progressField = new StackPane();
    private Rectangle background = new Rectangle();
    private Rectangle current = new Rectangle();
    private Label showProgress = new Label("0.0%");
    private static final Color defaultBackgroundColor = new Color(.8,.8,.8,.8);
    private static final Color defaultProgressColor = new Color(.16,.4,.82,1);

    ProgressBar(){}

    ProgressBar(double width, double height){
        this.height = height;
        this.width = width;

        this.background.setArcWidth(10);
        this.background.setHeight(10);
        this.current.setArcWidth(10);
        this.current.setArcHeight(10);

        progressField.getChildren().addAll(background, current);
        this.add(progressField,0,0);
    }

    ProgressBar(double width, double height, boolean showText){
        this.setHgap(5);
        this.progressField.setAlignment(Pos.CENTER_LEFT);
        this.height = height;
        this.width = width;
        this.showText = showText;
        this.background.setArcWidth(10);
        this.background.setArcHeight(10);
        this.current.setArcWidth(10);
        this.current.setArcHeight(10);
        this.background.setFill(defaultBackgroundColor);
        this.current.setFill(defaultProgressColor);
        progressField.getChildren().addAll(background, current);
        if(!showText)
            this.add(progressField,0,0);
        else{
            this.add(progressField,0,0);
            this.add(showProgress,1,0);
        }
    }

    void setLocation(double X, double Y) {
        this.setLayoutX(X);
        this.setLayoutY(Y);
    }

    void setShowText(double progress){
        String progressNum = (Math.round(progress * 1000) / 10.0)+"";
        if(progressNum.length()>4) progressNum = progressNum.substring(0,4);
        String text = progressNum + "%";
        this.showProgress.setText(text);
    }

    void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        if(!this.showText)
            this.background.setWidth(width);
        else
            this.background.setWidth(width-50);
        setUpdate();
        this.background.setHeight(height);
        this.current.setHeight(height);
    }

    void setArc(double width, double height){
        this.background.setArcWidth(width);
        this.background.setArcHeight(height);
        this.current.setArcWidth(width);
        this.current.setArcHeight(height);
    }

    void setColor(double r, double g, double b, double a){
        Color progressColor = new Color(r,g,b,a);
        this.current.setFill(progressColor);
    }

    void setBackgroundColor(double r, double g, double b, double a){
        Color backgroundColor = new Color(r,g,b,a);
        this.background.setFill(backgroundColor);
    }

    void reset(){
        this.current.setWidth(0);
        this.showProgress.setText("0.0%");
    }

    void setUpdate(double progress){
        this.progress = progress;
        this.current.setWidth(this.background.getWidth()*progress);
        if(this.showText) setShowText(progress);
    }

    public double getComponentWidth() {
        return this.width;
    }

    private void setUpdate(){
        this.current.setWidth(this.background.getWidth()*this.progress);
    }
}
