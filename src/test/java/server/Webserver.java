package server;

import server.io.HttpRequest;
import server.io.HttpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Webserver {

    private final int port;
    private final Router router = new Router();

    public Webserver(int port) {
        this.port = port;
    }

    public void addRoute(String path, Consumer<HttpResponse> handler) {
        router.addRoute(path, handler);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server gestartet auf Port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (clientSocket) {
            HttpRequest request = new HttpRequest(clientSocket.getInputStream());
            HttpResponse response = new HttpResponse(clientSocket.getOutputStream());
            router.handleRequest(request, response);
        } catch (IOException e) {
            System.err.println("Fehler bei der Anfrageverarbeitung: " + e.getMessage());
        }
    }


}
