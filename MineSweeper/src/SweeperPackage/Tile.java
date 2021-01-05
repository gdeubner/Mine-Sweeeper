/**
 * 
 */
package SweeperPackage;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Graham Deubner
 *
 */
public class Tile {
    
    private int proximalBombs;
    private boolean flagged;
    private boolean covered;
    private boolean isBomb;
    private Pane pane;
    
    public Tile(Pane pane) {
        proximalBombs = 0;
        flagged = false;
        covered = true;
        isBomb = false;
        this.pane = pane;
    }
    
    public int getProximalBombs() {
        return proximalBombs;
    }
    
    public void incrimentProximalBombs() {
        proximalBombs++;
    }
    
    public boolean isFlagged() {
        return flagged;
    }
    
    public void toggleFlag() {
        if(covered)
            flagged = !flagged;
    }
    
    public boolean isCovered() {
        return covered;
    }
    
    public void uncover() {
        covered = false;
    }
    
    public boolean isBomb() {
        return isBomb;
    }
    
    public boolean setBomb() {
        if(!isBomb) {
            isBomb = true;
            return true;
        }
        return false;
    }
    
    public boolean reveal() {
        if(covered == false)
            //do nothing
            return false;
        if(flagged) {
            return false;
        }
        covered = false;
        if(isBomb){
            //reveal bomb
            pane.setStyle("-fx-background-color: white; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            Image im = new Image("/Images/bombSmaller.png");
            ImageView imview = new ImageView(im);
            pane.getChildren().addAll(imview);
            return false;
        }
        if(proximalBombs > 0) {
            //reveal number
            pane.setStyle("-fx-background-color: white; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            Label label = new Label(""+proximalBombs);
            label.setTextFill(Color.web("#FF6767"));
            label.setStyle("-fx-font-weight: bold");
            label.setFont(new Font("Arial", 30));
            label.setAlignment(Pos.CENTER);
            label.layoutXProperty().bind(pane.widthProperty().subtract(label.widthProperty()).divide(2));
            label.layoutYProperty().bind(pane.heightProperty().subtract(label.heightProperty()).divide(2));
            pane.getChildren().add(label);
            return false;
        }else {
            //reveal blank
            //pane.setVisible(false);
            pane.setStyle("-fx-background-color: white; -fx-border-color:lightgrey; -fx-border-width: 1; -fx-border-style: solid;");
            return true;
        }
        
        //return state.reveal();
    }
    
    
}
