package com.chess;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import com.chess.http.handler.ChessHandler;

public class App {
    public static void main(String[] args) {
        HttpServer server = new HttpServer(new InetSocketAdress(8080, null));
        server.createContext("/api/chess", new ChessHandler());
    }
}