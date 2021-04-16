package Controllers;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

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
    private final  int UNVISITED = 0;
    private final  int WALL = 1;
    private final  int SOURCE = 2;
    private final  int DESTINATION = 3;

    private int gridRowNo = 20, gridColNo = 38;
    private int cellWidth = 25, cellHeight = 25;
    private int sourceX = -1, sourceY = -1;
    private int destinationX = -1, destinationY = -1;
    int gridMask[][] = new int[gridRowNo][gridColNo];
    private Rectangle[][] gridCells = new Rectangle[gridRowNo][gridColNo];

    //ComboBox options
    private String[] nodeItems = {"Source", "Destination", "Wall"};
    private String[] speedItems = {"Fast", "Medium", "Slow"};
    private String[] algoItems = {"Depth-First-Search", "Breadth-First-Search"};
    private String[] themeItems = {"Light", "Dark"};

    //colors
    private Color wallColor = Color.web("#EF709D");
    private Color sourceColor = Color.web("#E2EF70");
    private Color destinationColor = Color.web("red");
    private Color unvisitedColor = Color.web("white");

    //eventListeners
    EventHandler<MouseEvent> cellHandler = event->{
        Rectangle srcCell = (Rectangle)event.getSource();
        int location = Integer.parseInt(srcCell.getId());
        int locationX = location/100, locationY = location%100;
        System.out.println(location);
        String nodeVal = nodeComboBox.getValue();
        if(nodeVal == null){
            //CODE FOR NOTHING IS SELECTED
        }
        else if(nodeVal == "Wall"){

            if(gridMask[locationX][locationY] == SOURCE){
                sourceX = -1;
                sourceY = -1;
            }
            if(gridMask[locationX][locationY] == DESTINATION){
                destinationX = -1;
                destinationY = -1;
            }
            gridMask[locationX][locationY] = (gridMask[locationX][locationY] == WALL?UNVISITED:WALL);
            colorCell(locationX,locationY);
        }
        else if(nodeVal == "Source"){
            if(sourceX != -1 && gridMask[locationX][locationY] != SOURCE){
                gridCells[sourceX][sourceY].setFill(unvisitedColor);
            }

            if(gridMask[locationX][locationY] == DESTINATION){
                destinationX = -1;
                destinationY = -1;
            }
            sourceX = locationX;
            sourceY = locationY;
            gridMask[locationX][locationY] = (gridMask[locationX][locationY] == SOURCE?UNVISITED:SOURCE);
            colorCell(locationX, locationY);


        }
        else if(nodeVal == "Destination"){
            if(destinationX != -1 && gridMask[locationX][locationY] != DESTINATION){
                gridCells[destinationX][destinationY].setFill(unvisitedColor);
            }

            if(gridMask[locationX][locationY] == SOURCE){
                sourceX = -1;
                sourceY = -1;
            }
            destinationX = locationX;
            destinationY = locationY;
            gridMask[locationX][locationY] = (gridMask[locationX][locationY] == DESTINATION?UNVISITED:DESTINATION);
            colorCell(locationX, locationY);
        }
    };


    public void colorCell(int x, int y){
        switch (gridMask[x][y]){
            case UNVISITED:
                gridCells[x][y].setFill(unvisitedColor);
                break;
            case WALL:
                gridCells[x][y].setFill(wallColor);
                break;
            case SOURCE:
                gridCells[x][y].setFill(sourceColor);
                break;
            case DESTINATION:
                gridCells[x][y].setFill(destinationColor);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i = 0;i < gridRowNo; i++){
            for(int j = 0;j < gridColNo; j++){
                int cellId = i*100 + j;
                gridCells[i][j] = new Rectangle();
                gridCells[i][j].setWidth(cellWidth);
                gridCells[i][j].setHeight(cellHeight);
                gridCells[i][j].setId(String.valueOf(cellId));
                gridCells[i][j].setFill(unvisitedColor);
                gridCells[i][j].getStyleClass().add("grid-cell");
                centerGrid.getChildren().add(gridCells[i][j]);
                gridCells[i][j].addEventHandler(MouseEvent.MOUSE_CLICKED, cellHandler);
            }
        }
        nodeComboBox.getItems().addAll(nodeItems);
        speedComboBox.getItems().addAll(speedItems);
        algoComboBox.getItems().addAll(algoItems);
        themeComboBox.getItems().addAll(themeItems);
    }


}

