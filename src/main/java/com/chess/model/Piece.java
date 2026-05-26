package com.chess.model;

public abstract class Piece {
    private String name;
    private int[][] position;

    public Piece(String name, int[][] position) {
        this.name = name;
        this.position = position;
    }

    public abstract void Move(int[][] finalPosition);

    public int[][] getPosition() {
        return position;
    }
}