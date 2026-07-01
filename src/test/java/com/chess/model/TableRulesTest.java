package com.chess.model;

import com.chess.exception.BadRequestException;
import com.chess.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableRulesTest {

    @Test
    void initialBoardHasEveryPieceInTheExpectedSquare() {
        Table table = new Table();
        String[][] board = table.getBoard();

        assertArrayEquals(new String[]{"R", "H", "B", "Q", "K", "B", "H", "R"}, board[0]);
        assertArrayEquals(new String[]{"P", "P", "P", "P", "P", "P", "P", "P"}, board[1]);
        assertArrayEquals(new String[]{".", ".", ".", ".", ".", ".", ".", "."}, board[2]);
        assertArrayEquals(new String[]{".", ".", ".", ".", ".", ".", ".", "."}, board[3]);
        assertArrayEquals(new String[]{".", ".", ".", ".", ".", ".", ".", "."}, board[4]);
        assertArrayEquals(new String[]{".", ".", ".", ".", ".", ".", ".", "."}, board[5]);
        assertArrayEquals(new String[]{"p", "p", "p", "p", "p", "p", "p", "p"}, board[6]);
        assertArrayEquals(new String[]{"r", "h", "b", "q", "k", "b", "h", "r"}, board[7]);
    }

    @Test
    void movingFromEmptySquareThrowsNotFound() {
        Table table = new Table();

        assertThrows(NotFoundException.class, () -> table.move(4, 4, 3, 4));
    }

    @ParameterizedTest
    @CsvSource({
            "-1,0,0,0",
            "8,0,0,0",
            "6,-1,5,0",
            "6,8,5,0",
            "6,0,-1,0",
            "6,0,8,0",
            "6,0,5,-1",
            "6,0,5,8"
    })
    void movingOutsideBoardThrowsBadRequest(int startLine, int startColumn, int endLine, int endColumn) {
        Table table = new Table();

        assertThrows(BadRequestException.class, () -> table.move(startLine, startColumn, endLine, endColumn));
    }

    @Test
    void blackCannotMoveBeforeWhite() {
        Table table = new Table();

        assertThrows(BadRequestException.class, () -> table.move(1, 4, 2, 4));
    }

    @Test
    void sameSideCannotMoveTwiceInARow() {
        Table table = new Table();

        table.move(7, 1, 5, 2);

        assertThrows(BadRequestException.class, () -> table.move(7, 6, 5, 5));
    }

    @Test
    void captureChangesTheTurnToTheCapturedPiecesOpponent() {
        Table table = emptyBoard();
        whitePawn(table, 4, 4);
        blackPawn(table, 3, 5);
        blackRook(table, 0, 0);

        table.move(4, 4, 3, 5);
        table.move(0, 0, 0, 1);

        assertSquare(table, 3, 5, 'p');
        assertSquare(table, 0, 1, 'R');
    }

    @Test
    void movingOntoFriendPieceThrowsBadRequest() {
        Table table = new Table();

        assertThrows(BadRequestException.class, () -> table.move(7, 1, 6, 3));
    }

    @Test
    void kingSideCastlingMovesKingAndRook() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 7, 7);

        table.move(7, 4, 7, 6);

        assertSquare(table, 7, 6, 'k');
        assertSquare(table, 7, 5, 'r');
        assertEmpty(table, 7, 4);
        assertEmpty(table, 7, 7);
    }

    @Test
    void queenSideCastlingMovesKingAndRook() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 7, 0);

        table.move(7, 4, 7, 2);

        assertSquare(table, 7, 2, 'k');
        assertSquare(table, 7, 3, 'r');
        assertEmpty(table, 7, 4);
        assertEmpty(table, 7, 0);
    }

    @Test
    void castlingFailsWhenPathIsBlocked() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 7, 7);
        whiteBishop(table, 7, 5);

        assertThrows(BadRequestException.class, () -> table.move(7, 4, 7, 6));
    }

    @Test
    void castlingFailsAfterRookHasMoved() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        Rook rook = whiteRook(table, 7, 7);
        rook.firstMove();

        assertThrows(BadRequestException.class, () -> table.move(7, 4, 7, 6));
    }

    @Test
    void castlingFailsWhenKingIsInCheck() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 7, 7);
        blackRook(table, 0, 4);

        assertThrows(BadRequestException.class, () -> table.move(7, 4, 7, 6));
        assertSquare(table, 7, 4, 'k');
        assertSquare(table, 7, 7, 'r');
    }

    @Test
    void castlingFailsWhenKingPassesThroughAttackedSquare() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 7, 7);
        blackRook(table, 0, 5);

        assertThrows(BadRequestException.class, () -> table.move(7, 4, 7, 6));
        assertSquare(table, 7, 4, 'k');
        assertSquare(table, 7, 7, 'r');
    }

    @Test
    void castlingFailsWhenDestinationIsAttacked() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 7, 7);
        blackRook(table, 0, 6);

        assertThrows(BadRequestException.class, () -> table.move(7, 4, 7, 6));
        assertSquare(table, 7, 4, 'k');
        assertSquare(table, 7, 7, 'r');
    }

    @Test
    void kingCannotMoveIntoCheck() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        blackRook(table, 0, 5);

        assertThrows(BadRequestException.class, () -> table.move(7, 4, 7, 5));
    }

    @Test
    void moveCannotLeaveOwnKingInCheck() {
        Table table = emptyBoard();
        whiteKing(table, 7, 4);
        whiteRook(table, 6, 4);
        blackRook(table, 0, 4);

        assertThrows(BadRequestException.class, () -> table.move(6, 4, 6, 5));
    }

    @Test
    void kingsAreNotCapturedAsNormalPieces() {
        Table table = emptyBoard();
        whiteQueen(table, 3, 4);
        blackKing(table, 0, 4);

        assertThrows(BadRequestException.class, () -> table.move(3, 4, 0, 4));
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

    private static Rook blackRook(Table table, int line, int column) {
        Rook rook = new Rook('R', line, column, false, table);
        table.registerPos(line, column, rook);
        return rook;
    }

    private static Bishop whiteBishop(Table table, int line, int column) {
        Bishop bishop = new Bishop('b', line, column, true, table);
        table.registerPos(line, column, bishop);
        return bishop;
    }

    private static Queen whiteQueen(Table table, int line, int column) {
        Queen queen = new Queen('q', line, column, true, table);
        table.registerPos(line, column, queen);
        return queen;
    }

    private static King whiteKing(Table table, int line, int column) {
        King king = new King('k', line, column, true, table);
        table.registerPos(line, column, king);
        return king;
    }

    private static King blackKing(Table table, int line, int column) {
        King king = new King('K', line, column, false, table);
        table.registerPos(line, column, king);
        return king;
    }

    private static void assertSquare(Table table, int line, int column, char expected) {
        assertEquals(String.valueOf(expected), table.getBoard()[line][column]);
    }

    private static void assertEmpty(Table table, int line, int column) {
        assertSquare(table, line, column, '.');
    }
}
