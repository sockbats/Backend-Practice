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
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class KillersHandler implements HttpHandler {
    Connection connection;

    @Override
    public void handle(HttpExchange request) throws IOException {
        var method = request.getRequestMethod();
        System.out.printf("Handling Killers %s request\n", method);

        byte[] response;
        OutputStream os = request.getResponseBody();

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
            response = ("Error parsing request body.").getBytes();
            request.sendResponseHeaders(400, response.length);
            os.write(response);
            os.close();
            return;
        }

        // Connect to DB
        try {
            connection = DatabaseConnection.get_connection();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e);
            response = (e.toString()).getBytes();
            request.sendResponseHeaders(500, response.length);
            os.write(response);
            os.close();
            throw new RuntimeException(e);
        }


        int killer_id;
        String request_path;
        int segments;
        switch (method) {
            case "POST":
                killer_id = 0;
                try {
                    killer_id = (int) body.get("killer_id");
                    String name = (String) body.get("name");
                    String title = (String) body.get("title");
                    String image = (String) body.get("image");
                    String query = String.format("INSERT INTO killers VALUE (%d, '%s', '%s', '%s');",
                            killer_id, name, title, image);

                    Statement statement = connection.createStatement();
                    int lines_affected = statement.executeUpdate(query);
                    statement.close();

                    response = (lines_affected + " lines affected.").getBytes();
                    request.sendResponseHeaders(200, response.length);
                    os.write(response);
                    os.close();
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23000")) { // SQL error: duplicate primary key
                        response = ("killer_id " + killer_id + " already exists!").getBytes();
                        request.sendResponseHeaders(400, response.length);
                        os.write(response);
                        os.close();
                    } else {
                        System.out.println("Error executing SQL query: " + e);
                        response = (e.toString()).getBytes();
                        request.sendResponseHeaders(500, response.length);
                        os.write(response);
                        os.close();
                        throw new RuntimeException(e);
                    }
                    return;
                } catch (JSONException e) { // 400 Bad Request: Request body missing element
                    response = (e.getMessage()).getBytes();
                    request.sendResponseHeaders(400, response.length);
                    os.write(response);
                    os.close();
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException _) {
                    }
                }
                break;
            case "GET":
                // Check if ID is provided, else get all
                request_path = request.getRequestURI().getPath();
                segments = request_path.split("/").length;
                if (segments > 3) {
                    try {
                        killer_id = Integer.parseInt(request_path.substring(request_path.lastIndexOf("/") + 1));
                        String query = "SELECT * FROM killers WHERE killer_id = " + killer_id;
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(query);
                        if (resultSet.next()) {
                            JSONObject get_result = new JSONObject();
                            get_result.put("killer_id", resultSet.getInt("killer_id"));
                            get_result.put("name", resultSet.getString("name"));
                            get_result.put("title", resultSet.getString("title"));
                            get_result.put("image", resultSet.getString("image"));
                            response = (get_result.toString()).getBytes();
                        } else {
                            response = ("killer_id " + killer_id + " does not exist!").getBytes();
                            request.sendResponseHeaders(400, response.length);
                            os.write(response);
                            os.close();
                        }
                        statement.close();
                        resultSet.close();
                    } catch (SQLException e) {
                        System.out.println("SQL Exception: " + e);
                        response = (e.toString()).getBytes();
                        request.sendResponseHeaders(500, response.length);
                        os.write(response);
                        os.close();
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException _) {
                        }
                    }
                } else {
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
                        response = (killer_list.toString()).getBytes();
                        statement.close();
                        resultSet.close();
                    } catch (SQLException e) {
                        System.out.println("SQL Exception: " + e);
                        response = (e.toString()).getBytes();
                        request.sendResponseHeaders(500, response.length);
                        os.write(response);
                        os.close();
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException _) {
                        }
                    }
                }
                request.sendResponseHeaders(200, response.length);
                os.write(response);
                os.close();
                break;
            case "PUT":
                try {
                    request_path = request.getRequestURI().getPath();
                    segments = request_path.split("/").length;
                    if (segments <= 3) { // 400 Bad Request: No killer_id provided in url
                        response = ("killer_id not provided!").getBytes();
                        request.sendResponseHeaders(400, response.length);
                        os.write(response);
                        os.close();
                        break;
                    }
                    killer_id = Integer.parseInt(request_path.substring(request_path.lastIndexOf("/") + 1));
                    String name = (String) body.get("name");
                    String title = (String) body.get("title");
                    String image = (String) body.get("image");
                    String query = String.format("UPDATE killers SET name='%s', title='%s', image='%s' where killer_id = %d;",
                            name, title, image, killer_id);
                    Statement statement = connection.createStatement();
                    int lines_affected = statement.executeUpdate(query);
                    statement.close();

                    response = (lines_affected + " line(s) updated.").getBytes();
                    request.sendResponseHeaders(200, response.length);
                    os.write(response);
                    os.close();
                } catch (SQLException e) {
                    System.out.println("Error executing SQL query: " + e);
                    response = (e.toString()).getBytes();
                    request.sendResponseHeaders(500, response.length);
                    os.write(response);
                    os.close();
                    throw new RuntimeException(e);
                } catch (JSONException e) { // 400 Bad Request: Request body missing element
                    response = (e.getMessage()).getBytes();
                    request.sendResponseHeaders(400, response.length);
                    os.write(response);
                    os.close();
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException _) {
                    }
                }
                break;
            case "DELETE":
                try {
                    request_path = request.getRequestURI().getPath();
                    segments = request_path.split("/").length;
                    if (segments <= 3) { // 400 Bad Request: No killer_id provided in url
                        response = ("killer_id not provided!").getBytes();
                        request.sendResponseHeaders(400, response.length);
                        os.write(response);
                        os.close();
                        break;
                    }
                    killer_id = Integer.parseInt(request_path.substring(request_path.lastIndexOf("/") + 1));

                    String query = "DELETE FROM killers WHERE killer_id = " + killer_id;
                    Statement statement = connection.createStatement();
                    int lines_affected = statement.executeUpdate(query);
                    statement.close();

                    response = (lines_affected + " line(s) deleted.").getBytes();
                    request.sendResponseHeaders(200, response.length);
                    os.write(response);
                    os.close();
                } catch (SQLException e) {
                    System.out.println("Error executing SQL query: " + e);
                    response = (e.toString()).getBytes();
                    request.sendResponseHeaders(500, response.length);
                    os.write(response);
                    os.close();
                    throw new RuntimeException(e);
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException _) {
                    }
                }
                break;
            default:

        }
    }
}
