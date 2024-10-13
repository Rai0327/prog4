package assignment;

public class MerryChristmas extends JTetris{
    private boolean troll = true;
    public static void main(String[] args) {
        createGUI(new MerryChristmas());
    }

    @Override
    public Piece pickNextPiece() {
        if (troll) {
            troll = false;
            return PIECES[5];
        }else {
            troll = true;
            return PIECES[6];
        }
    }
}