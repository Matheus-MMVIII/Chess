package com.chess.model;

public abstract class Piece {
    private final char type;
    protected int line;
    protected int column;
    protected boolean white;

    public Piece(char type, int line, int column, boolean white) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.white = white;
    }

    public char getType() {
        return type;
    }

    public abstract void move(int endColumn, int endLine, Table table);

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}