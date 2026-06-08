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
    }
}