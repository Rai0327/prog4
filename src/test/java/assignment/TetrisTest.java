package assignment;

import assignment.TetrisPiece;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TetrisTest {
    @Test
    public void BodyNull() {
        Piece Rahul = new TetrisPiece(null);
        for(int i = 0; i < 4; i++) {
            assert(Rahul.getBody() == null);
            assert(Rahul.getType() == null);
            assert(Rahul.getRotationIndex() == 0);
            Rahul = Rahul.clockwisePiece();
        }
        for(int i = 0; i < 3; i++) {
            Rahul = Rahul.counterclockwisePiece();
            assert(Rahul.getBody() == null);
            assert(Rahul.getType() == null);
            assert(Rahul.getRotationIndex() == 0);
        }
    }


    @Test
    public void ClockwiseNull() {
        TetrisPiece Rahul = new TetrisPiece(null);
        Rahul.clockwisePiece();
    }

    @Test
    public void basicDropTest() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.T);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        assert(board.move(Board.Action.DROP) == Board.Result.PLACE);
        int minSkirt = getMinSkirt(board);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, 0 - minSkirt)));
    }

    @Test
    public void dropOnBlock() {
        TetrisBoard board = new TetrisBoard(10, 24);
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        newGrid[newGrid.length - 1][board.getWidth() / 2] = Piece.PieceType.STICK;
        board.setGrid(newGrid);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.T);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        assert(board.move(Board.Action.DROP) == Board.Result.PLACE);
        int minSkirt = getMinSkirt(board);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, 1 - minSkirt)));
    }

    @Test
    public void overHead() {
        TetrisBoard board = new TetrisBoard(10, 24);
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int i = 0; i < board.getWidth(); i++) {
            newGrid[0][i] = Piece.PieceType.STICK;
        }
        board.setGrid(newGrid);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.move(Board.Action.DROP) == Board.Result.PLACE);
        int minSkirt = getMinSkirt(board);
        System.out.println(board.getCurrentPiecePosition().getY());
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, 0 - minSkirt)));
    }

    @Test
    public void leftOutOfBounds() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(0, board.getHeight() / 2));
        assert(board.move(Board.Action.LEFT) == Board.Result.OUT_BOUNDS);
        assert(board.getCurrentPiecePosition().equals(new Point(0, board.getHeight() / 2)));
    }

    @Test
    public void leftIntoPiece() {
        TetrisBoard board = new TetrisBoard(10, 24);
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            newGrid[r][board.getWidth() / 2 - 1] = Piece.PieceType.STICK;
        }
        board.setGrid(newGrid);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.move(Board.Action.LEFT) == Board.Result.OUT_BOUNDS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, board.getHeight() / 2)));
    }

    @Test
    public void leftEmptyPieceSpace() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        piece = (TetrisPiece) piece.clockwisePiece();
        board.nextPiece(piece, new Point(0, board.getHeight() / 2));
        assert(board.move(Board.Action.LEFT) == Board.Result.SUCCESS);
        assert(board.getCurrentPiecePosition().equals(new Point(-1, board.getHeight() / 2)));
        assert(board.move(Board.Action.LEFT) == Board.Result.OUT_BOUNDS);
        assert(board.getCurrentPiecePosition().equals(new Point(-1, board.getHeight() / 2)));
    }

    @Test
    public void rightOutOfBounds() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() - piece.getWidth(), board.getHeight() / 2));
        assert(board.move(Board.Action.RIGHT) == Board.Result.OUT_BOUNDS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() - piece.getWidth(), board.getHeight() / 2)));
    }

    @Test
    public void rightIntoPiece() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            newGrid[r][board.getWidth() / 2 + piece.getWidth()] = Piece.PieceType.STICK;
        }
        board.setGrid(newGrid);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.move(Board.Action.RIGHT) == Board.Result.OUT_BOUNDS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, board.getHeight() / 2)));
    }

    @Test
    public void rightEmptyPieceSpace() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        piece = (TetrisPiece) piece.counterclockwisePiece();
        board.nextPiece(piece, new Point(board.getWidth() - piece.getWidth(), board.getHeight() / 2));
        assert(board.move(Board.Action.RIGHT) == Board.Result.SUCCESS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() - piece.getWidth() + 1, board.getHeight() / 2)));
        assert(board.move(Board.Action.RIGHT) == Board.Result.OUT_BOUNDS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() - piece.getWidth() + 1, board.getHeight() / 2)));
    }

    @Test
    public void downOutOfBounds() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, 0));
        assert(board.move(Board.Action.DOWN) == Board.Result.PLACE);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, 0)));
    }

    @Test
    public void DownIntoPiece() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int c = 0; c < newGrid[0].length; c++) {
            newGrid[newGrid.length - 1][c] = Piece.PieceType.STICK;
        }
        board.setGrid(newGrid);
        board.nextPiece(piece, new Point(board.getWidth() / 2, 1));
        assert(board.move(Board.Action.DOWN) == Board.Result.PLACE);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, 1)));
    }

    @Test
    public void downEmptyPieceSpace() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        board.nextPiece(piece, new Point(board.getWidth() / 2, 0));
        assert(board.move(Board.Action.DOWN) == Board.Result.SUCCESS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, -1)));
        assert(board.move(Board.Action.DOWN) == Board.Result.PLACE);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2, -1)));
    }

    @Test
    public void tClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.T);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 1));
        piece = piece.clockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 2);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(1, 2));
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(1, 0));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 1));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void tCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.T);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 2));
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(1, 0));
        piece = piece.counterclockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 2);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void leftLClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 2));
        piece = piece.clockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
        set.add(new Point(2, 0));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 2);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 2));
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    private int getMinSkirt(Board board) {
        int minSkirt = Integer.MAX_VALUE;
        for (int i = 0; i < board.getCurrentPiece().getSkirt().length; i++) {
            if (board.getCurrentPiece().getSkirt()[i] < minSkirt) {
                minSkirt = board.getCurrentPiece().getSkirt()[i];
            }
        }
        return minSkirt;
    }
}