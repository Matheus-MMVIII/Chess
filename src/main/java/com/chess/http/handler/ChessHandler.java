package com.chess.http.handler;

import com.chess.http.util.JsonUtil;
import com.chess.service.ChessService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ChessHandler extends BaseHandler {
    private final static String BASE_PATH = "/api/chess";

    private final ChessService chessService;

    public ChessHandler(ChessService chessService) {
        this.chessService = chessService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Access-Control-Allow-Origin", "*");//"http://localhost:5173");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");

        String method = exchange.getRequestMethod();
        int id = extractIdFromPath(exchange, BASE_PATH);

        if ("GET".equalsIgnoreCase(method) && id == -1) {
            String board = JsonUtil.board(chessService.getBoard());
            System.out.println(board);
            sendJson(exchange, 200, board);
        }
    }
}