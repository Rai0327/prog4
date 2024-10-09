package assignment;

import java.awt.*;
import java.util.HashMap;
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
        this.type = type;
        rotationIdx = 0;
        body = type.getSpawnBody();
        width = (int) type.getBoundingBox().getWidth();
        height = (int) type.getBoundingBox().getHeight();
        curr = new Node(body, width, height, rotationIdx);
        Node head = curr;
        // go through the linked list and store all the instance variables for each rotated instance of the piece
        for (int i = 0; i < numRotations; i++) {
            if (i == numRotations - 1) {
                curr.next = head;
            } else {
                Point[] temp = new Point[body.length];
                for (int j = 0; j < body.length; j++) {
                    temp[j] = new Point((int) body[j].getY(), width - 1 - (int) body[j].getX());
                }
                curr.next = new Node(temp, height, width, (curr.getRotationIdx() + 1) % numRotations);
            }
            curr.next.prev = curr;
            curr = curr.next;
            body = curr.getBody();
        }
        skirt = curr.getSkirt();
    }

    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public int getRotationIndex() {
        return rotationIdx;
    }

    // update the instance variables to those of the rotated piece
    @Override
    public Piece clockwisePiece() {
        curr = curr.next;
        body = curr.getBody();
        width = curr.getWidth();
        height = curr.getHeight();
        rotationIdx = curr.getRotationIdx();
        skirt = curr.getSkirt();
        return this;
    }

    // update the instance variables to those of the rotated piece
    @Override
    public Piece counterclockwisePiece() {
        curr = curr.prev;
        body = curr.getBody();
        width = curr.getWidth();
        height = curr.getHeight();
        rotationIdx = curr.getRotationIdx();
        skirt = curr.getSkirt();
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Point[] getBody() {
        return body;
    }

    @Override
    public int[] getSkirt() {
        return skirt;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        return (type == otherPiece.getType()) && (rotationIdx == otherPiece.getRotationIndex());
    }
}

// class to implement the linked list that stores the piece's rotations
class Node {

    private Point[] body;
    private int width;
    private int height;
    private int rotationIdx;
    private int[] skirt;
    Node next;
    Node prev;

    public Node(Point[] body, int width, int height, int rotationIdx) {
        this.body = body;
        this.width = width;
        this.height = height;
        this.rotationIdx = rotationIdx;
        skirt = new int[width];
        for (int i = 0; i < width; i++) {
            skirt[i] = Integer.MAX_VALUE;
        }
        for (Point p : body) {
            if (p.getY() < skirt[(int) p.getX()]) {
                skirt[(int) p.getX()] = (int) p.getY();
            }
        }

    }

    public Point[] getBody() {
        return body;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRotationIdx() {
        return rotationIdx;
    }

    public int[] getSkirt() {
        return skirt;
    }
}