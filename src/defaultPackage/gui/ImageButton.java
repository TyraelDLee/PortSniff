package defaultPackage.gui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class ImageButton extends StackPane {
    private Rectangle background = new Rectangle();
    private Label showText = new Label();
    private ImageView image_background = new ImageView();
    private Color backgroundColor = new Color(0, 0, 0, 0);

    ImageButton(double width, double height, String text) {
        this.backgroundColor = new Color(.5, .5, .5, .8);
        this.background.setHeight(height);
        this.background.setWidth(width);
        this.background.setFill(backgroundColor);
        this.showText.setText(text);

        this.getChildren().addAll(background, showText);
    }

    ImageButton() {

    }

    ImageButton(double width, double height, String text, double r, double g, double b, double a) {
        this.backgroundColor = new Color(r, g, b, a);
        this.background.setHeight(height);
        this.background.setWidth(width);
        this.background.setFill(backgroundColor);
        this.showText.setText(text);

        this.getChildren().addAll(background, showText);
    }

    ImageButton(double width, double height, Image background) {
        this.image_background.setImage(background);
        this.image_background.setFitHeight(height);
        this.image_background.setFitWidth(width);
        this.getChildren().add(image_background);
    }

    void setLocation(double X, double Y) {
        this.setLayoutX(X);
        this.setLayoutY(Y);
    }

    void setOver(double opacity) {
        double r, g, b;
        r = backgroundColor.getRed();
        g = backgroundColor.getGreen();
        b = backgroundColor.getBlue();
        backgroundColor = new Color(r, g, b, opacity);
        this.background.setFill(backgroundColor);
    }

    void setFont(double size) {
        this.showText.setFont(Font.font(size));
    }

    void setSize(double width, double height) {
        this.background.setWidth(width);
        this.background.setHeight(height);
        this.image_background.setFitHeight(height);
        this.image_background.setFitWidth(width);
    }
}