package org.example.Handlers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class KillerPerksHandler extends BaseHandler {
    void post(JSONObject body) throws IOException {
        int perk_id = 0;
        try {
            perk_id = (int) body.get("perk_id");
            String name = (String) body.get("name");
            String description = (String) body.get("description");
            String icon = (String) body.get("icon");
            int killer_id = (int) body.get("killer_id");

            PreparedStatement statement = connection.prepareStatement("INSERT INTO killer_perks VALUE (?, ?, ?, ?, ?)");
            statement.setInt(1, perk_id);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setString(4, icon);
            statement.setInt(5, killer_id);
            int lines_affected = statement.executeUpdate();
            statement.close();

            send_http_response(201, json_message(lines_affected + " lines affected."));
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate entry")) { // SQL error: duplicate primary key
                send_http_response(400, json_message("perk_id " + perk_id + " already exists!"));
            } else {
                System.out.println("SQL Error: " + e);
                send_http_response(500, json_message("SQL Error: " + e));
                throw new RuntimeException(e);
            }
        } catch (JSONException e) { // 400 Bad Request: Request body missing element
            var missing_element = e.getMessage().split("\"")[1];
            send_http_response(400, json_message("Request element '" + missing_element + "' not found!"));
        } catch (ClassCastException e) {
            send_http_response(400, json_message("perk_id must be int!"));
        } finally {
            try {
                connection.close();
            } catch (Exception _) {
            }
        }
    }

    void get_by_id(int perk_id) throws IOException {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT killer_perks.*, killers.title FROM killer_perks, killers WHERE perk_id = ? AND killers.killer_id = killer_perks.killer_id");
            statement.setInt(1, perk_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                JSONObject get_result = new JSONObject();
                get_result.put("perk_id", resultSet.getInt("perk_id"));
                get_result.put("name", resultSet.getString("name"));
                get_result.put("description", resultSet.getString("description"));
                get_result.put("icon", resultSet.getString("icon"));
                get_result.put("killer_id", resultSet.getInt("killer_id"));
                get_result.put("title", resultSet.getString("title"));
                send_http_response(200, get_result.toString());
            } else {
                send_http_response(404, json_message("perk_id " + perk_id + " not found!"));
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

    void get_all() throws IOException {
        try {
            String query = "SELECT killer_perks.*, killers.title FROM killer_perks, killers WHERE killers.killer_id = killer_perks.killer_id ORDER BY killer_perks.perk_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<JSONObject> perk_list = new ArrayList<>();
            while (resultSet.next()) {
                JSONObject get_result = new JSONObject();
                get_result.put("perk_id", resultSet.getInt("perk_id"));
                get_result.put("name", resultSet.getString("name"));
                get_result.put("description", resultSet.getString("description"));
                get_result.put("icon", resultSet.getString("icon"));
                get_result.put("killer_id", resultSet.getInt("killer_id"));
                get_result.put("title", resultSet.getString("title"));
                perk_list.add(get_result);
            }
            send_http_response(200, perk_list.toString());
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

    void put(int perk_id, JSONObject body) throws IOException {
        try {
            String name = (String) body.get("name");
            String description = (String) body.get("description");
            String icon = (String) body.get("icon");
            int killer_id = (int) body.get("killer_id");

            PreparedStatement statement = connection.prepareStatement("UPDATE killer_perks SET name=?, description=?, icon=?, killer_id=? where perk_id = ?");
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, icon);
            statement.setInt(4, killer_id);
            statement.setInt(5, perk_id);
            int lines_affected = statement.executeUpdate();
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

    void delete_by_id(int perk_id) throws IOException {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM killer_perks WHERE perk_id = ?");
            statement.setInt(1, perk_id);
            int lines_affected = statement.executeUpdate();
            statement.close();

            send_http_response(200, json_message(lines_affected + " lines deleted."));
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

    void delete_all() throws IOException {
        try {
            String query = "TRUNCATE killer_perks";
            Statement statement = connection.createStatement();
            int lines_affected = statement.executeUpdate(query);
            statement.close();

            send_http_response(200, json_message(lines_affected + " lines deleted."));
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
