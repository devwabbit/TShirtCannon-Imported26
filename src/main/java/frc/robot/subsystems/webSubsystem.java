package frc.robot.subsystems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;



public class webSubsystem {
private HttpServer server;

    public void startServer() {
        try {
            // Bind to FRC open port 5800
            server = HttpServer.create(new InetSocketAddress(5800), 0);
            
            // Register your different routes / handles here:
            server.createContext("/", new HomeHandler());               // Serves a main HTML page
            server.createContext("/status", new StatusHandler());       // Serves robot metrics (JSON)
            server.createContext("/command", new CommandPostHandler()); // Accepts incoming actions (POST)
            
            server.setExecutor(null); 
            server.start();
            System.out.println("Webserver started successfully on port 5800.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to send text responses quickly
    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Handle 1: Serve a basic HTML homepage
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String html = "<html><body>"
                        + "<h1>roboRIO Custom Controller</h1>"
                        + "<p>Navigate to <a href='/status'>/status</a> to see JSON telemetry.</p>"
                        + "</body></html>";
            sendResponse(t, 200, "text/html", html);
        }
    }

    // Handle 2: Serve robot JSON telemetry data
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // In a real robot project, fetch these from your real subsystems
            double batteryVoltage = 12.5; 
            boolean isAutonomous = false; 

            String json = String.format("{\"battery\": %.2f, \"isAuto\": %b}", batteryVoltage, isAutonomous);
            sendResponse(t, 200, "application/json", json);
        }
    }

    // Handle 3: Accept incoming data/actions from your web browser (POST request)
    static class CommandPostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Check if the browser sent a POST request
            if ("POST".equalsIgnoreCase(t.getRequestMethod())) {
                // Read the incoming message body
                InputStream is = t.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
                
                String receivedData = body.toString();
                System.out.println("Received command from web dashboard: " + receivedData);

                // TODO: Parse 'receivedData' and toggle things in your robot code here!

                sendResponse(t, 200, "text/plain", "Command processed successfully.");
            } else {
                // Reject GET requests trying to hit this command endpoint
                sendResponse(t, 405, "text/plain", "Method Not Allowed. Use POST.");
            }
        }
    }
}
