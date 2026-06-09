package com.chess.model;

import com.chess.exception.BadRequestException;

public class Queen extends Piece {
    public Queen(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
    }

    @Override
    public void move(int endLine, int endColumn) {
        basicValidations(endLine, endColumn);

        if (line != endLine && column != endColumn) {
            if (Math.abs(endLine - line) != Math.abs(endColumn - column))
                throw new BadRequestException("Invalid transaction attempt. ");

            int x = Integer.compare(endColumn, column);
            int y = Integer.compare(endLine, line);

            for (int l = line + y, c = column + x; l != endLine; l += y, c += x) {
                if (!table.getPosIsNull(l, c))
                    throw new BadRequestException("Attempt to move a piece over another. ");
            }
        }else {
            if (line == endLine) {
                int step = column < endColumn ? 1 : -1;

                for (int c = column + step; c != endColumn; c += step) {
                    if (!table.getPosIsNull(line, c))
                        throw new BadRequestException("Attempt to move a piece over another. ");
                }

            } else {
                int step = line < endLine ? 1 : -1;

                for (int l = line + step; l != endLine; l += step) {
                    if (!table.getPosIsNull(l, column))
                        throw new BadRequestException("Attempt to move a piece over another. ");
                }
            }
        }

        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
