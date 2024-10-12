//package assignment;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class JBrain implements Brain {
//
//    public Board.Action nextMove(Board board) {
//
//        return enumerateBoards(board);
//
//    }
//
//    private ArrayList<Board.Action> enumerateActions(Board board) {
//        ArrayList<Board.Action> possibleActions = new ArrayList<Board.Action>();
//        if (board.testMove(Board.Action.LEFT).getLastResult() == Board.Result.SUCCESS) {
//            possibleActions.add(Board.Action.LEFT);
//        }
//        if (board.testMove(Board.Action.RIGHT).getLastResult() == Board.Result.SUCCESS) {
//            possibleActions.add(Board.Action.RIGHT);
//        }
//        if (board.testMove(Board.Action.DOWN).getLastResult() == Board.Result.SUCCESS || board.testMove(Board.Action.DOWN).getLastResult() == Board.Result.PLACE) {
//            possibleActions.add(Board.Action.DOWN);
//        }
//        if (board.testMove(Board.Action.CLOCKWISE).getLastResult() == Board.Result.SUCCESS) {
//            possibleActions.add(Board.Action.CLOCKWISE);
//        }
//        if (board.testMove(Board.Action.COUNTERCLOCKWISE).getLastResult() == Board.Result.SUCCESS) {
//            possibleActions.add(Board.Action.COUNTERCLOCKWISE);
//        }
//        possibleActions.add(Board.Action.DROP);
//        return possibleActions;
//    }
//
//    private Board.Action enumerateBoards(Board board) {
//        ArrayList<Board.Action> possibleActions = enumerateActions(board);
//        HashMap<Double, Board.Action> scores = new HashMap<Double, Board.Action>();
//        //ArrayList<Double> scores = new ArrayList<Double>();
//
//        for (Board.Action action : possibleActions) {
//            double score = simulateActionSequence(board, action);
//            scores.put(score, action);
//        }
//
//        return scores.get(getMaxValue(new ArrayList<>(scores.keySet())));
//    }
//
//    private double simulateActionSequence(Board board, Board.Action action) {
//        Board newBoard = board.testMove(action);
//
//        if (newBoard.getLastResult() == Board.Result.PLACE) {
//            return getScore(board, action);
//        }
//
//        ArrayList<Board.Action> nextActions = enumerateActions(newBoard);
//        ArrayList<Double> scores = new ArrayList<Double>();
//
//        for (Board.Action nextAction : nextActions) {
//            if (newBoard.testMove(Board.Action.DOWN).getLastResult() == Board.Result.PLACE) {
//                return getScore(newBoard, Board.Action.DOWN);
//            }
//            double score = simulateActionSequence(newBoard, nextAction);
//            scores.add(score);
//        }
//
//        return getMaxValue(scores);
//    }
//
//
//    private double getMaxValue(List<Double> list) {
//        double max = Double.NEGATIVE_INFINITY;
//        for (double el : list) {
//            if (el > max) {
//                max = el;
//            }
//        }
//        return max;
//    }
//
//    private double getScore(Board board, Board.Action action) {
//        double score = 0;
//        Board newBoard = board.testMove(action);
//        int maxHeightDiff = newBoard.getMaxHeight() - board.getMaxHeight();
//        int holesDiff = getHoles(newBoard) - getHoles(board);
//        int rowsCleared = newBoard.getRowsCleared();
//        double avgHeightDiff = getAvgHeight(newBoard) - getAvgHeight(board);
//        score -= maxHeightDiff * 50;
//        score -= holesDiff * 10;
//        score -= avgHeightDiff * 40;
//        score += rowsCleared * 50;
//        return score;
//    }
//
//    private int getHoles(Board board) {
//        Piece.PieceType[][] grid = new Piece.PieceType[board.getHeight()][board.getWidth()];
//        for (int r = 0; r < grid.length; r++) {
//            for (int c = 0; c < grid[r].length; c++) {
//                grid[grid.length - 1 - r][c] = board.getGrid(c, r);
//            }
//        }
//        int numHoles = 0;
//        for (int r = 0; r < grid.length; r++) {
//            for (int c = 0; c < grid[r].length; c++) {
//                int surroundingBlocks = 0;
//                int blockCount = 0;
//                if (grid[r][c] == null) {
//                    for (int i = -1; i < 2; i += 2) {
//                        for (int j = -1; j < 2; j += 2) {
//                            int x = c + j;
//                            int y = r + i;
//                            if (y >= 0 && y < grid.length && x >= 0 && x < grid[y].length) {
//                                surroundingBlocks++;
//                                if (grid[y][x] != null) {
//                                    blockCount++;
//                                }
//
//                            }
//                        }
//                    }
//                    if (blockCount > surroundingBlocks / 2) {
//                        numHoles++;
//                    }
//                }
//            }
//        }
//        return numHoles;
//    }
//
//    private double getAvgHeight(Board board) {
//        int sum = 0;
//        for (int x = 0; x < board.getWidth(); x++) {
//            sum += board.getColumnHeight(x);
//        }
//        return sum / board.getWidth();
//    }
//
//}



package assignment;

import java.awt.*;
import java.util.*;

/**
 * A Lame Brain implementation for JTetris; tries all possible places to put the
 * piece (but ignoring rotations, because we're lame), trying to minimize the
 * total height of pieces on the board.
 */
public class JBrain implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    /**
     * Decide what the next move should be based on the state of the board.
     */
    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        double best = 0;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            double score = scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        Piece.PieceType[][] grid = new Piece.PieceType[currentBoard.getHeight()][currentBoard.getWidth()];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[grid.length - 1 - r][c] = currentBoard.getGrid(c, r);
            }
        }

        // We want to return the first move on the way to the best Board
        //System.out.println(firstMoves.get(bestIndex));
        System.out.println(options.size());
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);

        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }
    }


    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0.
     */
    private double scoreBoard(Board newBoard) {
        double score = 0;
        int maxHeight = newBoard.getMaxHeight();
        int holes = getHoles(newBoard);
        int rowsCleared = newBoard.getRowsCleared();
        double avgHeight = getAvgHeight(newBoard);
        score -= maxHeight * 50;
        score -= holes * 20;
        score -= avgHeight * 40;
        score += rowsCleared * 50;
        return score;
    }

    private int getHoles(Board board) {
        Piece.PieceType[][] grid = new Piece.PieceType[board.getHeight()][board.getWidth()];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[grid.length - 1 - r][c] = board.getGrid(c, r);
            }
        }
        int numHoles = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                int surroundingBlocks = 0;
                int blockCount = 0;
                if (grid[r][c] == null) {
                    for (int i = -1; i < 2; i += 2) {
                        for (int j = -1; j < 2; j += 2) {
                            int x = c + j;
                            int y = r + i;
                            if (y >= 0 && y < grid.length && x >= 0 && x < grid[y].length) {
                                surroundingBlocks++;
                                if (grid[y][x] != null) {
                                    blockCount++;
                                }

                            }
                        }
                    }
                    if (blockCount > surroundingBlocks / 2) {
                        numHoles++;
                    }
                }
            }
        }
        return numHoles;
    }

    private double getAvgHeight(Board board) {
        double sum = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            sum += board.getColumnHeight(x);
        }
        return sum / board.getWidth();
    }

}