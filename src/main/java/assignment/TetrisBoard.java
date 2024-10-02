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
    private Piece nextPiece;

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        grid = new Piece.PieceType[height][width];
        Piece.PieceType[] pieceTypes = {Piece.PieceType.T, Piece.PieceType.SQUARE, Piece.PieceType.STICK, Piece.PieceType.LEFT_L, Piece.PieceType.RIGHT_L, Piece.PieceType.LEFT_DOG, Piece.PieceType.RIGHT_DOG};
        currPiece = new TetrisPiece(pieceTypes[(int) (Math.random() * pieceTypes.length)]);
    }

    @Override
    public Result move(Action act) {
        switch(act) {
            case LEFT:

        }

        return Result.NO_PIECE;
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
        return null;
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        nextPiece = p;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

    @Override
    public Result getLastResult() {
        return Result.NO_PIECE;
    }

    @Override
    public Action getLastAction() {
        return Action.NOTHING;
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
}
