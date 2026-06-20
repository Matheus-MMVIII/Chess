package com.chess.service;

import com.chess.model.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChessService {
    private final Map<String, Table> games = new HashMap<>();

    public ChessService() {
    }

    public String createTable() {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Table());
        return gameId;
    }

    public void deleteTable(Table table) {
        games.remove(table);
    }

    public String[][] getBoard(String id) {
        return games.get(id).getBoard();
    }

    public void movePiece(String id, int startLine, int startColumn, int endLine, int endColumn) {
        games.get(id).move(startLine, startColumn, endLine, endColumn);
    }

    public void promotePawn(String id, int line, int column, char type) {
        games.get(id).promotePawn(line, column, type);
    }
}
