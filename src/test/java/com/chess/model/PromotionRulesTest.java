package com.chess.model;

import com.chess.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PromotionRulesTest {

    @ParameterizedTest
    @CsvSource({
            "q,q",
            "r,r",
            "b,b",
            "h,h",
            "Q,q",
            "R,r",
            "B,b",
            "H,h"
    })
    void whitePawnPromotesToAllowedPieces(char requestedType, char expectedType) {
        Table table = emptyBoard();
        whitePawn(table, 0, 1);

        table.promotePawn(0, 1, requestedType);

        assertSquare(table, 0, 1, expectedType);
    }

    @ParameterizedTest
    @CsvSource({
            "q,Q",
            "r,R",
            "b,B",
            "h,H",
            "Q,Q",
            "R,R",
            "B,B",
            "H,H"
    })
    void blackPawnPromotesToAllowedPieces(char requestedType, char expectedType) {
        Table table = emptyBoard();
        blackPawn(table, 7, 6);

        table.promotePawn(7, 6, requestedType);

        assertSquare(table, 7, 6, expectedType);
    }

    @Test
    void cannotPromoteEmptySquare() {
        Table table = emptyBoard();

        assertThrows(BadRequestException.class, () -> table.promotePawn(0, 1, 'q'));
    }

    @Test
    void cannotPromoteNonPawn() {
        Table table = emptyBoard();
        whiteRook(table, 0, 1);

        assertThrows(BadRequestException.class, () -> table.promotePawn(0, 1, 'q'));
    }

    @Test
    void whitePawnCannotPromoteBeforeLastRank() {
        Table table = emptyBoard();
        whitePawn(table, 1, 1);

        assertThrows(BadRequestException.class, () -> table.promotePawn(1, 1, 'q'));
    }

    @Test
    void blackPawnCannotPromoteBeforeLastRank() {
        Table table = emptyBoard();
        blackPawn(table, 6, 1);

        assertThrows(BadRequestException.class, () -> table.promotePawn(6, 1, 'Q'));
    }

    @ParameterizedTest
    @CsvSource({
            "k",
            "p",
            "x",
            "."
    })
    void cannotPromoteToInvalidPiece(char invalidType) {
        Table table = emptyBoard();
        whitePawn(table, 0, 1);

        assertThrows(BadRequestException.class, () -> table.promotePawn(0, 1, invalidType));
    }

    private static Table emptyBoard() {
        Table table = new Table();
        for (int line = 0; line < 8; line++) {
            for (int column = 0; column < 8; column++) {
                table.removePos(line, column);
            }
        }
        return table;
    }

    private static Pawn whitePawn(Table table, int line, int column) {
        Pawn pawn = new Pawn('p', line, column, true, table);
        table.registerPos(line, column, pawn);
        return pawn;
    }

    private static Pawn blackPawn(Table table, int line, int column) {
        Pawn pawn = new Pawn('P', line, column, false, table);
        table.registerPos(line, column, pawn);
        return pawn;
    }

    private static Rook whiteRook(Table table, int line, int column) {
        Rook rook = new Rook('r', line, column, true, table);
        table.registerPos(line, column, rook);
        return rook;
    }

    private static void assertSquare(Table table, int line, int column, char expected) {
        assertEquals(String.valueOf(expected), table.getBoard()[line][column]);
    }
}
