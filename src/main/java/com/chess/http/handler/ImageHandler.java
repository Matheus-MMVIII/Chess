package com.chess.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageHandler extends BaseHandler {
    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        System.out.println(uri);

        String imageName = uri.replace("/images/", "");

        Path imagePath = Path.of("images", imageName);
        System.out.println(imagePath);

        if (!Files.exists(imagePath) || Files.isDirectory(imagePath)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        exchange.getResponseHeaders().set("Content-Type", "image/png");
        exchange.sendResponseHeaders(200, imageBytes.length);

        exchange.getResponseBody().write(imageBytes);
        exchange.getResponseBody().close();
    }
}
