package defaultPackage.gui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**************************************************************************
 *                                                                        *
 *                       GUI component by Tyrael                          *
 *         ImageButton, which is the button could be powerful.            *
 *                                                                        *
 *                         Copyright LYL 2020                             *
 *                            @author LYL                                 *
 *                            @version 1.0                                *
 **************************************************************************/

public class ImageButton extends StackPane {
    private Rectangle background = new Rectangle();
    private Label showText = new Label();
    private ImageView image_background = new ImageView();
    private Color backgroundColor = new Color(0, 0, 0, 0);

    /**
     * This constructor set a new button with height, width and text.
     * With the basic button style. The default background color is gray,
     * {@code new Color(.5, .5, .5, .8)}.
     *
     * @param height button height
     * @param width button width
     * @param text the show text
     * */
    ImageButton(double width, double height, String text) {
        this.backgroundColor = new Color(.5, .5, .5, .8);
        this.background.setHeight(height);
        this.background.setWidth(width);
        this.background.setFill(backgroundColor);
        this.showText.setText(text);

        this.getChildren().addAll(background, showText);
    }

    ImageButton() {}

    /**
     * This constructor set a new button with height, width and text.
     * The background color is defined at here.
     *
     * @param height button height
     * @param width button width
     * @param text the show text
     * @param r the red channel for background color
     * @param g the green channel for background color
     * @param b the blue channel for background color
     * @param a the alpha channel for background color
     * */
    ImageButton(double width, double height, String text, double r, double g, double b, double a) {
        this.backgroundColor = new Color(r, g, b, a);
        this.background.setHeight(height);
        this.background.setWidth(width);
        this.background.setFill(backgroundColor);
        this.showText.setText(text);

        this.getChildren().addAll(background, showText);
    }

    /**
     * This constructor set a new button with height, width and text.
     * This new button background is an Image.
     *
     * In this new button background style definition is not recommend.
     * @param height button height
     * @param width button width
     * @param background button background Image
     * */
    ImageButton(double width, double height, Image background) {
        this.image_background.setImage(background);
        this.image_background.setFitHeight(height);
        this.image_background.setFitWidth(width);
        this.getChildren().add(image_background);
    }

    /**
     * Set the Button location by taken X and Y.
     * The anchor at top left.
     *
     * @param X the point at x-axis
     * @param Y the point at y-axis
     * */
    void setLocation(double X, double Y) {
        this.setLayoutX(X);
        this.setLayoutY(Y);
    }

    /**
     * Style set
     * Set the effect when mouse over the button
     * the button style will setting on background color
     * and could be defined later.
     *
     * @param opacity the alpha channel when mouse over*/
    void setOver(double opacity) {
        double r, g, b;
        r = backgroundColor.getRed();
        g = backgroundColor.getGreen();
        b = backgroundColor.getBlue();
        backgroundColor = new Color(r, g, b, opacity);
        this.background.setFill(backgroundColor);
    }

    /**
     * Style set
     * Set the font size for text which displayed on button
     *
     * @param size the size for font
     * */
    void setFont(double size) {
        this.showText.setFont(Font.font(size));
    }

    /**
     * Style set
     * Set a new background color for a button
     *
     * @param r the red channel for background color
     * @param g the green channel for background color
     * @param b the blue channel for background color
     * @param a the alpha channel for background color
     * */
    void setColor(double r, double g, double b, double a){
        this.backgroundColor = new Color(r,g,b,a);
        this.background.setFill(backgroundColor);
    }

    /**
     * Set the size for button. Also called for resize the button
     * by {@code resize()} in {@code main gui class}*/
    void setSize(double width, double height) {
        this.background.setWidth(width);
        this.background.setHeight(height);
        this.image_background.setFitHeight(height);
        this.image_background.setFitWidth(width);
    }
}