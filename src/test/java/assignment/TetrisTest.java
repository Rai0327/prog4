package assignment;

import assignment.TetrisPiece;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
}