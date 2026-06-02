package com.chess.model;

import com.chess.exception.BadRequestException;

public abstract class Piece {
    private final char type;
    protected int line;
    protected int column;
    protected boolean white;

    public Piece(char type, int line, int column, boolean white) {
        this.type = type;
        this.white = white;
        if (!isInsideBoard(line, column))
            throw new BadRequestException("Invalid create piece position. ");
        this.line = line;
        this.column = column;
    }

    public char getType() {
        return type;
    }

    public abstract void move(int endLine, int endColumn, Table table);

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean isInsideBoard(int line, int column) {
        return line >= 0 && line <= 7 && column >= 0 && column <= 7;
    }
}