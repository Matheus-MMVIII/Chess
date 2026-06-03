package com.chess.service;

import com.chess.model.Table;

public class ChessService {
    private Table table;

    public ChessService() {
        table = new Table();
    }

    public String[] getBoard() {
        return table.getBoard();
    }

    public void movePiece(int startLine, int startColumn, int endLine, int endColumn) {
        table.move(startLine, startColumn, endLine, endColumn);
    }
}
