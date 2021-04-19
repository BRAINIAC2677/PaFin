import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.Objects;
import Controllers.PathFinder;

public class Main extends Application {

    //for the quick start guide
    public void quickStart(){
        String title = "Quick Start Guide";
        String body = "1. Click a cell to create wall/source/destination node.\n" +
                "2. Change the node type from the sidebar.\n" +
                "3. Clicking on node cell will erase the node.\n" +
                "4. Press 'd' to activate smooth draw mode.\n" +
                "5. Wall will be drawn as your mouse moves inside the grid in smooth draw mode.\n" +
                "6. Press 'D' i.e 'Shift + d' to deactivate smooth draw mode.\n" +
                "7. After drawing the graph, select the algorithm and hit visualize.\n" +
                "8. Speed of the visualization can be controlled from the sidebar.\n" +
                "9. Random mazes can be drawn by Automaze but select a maze before hitting it.\n" +
                "10. Good Luck with PaFin.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("CSS/styling.css");
        dialogPane.getStyleClass().add("quick-start");
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(body);
        alert.show();
    }

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
        quickStart();
    }


}
