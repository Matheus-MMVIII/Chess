package com.chess.service;

import com.chess.model.Table;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChessService {
    private final Map<String, Table> games = new ConcurrentHashMap<>();

    public ChessService() {
    }

    public String createTable() {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Table());
        return gameId;
    }

    public void deleteTable(String id) {
        games.get(id).deleteBoard();
        games.remove(id);
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
