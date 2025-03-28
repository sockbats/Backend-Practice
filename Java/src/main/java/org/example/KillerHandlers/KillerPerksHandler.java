package org.example.KillerHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;


public class KillerPerksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange request) throws IOException {
        var method = request.getRequestMethod();
        System.out.printf("Handling Survivors %s request\n", method);

        byte[] response;
        OutputStream os = request.getResponseBody();

        switch (method) {
            case "POST":
                // Implement POST
                response = (method + " not yet implemented.").getBytes();
                request.sendResponseHeaders(501, response.length);
                os.write(response);
                os.close();
                break;
            case "GET":
                // Implement GET
                response = (method + " not yet implemented.").getBytes();
                request.sendResponseHeaders(501, response.length);
                os.write(response);
                os.close();
                break;
            case "PUT":
                // Implement PUT
                response = (method + " not yet implemented.").getBytes();
                request.sendResponseHeaders(501, response.length);
                os.write(response);
                os.close();
                break;
            case "DELETE":
                // Implement DELETE
                response = (method + " not yet implemented.").getBytes();
                request.sendResponseHeaders(501, response.length);
                os.write(response);
                os.close();
                break;
            default:
                return;
        }
    }
}
