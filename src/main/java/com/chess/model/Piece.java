package com.chess.model;

import com.chess.exception.BadRequestException;

public abstract class Piece {
    private final char type;
    protected int line;
    protected int column;
    private final boolean white;
    protected Table table;
    private boolean firstMove;

    public Piece(char type, int line, int column, boolean white, Table table) {
        this.type = type;
        this.white = white;
        if (!isInsideBoard(line, column))
            throw new BadRequestException("Invalid create piece position. ");
        this.line = line;
        this.column = column;
        this.table = table;
        firstMove = true;
    }

    public char getType() {
        return type;
    }

    public boolean getIsWhite() {
        return white;
    }

    public void firstMove() {
        firstMove = false;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    void setPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public abstract void move(int endLine, int endColumn);

    public boolean isInsideBoard(int line, int column) {
        return line >= 0 && line <= 7 && column >= 0 && column <= 7;
    }

    public boolean haveFriend(int line, int column) {
        return table.haveFriendPiece(line, column, white);
    }

    protected void basicValidations(int endLine, int endColumn) {
        if (!isInsideBoard(endLine, endColumn))
            throw new BadRequestException("Attempt to move a piece outside board. ");

        if (haveFriend(endLine, endColumn))
            throw new BadRequestException("Attempt to move a piece inside another friend piece. ");
    }

}
