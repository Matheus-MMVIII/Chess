package com.chess.model;

import com.chess.exception.BadRequestException;

public class King extends Piece {

    public King(char type, int line, int column, boolean white, Table table) {
        super(type, line, column, white, table);
    }

    @Override
    public void move(int endLine, int endColumn) {
        basicValidations(endLine, endColumn);

        if (isFirstMove()
                && line == endLine
                && Math.abs(endColumn - column) == 2) {
            castle(endColumn);
            return;
        }

        int lineDiff = Math.abs(endLine - line);
        int columnDiff = Math.abs(endColumn - column);

        if (Math.max(lineDiff, columnDiff) != 1) {
            throw new BadRequestException("Invalid king move.");
        }

        table.removePos(line, column);

        line = endLine;
        column = endColumn;

        table.registerPos(line, column, this);
        firstMove();
    }

    private void castle(int endColumn) {

        boolean kingSide = endColumn > column;

        int rookColumn = kingSide ? 7 : 0;
        int rookDestination = kingSide ? 5 : 3;

        if (!table.haveFriendPiece(line, rookColumn, getIsWhite())) {
            throw new BadRequestException("A friendly rook is required for castling.");
        }

        Piece rook = table.getPiece(line, rookColumn);

        if (!(rook instanceof Rook)) {
            throw new BadRequestException("Castling requires a rook.");
        }

        if (!rook.isFirstMove()) {
            throw new BadRequestException("The rook has already moved.");
        }

        int start = Math.min(column, rookColumn) + 1;
        int end = Math.max(column, rookColumn);

        for (int c = start; c < end; c++) {
            if (!table.getPosIsNull(line, c)) {
                throw new BadRequestException("There are pieces between the king and the rook.");
            }
        }

        table.removePos(line, column);
        table.move(line, rookColumn, line, rookDestination);

        column = endColumn;

        table.registerPos(line, column, this);
        firstMove();
    }
}