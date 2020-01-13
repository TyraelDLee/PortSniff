package defaultPackage.gui;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**************************************************************************
 *                                                                        *
 *                       GUI component by Tyrael                          *
 *       ShowPane, which extended from basic ScrollPane layout.           *
 *                                                                        *
 *                       Copyright (c) 2020 LYL                           *
 *                            @author LYL                                 *
 *                            @version 1.0                                *
 **************************************************************************/
public class ShowPane extends ScrollPane {
    private GridPane contextPane = new GridPane();
    private double width = 0, height = 0;
    private Label context = new Label();
    private int RowIndex = 0;
    private static final int MAX_ITEM = 2550;

    ShowPane(){}

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
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
    }

    void setContext(String con){
        if(contextPane.getChildren().size()>=MAX_ITEM) {
            int remove_leng = contextPane.getChildren().size() - MAX_ITEM;
            contextPane.getChildren().remove(0, remove_leng);
        }
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
