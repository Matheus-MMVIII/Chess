package com.chess.model;

import com.chess.exception.BadRequestException;

public class Pawn extends Piece {
    public Pawn(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
}

    @Override
    public void move(int endLine, int endColumn) {
        basicValidations(endLine, endColumn);

        if (column > (endColumn+1) || column < (endColumn-1))
            throw new BadRequestException("Invalid transaction attempt. ");

        int direction = getIsWhite() ? -1 : 1;

        if (column == endColumn && !table.getPosIsNull(endLine, endColumn))
            throw new BadRequestException("Invalid movement attempt. ");

        int maxMove = isFirstMove() ? 2 : 1;

        int distance = (endLine - line) * direction;

        if (distance <= 0 || distance > maxMove)
            throw new BadRequestException("Currently, the movement is invalid.");

        if (distance == 2) {
            int middleLine = line + direction;

            if (!table.getPosIsNull(middleLine, column))
                throw new BadRequestException("Attempt to move a piece over another. ");

            if (column != endColumn)
                throw new BadRequestException("Invalid movement attempt. ");
        }

        if (column != endColumn && table.getPosIsNull(endLine, endColumn))
            throw new BadRequestException("Currently, invalid movement attempt. ");

        firstMove();
        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
