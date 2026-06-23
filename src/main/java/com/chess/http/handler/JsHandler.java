package com.chess.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsHandler extends BaseHandler {

    public JsHandler() {

    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {

        Path file = Path.of("frontend/script.js");

        byte[] bytes = Files.readAllBytes(file);

        exchange.getResponseHeaders().set("Content-Type", "application/javascript");
        exchange.sendResponseHeaders(200, bytes.length);


        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
