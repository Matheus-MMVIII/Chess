package com.chess.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FrontendHandler extends BaseHandler {

    public FrontendHandler() {

    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {

        Path file = Path.of("frontend/index.html");

        byte[] bytes = Files.readAllBytes(file);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, bytes.length);

        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}
