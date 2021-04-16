package Controllers;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

public class PathFinder implements Initializable {
    @FXML private FlowPane centerGrid;
    @FXML private ComboBox<String> nodeComboBox;
    @FXML private ComboBox<String> speedComboBox;
    @FXML private ComboBox<String> algoComboBox;
    @FXML private ComboBox<String> themeComboBox;
    @FXML private Button visBtn;
    @FXML private Button clearBtn;

    private final  int UNVISITED = 0;
    private final int VISITED = 1;
    private final  int WALL = 2;
    private final  int SOURCE = 3;
    private final  int DESTINATION = 4;

    private int gridRowNo = 20, gridColNo = 38;
    private int cellWidth = 25, cellHeight = 25;
    private int sourceX = -1, sourceY = -1;
    private int destinationX = -1, destinationY = -1;
    int gridMask[][] = new int[gridRowNo][gridColNo];
    private Rectangle[][] gridCells = new Rectangle[gridRowNo][gridColNo];

    //dir array
    private int[] dirX = {1, -1, 0, 0};
    private int[] dirY = {0, 0, 1, -1};

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
    private Color visitedColor = Color.web("blue");
    private Color pathColor = Color.web("green");

    //speed
    private int speedControl = 70;

    //timelines for algoshow
    Timeline orderTimeline, pathTimeline;

    //bfs
    Queue< Pair<Integer, Integer> > orderOfAlgo = new LinkedList();
    Queue< Pair<Integer, Integer> > pathOfAlgo = new LinkedList();

    //eventHandlers
    EventHandler<MouseEvent> cellHandler = event->{
        Rectangle srcCell = (Rectangle)event.getSource();
        int location = Integer.parseInt(srcCell.getId());
        int locationX = location/100, locationY = location%100;
        //System.out.println(location);
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
    EventHandler<ActionEvent> visualizeHandler = event -> {
        if(sourceX == -1 || destinationX == -1){
            System.out.println("No source or destination");
            //alarm code here
        }
        else {
            switch (algoComboBox.getValue()){
                case "Breadth-First-Search":
                    bfs();
                    algoShow();
                    break;
                case "Depth-First-Search":
                    break;
                default:

            }

        }
    };
    //has to handle the glitch of clearing in the middle of algoshow
    EventHandler<ActionEvent> clearHandler = event -> {
        for(int i = 0; i< gridRowNo; i++){
            for(int j = 0;j < gridColNo; j++){
                gridMask[i][j] = UNVISITED;
                colorCell(i, j);
            }
        }
        sourceX = -1;
        sourceY = -1;
        destinationX = -1;
        destinationY = -1;
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

        //adding grid cells
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
        //adding combobox items
        nodeComboBox.getItems().addAll(nodeItems);
        speedComboBox.getItems().addAll(speedItems);
        algoComboBox.getItems().addAll(algoItems);
        themeComboBox.getItems().addAll(themeItems);

        visBtn.setOnAction(visualizeHandler);
        clearBtn.setOnAction(clearHandler);
    }

    public boolean bfs(){
        System.out.println("BFS");

        Queue< Pair<Integer, Integer> > q = new LinkedList();
        Map<Pair<Integer, Integer> , Pair<Integer, Integer>> prevNode = new HashMap();
        q.add(new Pair<>(sourceX, sourceY));
        boolean gotDestination = false;

        while (!q.isEmpty() && !gotDestination){
            Pair<Integer, Integer> nod = q.poll();
            int nodX = nod.getKey();
            int nodY = nod.getValue();
            for(int i = 0; i < 4; i++){
                int adjX = nodX + dirX[i];
                int adjY = nodY + dirY[i];
                if(adjX >= 0 && adjX < gridRowNo && adjY >=0 && adjY < gridColNo){
                    if(gridMask[adjX][adjY] == DESTINATION){
                        gotDestination = true;
                        prevNode.put(new Pair<>(adjX,adjY), new Pair<>(nodX, nodY));
                        break;
                    }
                    else if(gridMask[adjX][adjY] == UNVISITED){
                        gridMask[adjX][adjY] = VISITED;
                        orderOfAlgo.add(new Pair<>(adjX, adjY));
                        prevNode.put(new Pair<>(adjX,adjY), new Pair<>(nodX, nodY));
                        q.add(new Pair<>(adjX, adjY));
                    }
                }
            }
        }

        //deriving the path
        if(gotDestination){
            Stack<Pair<Integer, Integer>> temp = new Stack<>();
            Pair<Integer, Integer> cur = new Pair<>(destinationX, destinationY);
            while (gridMask[cur.getKey()][cur.getValue()] != SOURCE){
                cur = prevNode.get(cur);
                temp.add(cur);
            }
            temp.pop();  //for removing the source cell
            while (!temp.isEmpty()){
                pathOfAlgo.add(temp.pop());
            }
        }

        //System.out.println(pathOfAlgo);
        return gotDestination;
    }

    public void algoShow(){
        orderTimeline = new Timeline();
        pathTimeline = new Timeline();
        orderTimeline.setCycleCount(Animation.INDEFINITE);
        pathTimeline.setCycleCount(Animation.INDEFINITE);
        orderTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(speedControl), event -> {
            if(orderOfAlgo.isEmpty()){
                orderTimeline.stop();
                pathTimeline.play();
            }
            else {
                Pair<Integer, Integer> cur = orderOfAlgo.poll();
                gridCells[cur.getKey()][cur.getValue()].setFill(visitedColor);
            }
        }));
        pathTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(speedControl), event -> {
            if(pathOfAlgo.isEmpty()){
                pathTimeline.stop();
            }
            else {
                Pair<Integer, Integer> cur = pathOfAlgo.poll();
                gridCells[cur.getKey()][cur.getValue()].setFill(pathColor);
            }
        }));
        orderTimeline.play();
    }


}


