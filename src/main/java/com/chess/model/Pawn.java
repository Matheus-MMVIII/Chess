package com.chess.model;

import com.chess.exception.BadRequestException;

public class Pawn extends Piece {
    public Pawn(char type, int line, int column, boolean white) {
        super(type, line, column, white);
    }
    @Override
    public void move(int endLine, int endColumn, Table table) {
        if (!isInsideBoard(line, column))
            throw new BadRequestException("Attempt to move a piece outside board. ");

        if (column != endColumn)
            throw new BadRequestException("Invalid transaction attempt. ");

        int direction = white ? -1 : 1;
        int startLine = white ? 6 : 1;

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
