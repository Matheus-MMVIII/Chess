package com.chess.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompleteGameTest {

    @Test
    void playsScholarsMateMoveSequence() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();

        chessService.movePiece(id, 6, 4, 4, 4); // e4
        chessService.movePiece(id, 1, 4, 3, 4); // e5
        chessService.movePiece(id, 7, 5, 4, 2); // Bc4
        chessService.movePiece(id, 0, 1, 2, 2); // Nc6
        chessService.movePiece(id, 7, 3, 3, 7); // Qh5
        chessService.movePiece(id, 0, 6, 2, 5); // Nf6
        chessService.movePiece(id, 3, 7, 1, 5); // Qxf7

        assertEquals("q", chessService.getBoard(id)[1][5]);
        assertEquals(".", chessService.getBoard(id)[7][3]);
        assertEquals("K", chessService.getBoard(id)[0][4]);
    }

    @Test
    void playsOpeningDevelopmentSequenceWithCaptures() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();

        chessService.movePiece(id, 6, 4, 4, 4); // e4
        chessService.movePiece(id, 1, 2, 3, 2); // c5
        chessService.movePiece(id, 7, 6, 5, 5); // Nf3
        chessService.movePiece(id, 1, 3, 2, 3); // d6
        chessService.movePiece(id, 6, 3, 4, 3); // d4
        chessService.movePiece(id, 3, 2, 4, 3); // cxd4
        chessService.movePiece(id, 5, 5, 4, 3); // Nxd4
        chessService.movePiece(id, 0, 6, 2, 5); // Nf6
        chessService.movePiece(id, 7, 1, 5, 2); // Nc3

        String[][] board = chessService.getBoard(id);
        assertEquals("h", board[4][3]);
        assertEquals("H", board[2][5]);
        assertEquals("h", board[5][2]);
        assertEquals(".", board[6][3]);
        assertEquals(".", board[1][2]);
    }
}
