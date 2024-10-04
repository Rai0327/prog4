package assignment;

import java.awt.*;
import java.util.ArrayList;

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

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        grid = new Piece.PieceType[height][width];
        Piece.PieceType[] pieceTypes = {Piece.PieceType.T, Piece.PieceType.SQUARE, Piece.PieceType.STICK, Piece.PieceType.LEFT_L, Piece.PieceType.RIGHT_L, Piece.PieceType.LEFT_DOG, Piece.PieceType.RIGHT_DOG};
        currPiece = new TetrisPiece(pieceTypes[(int) (Math.random() * pieceTypes.length)]);
        position = new Point((grid[0].length - currPiece.getWidth()) / 2, grid.length - currPiece.getHeight());
    }

    @Override
    public Result move(Action act) {
        lastAction = act;
        switch(act) {
            case LEFT:
                position.setLocation((int) (position.getX() - 1), (int) (position.getY()));
                if (position.getX() < 0 && collision()) {
                    position.setLocation((int) (position.getX() + 1), (int) position.getY());
                    return (lastResult = Result.OUT_BOUNDS);
                }
                return (lastResult = Result.SUCCESS);
            case RIGHT:
                position.setLocation((int) (position.getX() + 1), (int) (position.getY()));
                if (position.getX() >= grid[0].length && collision()) {
                    position.setLocation((int) (position.getX() - 1), (int) position.getY());
                    return (lastResult = Result.OUT_BOUNDS);
                }
                return (lastResult = Result.SUCCESS);
            case DOWN:
                int[] currSkirt = currPiece.getSkirt();
                for(int i = 0; i < currSkirt.length; i++) {
                    if(currSkirt[i] != Integer.MAX_VALUE && grid[(int) position.getY() + currSkirt[i] - 1][(int) (position.getX() + i)] != null) {
                        return (lastResult = Result.OUT_BOUNDS);
                    }
                }
                position.setLocation((int) position.getX(), (int) (position.getY() - 1));
                return (lastResult = Result.SUCCESS);
            case DROP:
                currSkirt = currPiece.getSkirt();
                boolean empty = true;
                while (empty) {
                    position.setLocation(position.getX(), position.getY() - 1);
                    for (int i = 0; i < currSkirt.length; i++) {
                        if (currSkirt[i] != Integer.MAX_VALUE && grid[(int) position.getY() + currSkirt[i]][(int) position.getX() + i] != null) {
                            empty = false;
                        }
                    }
                }
                position.setLocation(position.getX(), position.getY() + 1);
                Point[] body = currPiece.getBody();
                for (Point p : body) {
                    grid[(int) (position.getY() + p.getY())][(int) (position.getX() + p.getX())] = currPiece.getType();
                }
                return (lastResult = Result.PLACE);
            case CLOCKWISE:
                Point[][] wallKicks = Piece.NORMAL_CLOCKWISE_WALL_KICKS;
                if (currPiece.getType() == Piece.PieceType.STICK) {
                    wallKicks = Piece.I_CLOCKWISE_WALL_KICKS;
                }
                return (lastResult = rotate(wallKicks, currPiece.clockwisePiece(), currPiece));
            case COUNTERCLOCKWISE:
                wallKicks = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS;
                if (currPiece.getType() == Piece.PieceType.STICK) {
                    wallKicks = Piece.I_COUNTERCLOCKWISE_WALL_KICKS;
                }
                return (lastResult = rotate(wallKicks, currPiece.counterclockwisePiece(), currPiece));
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
        return -1;
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
        return -1;
    }

    @Override
    public int dropHeight(Piece piece, int x) {
        return -1;
    }

    @Override
    public int getColumnHeight(int x) {
        return -1;
    }

    @Override
    public int getRowWidth(int y) {
        int count = 0;
        for(int i = 0; i < grid[y].length; i++) {
            if(grid[y][i] != null) {
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
            if (grid[(int) (i.getY() + position.getY())][(int) (i.getX() + position.getX())] != null) {
                return true;
            }
        }
        return false;
    }

    private Result rotate(Point[][] wallKicks, Piece rotatedPiece, Piece storedPiece) {
        Point storePosition = position;
        currPiece = rotatedPiece;
        for (int i = 0; i < wallKicks[currPiece.getRotationIndex()].length; i++) {
            if (!collision()) {
                break;
            }
            int wallKickX = (int) wallKicks[currPiece.getRotationIndex()][i].getX();
            int wallKickY = (int) wallKicks[currPiece.getRotationIndex()][i].getY();
            position.setLocation(position.getX() + wallKickX, position.getY() + wallKickY);
        }
        if (collision()) {
            currPiece = storedPiece;
            position = storePosition;
            return Result.OUT_BOUNDS;
        } else {
            return Result.SUCCESS;
        }
    }
}
