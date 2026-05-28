package com.chess.http.handler;

public abstract BaseHandler implements HttpHandler {

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            handleRequest(exchange);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            exchange.close();
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws Exception;

    protected String requireJsonBody(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).contains("application/json")) {
            throw new BadRequestException("Content-Type must be application/json.");
        }
        return HttpExchangeHelper.readRequestBody(exchange);
    }

    protected int extractIdFromPath(HttpExchange exchange, String basePath) {
        String path = exchange.getRequestURI().getPath();
        if (path.equals(basePath) || path.equals(basePath + "/")) {
            return -1;
        }

        if (!path.startsWith(basePath + "/")) {
            throw new BadRequestException("Invalid route.");
        }

        int idSegment = path.substring(basePath.length() + 1);
        if (idSegment.contains("/")) {
            throw new BadRequestException("Invalid route.");
        }

        return Integer.valueOf(idSegment);
    }

    public static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(responseBytes);
        }
    }

    public static void sendNoContent(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
    }

    public static void sendMethodNotAllowed(HttpExchange exchange, String allowedMethods) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowedMethods);
        sendJson(exchange, 405, JsonUtil.error("This method is not permitted."));
    }
}