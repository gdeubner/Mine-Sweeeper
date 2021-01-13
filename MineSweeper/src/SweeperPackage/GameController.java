package SweeperPackage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
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

/**
 * @author Graham Deubner
 *
 */
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
    
    /**
     *Initial setup of the game.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        difficulty = Difficulty.EASY;
        setBoard(difficulty);
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
        Tooltip tooltip = new Tooltip("Press the \"f\" key to toggle flag mode");
        tooltip.setShowDelay(Duration.seconds(0));
        FlagButton.setTooltip(tooltip);
        FlagButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                try {
                    if (t.getCode() == KeyCode.F) {
                        setFlagMouse();
                    }
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }); 
    }
    
    /**
     * This method resets the board, creating a new game.
     */
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
    
    /**
     * This method fills the GridPane with Panes.
     * @param dif
     */
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
    
    /**
     * This method creates a pane object and assigns it the row and column values
     * it will be found at. These values are used to link the Pane to the corresponding 
     * entry in Game class's board[][].
     * @param row
     * @param col
     * @return
     */
    private Pane createCell(int row, int col) {
        Pane pane = new Pane();
        pane.setPrefSize(Dimensions.TILE_WIDTH, Dimensions.TILE_WIDTH);
        pane.setStyle("-fx-background-color: grey; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
        //pane.setCursor(Cursor.HAND);
        pane.setOnMouseEntered((event) -> {
            try {
                if (game.getTile(row, col).isCovered())
                    pane.setStyle(
                            "-fx-background-color: lightgrey; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        pane.setOnMouseExited((event)->{
            try {
                if(game.getTile(row, col).isCovered())
                    pane.setStyle("-fx-background-color: grey; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            });
        pane.setOnMouseClicked((event)->{
            Pane myPane = (Pane)event.getSource();
            if (firstSelection == true) {
                firstSelection = false;
                game.setBombs(row, col);
            }
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
                                if(game.checkWin()) {
                                    endGame(1);
                                }
                            }else {
                                endGame(0);
                            }
                        }
                    }
                }
            });
        return pane;
    }
    
    
    /**
     * This method will replace the mouse with a flag icon, or reset the mouse to
     * its default icon. It will set the boolean flagMouse accordingly.
     */
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
    
    
    /**
     * This method creates the end game sign on the screen, allowing the user to reset the game.
     * @param outcome: 0=lose, 1=win 
     */
    public void endGame(int outcome) {
        gameOver = true;
        int width = 400;
        int height = 140;
        int fontSize = 30;
        String font = "Arial";
        String fontStyle = "-FX-font-weight: bold";
        ArrayList<Node> stageContents = new ArrayList<Node>();
        Button resetButton = new Button("reset");
        resetButton.setOnMouseClicked((e)->{
            Button b = (Button)e.getSource();
            Stage tempStage = (Stage) b.getScene().getWindow();
            resetBoard();
            tempStage.close();
        });
        if( outcome==0) {
            Text lossMessage = new Text("Game Over");
            lossMessage.setStyle(fontStyle);
            lossMessage.setFont(new Font(font, fontSize));
            lossMessage.setUnderline(true);
            lossMessage.setFill(Color.RED);
            stageContents.add(lossMessage);
        } else if (outcome == 1) {
            Text victoryMessage = new Text("You Win!");
            victoryMessage.setStyle(fontStyle);
            victoryMessage.setFont(new Font(font, fontSize));
            victoryMessage.setUnderline(true);
            stageContents.add(victoryMessage);
            Text timeMessage = new Text("Your time: " + TimerTextField.getText() + " seconds");
            timeMessage.setFont(new Font(font, fontSize));
            stageContents.add(timeMessage);
        }else {
            throw new IllegalArgumentException("expecting either 0 or 1");
        }
        VBox vbox = new VBox();
        for(Node n : stageContents) {
            vbox.getChildren().addAll(n);
        }
        vbox.getChildren().addAll(new Label(""), resetButton, new Label(""));
        vbox.setPrefSize(width, height);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-border-color:black; -fx-border-width: 5; -fx-border-style: solid;");
        Scene scene = new Scene(new Group(vbox), width, height, Color.GREY);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        Stage mainStage = (Stage)grid.getScene().getWindow();
        stage.setX(mainStage.getX() + (mainStage.getWidth() / 2) - (width / 2));
        stage.setY(mainStage.getY() + (mainStage.getHeight() / 2) - (height / 2));
        stage.show();
    }
}



