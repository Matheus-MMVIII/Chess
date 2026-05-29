package com.chess.model;

public class Pawn extends Piece {
    public Pawn(String name, int line, int column, boolean white) {
        super(name, line, column, white);
    }
    @Override
    public void move(int endColumn, int endLine, boolean white) {
        if (column != endColumn)
            throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca. ");

        if (white) {
            if (line == 6) {
                if (endLine < (line-2) || endLine >= line)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca. ");

                if (!(board[(endLine-1)][endColumn].equals(".")))
                    throw new IllegalArgumentException("Tentativa de mover peca por cima de outra peca. ");
            }else {
                if (endLine < (line-1) || endLine >= line)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca no momento. ");
            }


        }else {
            if (line == 1) {
                if (endLine > (startLine+2) || endLine <= startLine)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca. ");

                if (!(board[(endLine+1)][endColumn].equals(".")))
                    throw new IllegalArgumentException("Tentativa de mover peca por cima de outra peca. ");
            }else {
                if (endLine > (startLine+1) || endLine <= startLine)
                    throw new IllegalArgumentException("Tentativa de movimentacao fora do escopo da peca no momento. ");
            }
        }
        line = endLine;
        column = endColumn;
    }
}
