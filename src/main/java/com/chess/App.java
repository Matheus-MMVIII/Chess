package com.chess;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chess.http.handler.*;
import com.chess.model.Table;
import com.chess.service.ChessService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

public class App {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 50);
        server.createContext("/", new FrontendHandler());
        server.createContext("/api/chess", new ChessHandler(new ChessService()));
        server.createContext("/images", new ImageHandler());
        server.createContext("/style.css", new CssHandler());
        server.createContext("/script.js", new JsHandler());
        server.start();
        System.out.println("Server started on port 8080");
        /*
        Table table = new Table();
        table.move(1, 0, 3, 0);
        table.move(0, 0, 2, 0);
        table.move(2, 0, 2, 4);
        table.move(1, 1, 2, 1);
        table.move(0, 2, 2, 0);
        table.move(0, 1, 2, 2);
        table.move(2, 2, 3, 4);
        table.move(0, 3, 0, 0);
        table.move(0, 0, 4, 4);
        table.move(0, 4, 0, 3);
        table.move(0, 3, 0, 2);
        table.move(0, 2, 1, 1);
        table.move(1, 1, 2, 2);
        */
    }
}