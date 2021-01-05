/**
 * 
 */
package SweeperPackage;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;


/**
 * @author Graham Deubner
 *
 */

public class Game {

    private int flagCount;
    private int bombCount;
    private int rows;
    private int cols;
    private Tile[][] board;
    //private GridPane grid;

    /**
     * This method initializes a new game. It resets the board and the number of flags.
     * 
     * @param difficulty an integer representing the level of difficulty
     */
    public Game(int[] difficulty, GridPane grid) {
        rows = difficulty[0];
        cols = difficulty[1];
        bombCount = difficulty[2];
        flagCount = bombCount;
        
        //creates a new gameBoard
        board = new Tile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Tile((Pane)grid.getChildren().get(i*cols+j));
            }
        }
        setBombs(bombCount);
        
    }
    
    
    /**
     * This method assigns a designated number of tiles to hide bombs.
     */
    private void setBombs(int count) {
        int totalTiles = rows * cols;
        for(int i = 0; i < count; i++) {
            int bombPos = (int) (Math.random() * (totalTiles - 1));
            int columnIndex = bombPos % cols;
            int rowIndex = bombPos / cols;
            
            //System.out.println("Set bomb: " + i + "  bombPos: " + bombPos + "  rowIndex: " + rowIndex + "  columnIndex: " + columnIndex);
            if(!board[rowIndex][columnIndex].setBomb()) {
                i--;
            }else {
                incrimentSurroundingTiles(rowIndex, columnIndex);
            }
        }
    }
    
    /**
     * @param row
     * @param col
     */
    private void incrimentSurroundingTiles(int row, int col) {
        if(row-1 >= 0 && col-1 >= 0) {
            board[row-1][col-1].incrimentProximalBombs();
        }
        if(row-1 >= 0) {
            board[row-1][col].incrimentProximalBombs();
        }
        if(row-1 >= 0 && col+1 <cols) {
            board[row-1][col+1].incrimentProximalBombs();
        }
        if(col+1 < cols) {
            board[row][col+1].incrimentProximalBombs();
        }
        if(row+1 < rows && col+1 < cols) {
            board[row+1][col+1].incrimentProximalBombs();
        }
        if(row+1 < rows) {
            board[row+1][col].incrimentProximalBombs();
        }
        if(row+1 < rows && col-1 >= 0) {
            board[row+1][col-1].incrimentProximalBombs();
        }
        if(col-1 >= 0) {
            board[row][col-1].incrimentProximalBombs();
        }
    }
    
    
    /**
     * Decreases the number of flags that are allowed to be placed
     * on the board by one.
     */
    public void decrimentFlags() {
        flagCount--;
    }
    
    /**
     * Increases the number of flags that are allowed to be placed
     * on the board by one.
     */
    public void incrimentFlags() {
        flagCount++;
    }
    
    /**
     * This method returns the number of unplaced flags.
     * @return Returns an integer value greater than or equal to 0.
     */
    public int getFlagCount() {
        return flagCount;
    }
    
    /**
     * A recursive method which reveals the tile hidden at the given x and y 
     * coordinates and then reveals the surrounding tiles if the central tile
     * is not covering or neighboring a bomb.
     * @param x
     * @param y
     */
    public boolean checkTile(int row, int col) {
        if(board[row][col].reveal()) {
            
            if(row-1 >= 0 && col-1 >= 0) {
                checkTile(row-1, col-1);
            }
            if(row-1 >= 0) {
                checkTile(row-1, col);
            }
            if(row-1 >= 0 && col+1 <cols) {
                checkTile(row-1, col+1);
            }
            if(col+1 < cols) {
                checkTile(row, col+1);
            }
            if(row+1 < rows && col+1 < cols) {
                checkTile(row+1, col+1);
            }
            if(row+1 < rows) {
                checkTile(row+1, col);
            }
            if(row+1 < rows && col-1 >= 0) {
                checkTile(row+1, col-1);
            }
            if(col-1 >= 0) {
                checkTile(row, col-1);
            }
        }
        if(board[row][col].isBomb()) {
            return false;
        }else
            return true;
    }
    
    private void revealAll() {
        for(Tile[] row : board) {
            for(Tile t : row) {
                t.reveal();
            }
        }
    }
    
    /**This method returns the Tile object at the given row and column.
     * @param row
     * @param col
     * @return Tile object, or Null if invalid Row/Col were given.
     */ 
    public Tile getTile(int row, int col) {
        if(row >= 0 && col >=0 && row <rows && col < cols) {
            return board[row][col];
        }else {
            return null;
        }
    }
    
    public boolean checkWin() {
        for(Tile[] row : board) {
            for(Tile t : row) {
                if(t.isCovered()) {
                    if(!t.isBomb()) {
                        return false;
                    }
                }else {
                    if(t.isBomb()) {
                        return false;
                    }
                }
             } 
         }
        return true;
    }

}
