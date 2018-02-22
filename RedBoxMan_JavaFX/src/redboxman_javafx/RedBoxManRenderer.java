package redboxman_javafx;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *
 * @author McKillaGorilla
 */
public class RedBoxManRenderer extends Application {
    Canvas canvas;
    GraphicsContext gc;
    ArrayList<Point2D> imagesRedBoxManLocations;
    ArrayList<Point2D> shapesRedBoxManLocations;
    Image redBoxManImage;
    
    @Override
    public void start(Stage primaryStage) {
	// INIT THE DATA MANAGERS
	imagesRedBoxManLocations = new ArrayList<>();
	shapesRedBoxManLocations = new ArrayList<>();
	
	// LOAD THE RED BOX MAN IMAGE
        InputStream str = getClass().getResourceAsStream("/RedBoxMan.png");
	redBoxManImage = new Image(str);
	
	// MAKE THE CANVAS
	canvas = new Canvas();
	canvas.setStyle("-fx-background-color: cyan");
	gc = canvas.getGraphicsContext2D();

	// PUT THE CANVAS IN A CONTAINER
	Group root = new Group();
	root.getChildren().add(canvas);
	
	canvas.setOnMouseClicked(e->{
	    if (e.isShiftDown()) {
		shapesRedBoxManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }
	    else if (e.isControlDown()) {
		imagesRedBoxManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }
	    else {
		clear();
	    }
	});
	
	// PUT THE CONTAINER IN A SCENE
	Scene scene = new Scene(root, 800, 600);
	canvas.setWidth(scene.getWidth());
	canvas.setHeight(scene.getHeight());

	// AND START UP THE WINDOW
	primaryStage.setTitle("Red Box Man Renderer");
	primaryStage.setScene(scene);
	primaryStage.show();
    }
    
    public void clearCanvas() {
	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void clear() {
	shapesRedBoxManLocations.clear();
	imagesRedBoxManLocations.clear();
	render();
    }
    
    public void render() {
	clearCanvas();
	for (int i = 0; i < shapesRedBoxManLocations.size(); i++) {
	    renderShapeRedBoxMan(shapesRedBoxManLocations.get(i));
	}
	for (int j = 0; j < imagesRedBoxManLocations.size(); j++) {
	    renderImageRedBoxMan(imagesRedBoxManLocations.get(j));
	}
    }
    
    public void renderShapeRedBoxMan(Point2D location) {
	String headColor = "#DD0000";
	String outlineColor = "#000000";
	int headW = 115;
	int headH = 88;
    
	// DRAW HIS RED HEAD
        gc.setFill(Paint.valueOf(headColor));
	gc.fillRect(location.getX(), location.getY(), headW, headH);
        gc.beginPath();
	gc.setStroke(Paint.valueOf(outlineColor));
	gc.setLineWidth(1);
	gc.rect(location.getX(), location.getY(), headW, headH);
	gc.stroke();
	
	// AND THEN DRAW THE REST OF HIM
    }
    
    public void renderImageRedBoxMan(Point2D location) {
	//gc.drawImage(redBoxManImage, location.getX(), location.getY());
        String eyes = "#FFFF00";
        String outlineColor = "#000000";
        renderShapeRedBoxMan(location);
        gc.setFill(Paint.valueOf(eyes));
        gc.fillRect(location.getX()+20, location.getY()+15, 25, 25);
        gc.fillRect(location.getX()+60, location.getY()+15, 25, 25);
        gc.setFill(Paint.valueOf(outlineColor));
        gc.rect(location.getX()+20, location.getY()+15, 25, 25);
        gc.rect(location.getX()+60, location.getY()+15, 25, 25);
        gc.fillRect(location.getX()+25, location.getY()+25, 10, 10);
        gc.fillRect(location.getX()+65, location.getY()+25, 10, 10);
        gc.fillRect(location.getX()+15, location.getY()+70, 75, 10);
        int bodyX = (int) (location.getX() + 20);
        int bodyY = (int) (location.getY()+88);
        gc.fillRect(bodyX, bodyY, 80, 20);
        gc.fillRect(bodyX + 10, bodyY, 60, 40);
        gc.fillRect(bodyX, bodyY +40, 10, 10);
        gc.fillRect(bodyX + 70, bodyY +40, 10, 10);
        
       
        gc.stroke();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
    
}
