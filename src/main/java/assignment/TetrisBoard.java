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
                position.setLocation((int) (position.getX() - 1), (int) (position.getY()));
                if (collision()) {
                    position.setLocation((int) (position.getX() + 1), (int) position.getY());
                    return (lastResult = Result.OUT_BOUNDS);
                }
                return (lastResult = Result.SUCCESS);
            case RIGHT:
                position.setLocation((int) (position.getX() + 1), (int) (position.getY()));
                if (collision()) {
                    position.setLocation((int) (position.getX() - 1), (int) position.getY());
                    return (lastResult = Result.OUT_BOUNDS);
                }
                return (lastResult = Result.SUCCESS);
            case DOWN:
                int[] currSkirt = currPiece.getSkirt();
                for (int i = 0; i < currSkirt.length; i++) {
                    if (currSkirt[i] != Integer.MAX_VALUE && currSkirt[i] != Integer.MIN_VALUE && ((int) position.getY() + currSkirt[i] - 1 < 0 || grid[grid.length - 1 - ((int) position.getY() + currSkirt[i] - 1)][(int) (position.getX() + i)] != null)) {
                        place();
                        return (lastResult = Result.PLACE);
                    }
                }
                position.setLocation((int) position.getX(), (int) (position.getY() - 1));
                return (lastResult = Result.SUCCESS);
            case DROP:
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
                return (lastResult = Result.SUCCESS);
            default:
                return (lastResult = Result.NO_PIECE);
        }
    }

    @Override
    public Board testMove(Action act) {
        return null;
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
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[grid.length - 1 - r][c] != ((TetrisBoard) other).getGrid(c, r)) {
                    return false;
                }
            }
        }
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
        if (getLastAction().equals(Result.PLACE)) {
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
        for (int i = 0; i < currSkirt.length; i++) {
            if (position.getX() + minIdx < 0) {
                minIdx++;
                minSkirt = currSkirt[minIdx];
            } else if (position.getX() + i >= getWidth()) {
                break;
            } else if (minSkirt + (position.getY() - getColumnHeight((int) position.getX() + minIdx) - 1) > currSkirt[i] + (position.getY() - getColumnHeight((int) position.getX() + i) - 1)) {
                minIdx = i;
                minSkirt = currSkirt[i];
            }
        }
        return getColumnHeight((int) position.getX() + minIdx) - 1 - minSkirt;
    }

    @Override
    public int getColumnHeight(int x) {
        return colHeights[x] + 1;
    }

    @Override
    public int getRowWidth(int y) {
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

    private boolean collision() {
        for (Point i : currPiece.getBody()) {
            if ((int) (i.getX() + position.getX()) < 0 || (int) (i.getX() + position.getX()) > grid[0].length - 1 || grid[grid.length - 1 - (int) (i.getY() + position.getY())][(int) (i.getX() + position.getX())] != null) {
                return true;
            }
        }
        return false;
    }

    private Result rotate(Point[][] wallKicks, boolean clockwise) {
        int rotationIdx = currPiece.getRotationIndex();
        Point storePosition = new Point((int) position.getX(), (int) position.getY());
        if (clockwise) {
            currPiece = currPiece.clockwisePiece();
        } else {
            currPiece = currPiece.counterclockwisePiece();
        }
        for (int i = 0; i < wallKicks[rotationIdx].length; i++) {
            int wallKickX = (int) wallKicks[rotationIdx][i].getX();
            int wallKickY = (int) wallKicks[rotationIdx][i].getY();
            position.setLocation(position.getX() + wallKickX, position.getY() + wallKickY);
            if (!collision()) {
                return Result.SUCCESS;
            }
        }
        if (clockwise) {
            currPiece = currPiece.counterclockwisePiece();
        } else {
            currPiece = currPiece.clockwisePiece();
        }
        position = storePosition;
        return Result.OUT_BOUNDS;
    }

    private void place() {
        int[] maxBlocks = new int[currPiece.getWidth()];
        for (int i = 0; i < maxBlocks.length; i++) {
            maxBlocks[i] = Integer.MIN_VALUE;
        }
        for (Point p : currPiece.getBody()) {
            grid[grid.length - 1 - (int) (position.getY() + p.getY())][(int) (position.getX() + p.getX())] = currPiece.getType();
            if (p.getY() > maxBlocks[(int) p.getX()]) {
                maxBlocks[(int) p.getX()] = (int) p.getY();
            }
        }
        for (int i = 0; i < maxBlocks.length; i++) {
            if (maxBlocks[i] != Integer.MIN_VALUE && position.getX() + i >= 0 && position.getX() + i < getWidth()) {
                colHeights[(int) position.getX() + i] = (int) position.getY() + maxBlocks[i] + 1;
                if (colHeights[(int) position.getX() + i] > maxHeight) {
                    maxHeight = colHeights[(int) position.getX() + i];
                }
            }
        }
        while (currPiece.getRotationIndex() != 0) {
            if (currPiece.getRotationIndex() < 2) {
                currPiece = currPiece.counterclockwisePiece();
            } else {
                currPiece = currPiece.clockwisePiece();
            }
        }
        rowsCleared = rowClear();
        System.out.println(rowsCleared);
        System.out.println(getMaxHeight());
    }

    private int rowClear() {
        int clears = 0;
        for(int r = 0; r < getMaxHeight(); r++) {
            if (getRowWidth(r) == grid[grid.length - 1 - r].length) {
                for (int i = r + 1; i < getMaxHeight(); i++) {
                    for (int j = 0; j < grid[grid.length - 1 - i].length; j++) {
                        grid[grid.length - 1 - (i - 1)][j] = grid[grid.length - 1 - i][j];
                    }
                }
                clears++;
                r--;
                //Need to fix column Heights cuz when you remove a row not all columns subtract by 1
                for (int i = 0; i < colHeights.length; i++) {
                    colHeights[i]--;
                }
                maxHeight--;
                for (int i = 0; i < grid[getMaxHeight()].length; i++) {
                    grid[grid.length - 1 - (getMaxHeight())][i] = null;
                }
            }
        }
        return clears;
    }
}