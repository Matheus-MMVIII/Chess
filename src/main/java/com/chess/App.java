package com.chess;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.chess.model.Table;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import com.chess.http.handler.ChessHandler;

public class App {
    public static void main(String[] args) throws IOException {/*        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 50);
        server.createContext("/api/chess", new ChessHandler());
        server.start();
        System.out.println("Server started on port 8080");*/
        Table table = new Table();
        table.move(1, 1, 3, 1);
        table.move(0, 0, 0, 1);
        table.move(0, 1, 2, 1);
        table.move(2, 1, 2, 0);
        table.move(0, 2, 1, 1);
        table.move(1, 1, 3, 3);
    }
}