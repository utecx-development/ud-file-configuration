package server.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String method;
    private String path;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        if (line != null && !line.isEmpty()) {
            String[] parts = line.split(" ");
            this.method = parts[0];
            this.path = parts[1];
        }

        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        System.out.println(path);
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

}
