package com.chess.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageHandler extends BaseHandler {

    public ImageHandler() {

    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();

        String imageName = uri.replace("/images/", "");

        Path imagesDir = Path.of("images").toAbsolutePath().normalize();

        Path imagePath = imagesDir
                .resolve(imageName)
                .normalize();

        if (!imagePath.startsWith(imagesDir)) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        if (!imageName.endsWith(".png")) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        if (!Files.exists(imagePath) || Files.isDirectory(imagePath)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        exchange.getResponseHeaders().set("Content-Type", "image/png");
        exchange.sendResponseHeaders(200, imageBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(imageBytes);
        }
    }
}
