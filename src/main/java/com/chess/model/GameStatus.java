package com.chess.model;

public enum GameStatus {
    ACTIVE(false, false),
    CHECK(false, false),
    PROMOTION_REQUIRED(false, false),
    CHECKMATE(true, false),
    STALEMATE(true, true),
    INSUFFICIENT_MATERIAL(true, true),
    THREEFOLD_REPETITION(true, true),
    FIFTY_MOVE_RULE(true, true);

    private final boolean gameOver;
    private final boolean draw;

    GameStatus(boolean gameOver, boolean draw) {
        this.gameOver = gameOver;
        this.draw = draw;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isDraw() {
        return draw;
    }
}
