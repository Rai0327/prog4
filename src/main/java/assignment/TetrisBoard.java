package assignment;

import java.awt.*;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {

    private Piece.PieceType[][] grid;
    private Piece currPiece;
    private Point position;
    private Result lastResult;
    private Action lastAction;
    private int[] colHeights;
    private int maxHeight = 0;
    private int rowsCleared;

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        grid = new Piece.PieceType[height][width];
        colHeights = new int[width];
    }

    @Override
    public Result move(Action act) {
        lastAction = act;
        switch(act) {
            case LEFT:
                // shift position 1 to the left
                position.setLocation((int) (position.getX() - 1), (int) (position.getY()));
                // if the current piece collides with another or goes out of bounds, return to original position
                if (collision()) {
                    position.setLocation((int) (position.getX() + 1), (int) position.getY());
                    return (lastResult = Result.OUT_BOUNDS);
                }
                return (lastResult = Result.SUCCESS);
            case RIGHT:
                // shift position 1 to the right
                position.setLocation((int) (position.getX() + 1), (int) (position.getY()));
                // if the current piece collides with another or goes out of bounds, return to original position
                if (collision()) {
                    position.setLocation((int) (position.getX() - 1), (int) position.getY());
                    return (lastResult = Result.OUT_BOUNDS);
                }
                return (lastResult = Result.SUCCESS);
            case DOWN:
                int[] currSkirt = currPiece.getSkirt();
                for (int i = 0; i < currSkirt.length; i++) {
                    // for each skirt element, if there is a block below it or out of bounds, place the piece
                    if (currSkirt[i] != Integer.MAX_VALUE && currSkirt[i] != Integer.MIN_VALUE && ((int) position.getY() + currSkirt[i] - 1 < 0 || grid[grid.length - 1 - ((int) position.getY() + currSkirt[i] - 1)][(int) (position.getX() + i)] != null)) {
                        place();
                        return (lastResult = Result.PLACE);
                    }
                }
                // move the position down by 1
                position.setLocation((int) position.getX(), (int) (position.getY() - 1));
                return (lastResult = Result.SUCCESS);
            case DROP:
                // drop and place the piece
                position.setLocation(position.getX(), dropHeight(currPiece, (int) position.getY()));
                place();
                return (lastResult = Result.PLACE);
            case CLOCKWISE:
                Point[][] wallKicks = Piece.NORMAL_CLOCKWISE_WALL_KICKS;
                if (currPiece.getType() == Piece.PieceType.STICK) {
                    wallKicks = Piece.I_CLOCKWISE_WALL_KICKS;
                }
                return (lastResult = rotate(wallKicks, true));
            case COUNTERCLOCKWISE:
                wallKicks = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS;
                if (currPiece.getType() == Piece.PieceType.STICK) {
                    wallKicks = Piece.I_COUNTERCLOCKWISE_WALL_KICKS;
                }
                return (lastResult = rotate(wallKicks, false));
            case NOTHING:
                // do nothing :)
                return (lastResult = Result.SUCCESS);
            default:
                return (lastResult = Result.NO_PIECE);
        }
    }

    @Override
    public Board testMove(Action act) {
        // clone the current board and perform the action on it
        Board b = clone();
        b.move(act);
        return b;
    }

    @Override
    public Piece getCurrentPiece() {
        return currPiece;
    }

    @Override
    public Point getCurrentPiecePosition() {
        return position;
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        currPiece = p;
        position = spawnPosition;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TetrisBoard)) {
            return false;
        }
        // check if the grids are equal
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[grid.length - 1 - r][c] != ((TetrisBoard) other).getGrid(c, r)) {
                    return false;
                }
            }
        }
        // check if the current piece and position are equal
        return currPiece.equals(((TetrisBoard) other).getCurrentPiece()) && position.equals(((TetrisBoard) other).getCurrentPiecePosition());
    }

    @Override
    public Result getLastResult() {
        return lastResult;
    }

    @Override
    public Action getLastAction() {
        return lastAction;
    }

    @Override
    public int getRowsCleared() {
        if (getLastResult().equals(Result.PLACE)) {
            return rowsCleared;
        } else {
            return 0;
        }
    }

    @Override
    public int getWidth() {
        return grid[0].length;
    }

    @Override
    public int getHeight() {
        return grid.length;
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public int dropHeight(Piece piece, int x) {
        int[] currSkirt = currPiece.getSkirt();
        int minIdx = 0;
        int minSkirt = currSkirt[minIdx];
        // get the skirt element that has the smallest difference between y-value and column height
        for (int i = 0; i < currSkirt.length; i++) {
            if (position.getX() + minIdx < 0) {
                minIdx++;
                minSkirt = currSkirt[minIdx];
            } else if (position.getX() + i >= getWidth()) {
                break;
            } else if (minSkirt + (position.getY() - getColumnHeight((int) position.getX() + minIdx)) > currSkirt[i] + (position.getY() - getColumnHeight((int) position.getX() + i) - 1)) {
                minIdx = i;
                minSkirt = currSkirt[i];
            }
        }
        // return the difference between y-value and column height subtracted from the current y-position
        return getColumnHeight((int) position.getX() + minIdx) - minSkirt;
    }

    @Override
    public int getColumnHeight(int x) {
        return colHeights[x];
    }

    @Override
    public int getRowWidth(int y) {
        // count all the elements in a row that is not null
        int count = 0;
        for(int i = 0; i < grid[y].length; i++) {
            if(grid[grid.length - 1 - y][i] != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Piece.PieceType getGrid(int x, int y) {
        return grid[grid.length - 1 - y][x];
    }

    // helper method to detect collision and out of bounds
    private boolean collision() {
        // check if current piece intersects with any other blocks or is out of bounds
        for (Point i : currPiece.getBody()) {
            if ((int) (i.getX() + position.getX()) < 0 || (int) (i.getX() + position.getX()) > grid[0].length - 1 || grid[grid.length - 1 - (int) (i.getY() + position.getY())][(int) (i.getX() + position.getX())] != null) {
                return true;
            }
        }
        return false;
    }

    // helper method for rotation
    private Result rotate(Point[][] wallKicks, boolean clockwise) {
        int rotationIdx = currPiece.getRotationIndex();
        Point storePosition = new Point((int) position.getX(), (int) position.getY());
        if (clockwise) {
            currPiece = currPiece.clockwisePiece();
        } else {
            currPiece = currPiece.counterclockwisePiece();
        }
        // iterate through all the wall-kicks until one is a valid move
        for (int i = 0; i < wallKicks[rotationIdx].length; i++) {
            int wallKickX = (int) wallKicks[rotationIdx][i].getX();
            int wallKickY = (int) wallKicks[rotationIdx][i].getY();
            position.setLocation(position.getX() + wallKickX, position.getY() + wallKickY);
            if (!collision()) {
                return Result.SUCCESS;
            }
        }
        // if there is no valid wall-kick, revert to prior piece position
        if (clockwise) {
            currPiece = currPiece.counterclockwisePiece();
        } else {
            currPiece = currPiece.clockwisePiece();
        }
        position = storePosition;
        return Result.OUT_BOUNDS;
    }

    // helper method for placing pieces
    private void place() {
        int[] maxBlocks = new int[currPiece.getWidth()];
        for (int i = 0; i < maxBlocks.length; i++) {
            maxBlocks[i] = Integer.MIN_VALUE;
        }
        // place the current piece on the grid and store the highest block in each altered column
        for (Point p : currPiece.getBody()) {
            grid[grid.length - 1 - (int) (position.getY() + p.getY())][(int) (position.getX() + p.getX())] = currPiece.getType();
            if (p.getY() > maxBlocks[(int) p.getX()]) {
                maxBlocks[(int) p.getX()] = (int) p.getY();
            }
        }
        // update the column heights and max height variables
        for (int i = 0; i < maxBlocks.length; i++) {
            if (maxBlocks[i] != Integer.MIN_VALUE && position.getX() + i >= 0 && position.getX() + i < getWidth()) {
                if (colHeights[(int) position.getX() + i] < position.getY() + maxBlocks[i] + 1) {
                    colHeights[(int) position.getX() + i] = (int) position.getY() + maxBlocks[i] + 1;
                }
                if (colHeights[(int) position.getX() + i] > maxHeight) {
                    maxHeight = colHeights[(int) position.getX() + i];
                }
            }
        }

        // reset the piece so it won't stay rotated when another piece of the same type spawns
        while (currPiece.getRotationIndex() != 0) {
            if (currPiece.getRotationIndex() < 2) {
                currPiece = currPiece.counterclockwisePiece();
            } else {
                currPiece = currPiece.clockwisePiece();
            }
        }
        rowsCleared = rowClear();
    }

    private int rowClear() {
        //Variable counts row clears
        int clears = 0;
        //Iterate through rows with a piece
        for(int r = 1; r < getMaxHeight() + 1; r++) {
            //Checks for filled rows
            if (getRowWidth(r - 1) == grid[grid.length - 1 - (r - 1)].length) {
                //Moves filled rows down
                for (int i = r + 1; i < getMaxHeight() + 2; i++) {
                    for (int j = 0; j < grid[grid.length - 1 - (i - 1)].length; j++) {
                        grid[grid.length - 1 - (i - 2)][j] = grid[grid.length - 1 - (i - 1)][j];
                    }
                }

                maxHeight--;

                //Change column heights
                for (int i = 0; i < colHeights.length; i++) {
                    //Checks if the row cleared is the height
                    if (r == getColumnHeight(i)) {
                        //Makes next non-empty array y index the new column height
                        for (int j = getColumnHeight(i) - 1; j >= 0; j--) {
                            colHeights[i] = 0;
                            if (grid[grid.length - 1 - j][i] != null) {
                                colHeights[i] = j + 1;
                                break;
                            }
                        }
                    } else {
                        colHeights[i]--;
                    }
                }
                clears++;
                r--;
            }
        }
        return clears;
    }

    public void setCurrentPiecePosition(Point pos) {
        position = pos;
    }

    public void setCurrentPiece(Piece p) {
        currPiece = p;
    }

    public void setGridArray(Piece.PieceType[][] grid) {
        this.grid = grid;
    }

    // clone the current TetrisBoard object
    public TetrisBoard clone() {
        TetrisBoard b = new TetrisBoard(getWidth(), getHeight());
        b.setGridArray(grid);
        b.setCurrentPiece(currPiece);
        b.setCurrentPiecePosition(position);
        return b;
    }
}