package com.chess.http.util;

public final class JsonUtil {

    private JsonUtil() {

    }

    public static String board(String[] board) {
        return "{"
                + "\"line1\":\"" + board[0] + "\","
                + "\"line2\":\"" + board[1] + "\","
                + "\"line3\":\"" + board[2] + "\","
                + "\"line4\":\"" + board[3] + "\","
                + "\"line5\":\"" + board[4] + "\","
                + "\"line6\":\"" + board[5] + "\","
                + "\"line7\":\"" + board[6] + "\","
                + "\"line8\":\"" + board[7] + "\""
                + "}";
    }
}
