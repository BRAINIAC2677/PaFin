package Controllers;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class PathFinder implements Initializable {
    @FXML
    private FlowPane centerGrid;
    @FXML
    ComboBox<String> nodeComboBox;
    @FXML
    ComboBox<String> speedComboBox;
    @FXML
    ComboBox<String> algoComboBox;
    @FXML
    ComboBox<String> themeComboBox;
    private int gridRowNo = 20, gridColNo = 38;
    private int cellWidth = 25, cellHeight = 25;
    private Rectangle[][] gridCells = new Rectangle[gridRowNo][gridColNo];
    private String[] nodeItems = {"Source", "Destination", "Wall"};
    private String[] speedItems = {"Fast", "Medium", "Slow"};
    private String[] algoItems = {"Depth-First-Search", "Breadth-First-Search"};
    private String[] themeItems = {"Light", "Dark"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i = 0;i < gridRowNo; i++){
            for(int j = 0;j < gridColNo; j++){
                gridCells[i][j] = new Rectangle();
                gridCells[i][j].setWidth(cellWidth);
                gridCells[i][j].setHeight(cellHeight);
                gridCells[i][j].getStyleClass().add("grid-cell");
                centerGrid.getChildren().add(gridCells[i][j]);
            }
        }
        nodeComboBox.getItems().addAll(nodeItems);
        speedComboBox.getItems().addAll(speedItems);
        algoComboBox.getItems().addAll(algoItems);
        themeComboBox.getItems().addAll(themeItems);
    }
}

