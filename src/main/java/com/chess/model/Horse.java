package com.chess.model;

import com.chess.exception.BadRequestException;

public class Horse extends Piece {
    public Horse(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
    }

    @Override
    public void move(int endLine, int endColumn) {
        basicValidations(endLine, endColumn);

        int lineDiff = Math.abs(endLine - line);
        int columnDiff = Math.abs(endColumn - column);

        if (!((lineDiff == 2 && columnDiff == 1) ||
                (lineDiff == 1 && columnDiff == 2))) {
            throw new BadRequestException("Invalid transaction attempt.");
        }

        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
