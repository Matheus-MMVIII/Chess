package com.chess.model;

import com.chess.exception.BadRequestException;

public class Rook extends Piece {
    public Rook(char type, int line, int column, boolean white) {
        super(type, line, column, white);
    }
    @Override
    public void move(int endLine, int endColumn, Table table) {
        if (line != endLine && column != endColumn)
            throw new BadRequestException("Invalid transaction attempt.");

        if (line == endLine) {
            int step = column < endColumn ? 1 : -1;

            for (int c = column + step; c != endColumn; c += step) {
                if (!table.getPosNull(line, c))
                    throw new BadRequestException("Existe uma peça no caminho.");
            }

        } else {
            int step = line < endLine ? 1 : -1;

            for (int l = line + step; l != endLine; l += step) {
                if (!table.getPosNull(l, column))
                    throw new BadRequestException("Existe uma peça no caminho.");
            }
        }

        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
