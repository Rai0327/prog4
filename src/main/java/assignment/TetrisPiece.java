package assignment;

import java.awt.*;
import java.util.LinkedList;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for its
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do pre-computation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {

    /**
     * Construct a tetris piece of the given type. The piece should be in its spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */

    private PieceType type;
    private int[] skirt;
    private int rotationIdx;
    private int width;
    private int height;
    private Point[] body;
    private Node curr;
    private static final int numRotations = 4;

    public TetrisPiece(PieceType type) {
        // TODO: Implement me.
        this.type = type;
        rotationIdx = 0;
        body = type.getSpawnBody();
        width = (int) type.getBoundingBox().getWidth();
        height = (int) type.getBoundingBox().getHeight();
        skirt = new int[width];
        curr = new Node(body, width, height);
        Node head = curr;
        for (int i = 0; i < numRotations; i++) {
            if (i == numRotations - 1) {
                curr.next = head;
            } else {
                Point[] temp = new Point[body.length];
                for (int j = 0; j < body.length; j++) {
                    temp[j] = new Point((int) body[j].getY(), height - 1 - (int) body[j].getX());
                }
                curr.next = new Node(temp, height, width);
            }
            curr.next.prev = curr;
            curr = curr.next;
        }
    }

    @Override
    public PieceType getType() {
        // TODO: Implement me.
        return type;
    }

    @Override
    public int getRotationIndex() {
        // TODO: Implement me.
        return rotationIdx;
    }

    @Override
    public Piece clockwisePiece() {
        // TODO: Implement me.
        curr = curr.next;
        width = curr.getWidth();
        height = curr.getHeight();
        rotationIdx = (rotationIdx + 1) % numRotations;
        return this;
    }

    @Override
    public Piece counterclockwisePiece() {
        curr = curr.prev;
        width = curr.getWidth();
        height = curr.getHeight();
        rotationIdx = (rotationIdx - 1) % numRotations;
        return this;
    }

    @Override
    public int getWidth() {
        // TODO: Implement me.
        return width;
    }

    @Override
    public int getHeight() {
        // TODO: Implement me.
        return height;
    }

    @Override
    public Point[] getBody() {
        // TODO: Implement me.
        return body;
    }

    @Override
    public int[] getSkirt() {
        // TODO: Implement me.
        return skirt;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        // TODO: Implement me.
        return false;
    }

    public void testRotations() {
        for (int i = 0; i < numRotations; i++) {
            int[][] temp = new int[width][height];
            for (int j = 0; j < curr.getBody().length; j++) {
                temp[(int) curr.getBody()[j].getX()][(int) curr.getBody()[j].getY()] = 1;
            }
            print(temp);
            System.out.println(rotationIdx);
            curr = curr.next;
            rotationIdx = (rotationIdx + 1) % numRotations;
            width = curr.getWidth();
            height = curr.getHeight();
        }
        for (int i = 0; i < numRotations; i++) {
            int[][] temp = new int[width][height];
            for (int j = 0; j < curr.getBody().length; j++) {
                temp[(int) curr.getBody()[j].getX()][(int) curr.getBody()[j].getY()] = 1;
            }
            print(temp);
            System.out.println(rotationIdx);
            curr = curr.prev;
            rotationIdx = (rotationIdx - 1) % numRotations;
            width = curr.getWidth();
            height = curr.getHeight();
        }
    }

    public void print(int[][] arr) {
        for (int r = arr.length - 1; r >= 0; r--) {
            for (int c = 0; c < arr[r].length; c++) {
                System.out.print(arr[r][c]);
            }
            System.out.println();
        }
    }
}

class Node {

    private Point[] body;
    private int width;
    private int height;
    Node next;
    Node prev;

    public Node(Point[] body, int width, int height) {
        this.body = body;
        this.width = width;
        this.height = height;
    }

    public Point[] getBody() {
        return body;
    }

    public void setBody(Point[] body) {
        this.body = body;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}