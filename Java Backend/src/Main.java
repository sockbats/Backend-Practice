import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import KillerHandlers.*;
import SurvivorHandlers.*;


public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/killers", new KillersHandler());
        server.createContext("/api/survivors", new KillerPerksHandler());
        server.createContext("/api/killer_perks", new SurvivorsHandler());
        server.createContext("/api/survivor_perks", new SurvivorPerksHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}