package server;

import server.io.HttpRequest;

import server.io.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Router {

    private final Map<String, Consumer<HttpResponse>> routes = new HashMap<>();

    public void addRoute(String path, Consumer<HttpResponse> handler) {
        routes.put(path, handler);
    }

    public void handleRequest(HttpRequest request, HttpResponse response) {
        Consumer<HttpResponse> handler = routes.get(request.getPath());
        if (handler != null) {
            handler.accept(response);
        } else {
            try {
                response.sendFile("404", "txt/html", new File(""));
            } catch (IOException e) {
                response.sendResponse("404 Not Found", "text/html", "<h1>404 Not Found</h1>");
            }

        }
    }

}
