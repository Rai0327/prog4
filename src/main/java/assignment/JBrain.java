package assignment;

import java.awt.*;
import java.util.*;
public class JBrain implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    /**
     * Lame Brain but with a better reward function
     */
    public Board.Action nextMove(Board currentBoard) {
        if (currentBoard == null) {
            return null;
        }

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
        // takes these factors into consideration
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

    // calculated num of holes
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

    // calculates avg column height
    private double getAvgHeight(Board board) {
        double sum = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            sum += board.getColumnHeight(x);
        }
        return sum / board.getWidth();
    }

}