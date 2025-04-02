package org.example;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.example.Handlers.KillerPerksHandler;
import org.example.Handlers.KillersHandler;
import org.example.Handlers.SurvivorPerksHandler;
import org.example.Handlers.SurvivorsHandler;


public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Class.forName("org.example.DatabaseConnection"); // Create connection pool

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/killers", new KillersHandler());
        server.createContext("/api/killer_perks", new KillerPerksHandler());
        server.createContext("/api/survivors", new SurvivorsHandler());
        server.createContext("/api/survivor_perks", new SurvivorPerksHandler());
        server.setExecutor(null);
        server.start();
    }
}