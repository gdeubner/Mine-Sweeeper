package SweeperPackage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameController  implements Initializable  {
    
    private Game game;
    private int[] difficulty;
    private Timer gameTimer;
    private boolean gameOver;
    private boolean flagMouse;
    private boolean firstSelection;
  
    @FXML
    private Button ResetButton;

    @FXML
    private ChoiceBox<String> DifficultyChoiceBox;

    @FXML
    private TextField TimerTextField;

    @FXML
    private Button FlagButton;

    @FXML
    private TextField FlagCountTextField;
    
    @FXML
    private GridPane grid;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        difficulty = Difficulty.EASY;
        setBoard(difficulty);
//        fillBoard(difficulty);
//        gameOver = false;
//        FlagCountTextField.setText("" + difficulty[2]);
//        flagMouse = false;
//        firstSelection = true;
//        TimerTextField.setText("0");
        resetBoard();
        game = new Game(difficulty, grid);
        for(String dif : Difficulty.DIFFICULTIES) {
            DifficultyChoiceBox.getItems().add(dif);
        }
        DifficultyChoiceBox.getSelectionModel().select(0);
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if(!gameOver)
                    TimerTextField.setText("" + (Integer.parseInt(TimerTextField.getText())+1));
            }
          }, 0, 1000);
        FlagButton.setTooltip(new Tooltip("Press the \"f\" key to toggle between flag mode"));
        FlagButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.F) {
                    setFlagMouse();
                }
            }
        }); 
    }
    
    @FXML
    public void resetBoard() {
        grid.getChildren().clear();
        FlagCountTextField.setText("" + difficulty[2]);
        fillBoard(difficulty);
        game = new Game(difficulty, grid);
        gameOver = false;
        TimerTextField.setText("0");
        if(flagMouse == true) {
            flagMouse = false;
            grid.getScene().setCursor(Cursor.DEFAULT);
        }
        firstSelection = true;
    }
    
    /**
     * This method sets up the game board at the given difficulty 
     * @param dif the difficulty setting for the game
     */
    private void setBoard(int[] dif) {
        //creates board's columns
        for(int i = 0; i < dif[1]; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(Dimensions.TILE_WIDTH + 1);
            grid.getColumnConstraints().add(column);
        }
        //creates board's rows
        for(int i = 0; i < dif[0]; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(Dimensions.TILE_WIDTH + 1);
            grid.getRowConstraints().add(row);
        }
       //creates rectangular cells and fills them in the grid
        
    }
    
    private void fillBoard(int[] dif) {
        for(int row = 0; row < dif[0]; row++) {
            for(int col = 0; col < dif[1]; col++) {
                Pane cell = createCell(row, col);
                //GridPane.setHalignment(cell, HPos.CENTER);
                GridPane.setRowIndex(cell, row);
                GridPane.setColumnIndex(cell, col);
                grid.getChildren().addAll(cell);
            }
        }
    }
    
    private Pane createCell(int row, int col) {
        Pane pane = new Pane();
        pane.setPrefSize(Dimensions.TILE_WIDTH, Dimensions.TILE_WIDTH);
        pane.setStyle("-fx-background-color: grey; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
        //pane.setCursor(Cursor.HAND);
        pane.setOnMouseEntered((event)->{
            if(game.getTile(row,col).isCovered())
                pane.setStyle("-fx-background-color: lightgrey; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            });
        pane.setOnMouseExited((event)->{
            if(game.getTile(row,col).isCovered())
                pane.setStyle("-fx-background-color: grey; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            });
        pane.setOnMouseClicked((event)->{
            Pane myPane = (Pane)event.getSource();
                if(!gameOver) {
                    if(flagMouse) {
                        if(game.getFlagCount()>0 && game.getTile(row, col).isCovered()) {
                            if(game.getTile(row, col).isFlagged()) {
                                myPane.getChildren().remove(0);
                                game.incrimentFlags();
                            }else {
                                Image im = new Image("/Images/redFlagSmallCentered.png");
                                ImageView imview = new ImageView(im);
                                myPane.getChildren().addAll(imview);
                                game.decrimentFlags();
                                FlagCountTextField.setText(""+game.getFlagCount());
                            }
                            game.getTile(row, col).toggleFlag();
                        }
                    }else {
                        if(!game.getTile(row, col).isFlagged()) {
                            if(game.checkTile(row, col)) {
                                checkWin();
                            }else {
                                gameOver();
                            }
                        }
                    }
                }
            });
        return pane;
    }
    
    @FXML
    public boolean checkWin() {
        if(game.checkWin()) {
            gameOver = true;
            createVictoryBanner();
            
            return true;
        } 
        return false;
    }
    
    @FXML
    public void setFlagMouse() {
        if(flagMouse) {
            grid.getScene().setCursor(Cursor.DEFAULT);
        }else {
            Image image = new Image("/Images/redFlagSmall.png");
            grid.getScene().setCursor(new ImageCursor(image));
            TimerTextField.getScene().setCursor(new ImageCursor(image));
        }
        flagMouse = !flagMouse;
    }
    
    public  void createVictoryBanner(){
        int width = 400;
        int height = 120;
        Stage stage = new Stage(StageStyle.UNDECORATED);
        //stage.initModality(Modality.WINDOW_MODAL);
        stage.initModality(Modality.APPLICATION_MODAL);
        Button resetButton = new Button("reset");
        Text victoryMessage = new Text("You Win!");
        victoryMessage.setStyle("-FX-font-weight: bold");
        victoryMessage.setFont(new Font("Arial", 30));
        victoryMessage.setUnderline(true);
        Text timeMessage = new Text("Your time: " + TimerTextField.getText() + " seconds");
        timeMessage.setFont(new Font("Arial", 30));
        resetButton.setOnMouseClicked((e)->{
            Button b = (Button)e.getSource();
            Stage tempStage = (Stage) b.getScene().getWindow();
            resetBoard();
            tempStage.close();
        });
        VBox vbox = new VBox(victoryMessage, timeMessage, new Label(""), resetButton);
        vbox.setPrefSize(width, height);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-border-color:black; -fx-border-width: 5; -fx-border-style: solid;");
       // vbox.getChildren(
        Scene scene = new Scene(new Group(vbox), width, height, Color.GREY);
        stage.setScene(scene);
        stage.show();
    }
    
    public void gameOver() {
        gameOver = true;
        int width = 400;
        int height = 120;
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Button resetButton = new Button("reset");
        Text lossMessage = new Text("Game Over");
        lossMessage.setStyle("-FX-font-weight: bold");
        lossMessage.setFont(new Font("Arial", 30));
        lossMessage.setUnderline(true);
        lossMessage.setFill(Color.RED);
        resetButton.setOnMouseClicked((e)->{
            Button b = (Button)e.getSource();
            Stage tempStage = (Stage) b.getScene().getWindow();
            resetBoard();
            tempStage.close();
        });
        VBox vbox = new VBox(lossMessage, new Label(""), resetButton);
        vbox.setPrefSize(width, height);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-border-color:black; -fx-border-width: 5; -fx-border-style: solid;");
       // vbox.getChildren(
        Scene scene = new Scene(new Group(vbox), width, height, Color.GREY);
        stage.setScene(scene);
        stage.show();
    }
}
