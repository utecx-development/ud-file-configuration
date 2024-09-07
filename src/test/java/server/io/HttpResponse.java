package server.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;

public class HttpResponse {

    private final OutputStream outputStream;
    private final PrintWriter out;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.out = new PrintWriter(outputStream, true);
    }

    public void sendResponse(String statusCode, String contentType, String body) {
        out.println("HTTP/1.1 " + statusCode);
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + body.length());
        out.println();
        out.println(body);
        out.flush();
    }

    public void sendFile(String statusCode, String contentType, File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        out.println("HTTP/1.1 " + statusCode);
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + fileContent.length);
        out.println();
        out.flush(); // Flush headers before sending file content
        outputStream.write(fileContent);
        outputStream.flush();
    }

}
