package com.chess.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChessServiceTests {
    private ChessService chessService;
    private final List<String> idsTable = new ArrayList<>();

    @BeforeEach
    void setup() {
        chessService = new ChessService();
    }

    @AfterEach
    void cleanup() {
        for (String id : idsTable) {
            chessService.deleteTable(id);
        }
        idsTable.clear();
    }

    @Test
    void mustCreateTable() {
        String id = chessService.createTable();
        assertNotNull(id);
        chessService.deleteTable(id);
    }

    @Test
    void mustDeleteTable() {
        String id = chessService.createTable();
        assertNotNull(id);
        chessService.deleteTable(id);
    }

    @Test
    void mustGetBoard() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        String[][] board = chessService.getBoard(id);
        for (String[] line : board) {
            for (String piece : line) {
                assertNotNull(piece);
            }
        }
    }

    @Test
    void mustPromotedWhitePawnToQueen() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 4, 4, 3, 3);
        chessService.movePiece(id, 1, 2, 2, 2);
        chessService.movePiece(id, 3, 3, 2, 2);
        chessService.movePiece(id, 0, 2, 1, 3);
        chessService.movePiece(id, 2, 2, 1, 1);
        chessService.movePiece(id, 0, 1, 2, 2);
        chessService.movePiece(id, 1, 1, 0, 1);
        chessService.promotePawn(id, 0, 1, 'q');
    }

    @Test
    void mustPromotedWhitePawnToRook() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 4, 4, 3, 3);
        chessService.movePiece(id, 1, 2, 2, 2);
        chessService.movePiece(id, 3, 3, 2, 2);
        chessService.movePiece(id, 0, 2, 1, 3);
        chessService.movePiece(id, 2, 2, 1, 1);
        chessService.movePiece(id, 0, 1, 2, 2);
        chessService.movePiece(id, 1, 1, 0, 1);
        chessService.promotePawn(id, 0, 1, 'r');
    }

    @Test
    void mustPromotedWhitePawnToBishop() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 4, 4, 3, 3);
        chessService.movePiece(id, 1, 2, 2, 2);
        chessService.movePiece(id, 3, 3, 2, 2);
        chessService.movePiece(id, 0, 2, 1, 3);
        chessService.movePiece(id, 2, 2, 1, 1);
        chessService.movePiece(id, 0, 1, 2, 2);
        chessService.movePiece(id, 1, 1, 0, 1);
        chessService.promotePawn(id, 0, 1, 'b');
    }

    @Test
    void mustPromotedWhitePawnToHorse() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 4, 4, 3, 3);
        chessService.movePiece(id, 1, 2, 2, 2);
        chessService.movePiece(id, 3, 3, 2, 2);
        chessService.movePiece(id, 0, 2, 1, 3);
        chessService.movePiece(id, 2, 2, 1, 1);
        chessService.movePiece(id, 0, 1, 2, 2);
        chessService.movePiece(id, 1, 1, 0, 1);
        chessService.promotePawn(id, 0, 1, 'h');
    }

    @Test
    void mustPromotedBlackPawnToQueen() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 6, 5, 5, 5);
        chessService.movePiece(id, 3, 3, 4, 4);
        chessService.movePiece(id, 7, 6, 6, 4);
        chessService.movePiece(id, 4, 4, 5, 5);
        chessService.movePiece(id, 7, 1, 5, 2);
        chessService.movePiece(id, 5, 5, 6, 6);
        chessService.movePiece(id, 6, 3, 4, 3);
        chessService.movePiece(id, 6, 6, 7, 7);
        chessService.promotePawn(id, 7, 7, 'Q');
    }

    @Test
    void mustPromotedBlackPawnToRook() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 6, 5, 5, 5);
        chessService.movePiece(id, 3, 3, 4, 4);
        chessService.movePiece(id, 7, 6, 6, 4);
        chessService.movePiece(id, 4, 4, 5, 5);
        chessService.movePiece(id, 7, 1, 5, 2);
        chessService.movePiece(id, 5, 5, 6, 6);
        chessService.movePiece(id, 6, 3, 4, 3);
        chessService.movePiece(id, 6, 6, 7, 7);
        chessService.promotePawn(id, 7, 7, 'R');
    }

    @Test
    void mustPromotedBlackPawnToBishop() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 6, 5, 5, 5);
        chessService.movePiece(id, 3, 3, 4, 4);
        chessService.movePiece(id, 7, 6, 6, 4);
        chessService.movePiece(id, 4, 4, 5, 5);
        chessService.movePiece(id, 7, 1, 5, 2);
        chessService.movePiece(id, 5, 5, 6, 6);
        chessService.movePiece(id, 6, 3, 4, 3);
        chessService.movePiece(id, 6, 6, 7, 7);
        chessService.promotePawn(id, 7, 7, 'B');
    }

    @Test
    void mustPromotedBlackPawnToHorse() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 6, 5, 5, 5);
        chessService.movePiece(id, 3, 3, 4, 4);
        chessService.movePiece(id, 7, 6, 6, 4);
        chessService.movePiece(id, 4, 4, 5, 5);
        chessService.movePiece(id, 7, 1, 5, 2);
        chessService.movePiece(id, 5, 5, 6, 6);
        chessService.movePiece(id, 6, 3, 4, 3);
        chessService.movePiece(id, 6, 6, 7, 7);
        chessService.promotePawn(id, 7, 7, 'H');
    }

    @Test
    void mustMovPawnLong() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
    }

    @Test
    void mustMovPawn() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 5, 4);
    }

    @Test
    void mustMovKing() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 4, 3, 4);
        chessService.movePiece(id, 7, 4, 6, 4);
    }

    @Test
    void mustMovQueen() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 4, 3, 4);
        chessService.movePiece(id, 7, 3, 3, 7);
    }

    @Test
    void mustMovHorse() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 7, 1, 5, 2);
    }

    @Test
    void mustMovBishop() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 4, 3, 4);
        chessService.movePiece(id, 7, 5, 4, 2);
    }

    @Test
    void mustMovRook() {
        String id = chessService.createTable();
        idsTable.add(id);
        assertNotNull(id);

        chessService.movePiece(id, 6, 0, 4, 0);
        chessService.movePiece(id, 1, 4, 3, 4);
        chessService.movePiece(id, 7, 0, 5, 0);
    }
}
