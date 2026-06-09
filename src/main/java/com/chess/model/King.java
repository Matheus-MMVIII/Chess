package com.chess.model;

import com.chess.exception.BadRequestException;

public class King extends Piece {
    public King(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
    }

    @Override
    public void move(int endLine, int endColumn) {
        basicValidations(endLine, endColumn);

        if (isFirstMove() && (column == (endColumn+2) || column == (endColumn+3) || column == (endColumn-2))) {
            if (endColumn > column) {
                if (table.getPieceFirstMove(line, 7)) {
                    table.removePos(line, column);
                    table.move(line, 7, endLine, 5);
                    line = endLine;
                    column = endColumn;
                    table.registerPos(line, column, this);
                    firstMove();
                    return;
                }
            }else {
                if (table.getPieceFirstMove(line, 7)) {
                    table.removePos(line, column);
                    table.move(line, 0, endLine, 3);
                    line = endLine;
                    column = 2;
                    table.registerPos(line, column, this);
                    firstMove();
                    return;
                }
            }
        }

        int lineDiff = Math.abs(endLine - line);
        int columnDiff = Math.abs(endColumn - column);

        if (!((lineDiff == 1 && columnDiff == 0) || (lineDiff == 1 && columnDiff == 1) || (lineDiff == 0 && columnDiff == 1))) {
            throw new BadRequestException("Invalid transaction attempt.");
        }

        firstMove();
        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
