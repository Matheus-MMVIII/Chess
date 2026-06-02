package com.chess.model;

import com.chess.exception.BadRequestException;

public abstract class Piece {
    private final char type;
    protected int line;
    protected int column;
    private final boolean white;
    protected Table table;

    public Piece(char type, int line, int column, boolean white, Table table) {
        this.type = type;
        this.white = white;
        if (!isInsideBoard(line, column))
            throw new BadRequestException("Invalid create piece position. ");
        this.line = line;
        this.column = column;
        this.table = table;
    }

    public char getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean getIsWhite() {
        return white;
    }

    public abstract void move(int endLine, int endColumn);

    public boolean isInsideBoard(int line, int column) {
        return line >= 0 && line <= 7 && column >= 0 && column <= 7;
    }

    public boolean haveFriend(int line, int column) {
        return table.haveFriendPiece(line, column, white);
    }

}