package com.chess.http.handler;

import com.chess.service.ChessService;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessHttpTest {
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\":\"([^\"]+)\"");

    private HttpServer server;
    private URI baseUri;
    private HttpClient client;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", new FrontendHandler());
        server.createContext("/api/chess", new ChessHandler(new ChessService()));
        server.createContext("/images", new ImageHandler());
        server.createContext("/style.css", new CssHandler());
        server.createContext("/script.js", new JsHandler());
        server.start();

        baseUri = URI.create("http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort());
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void postCreatesGameAndGetReturnsInitialBoard() throws Exception {
        HttpResponse<String> post = send(HttpRequest.newBuilder(uri("/api/chess")).POST(HttpRequest.BodyPublishers.noBody()));

        assertEquals(201, post.statusCode());
        assertContentType(post, "application/json");

        String id = extractId(post.body());
        HttpResponse<String> get = send(HttpRequest.newBuilder(uri("/api/chess/" + id)).GET());

        assertEquals(200, get.statusCode());
        assertTrue(get.body().contains("\"0\":[\"R\",\"H\",\"B\",\"Q\",\"K\",\"B\",\"H\",\"R\"]"));
        assertTrue(get.body().contains("\"6\":[\"p\",\"p\",\"p\",\"p\",\"p\",\"p\",\"p\",\"p\"]"));
    }

    @Test
    void putMovesPieceAndReturnsUpdatedBoard() throws Exception {
        String id = createGame();
        String body = """
                {
                  "InitialLine": 6,
                  "InitialColumn": 4,
                  "EndLine": 4,
                  "EndColumn": 4
                }
                """;

        HttpResponse<String> put = send(jsonPut("/api/chess/" + id, body));

        assertEquals(200, put.statusCode());
        assertTrue(put.body().contains("\"4\":[\".\",\".\",\".\",\".\",\"p\",\".\",\".\",\".\"]"));
        assertTrue(put.body().contains("\"6\":[\"p\",\"p\",\"p\",\"p\",\".\",\"p\",\"p\",\"p\"]"));
    }

    @Test
    void promotionEndpointMovesAndPromotesPawn() throws Exception {
        String id = createGame();
        playWhitePromotionPathOverHttp(id);
        String promotionMove = """
                {
                  "InitialLine": 1,
                  "InitialColumn": 1,
                  "EndLine": 0,
                  "EndColumn": 1
                }
                """;

        HttpResponse<String> put = send(jsonPut("/api/chess/" + id + "/q", promotionMove));

        assertEquals(200, put.statusCode());
        assertTrue(put.body().contains("\"0\":[\"R\",\"q\",\".\",\"Q\",\"K\",\"B\",\"H\",\"R\"]"));
    }

    @Test
    void putWithoutJsonContentTypeReturnsBadRequestJson() throws Exception {
        String id = createGame();
        HttpRequest request = HttpRequest.newBuilder(uri("/api/chess/" + id))
                .PUT(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = send(request);

        assertEquals(400, response.statusCode());
        assertContentType(response, "application/json");
        assertTrue(response.body().contains("Content-Type"));
    }

    @Test
    void unknownGameReturnsNotFoundJson() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/chess/missing-game")).GET());

        assertEquals(404, response.statusCode());
        assertContentType(response, "application/json");
        assertTrue(response.body().contains("error"));
    }

    @Test
    void unsupportedApiMethodReturnsMethodNotAllowed() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/chess")).GET());

        assertEquals(405, response.statusCode());
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", response.headers().firstValue("Allow").orElse(""));
    }

    @Test
    void optionsPreflightReturnsCorsHeaders() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri("/api/chess"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = send(request);

        assertEquals(204, response.statusCode());
        assertEquals("*", response.headers().firstValue("Access-Control-Allow-Origin").orElse(""));
        assertTrue(response.headers().firstValue("Access-Control-Allow-Methods").orElse("").contains("PUT"));
        assertTrue(response.headers().firstValue("Access-Control-Allow-Headers").orElse("").contains("Content-Type"));
    }

    @Test
    void apiSuccessResponsesIncludeCorsHeader() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/chess")).POST(HttpRequest.BodyPublishers.noBody()));

        assertEquals(201, response.statusCode());
        assertEquals("*", response.headers().firstValue("Access-Control-Allow-Origin").orElse(""));
    }

    @Test
    void frontendAndStaticAssetsAreServed() throws Exception {
        HttpResponse<String> index = send(HttpRequest.newBuilder(uri("/")).GET());
        HttpResponse<String> css = send(HttpRequest.newBuilder(uri("/style.css")).GET());
        HttpResponse<String> js = send(HttpRequest.newBuilder(uri("/script.js")).GET());
        HttpResponse<String> image = send(HttpRequest.newBuilder(uri("/images/white-king.png")).GET());

        assertEquals(200, index.statusCode());
        assertContentType(index, "text/html");
        assertTrue(index.body().contains("<title>Chess</title>"));

        assertEquals(200, css.statusCode());
        assertContentType(css, "text/css");
        assertTrue(css.body().contains(".pieces"));

        assertEquals(200, js.statusCode());
        assertContentType(js, "application/javascript");
        assertTrue(js.body().contains("createBoard"));

        assertEquals(200, image.statusCode());
        assertContentType(image, "image/png");
    }

    @Test
    void missingImageReturnsNotFound() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/images/missing.png")).GET());

        assertEquals(404, response.statusCode());
    }

    @Test
    void staticFilesRejectUnsupportedMethods() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/style.css")).POST(HttpRequest.BodyPublishers.noBody()));

        assertEquals(405, response.statusCode());
    }

    @Test
    void imageEndpointRejectsPathTraversal() throws Exception {
        String rawResponse = rawGet("/images/../pom.xml");

        assertFalse(rawResponse.startsWith("HTTP/1.1 200"));
        assertFalse(rawResponse.contains("<project"));
    }

    private String createGame() throws Exception {
        HttpResponse<String> post = send(HttpRequest.newBuilder(uri("/api/chess")).POST(HttpRequest.BodyPublishers.noBody()));
        assertEquals(201, post.statusCode());
        return extractId(post.body());
    }

    private void playWhitePromotionPathOverHttp(String id) throws Exception {
        putMove(id, 6, 4, 4, 4);
        putMove(id, 1, 3, 3, 3);
        putMove(id, 4, 4, 3, 3);
        putMove(id, 1, 2, 2, 2);
        putMove(id, 3, 3, 2, 2);
        putMove(id, 0, 2, 1, 3);
        putMove(id, 2, 2, 1, 1);
        putMove(id, 0, 1, 2, 2);
    }

    private void putMove(String id, int initialLine, int initialColumn, int endLine, int endColumn) throws Exception {
        String body = """
                {
                  "InitialLine": %d,
                  "InitialColumn": %d,
                  "EndLine": %d,
                  "EndColumn": %d
                }
                """.formatted(initialLine, initialColumn, endLine, endColumn);

        HttpResponse<String> response = send(jsonPut("/api/chess/" + id, body));
        assertEquals(200, response.statusCode(), response.body());
    }

    private HttpRequest jsonPut(String path, String body) {
        return HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpResponse<String> send(HttpRequest.Builder builder) throws IOException, InterruptedException {
        return send(builder.build());
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return baseUri.resolve(path);
    }

    private String rawGet(String path) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), server.getAddress().getPort()), 2_000);
            socket.setSoTimeout(2_000);

            String request = "GET " + path + " HTTP/1.1\r\n"
                    + "Host: localhost\r\n"
                    + "Connection: close\r\n"
                    + "\r\n";

            socket.getOutputStream().write(request.getBytes(StandardCharsets.US_ASCII));
            return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String extractId(String body) {
        Matcher matcher = ID_PATTERN.matcher(body);
        assertTrue(matcher.find(), "Response body should contain a game id: " + body);
        return matcher.group(1);
    }

    private static void assertContentType(HttpResponse<String> response, String expectedContentType) {
        String actual = response.headers().firstValue("Content-Type").orElse("");
        assertTrue(actual.contains(expectedContentType), "Expected Content-Type containing " + expectedContentType + " but was " + actual);
    }
}
