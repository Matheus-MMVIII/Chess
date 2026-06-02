package com.chess.model;

import com.chess.exception.BadRequestException;

public class Bishop extends Piece {
    public Bishop(char type, int line, int column, boolean white) {
        super(type, line, column, white);
    }

    @Override
    public void move(int endLine, int endColumn, Table table) {
        if (!isInsideBoard(line, column))
            throw new BadRequestException("Attempt to move a piece outside board. ");

        if (Math.abs(endLine - line) != Math.abs(endColumn - column))
            throw new BadRequestException("Invalid transaction attempt. ");

        int x = Integer.compare(endColumn, column);
        int y = Integer.compare(endLine, line);

        for (int l = line + y, c = column + x; l != endLine; l += y, c += x) {
            if (!table.getPosNull(l, c))
                throw new BadRequestException("Attempt to move a piece over another. ");
        }

        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
