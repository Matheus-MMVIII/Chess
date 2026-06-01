package com.chess.model;

public class Pawn extends Piece {
    public Pawn(char type, int line, int column, boolean white) {
        super(type, line, column, white);
    }
    @Override
    public void move(int endLine, int endColumn, Table table) {
        if (column != endColumn)
            throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca. ");

        if (white) {
            if (line == 6) {
                if (endLine < (line-2) || endLine >= line)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca. ");

                if (!(table.getPosNull((endLine-1), endColumn)))
                    throw new IllegalArgumentException("Tentativa de mover peca por cima de outra peca. ");
            }else {
                if (endLine < (line-1) || endLine >= line)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca no momento. ");
            }


        }else {
            if (line == 1) {
                if (endLine > (line+2) || endLine <= line)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca. ");

                if (!(table.getPosNull((endLine+1), endColumn)))
                    throw new IllegalArgumentException("Tentativa de mover peca por cima de outra peca. ");
            }else {
                if (endLine > (line+1) || endLine <= line)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca no momento. ");
            }
        }
        table.removePos(line, column);
        line = endLine;
        column = endColumn;
        table.registerPos(line, column, this);
    }
}
