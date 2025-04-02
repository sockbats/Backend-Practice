package org.example.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.DatabaseConnection;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

public abstract class BaseHandler implements HttpHandler {
    Connection connection;
    HttpExchange request;
    OutputStream os;


    void send_http_response(int status_code, String response) throws IOException {
        byte[] response_bytes = response.getBytes();
        request.sendResponseHeaders(status_code, response_bytes.length);
        os.write(response_bytes);
        os.close();
    }

    String json_message(String message) {
        return "{\"message\": \"" + message + "\"}";
    }

    private JSONObject get_request_parameters(URI uri) {
        JSONObject parameters = new JSONObject();
        if (uri.getQuery() == null) {
            return parameters;
        }
        var parameter_list = uri.getQuery().split("&");
        for (var parameter : parameter_list) {
            var key_value_pair = parameter.split("=");
            parameters.put(key_value_pair[0], key_value_pair[1]);
        }
        return parameters;
    }

    @Override
    public void handle(HttpExchange http_request) throws IOException {
        request = http_request;
        var method = request.getRequestMethod();
        System.out.printf("Handling Killers %s request\n", method);

        os = request.getResponseBody();

        // Get request body if exists
        JSONObject body;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8))) {
            var request_body = reader.lines().collect(Collectors.joining("\n"));
            if (!request_body.isEmpty()) {
                body = new JSONObject(request_body);
            } else {
                body = new JSONObject();
            }
        } catch (Exception e) {
            System.out.println("Error parsing request body.");
            send_http_response(400, json_message("Error parsing request body"));
            return;
        }

        // Connect to DB
        try {
            connection = DatabaseConnection.get_connection();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e);
            send_http_response(500, json_message("Error connecting to database: " + e));
            throw new RuntimeException(e);
        }

        var parameters = get_request_parameters(request.getRequestURI());
        switch (method) {
            case "POST":
                post(body);
                break;
            case "GET":
                // Check if ID is provided, else get all
                if (parameters.has("id")) {
                    get_by_id(Integer.parseInt((String) parameters.get("id")));
                } else {
                    get_all();
                }
                break;
            case "PUT":
                if (parameters.has("id")) {
                    put(Integer.parseInt((String) parameters.get("id")), body);
                } else {
                    send_http_response(400, json_message("killer_id not provided!"));
                }
                break;
            case "DELETE":
                if (parameters.has("id")) {
                    delete_by_id(Integer.parseInt((String) parameters.get("id")));
                } else if (parameters.has("deleteAll") && parameters.get("deleteAll").equals("true")) {
                    delete_all();
                } else {
                    send_http_response(400, json_message("Delete all request sent without 'deleteAll=true' parameter!"));
                }
                break;
            default:
                send_http_response(418, json_message("I don't know what you are trying to do, and I cannot handle it"));
        }
    }

    abstract void post(JSONObject body) throws IOException;
    abstract void get_by_id(int killer_id) throws IOException;
    abstract void get_all() throws IOException;
    abstract void put(int killer_id, JSONObject body) throws IOException;
    abstract void delete_by_id(int killer_id) throws IOException;
    abstract void delete_all() throws IOException;
}
