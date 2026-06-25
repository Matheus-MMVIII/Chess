package com.chess.service;

import com.chess.exception.BadRequestException;
import com.chess.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessServiceBehaviorTest {

    @Test
    void createdGameIdsAreUnique() {
        ChessService chessService = new ChessService();
        Set<String> ids = new HashSet<>();

        for (int i = 0; i < 25; i++) {
            assertTrue(ids.add(chessService.createTable()));
        }
    }

    @Test
    void gamesAreIndependent() {
        ChessService chessService = new ChessService();
        String firstGame = chessService.createTable();
        String secondGame = chessService.createTable();

        chessService.movePiece(firstGame, 6, 4, 4, 4);

        assertEquals("p", chessService.getBoard(firstGame)[4][4]);
        assertEquals(".", chessService.getBoard(secondGame)[4][4]);
        assertEquals("p", chessService.getBoard(secondGame)[6][4]);
    }

    @Test
    void getBoardReturnsACopyOfTheCurrentBoard() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();

        String[][] board = chessService.getBoard(id);
        board[6][4] = "changed";

        assertEquals("p", chessService.getBoard(id)[6][4]);
    }

    @Test
    void deletedGameCannotBeReadAgain() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();

        chessService.deleteTable(id);

        assertThrows(NotFoundException.class, () -> chessService.getBoard(id));
    }

    @Test
    void unknownGameCannotBeRead() {
        ChessService chessService = new ChessService();

        assertThrows(NotFoundException.class, () -> chessService.getBoard("missing-game"));
    }

    @Test
    void unknownGameCannotBeMoved() {
        ChessService chessService = new ChessService();

        assertThrows(NotFoundException.class, () -> chessService.movePiece("missing-game", 6, 4, 4, 4));
    }

    @Test
    void unknownGameCannotBeDeleted() {
        ChessService chessService = new ChessService();

        assertThrows(NotFoundException.class, () -> chessService.deleteTable("missing-game"));
    }

    @Test
    void unknownGameCannotPromotePawn() {
        ChessService chessService = new ChessService();

        assertThrows(NotFoundException.class, () -> chessService.promotePawn("missing-game", 0, 1, 'q'));
    }

    @Test
    void movePieceUpdatesTheBoard() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();

        chessService.movePiece(id, 6, 4, 4, 4);

        assertEquals(".", chessService.getBoard(id)[6][4]);
        assertEquals("p", chessService.getBoard(id)[4][4]);
    }

    @Test
    void invalidMoveDoesNotChangeBoard() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();
        String before = chessService.getBoard(id)[6][4];

        assertThrows(BadRequestException.class, () -> chessService.movePiece(id, 6, 4, 3, 4));

        assertEquals(before, chessService.getBoard(id)[6][4]);
        assertEquals(".", chessService.getBoard(id)[3][4]);
    }

    @Test
    void promotionChangesThePawnType() {
        ChessService chessService = new ChessService();
        String id = chessService.createTable();

        playWhitePromotionPath(chessService, id);
        chessService.promotePawn(id, 0, 1, 'q');

        assertEquals("q", chessService.getBoard(id)[0][1]);
        assertNotEquals("p", chessService.getBoard(id)[0][1]);
    }

    private static void playWhitePromotionPath(ChessService chessService, String id) {
        chessService.movePiece(id, 6, 4, 4, 4);
        chessService.movePiece(id, 1, 3, 3, 3);
        chessService.movePiece(id, 4, 4, 3, 3);
        chessService.movePiece(id, 1, 2, 2, 2);
        chessService.movePiece(id, 3, 3, 2, 2);
        chessService.movePiece(id, 0, 2, 1, 3);
        chessService.movePiece(id, 2, 2, 1, 1);
        chessService.movePiece(id, 0, 1, 2, 2);
        chessService.movePiece(id, 1, 1, 0, 1);
    }
}
