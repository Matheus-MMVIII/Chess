package com.chess.model;

import com.chess.exception.BadRequestException;

public class King extends Piece {
    public King(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
    }

    @Override
    public void move(int endLine, int endColumn) {
        if (haveFriend(endLine, endColumn))
            throw new BadRequestException("Attempt to move a piece inside another friend piece. ");

        if (!isInsideBoard(line, column))
            throw new BadRequestException("Attempt to move a piece outside board. ");

        int lineDiff = Math.abs(endLine - line);
        int columnDiff = Math.abs(endColumn - column);

        if (!((lineDiff == 1 && columnDiff == 0) || (lineDiff == 1 && columnDiff == 1) || (lineDiff == 0 && columnDiff == 1))) {
            throw new BadRequestException("Invalid transaction attempt.");
        }

        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
