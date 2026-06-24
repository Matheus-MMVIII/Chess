package com.chess.http.handler;

import com.chess.exception.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FrontendHandler extends BaseHandler {

    public FrontendHandler() {

    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if (!"GET".equalsIgnoreCase(method)) throw new MethodNotAllowedException("");

        Path file = Path.of("frontend/index.html");

        byte[] bytes = Files.readAllBytes(file);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, bytes.length);


        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
