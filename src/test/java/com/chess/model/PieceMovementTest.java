package com.chess.model;

import com.chess.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PieceMovementTest {

    @Test
    void whitePawnMovesOneSquareForward() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 6, 4);

        pawn.move(5, 4);

        assertEmpty(table, 6, 4);
        assertSquare(table, 5, 4, 'p');
    }

    @Test
    void whitePawnMovesTwoSquaresOnlyOnFirstMove() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 6, 4);

        pawn.move(4, 4);

        assertEmpty(table, 6, 4);
        assertSquare(table, 4, 4, 'p');
        assertThrows(BadRequestException.class, () -> pawn.move(2, 4));
    }

    @Test
    void blackPawnMovesTowardIncreasingRows() {
        Table table = emptyBoard();
        Pawn pawn = blackPawn(table, 1, 4);

        pawn.move(2, 4);

        assertEmpty(table, 1, 4);
        assertSquare(table, 2, 4, 'P');
    }

    @Test
    void pawnCapturesOneSquareDiagonally() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 4, 4);
        blackPawn(table, 3, 5);

        pawn.move(3, 5);

        assertEmpty(table, 4, 4);
        assertSquare(table, 3, 5, 'p');
    }

    @Test
    void pawnCannotMoveForwardIntoOccupiedSquare() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 6, 4);
        blackPawn(table, 5, 4);

        assertThrows(BadRequestException.class, () -> pawn.move(5, 4));
    }

    @Test
    void pawnCannotCaptureEmptyDiagonalSquare() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 6, 4);

        assertThrows(BadRequestException.class, () -> pawn.move(5, 5));
    }

    @Test
    void pawnCannotMoveBackward() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 4, 4);

        assertThrows(BadRequestException.class, () -> pawn.move(5, 4));
    }

    @Test
    void pawnCannotMoveThreeSquares() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 6, 4);

        assertThrows(BadRequestException.class, () -> pawn.move(3, 4));
    }

    @Test
    void pawnCannotCaptureTwoRowsAwayOnFirstMove() {
        Table table = emptyBoard();
        Pawn pawn = whitePawn(table, 6, 4);
        blackPawn(table, 4, 5);

        assertThrows(BadRequestException.class, () -> pawn.move(4, 5));
    }

    @Test
    void rookMovesHorizontally() {
        Table table = emptyBoard();
        Rook rook = whiteRook(table, 4, 4);

        rook.move(4, 7);

        assertEmpty(table, 4, 4);
        assertSquare(table, 4, 7, 'r');
    }

    @Test
    void rookMovesVertically() {
        Table table = emptyBoard();
        Rook rook = whiteRook(table, 4, 4);

        rook.move(1, 4);

        assertEmpty(table, 4, 4);
        assertSquare(table, 1, 4, 'r');
    }

    @Test
    void rookCapturesEnemyOnSameLine() {
        Table table = emptyBoard();
        Rook rook = whiteRook(table, 4, 4);
        blackPawn(table, 4, 1);

        rook.move(4, 1);

        assertSquare(table, 4, 1, 'r');
    }

    @Test
    void rookCannotMoveDiagonally() {
        Table table = emptyBoard();
        Rook rook = whiteRook(table, 4, 4);

        assertThrows(BadRequestException.class, () -> rook.move(3, 5));
    }

    @Test
    void rookCannotJumpOverPieces() {
        Table table = emptyBoard();
        Rook rook = whiteRook(table, 4, 4);
        blackPawn(table, 4, 6);

        assertThrows(BadRequestException.class, () -> rook.move(4, 7));
    }

    @Test
    void rookCannotCaptureFriend() {
        Table table = emptyBoard();
        Rook rook = whiteRook(table, 4, 4);
        whitePawn(table, 4, 7);

        assertThrows(BadRequestException.class, () -> rook.move(4, 7));
    }

    @Test
    void bishopMovesDiagonally() {
        Table table = emptyBoard();
        Bishop bishop = whiteBishop(table, 4, 4);

        bishop.move(1, 1);

        assertEmpty(table, 4, 4);
        assertSquare(table, 1, 1, 'b');
    }

    @Test
    void bishopCapturesEnemyDiagonally() {
        Table table = emptyBoard();
        Bishop bishop = whiteBishop(table, 4, 4);
        blackPawn(table, 2, 6);

        bishop.move(2, 6);

        assertSquare(table, 2, 6, 'b');
    }

    @Test
    void bishopCannotMoveStraight() {
        Table table = emptyBoard();
        Bishop bishop = whiteBishop(table, 4, 4);

        assertThrows(BadRequestException.class, () -> bishop.move(4, 6));
    }

    @Test
    void bishopCannotJumpOverPieces() {
        Table table = emptyBoard();
        Bishop bishop = whiteBishop(table, 4, 4);
        blackPawn(table, 3, 5);

        assertThrows(BadRequestException.class, () -> bishop.move(2, 6));
    }

    @ParameterizedTest
    @CsvSource({
            "2,3",
            "2,5",
            "3,2",
            "3,6",
            "5,2",
            "5,6",
            "6,3",
            "6,5"
    })
    void horseMovesInEveryLShape(int endLine, int endColumn) {
        Table table = emptyBoard();
        Horse horse = whiteHorse(table, 4, 4);

        horse.move(endLine, endColumn);

        assertEmpty(table, 4, 4);
        assertSquare(table, endLine, endColumn, 'h');
    }

    @ParameterizedTest
    @CsvSource({
            "4,5",
            "5,5",
            "4,7",
            "1,1"
    })
    void horseRejectsNonLShapeMoves(int endLine, int endColumn) {
        Table table = emptyBoard();
        Horse horse = whiteHorse(table, 4, 4);

        assertThrows(BadRequestException.class, () -> horse.move(endLine, endColumn));
    }

    @Test
    void horseCannotCaptureFriend() {
        Table table = emptyBoard();
        Horse horse = whiteHorse(table, 4, 4);
        whitePawn(table, 2, 5);

        assertThrows(BadRequestException.class, () -> horse.move(2, 5));
    }

    @Test
    void queenMovesHorizontally() {
        Table table = emptyBoard();
        Queen queen = whiteQueen(table, 4, 4);

        queen.move(4, 0);

        assertEmpty(table, 4, 4);
        assertSquare(table, 4, 0, 'q');
    }

    @Test
    void queenMovesVertically() {
        Table table = emptyBoard();
        Queen queen = whiteQueen(table, 4, 4);

        queen.move(0, 4);

        assertEmpty(table, 4, 4);
        assertSquare(table, 0, 4, 'q');
    }

    @Test
    void queenMovesDiagonally() {
        Table table = emptyBoard();
        Queen queen = whiteQueen(table, 4, 4);

        queen.move(7, 7);

        assertEmpty(table, 4, 4);
        assertSquare(table, 7, 7, 'q');
    }

    @Test
    void queenCapturesEnemy() {
        Table table = emptyBoard();
        Queen queen = whiteQueen(table, 4, 4);
        blackPawn(table, 4, 7);

        queen.move(4, 7);

        assertSquare(table, 4, 7, 'q');
    }

    @Test
    void queenCannotMoveLikeHorse() {
        Table table = emptyBoard();
        Queen queen = whiteQueen(table, 4, 4);

        assertThrows(BadRequestException.class, () -> queen.move(2, 5));
    }

    @Test
    void queenCannotJumpOverPieces() {
        Table table = emptyBoard();
        Queen queen = whiteQueen(table, 4, 4);
        blackPawn(table, 4, 5);

        assertThrows(BadRequestException.class, () -> queen.move(4, 7));
    }

    @ParameterizedTest
    @CsvSource({
            "3,3",
            "3,4",
            "3,5",
            "4,3",
            "4,5",
            "5,3",
            "5,4",
            "5,5"
    })
    void kingMovesOneSquareInAnyDirection(int endLine, int endColumn) {
        Table table = emptyBoard();
        King king = whiteKing(table, 4, 4);

        king.move(endLine, endColumn);

        assertEmpty(table, 4, 4);
        assertSquare(table, endLine, endColumn, 'k');
    }

    @Test
    void kingCannotMoveTwoSquaresWithoutCastling() {
        Table table = emptyBoard();
        King king = whiteKing(table, 4, 4);

        assertThrows(BadRequestException.class, () -> king.move(4, 6));
    }

    @Test
    void kingCannotCaptureFriend() {
        Table table = emptyBoard();
        King king = whiteKing(table, 4, 4);
        whitePawn(table, 5, 5);

        assertThrows(BadRequestException.class, () -> king.move(5, 5));
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

    private static Bishop whiteBishop(Table table, int line, int column) {
        Bishop bishop = new Bishop('b', line, column, true, table);
        table.registerPos(line, column, bishop);
        return bishop;
    }

    private static Horse whiteHorse(Table table, int line, int column) {
        Horse horse = new Horse('h', line, column, true, table);
        table.registerPos(line, column, horse);
        return horse;
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

    private static void assertSquare(Table table, int line, int column, char expected) {
        assertEquals(String.valueOf(expected), table.getBoard()[line][column]);
    }

    private static void assertEmpty(Table table, int line, int column) {
        assertSquare(table, line, column, '.');
    }
}
