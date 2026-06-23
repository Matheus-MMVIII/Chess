package com.chess.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CssHandler extends BaseHandler {

    public CssHandler() {

    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {

        Path file = Path.of("frontend/style.css");

        byte[] bytes = Files.readAllBytes(file);

        exchange.getResponseHeaders().set("Content-Type", "text/css");
        exchange.sendResponseHeaders(200, bytes.length);

        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
