package defaultPackage.gui;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class ShowPane extends ScrollPane {
    private GridPane contextPane = new GridPane();
    private double width = 0, height = 0;
    private Label context = new Label();
    private int RowIndex = 0;

    ShowPane(){

    }

    ShowPane(double width, double height){
        this.width = width;
        this.height = height;
        setSize(width, height);
    }

    void setLocation(double X, double Y) {
        this.setLayoutX(X);
        this.setLayoutY(Y);
    }


    void setFont(double size) {
        this.context.setFont(Font.font(size));
    }

    void setSize(double width, double height) {
        //this.setPrefSize(width,height);
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        System.out.println(this.getHeight()+" "+this.getWidth());
    }

    void setContext(String con){
        context = new Label(con);
        contextPane.add(context, 0, RowIndex);
        this.setContent(contextPane);
        this.RowIndex++;
    }

    void clear(){
        this.RowIndex = 0;
        contextPane.getChildren().clear();
    }
}
