package com.chess.model;

public abstract class Piece {
    private String name;
    protected int line;
    protected int column;
    private boolean white;

    public Piece(String name, int line, int column, boolean white) {
        this.name = name;
        this.line = line;
        this.column = column;
        this.white = white;
    }

    public abstract void move(int[][] finalPosition, boolean white);

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}