package Controllers;


import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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
    @FXML private ComboBox<String> mazeComboBox;
    @FXML private Button visBtn;
    @FXML private Button clearBtn;
    @FXML private Button autoMazeBtn;
    @FXML private ImageView leftImageView;
    @FXML private ImageView rightImageView;
    @FXML private FlowPane titleBar;
    @FXML private FlowPane menuBar;
    @FXML private VBox sideBar;
    @FXML private Rectangle wallSign;
    @FXML private Rectangle sourceSign;
    @FXML private Rectangle destinationSign;
    @FXML private Rectangle visitedSign;
    @FXML private Rectangle unvisitedSign;


    Image logo = new Image("Images/Pafin.png");

    //defining cell states
    private final  int UNVISITED = 0;
    private final int VISITED = 1;
    private final  int WALL = 2;
    private final  int SOURCE = 3;
    private final  int DESTINATION = 4;

    //grid info
    private final int gridRowNo = 24, gridColNo = 47;
    private int cellWidth = 25, cellHeight = 25;
    private int sourceX = -1, sourceY = -1;
    private int destinationX = -1, destinationY = -1;
    int[][] gridMask = new int[gridRowNo][gridColNo];
    private final Rectangle[][] gridCells = new Rectangle[gridRowNo][gridColNo];

    //dir array
    private final int[] dirX = {1, 0, -1, 0};
    private final int[] dirY = {0, 1, 0, -1};

    //ComboBox options
    private final String[] nodeItems = {"Wall", "Source", "Destination"};
    private final String[] speedItems = {"Fast", "Medium", "Slow"};
    private final String[] algoItems = {"Depth-First-Search", "Breadth-First-Search"};
    private final String[] themeItems = {"Light", "Dark"};
    private final String[] mazeItems = {"BFS Maze", "DFS Maze", "Recursive Division"};

    //colors
    private final Color sourceColor = Color.web("#ffe66d");
    private final Color destinationColor = Color.web("#9381ff");
    private String primaryColor;
    private String secondaryColor;
    private String tertiaryColor;
    private Color wallColor;
    private Color unvisitedColor;
    private Color visitedColor;
    private Color pathColor;

    //speed
    private int speedControl;
    private int transitionControl;

    //animation timelines for algorithmShow method
    Timeline orderTimeline, pathTimeline;

    //main Algorithms(dfs, bfs, recursive division)
    private Queue< Pair<Integer, Integer> > orderOfAlgo = new LinkedList();
    private Queue< Pair<Integer, Integer> > pathOfAlgo = new LinkedList();
    private Queue< Pair<Integer, Integer> > orderOfMaze = new LinkedList();
    private Map<Pair<Integer, Integer> , Pair<Integer, Integer>> prevNode = new HashMap<>();
    private boolean gotDestination = false;
    private final int minConnectedComp = 50, maxConnectedComp = 60,randomness = 3;

    /*eventHandlers*/
    EventHandler<MouseEvent> cellHandler = event->{
    //for handling cell selection by Mouse
        Rectangle srcCell = (Rectangle)(event.getSource());
        int location = Integer.parseInt(srcCell.getId());
        int locationX = location/100;
        int locationY = location%100;
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
    //for handling click on the visualize button
        if(sourceX == -1 || destinationX == -1){
            //System.out.println("No source or destination");
            //alarm code here
        }
        else {
            if(algoComboBox.getValue() == null){
                //Alert for no algo selection
            }
            else if(algoComboBox.getValue().equals(algoItems[0])){
                dfsWrapper();
                algorithmShow();
            }

            else if(algoComboBox.getValue().equals(algoItems[1])){
                bfs();
                algorithmShow();
            }
        }
    };
    EventHandler<ActionEvent> clearHandler = event -> {
    //has to handle the glitch of clearing in the middle of algorithmShow
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
    EventHandler<ActionEvent> speedHandler = event -> {
        if(speedComboBox.getValue() == speedItems[0]){
            fast();
        }
        else if(speedComboBox.getValue() == speedItems[1]){
            medium();
        }
        else if(speedComboBox.getValue() == speedItems[2]){
            slow();
        }
    };
    EventHandler<ActionEvent> themeHandler = event -> {
        if(themeComboBox.getValue() == themeItems[0]){
            light();
        }
        else if(themeComboBox.getValue() == themeItems[1]){
            dark();
        }
        setUIColor(1);
    };
    EventHandler<ActionEvent> mazeHandler = event -> {
        if(mazeComboBox.getValue() == mazeItems[0]){
            int connectedComp = myRand(minConnectedComp, maxConnectedComp);
            for(int i = 0; i < connectedComp; i++){
                //one iteration of bfsMazeGenerator creates a single component
                bfsMazeGenerator();
            }
        }
        else if (mazeComboBox.getValue() == mazeItems[1]){
            int connectedComp = myRand(minConnectedComp, maxConnectedComp);
            for(int i = 0; i < connectedComp; i++){
                //one iteration of dfsMazeGenerator creates a single component
                dfsMazeGenerator(myRand(0, gridRowNo-1), myRand(0, gridColNo-1));
            }
        }
        else if(mazeComboBox.getValue() == mazeItems[2]){
            recursiveDivision( 0, gridRowNo-1, 0, gridColNo-1);
        }
        mazeShow();
    };
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initial settings
        light();
        setUIColor(0);
        fast();
        //creating and adding grid cells
        for(int i = 0;i < gridRowNo; i++){
            for(int j = 0;j < gridColNo; j++){
                int cellId = i*100 + j;
                gridCells[i][j] = new Rectangle();
                gridCells[i][j].setWidth(cellWidth);
                gridCells[i][j].setHeight(cellHeight);
                gridCells[i][j].setId(String.valueOf(cellId));
                gridCells[i][j].setFill(unvisitedColor);
                gridCells[i][j].setStroke(Color.web(tertiaryColor));
                gridCells[i][j].setStrokeWidth(0.0);
                centerGrid.getChildren().add(gridCells[i][j]);
                gridCells[i][j].addEventHandler(MouseEvent.MOUSE_CLICKED, cellHandler);
            }
        }
        //adding combobox items
        nodeComboBox.getItems().addAll(nodeItems);
        speedComboBox.getItems().addAll(speedItems);
        algoComboBox.getItems().addAll(algoItems);
        themeComboBox.getItems().addAll(themeItems);
        mazeComboBox.getItems().addAll(mazeItems);
        
        speedComboBox.setValue(speedItems[0]);
        nodeComboBox.setValue(nodeItems[0]);

        visBtn.setOnAction(visualizeHandler);
        clearBtn.setOnAction(clearHandler);
        autoMazeBtn.setOnAction(mazeHandler);
        speedComboBox.setOnAction(speedHandler);
        themeComboBox.setOnAction(themeHandler);

        leftImageView.setImage(logo);
        rightImageView.setImage(logo);
    }
    
    private void colorCell(int x, int y){
        //coloring the cells according to their cell type
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
    
    private int myRand(int low, int high){
        //generates random number between low and high
        int range = high - low + 1;
        return (int)(Math.random()*range) + low;
    }
    
    public void fast(){
        speedControl = 30;
        transitionControl = 400;
    }
    public void medium(){
        speedControl = 50;
        transitionControl = 600;
    }
    public void slow(){
        speedControl = 70;
        transitionControl = 800;
    }
    
    private void dark(){
        primaryColor = "#fdfffc";
        secondaryColor = "#1c2541";
        tertiaryColor = "#3a506b";
        visitedColor = Color.web("#ff5d8f");
        unvisitedColor = Color.web(primaryColor);
        wallColor = Color.web("#1e6091");
        pathColor = Color.web("black");
    }
    private void light(){
        primaryColor = "#fdfffc";
        secondaryColor = "#bfd7ff";
        tertiaryColor = "#9bb1ff";
        visitedColor = Color.web("#ffa5ab");
        unvisitedColor = Color.web(primaryColor);
        wallColor = Color.web("#00afb9");
        pathColor = Color.web("black");
    }
    private void setUIColor(int flag){
        titleBar.setStyle("-fx-background-color: "+secondaryColor+";");
        menuBar.setStyle("-fx-background-color: "+ tertiaryColor+";");
        sideBar.setStyle("-fx-background-color: "+secondaryColor+";");
        centerGrid.setStyle("-fx-background-color: "+toHexString(wallColor)+";");
        wallSign.setFill(wallColor);
        sourceSign.setFill(sourceColor);
        destinationSign.setFill(destinationColor);
        visitedSign.setFill(visitedColor);
        unvisitedSign.setFill(unvisitedColor);

        for(int i = 0; i< gridRowNo && flag == 1; i++){
            for(int j = 0;j < gridColNo; j++){
                if(gridMask[i][j] == UNVISITED){
                    gridCells[i][j].setFill(unvisitedColor);
                }
                else if(gridMask[i][j] == VISITED){
                    gridCells[i][j].setFill(visitedColor);
                }
                else if(gridMask[i][j] == WALL){
                    gridCells[i][j].setFill(wallColor);
                }
                else if(gridMask[i][j] == SOURCE){
                    gridCells[i][j].setFill(sourceColor);
                }
                else if(gridMask[i][j] == DESTINATION){
                    gridCells[i][j].setFill(destinationColor);
                }

            }
        }
    }
    
    private String format(double val) {
    // Helper method for Color class to RGB conversion
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }
    private String toHexString(Color value) {
    //Color class to RGB conversion
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }
    
    private void bfs(){
        Queue< Pair<Integer, Integer> > q = new LinkedList();
        q.add(new Pair<>(sourceX, sourceY));
        gotDestination = false;

        while (!q.isEmpty() && !gotDestination){
            Pair<Integer, Integer> nod = q.poll();
            int nodX = nod.getKey();
            int nodY = nod.getValue();
            for(int i = 0; i < 4; i++){
                int adjX = nodX + dirX[i];
                int adjY = nodY + dirY[i];
                if(adjX >= 0 && adjX < gridRowNo && adjY >=0 && adjY < gridColNo){
                    //stop the search if destination found
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

        //extracting the path
        if(gotDestination){
            Stack<Pair<Integer, Integer>> temp = new Stack<>();
            Pair<Integer, Integer> cur = new Pair<>(destinationX, destinationY);
            while (gridMask[cur.getKey()][cur.getValue()] != SOURCE){
                cur = prevNode.get(cur);
                temp.add(cur);
            }
            temp.pop();  //for removing the source cell
            //reversing the order
            while (!temp.isEmpty()){
                pathOfAlgo.add(temp.pop());
            }
        }
    }
    private void dfs(Pair<Integer, Integer> nod){
        int x = nod.getKey(), y = nod.getValue();
        if(gridMask[x][y] == DESTINATION){
            gotDestination = true;
            orderOfAlgo.add(nod);
            return;
        }
        if(gridMask[x][y] == UNVISITED){
            orderOfAlgo.add(nod);
            gridMask[x][y] = VISITED;
        }
        for(int i = 0; i< 4; i++){
            int adjX = x + dirX[i], adjY = y + dirY[i];
            if(adjX >= 0 && adjX < gridRowNo && adjY >= 0 && adjY < gridColNo && (gridMask[adjX][adjY] == UNVISITED||gridMask[adjX][adjY] == DESTINATION)){
                Pair<Integer, Integer> adjNod = new Pair<>(adjX, adjY);
                prevNode.put(adjNod, nod);
                dfs(adjNod);
            }
        }
    }
    private void dfsWrapper(){
        gotDestination = false;
        dfs(new Pair<>(sourceX, sourceY));

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


    }
    private void algorithmShow(){
    //for showing the algorithms visualization by using a timeline animation
        orderTimeline = new Timeline(); //for showing the search of algorithm
        pathTimeline = new Timeline(); //for showing the shortest path
        orderTimeline.setCycleCount(Animation.INDEFINITE);
        pathTimeline.setCycleCount(Animation.INDEFINITE);
        orderTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(speedControl), event -> {
            if(orderOfAlgo.isEmpty() || (orderOfAlgo.peek().getKey() == destinationX && orderOfAlgo.peek().getValue() == destinationY)){
                //when the search ends shortest path is shown
                orderOfAlgo.clear();
                orderTimeline.stop();
                pathTimeline.play();
            }
            else {
                Pair<Integer, Integer> cur = orderOfAlgo.poll();
                FillTransition rectTransition = new FillTransition(Duration.millis(transitionControl), gridCells[cur.getKey()][cur.getValue()]);
                rectTransition.setToValue(visitedColor);
                rectTransition.play();
            }
        }));
        pathTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(speedControl), event -> {
            if(pathOfAlgo.isEmpty()){
                pathTimeline.stop();
            }
            else {
                Pair<Integer, Integer> cur = pathOfAlgo.poll();
                FillTransition rectTransition = new FillTransition(Duration.millis(transitionControl), gridCells[cur.getKey()][cur.getValue()]);
                rectTransition.setToValue(pathColor);
                rectTransition.play();
            }
        }));
        orderTimeline.play();
    }
    
    //maze generators
    private void bfsMazeGenerator(){
        System.out.println("BFS");

        Queue< Pair<Integer, Integer> > q = new LinkedList();
        q.add(new Pair<>(myRand(0, gridRowNo-1), myRand(0, gridColNo-1)));

        while (!q.isEmpty()){
            Pair<Integer, Integer> nod = q.poll();
            int nodX = nod.getKey();
            int nodY = nod.getValue();
            for(int i = 0; i < 4; i++){
                int adjX = nodX + dirX[i];
                int adjY = nodY + dirY[i];
                if(adjX >= 0 && adjX < gridRowNo && adjY >=0 && adjY < gridColNo){
                    if(gridMask[adjX][adjY] == UNVISITED){
                        int toss = myRand(0, randomness);
                        if(toss != 0){
                            continue;
                        }
                        gridMask[adjX][adjY] = WALL;
                        orderOfMaze.add(new Pair<>(adjX, adjY));
                        q.add(new Pair<>(adjX, adjY));
                    }
                }
            }
        }
    }
    private void dfsMazeGenerator(int nodX, int nodY){
        if(gridMask[nodX][nodY] != UNVISITED){
            return;
        }
        orderOfMaze.add(new Pair<>(nodX, nodY));
        gridMask[nodX][nodY] = WALL;
        for(int i = 0; i< 4; i++){
            int adjX = nodX + dirX[i], adjY = nodY + dirY[i];
            if(adjX >= 0 && adjX < gridRowNo && adjY >= 0 && adjY < gridColNo){
                int toss = myRand(0, randomness);
                if( toss != 0){
                    continue;
                }
                dfsMazeGenerator(adjX, adjY);
            }
        }

    }
    private void recursiveDivision(int startX, int endX, int startY, int endY){
        if(Math.max(endX - startX,endY - startY) < 6){
            return;
        }
        //vertical division
        int flag = 1;
        if(endX - startX > endY - startY){
            flag = 0;
        }
        if(flag == 1){
            int fixedY = (startY + endY)/2;
            int passageX = myRand(startX + 1, endX - 2);
            while (passageX == (startX + endX)/2){
                passageX = myRand(startX + 1, endX - 2);
            }
            for(int i = startX; i <= endX; i++){
                if(i != passageX && i != (passageX + 1)) {
                    orderOfMaze.add(new Pair<>(i, fixedY));
                    gridMask[i][fixedY] = WALL;
                }
            }
            recursiveDivision( startX, endX, startY, fixedY-1);
            recursiveDivision( startX, endX, fixedY + 1, endY);
        }
        else{
            int fixedX = (startX + endX)/2;
            int passageY = myRand(startY + 1, endY - 2);
            while (passageY == (startY + endY)/2){
                passageY = myRand(startY + 1, endY - 2);
            }
            for(int i = startY; i <= endY; i++){
                if(i != passageY && i != (passageY + 1)){
                    orderOfMaze.add(new Pair<>(fixedX, i));
                    gridMask[fixedX][i] = WALL;
                }
            }
            recursiveDivision( startX, fixedX-1, startY, endY);
            recursiveDivision( fixedX + 1, endX, startY, endY);
        }
    }

    private void mazeShow(){
        Timeline mazeTimeline = new Timeline();
        mazeTimeline.setCycleCount(Animation.INDEFINITE);
        mazeTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(speedControl), event -> {
            if(orderOfMaze.isEmpty()){
                mazeTimeline.stop();
            }
            else{
                Pair<Integer, Integer> cur = orderOfMaze.poll();
                FillTransition rectTransition = new FillTransition(Duration.millis(transitionControl), gridCells[cur.getKey()][cur.getValue()]);
                rectTransition.setToValue(wallColor);
                rectTransition.play();
            }
        }));
        mazeTimeline.play();
    }
    
}


