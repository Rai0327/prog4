package assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FairPieceGeneration extends JTetris{
    private ArrayList<Integer> shuffled = new ArrayList<Integer>(
            Arrays.asList(0, 1, 2, 3, 4, 5, 6));
    private int shuffledIndex = 0;
    public static void main(String[] args) {
        createGUI(new FairPieceGeneration());
    }

    @Override
    public Piece pickNextPiece() {
        shuffledIndex = shuffledIndex%7;
        if (shuffledIndex == 0) {
            Collections.shuffle(shuffled);
        }
        for (int i = 0; i < 7; i++) {
            System.out.print(shuffled.get(i));
        }
        System.out.println();
        System.out.println(shuffledIndex);
        shuffledIndex++;
        return PIECES[shuffled.get(shuffledIndex - 1)];
    }

    private void enableButtons() {
        startButton.setEnabled(!gameOn);
        stopButton.setEnabled(gameOn);
    }

    public void stopGame() {
        gameOn = false;
        enableButtons();
        timer.stop();

        long delta = (System.currentTimeMillis() - startTime)/10;
        timeLabel.setText(Double.toString(delta/100.0) + " seconds");
        shuffledIndex = 0;
    }

}