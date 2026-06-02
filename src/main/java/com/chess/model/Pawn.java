package com.chess.model;

import com.chess.exception.BadRequestException;

public class Pawn extends Piece {
    public Pawn(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
}

    @Override
    public void move(int endLine, int endColumn) {
        if (haveFriend(endLine, endColumn))
            throw new BadRequestException("Attempt to move a piece inside another friend piece. ");

        if (!isInsideBoard(line, column))
            throw new BadRequestException("Attempt to move a piece outside board. ");

        if (column != endColumn)
            throw new BadRequestException("Invalid transaction attempt. ");

        int direction = getIsWhite() ? -1 : 1;
        int startLine = getIsWhite() ? 6 : 1;

        boolean firstMove = line == startLine;
        int maxMove = firstMove ? 2 : 1;

        int distance = (endLine - line) * direction;

        if (distance <= 0 || distance > maxMove)
            throw new BadRequestException("Invalid movement attempt. ");

        if (distance == 2) {
            int middleLine = line + direction;

            if (!table.getPosNull(middleLine, column))
                throw new BadRequestException("Attempt to move a piece over another. ");
        }

        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
