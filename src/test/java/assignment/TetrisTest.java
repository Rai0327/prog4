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
    public void bodyNull() {
        Piece piece = new TetrisPiece(null);
        assert(piece.getBody() == null);
        assert(piece.getType() == null);
        assert(piece.getRotationIndex() == 0);
    }


    @Test
    public void clockwiseNull() {
        TetrisPiece piece = new TetrisPiece(null);
        assert(piece.clockwisePiece() == null);
    }

    @Test
    public void counterClockwiseNull() {
        TetrisPiece piece = new TetrisPiece(null);
        assert(piece.counterclockwisePiece() == null);
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
        board.setGridArray(newGrid);
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
        board.setGridArray(newGrid);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.move(Board.Action.DROP) == Board.Result.PLACE);
        int minSkirt = getMinSkirt(board);
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
        board.setGridArray(newGrid);
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
        board.setGridArray(newGrid);
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
        board.setGridArray(newGrid);
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

    @Test
    public void leftLCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(0, 0));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        piece = piece.counterclockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
        set.add(new Point(2, 0));
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
        set.add(new Point(2, 2));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 2));
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
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
    public void rightLClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.RIGHT_L);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 0));
        piece = piece.clockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
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
        set.add(new Point(0, 2));
        set.add(new Point(1, 2));
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
        set.add(new Point(2, 1));
        set.add(new Point(2, 2));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void rightLCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.RIGHT_L);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(0, 2));
        set.add(new Point(1, 2));
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
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
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
        set.add(new Point(2, 0));
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
        set.add(new Point(2, 1));
        set.add(new Point(2, 2));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void squareClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.clockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 2);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void squareCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.counterclockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 2);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void rightDogClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 0));
        set.add(new Point(2, 1));
        piece = piece.clockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
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
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(0, 1));
        set.add(new Point(0, 2));
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
        set.add(new Point(2, 2));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void rightDogCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(0, 1));
        set.add(new Point(0, 2));
        piece = piece.counterclockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 0));
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
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(2, 0));
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
        set.add(new Point(2, 2));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void leftDogClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
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
        set.add(new Point(1, 0));
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
        set.add(new Point(0, 1));
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
    public void leftDogCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(0, 0));
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        piece = piece.counterclockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(1, 0));
        set.add(new Point(2, 0));
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
        set.add(new Point(2, 1));
        set.add(new Point(2, 2));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 2));
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
    public void stickClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.STICK);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(2, 0));
        set.add(new Point(2, 1));
        set.add(new Point(2, 2));
        set.add(new Point(2, 3));
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
        set.add(new Point(3, 1));
        piece = piece.clockwisePiece();
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
        set.add(new Point(1, 3));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 2));
        set.add(new Point(1, 2));
        set.add(new Point(2, 2));
        set.add(new Point(3, 2));
        piece = piece.clockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void stickCounterClockwiseRotations() {
        Piece piece = new TetrisPiece(Piece.PieceType.STICK);
        Set<Point> set = new HashSet<Point>();
        set.add(new Point(1, 0));
        set.add(new Point(1, 1));
        set.add(new Point(1, 2));
        set.add(new Point(1, 3));
        piece = piece.counterclockwisePiece();
        Set<Point> ans = new HashSet<Point>();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 3);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 1));
        set.add(new Point(1, 1));
        set.add(new Point(2, 1));
        set.add(new Point(3, 1));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 2);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(2, 0));
        set.add(new Point(2, 1));
        set.add(new Point(2, 2));
        set.add(new Point(2, 3));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 1);
        assert(ans.equals(set));
        set.clear();
        set.add(new Point(0, 2));
        set.add(new Point(1, 2));
        set.add(new Point(2, 2));
        set.add(new Point(3, 2));
        piece = piece.counterclockwisePiece();
        ans.clear();
        for (Point p : piece.getBody()) {
            ans.add(p);
        }
        assert(piece.getRotationIndex() == 0);
        assert(ans.equals(set));
    }

    @Test
    public void unevenPlacement() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        newGrid[board.getHeight() / 2 - 1][board.getWidth() / 2 + piece.getWidth()] = Piece.PieceType.T;
        newGrid[board.getHeight() / 2 - 1][board.getWidth() / 2 + piece.getWidth() + 1] = Piece.PieceType.T;
        newGrid[board.getHeight() / 2][board.getWidth() / 2 + piece.getWidth() + 1] = Piece.PieceType.T;
        board.setGridArray(newGrid);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.move(Board.Action.RIGHT) == Board.Result.SUCCESS);
        assert(board.getCurrentPiecePosition().equals(new Point(board.getWidth() / 2 + 1, board.getHeight() / 2)));
        assert(board.move(Board.Action.DOWN) == Board.Result.PLACE);
    }

    @Test
    public void stickWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.I_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.STICK);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.I_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void rightDogWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.NORMAL_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void leftDogWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.LEFT_DOG);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.NORMAL_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void rightLWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.RIGHT_L);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.RIGHT_L);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.NORMAL_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void leftLWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.LEFT_L);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.NORMAL_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void squareWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.SQUARE);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.NORMAL_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void tWallKicks() {
        TetrisBoard board = new TetrisBoard(50, 50);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.T);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < newGrid.length; r++) {
            for (int c = 0; c < newGrid[0].length; c++) {
                newGrid[r][c] = Piece.PieceType.STICK;
            }
        }
        for (int i = 0; i < Piece.NORMAL_CLOCKWISE_WALL_KICKS.length; i++) {
            TetrisPiece temp = new TetrisPiece(Piece.PieceType.T);
            board.nextPiece(temp, new Point(board.getWidth() / 2, board.getHeight() / 2));
            while (temp.getRotationIndex() != i) {
                board.move(Board.Action.CLOCKWISE);
            }
            for (int j = 0; j < Piece.NORMAL_CLOCKWISE_WALL_KICKS[i].length; j++) {
                for (int k = 0; k < j; k++) {
                    for (Point p : board.getCurrentPiece().getBody()) {
                        newGrid[newGrid.length - 1 - (int) (board.getCurrentPiecePosition().getY() + p.getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getY())][(int) (board.getCurrentPiecePosition().getX() + p.getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][k].getX())] = null;
                    }
                }
                board.setGridArray(newGrid);
                if (j == 0) {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.OUT_BOUNDS);
                } else {
                    assert(board.move(Board.Action.CLOCKWISE) == Board.Result.SUCCESS);
                }
                assert(board.getCurrentPiecePosition().equals(new Point((int) (board.getCurrentPiecePosition().getX() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getX()), (int) (board.getCurrentPiecePosition().getY() + Piece.NORMAL_CLOCKWISE_WALL_KICKS[i][j].getY()))));
                assert(board.getCurrentPiece().getRotationIndex() == i);
            }
            for (int r = 0; r < newGrid.length; r++) {
                for (int c = 0; c < newGrid[0].length; c++) {
                    newGrid[r][c] = Piece.PieceType.STICK;
                }
            }
        }
    }

    @Test
    public void testTestMove() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.T);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        assert(board.testMove(Board.Action.NOTHING).equals(board));
        Board testBoard = board.testMove(Board.Action.DROP);
        assert(!board.equals(testBoard));
        assert(board.getLastAction() != Board.Action.DROP);
        assert(testBoard.getLastAction() == Board.Action.DROP);
    }

    @Test
    public void tetrisBoardInvalidInputs() {
        TetrisBoard board = new TetrisBoard(-3, 0);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.T);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        board.move(Board.Action.DROP);
    }

    @Test
    public void moveNullInput() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        board.move(null);
        assert(board.getLastAction() == null);
        assert(board.getLastResult() == null);
    }

    @Test
    public void testMoveNullInput() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.testMove(null) == null);
    }

    @Test
    public void nullCurrentPiece() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getCurrentPiece() == null);
    }

    @Test
    public void nullCurrentPosition() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getCurrentPiecePosition() == null);
    }

    @Test
    public void nextPieceNullInputs() {
        TetrisBoard board = new TetrisBoard(10, 24);
        board.nextPiece(null, null);
        assert(board.getCurrentPiecePosition() == null);
        assert(board.getCurrentPiece() == null);
        assert(board.move(Board.Action.DOWN) == null);
    }

    @Test
    public void invalidSpawnPosition() {
        TetrisBoard board = new TetrisBoard(2, 2);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(10, 10));
        assert(board.getCurrentPiecePosition() == null);
        assert(board.getCurrentPiece() == null);
        assert(board.move(Board.Action.LEFT) == null);
    }

    @Test
    public void equalsTest() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.equals(board));
        assert(!board.equals(3));
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(!board.equals(board.testMove(Board.Action.DROP)));
        Board newBoard = board.testMove(Board.Action.NOTHING);
        assert(board.equals(newBoard));
        newBoard.nextPiece(piece, new Point(0, 0));
        assert(!board.equals(newBoard));
    }

    @Test
    public void testGetLastResult() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getLastResult() == null);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.testMove(Board.Action.CLOCKWISE).getLastResult() == Board.Result.SUCCESS);
        assert(board.testMove(Board.Action.COUNTERCLOCKWISE).getLastResult() == Board.Result.SUCCESS);
        assert(board.testMove(Board.Action.DOWN).getLastResult() == Board.Result.SUCCESS);
        assert(board.testMove(Board.Action.LEFT).getLastResult() == Board.Result.SUCCESS);
        assert(board.testMove(Board.Action.RIGHT).getLastResult() == Board.Result.SUCCESS);
        assert(board.testMove(Board.Action.NOTHING).getLastResult() == Board.Result.SUCCESS);
        assert(board.testMove(Board.Action.DROP).getLastResult() == Board.Result.PLACE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, 0));
        assert(board.testMove(Board.Action.DOWN).getLastResult() == Board.Result.PLACE);
        board.nextPiece(piece, new Point(0, board.getHeight() / 2));
        assert(board.testMove(Board.Action.LEFT).getLastResult() == Board.Result.OUT_BOUNDS);
        board.nextPiece(piece, new Point(board.getWidth() - piece.getWidth(), board.getHeight() / 2));
        assert(board.testMove(Board.Action.RIGHT).getLastResult() == Board.Result.OUT_BOUNDS);
    }

    @Test
    public void testGetLastAction() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getLastAction() == null);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() / 2));
        assert(board.testMove(Board.Action.CLOCKWISE).getLastAction() == Board.Action.CLOCKWISE);
        assert(board.testMove(Board.Action.COUNTERCLOCKWISE).getLastAction() == Board.Action.COUNTERCLOCKWISE);
        assert(board.testMove(Board.Action.DOWN).getLastAction() == Board.Action.DOWN);
        assert(board.testMove(Board.Action.LEFT).getLastAction() == Board.Action.LEFT);
        assert(board.testMove(Board.Action.RIGHT).getLastAction() == Board.Action.RIGHT);
        assert(board.testMove(Board.Action.NOTHING).getLastAction() == Board.Action.NOTHING);
        assert(board.testMove(Board.Action.DROP).getLastAction() == Board.Action.DROP);
        Board testBoard = board.testMove(Board.Action.NOTHING);
        testBoard.move(null);
        assert(testBoard.getLastAction() == null);
    }

    @Test
    public void rowsClearedTest() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getRowsCleared() == 0);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(board.getWidth() - piece.getWidth(), board.getHeight() - 4));
        Piece.PieceType[][] newGrid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < newGrid[r].length - 1; c++) {
                newGrid[newGrid.length - 1 - r][c] = Piece.PieceType.STICK;
            }
        }
        board.setGridArray(newGrid);
        board.move(Board.Action.CLOCKWISE);
        assert(board.getRowsCleared() == 0);
        board.move(Board.Action.RIGHT);
        board.move(Board.Action.DROP);
        assert(board.getRowsCleared() == 4);
        assert(board.getMaxHeight() == 0);
    }

    @Test
    public void testGetWidth() {
        TetrisBoard board = new TetrisBoard(-93274, 24);
        assert(board.getWidth() == -1);
        board = new TetrisBoard(0, 50);
        assert(board.getWidth() == -1);
        board = new TetrisBoard(50, 50);
        assert(board.getWidth() == 50);
    }

    @Test
    public void testGetHeight() {
        TetrisBoard board = new TetrisBoard(24, -93274);
        assert(board.getHeight() == -1);
        board = new TetrisBoard(50, 0);
        assert(board.getHeight() == -1);
        board = new TetrisBoard(50, 50);
        assert(board.getHeight() == 50);
    }

    @Test
    public void testMaxHeight() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getMaxHeight() == 0);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(board.getWidth() - piece.getWidth(), board.getHeight() - 4));
        board.move(Board.Action.DROP);
        assert(board.getMaxHeight() == 1);
        piece = new TetrisPiece(Piece.PieceType.T);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        board.move(Board.Action.DROP);
        assert(board.getMaxHeight() == 3);
        // already tested maxHeight adjustments with tetrises when testing the getRowsCleared method
    }

    @Test
    public void testDropHeight() {
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        assert(board.dropHeight(null, 3) == -1);
        assert(board.dropHeight(piece, -1) == -1);
        assert(board.dropHeight(piece, (int) board.getCurrentPiecePosition().getX()) == -2);
        board.move(Board.Action.DROP);
        piece = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        board.move(Board.Action.CLOCKWISE);
        assert(board.dropHeight(piece, (int) board.getCurrentPiecePosition().getX()) == 1);
        board.move(Board.Action.DROP);
        piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        board.nextPiece(piece, new Point(board.getWidth() / 2, board.getHeight() - 4));
        assert(board.dropHeight(piece, (int) board.getCurrentPiecePosition().getX()) == 2);
    }

    @Test
    public void testColumnHeight() {
        TetrisBoard board = new TetrisBoard(10, 24);
        assert(board.getColumnHeight(0) == 0);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(0, board.getHeight() - 4));
        board.move(Board.Action.DROP);
        for (int i = 0; i < board.getCurrentPiece().getWidth(); i++) {
            assert(board.getColumnHeight(i) == 1);
        }
        piece = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        board.nextPiece(piece, new Point(0, board.getHeight() - 4));
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.DROP);
        assert(board.getColumnHeight(0) == 3);
        assert(board.getColumnHeight(1) == 4);
    }

    @Test
    public void nullTetrisPiece() {
        TetrisPiece piece = new TetrisPiece(null);
        assert(piece.clockwisePiece() == null);
        assert(piece.getRotationIndex() == 0);
    }

    @Test
    public void testRotationIdx() {
        Piece piece = new TetrisPiece(Piece.PieceType.STICK);
        for (int i = 0; i < 4; i++) {
            assert(piece.getRotationIndex() == i);
            piece = piece.clockwisePiece();
        }
    }

    @Test
    public void twoByTwoGrid() {
        TetrisBoard board = new TetrisBoard(2, 2);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.SQUARE);
        board.nextPiece(piece, new Point(0, 0));
        assert(board.getCurrentPiecePosition().equals(new Point(0, 0)));
        assert(board.getCurrentPiece().getType() == Piece.PieceType.SQUARE);
        assert(board.testMove(Board.Action.DOWN).getLastResult() == Board.Result.PLACE);
        piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(0, 0));
        assert(board.getCurrentPiecePosition() == null);
        assert(board.getCurrentPiece() == null);
    }

    @Test
    public void nullBrainTest() {
        Brain brain = new JBrain();
        assert(brain.nextMove(null) == null);
    }

    @Test
    public void brainTest() {
        Brain brain = new JBrain();
        TetrisBoard board = new TetrisBoard(10, 24);
        TetrisPiece piece = new TetrisPiece(Piece.PieceType.STICK);
        board.nextPiece(piece, new Point(0, board.getHeight() - 4));
        board.move(brain.nextMove(board));
        assert(board.getLastResult() == Board.Result.SUCCESS || board.getLastResult() == Board.Result.PLACE);
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