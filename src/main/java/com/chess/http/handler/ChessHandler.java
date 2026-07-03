package com.chess.http.handler;

import com.chess.exception.BadRequestException;
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

        String method = exchange.getRequestMethod();
        String id = extractIdFromPath(exchange, BASE_PATH);
        String routeSuffix = "";
        if (!id.equals("-1")) {
            routeSuffix = extractSuffixFromPath(exchange, BASE_PATH + "/" + id);
        }

        if ("GET".equalsIgnoreCase(method) && !id.equals("-1") && routeSuffix.isEmpty()) {
            String board = JsonUtil.board(chessService.getBoard(id));
            sendJson(exchange, 200, board);
            return;
        }
        if ("GET".equalsIgnoreCase(method) && !id.equals("-1") && routeSuffix.equals("status")) {
            String status = JsonUtil.status(
                    chessService.getGameStatus(id),
                    chessService.getTurn(id),
                    chessService.getWinner(id));
            sendJson(exchange, 200, status);
            return;
        }
        if ("POST".equalsIgnoreCase(method) && id.equals("-1")) {
            String idTable = chessService.createTable();
            String json = JsonUtil.jsonId(idTable);
            sendJson(exchange, 201, json);
            return;
        }
        if ("PUT".equalsIgnoreCase(method) && !id.equals("-1") && routeSuffix.isEmpty()) {
            String json = requireJsonBody(exchange);
            int[] pos = JsonUtil.getPos(json);
            chessService.movePiece(id, pos[0], pos[1], pos[2], pos[3]);
            System.out.printf("chessService.movePiece(idTable, %d, %d, %d, %d);\n", pos[0], pos[1], pos[2], pos[3]);
            sendJson(exchange, 200, JsonUtil.board(chessService.getBoard(id)));
            return;
        }
        if ("PUT".equalsIgnoreCase(method) && !id.equals("-1") && routeSuffix.length() == 1) {
            String json = requireJsonBody(exchange);
            int[] pos = JsonUtil.getPos(json);
            chessService.movePiece(id, pos[0], pos[1], pos[2], pos[3]);
            System.out.printf("chessService.movePiece(idTable, %d, %d, %d, %d);\n", pos[0], pos[1], pos[2], pos[3]);
            chessService.promotePawn(id, pos[2], pos[3], routeSuffix.charAt(0));
            sendJson(exchange, 200, JsonUtil.board(chessService.getBoard(id)));
            return;
        }
        if ("DELETE".equalsIgnoreCase(method) && !id.equals("-1") && routeSuffix.isEmpty()) {
            chessService.deleteTable(id);
            sendNoContent(exchange);
            return;
        }

        sendMethodNotAllowed(exchange, "GET, POST, PUT, DELETE, OPTIONS");
    }

    private String extractSuffixFromPath(HttpExchange exchange, String basePath) {
        String path = exchange.getRequestURI().getPath();
        if (path.equals(basePath) || path.equals(basePath + "/")) {
            return "";
        }

        if (!path.startsWith(basePath + "/")) {
            throw new BadRequestException("Invalid route.");
        }

        return path.substring(basePath.length() + 1);
    }
}
