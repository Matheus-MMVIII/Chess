package com.chess.http.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilTest {

    @Test
    void boardSerializesAllRowsAndSquares() {
        String[][] board = new String[8][8];
        for (int line = 0; line < 8; line++) {
            for (int column = 0; column < 8; column++) {
                board[line][column] = ".";
            }
        }
        board[0][4] = "K";
        board[7][4] = "k";

        String json = JsonUtil.board(board);

        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("\"0\":[\".\",\".\",\".\",\".\",\"K\""));
        assertTrue(json.contains("\"7\":[\".\",\".\",\".\",\".\",\"k\""));
    }

    @Test
    void jsonIdSerializesIdField() {
        String json = JsonUtil.jsonId("game-123");

        assertTrue(json.contains("\"id\":\"game-123\""));
    }

    @Test
    void getPosReadsMoveCoordinates() {
        String json = """
                {
                  "InitialLine": 6,
                  "InitialColumn": 4,
                  "EndLine": 4,
                  "EndColumn": 4
                }
                """;

        assertArrayEquals(new int[]{6, 4, 4, 4}, JsonUtil.getPos(json));
    }

    @Test
    void getPosRejectsMissingCoordinates() {
        String json = """
                {
                  "InitialLine": 6,
                  "InitialColumn": 4
                }
                """;

        assertThrows(RuntimeException.class, () -> JsonUtil.getPos(json));
    }
}
