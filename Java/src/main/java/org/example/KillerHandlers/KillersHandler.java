package org.example.KillerHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.DatabaseConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class KillersHandler implements HttpHandler {
    Connection connection;
    HttpExchange request;
    OutputStream os;


    private void send_http_response(int status_code, String response) throws IOException {
        byte[] response_bytes = response.getBytes();
        request.sendResponseHeaders(status_code, response_bytes.length);
        os.write(response_bytes);
        os.close();
    }

    private String json_message(String message) {
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

    private void post(JSONObject body) throws IOException {
        int killer_id = 0;
        try {
            killer_id = (int) body.get("killer_id");
            String name = ((String) body.get("name")).replace("'", "\\'");
            String title = ((String) body.get("title")).replace("'", "\\'");
            String image = ((String) body.get("image")).replace("'", "\\'");
            String query = String.format("INSERT INTO killers VALUE (%d, '%s', '%s', '%s');",
                    killer_id, name, title, image);

            Statement statement = connection.createStatement();
            int lines_affected = statement.executeUpdate(query);
            statement.close();

            send_http_response(201, json_message(lines_affected + " lines affected."));
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate entry")) { // SQL error: duplicate primary key
                send_http_response(400, json_message("killer_id " + killer_id + " already exists!"));
            } else {
                System.out.println("SQL Error: " + e);
                send_http_response(500, json_message("SQL Error: " + e));
                throw new RuntimeException(e);
            }
        } catch (JSONException e) { // 400 Bad Request: Request body missing element
            var missing_element = e.getMessage().split("\"")[1];
            send_http_response(400, json_message("Request element '" + missing_element + "' not found!"));
        } catch (ClassCastException e) {
            send_http_response(400, json_message("killer_id must be int!"));
        } finally {
            try {
                connection.close();
            } catch (Exception _) {
            }
        }
    }

    private void get_by_id(int killer_id) throws IOException {
        try {
            String query = "SELECT * FROM killers WHERE killer_id = " + killer_id;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                JSONObject get_result = new JSONObject();
                get_result.put("killer_id", resultSet.getInt("killer_id"));
                get_result.put("name", resultSet.getString("name"));
                get_result.put("title", resultSet.getString("title"));
                get_result.put("image", resultSet.getString("image"));
                send_http_response(200, get_result.toString());
            } else {
                send_http_response(404, json_message("killer_id " + killer_id + " not found!"));
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            send_http_response(500, json_message(e.toString()));
        } finally {
            try {
                connection.close();
            } catch (SQLException _) {
            }
        }
    }

    private void get_all() throws IOException {
        try {
            String query = "SELECT * FROM killers";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<JSONObject> killer_list = new ArrayList<>();
            while (resultSet.next()) {
                JSONObject get_result = new JSONObject();
                get_result.put("killer_id", resultSet.getInt("killer_id"));
                get_result.put("name", resultSet.getString("name"));
                get_result.put("title", resultSet.getString("title"));
                get_result.put("image", resultSet.getString("image"));
                killer_list.add(get_result);
            }
            send_http_response(200, killer_list.toString());
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            send_http_response(500, json_message("SQL Exception: " + e));
        } finally {
            try {
                connection.close();
            } catch (SQLException _) {
            }
        }
    }

    private void put(int killer_id, JSONObject body) throws IOException {
        try {
            String name = (String) body.get("name");
            String title = (String) body.get("title");
            String image = (String) body.get("image");
            String query = String.format("UPDATE killers SET name='%s', title='%s', image='%s' where killer_id = %d;",
                    name, title, image, killer_id);
            Statement statement = connection.createStatement();
            int lines_affected = statement.executeUpdate(query);
            statement.close();

            send_http_response(200, json_message(lines_affected + " lines affected."));
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
            send_http_response(500, json_message("SQL Error: " + e));
            throw new RuntimeException(e);
        } catch (JSONException e) { // 400 Bad Request: Request body missing element
            var missing_element = e.getMessage().split("\"")[1];
            send_http_response(400, json_message("Request element '" + missing_element + "' not found!"));
        } finally {
            try {
                connection.close();
            } catch (SQLException _) {
            }
        }
    }

    private void delete_by_id(int killer_id) throws IOException {
        try {
            String query = "DELETE FROM killers WHERE killer_id = " + killer_id;
            Statement statement = connection.createStatement();
            int lines_affected = statement.executeUpdate(query);
            statement.close();

            send_http_response(200, json_message(lines_affected + " line(s) deleted."));
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
            send_http_response(500, json_message("SQL Error: " + e));
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException _) {
            }
        }
    }

    private void delete_all() throws IOException {
        try {
            String query = "TRUNCATE killers";
            Statement statement = connection.createStatement();
            int lines_affected = statement.executeUpdate(query);
            statement.close();

            send_http_response(200, json_message(lines_affected + " line(s) deleted."));
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
            send_http_response(500, json_message("SQL Error: " + e));
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException _) {
            }
        }
    }

}
