import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.Objects;

public class Main extends Application {



    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MyFXML/PathFinder.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("CSS/styling.css");
        Image logo = new Image("Images/PaFin.png");
        stage.getIcons().add(logo);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("PaFin");
        stage.show();
    }
}
