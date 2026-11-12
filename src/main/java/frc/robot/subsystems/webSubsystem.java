package frc.robot.subsystems;

import java.awt.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;


public class webSubsystem extends SubsystemBase {
    private HttpServer server;

    private static String determineContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".json")) return "application/json";
        return "application/octet-stream"; // standard fallback binary chunk
    }

    public void startServer() {
        try {
            // Bind to FRC open port 5800
            server = HttpServer.create(new InetSocketAddress(5800), 0);
            
            // Register your different routes / handles here:
            server.createContext("/", new HomeHandler()); 
            server.createContext("/storage", new FileHandler());               // Serves a main HTML page
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
    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Locate the file in the roboRIO deploy directory
            String requestPath = t.getRequestURI().getPath();
            String relativeFileName = requestPath.replaceFirst("^/storage/?", "");

            Path filePath = Filesystem.getDeployDirectory().toPath().resolve(relativeFileName);
            
            if (Files.exists(filePath)) {
                byte[] response = Files.readAllBytes(filePath);
                String contentType = determineContentType(relativeFileName);
                t.getResponseHeaders().set("Content-Type", contentType);
                t.sendResponseHeaders(200, response.length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response);
                }
            } else {
                String error = "404 File Not Found";
                t.sendResponseHeaders(404, error.length());
                try (OutputStream os = t.getResponseBody()) {
                    os.write(error.getBytes());
                }
            }
        }
    }

    // Handle 1: Serve a basic HTML homepage
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String html = "<html><body>"
                        + "<h1>roboRIO Custom Controller</h1>"
                        + "<p>Navigate to <a href='/storage/index.html'>/status</a> to see JSON telemetry.</p>"
                        + "</body></html>";
            sendResponse(t, 200, "text/html", html);
        }
    }
    

    // Handle 2: Serve robot JSON telemetry data
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String robotState = DriverStation.isDisabled() ? "Disabled" :
            DriverStation.isTeleop() ? "TeleOp" :
            DriverStation.isAutonomous() ? "Autonomous" :
            DriverStation.isTest() ? "Test" : "Unknown";

            String json = """
            {
                "timestamp": 0,
                "robotMode": "%s",
                "disabled": %b,
                "batteryVoltage": %f,
                "driveEnabled": true,
                "safetyFire": true,
                "isFiring": false,
                "slowMode": %b,
                "slowModeValue": %f,
                "ledState": %f,
                "debugEnabled": %b,
                "fireDisabled": false,
                "forceFireActive": false,
                "leftStickY": %.2f,
                "rightStickY": %.2f,
                "controllerConnected": true,
                "driveState": false
            }
            """;
            String formattedJson = json.formatted(
                robotState, //Robot Mode
                DriverStation.isDisabled(),  //disabled
                RobotController.getBatteryVoltage(), // batteryVoltage
                RobotContainer.slowmode, // slowMode
                RobotContainer.slowmodeval,
                RobotContainer.ledStatus, //LedState
                RobotContainer.debug, //debugEnabled
                RobotContainer.stickL, //leftStickY
                RobotContainer.stickR //rightStickY
                
            );
            sendResponse(t, 200, "application/json", formattedJson);
        }
    }
    //{
//   "timestamp": 1730000000,
//   "robotMode": "teleop",
//   "disabled": false,
//   "batteryVoltage": 12.6,
//   "driveEnabled": true,
//   "safetyFire": true,
//   "isFiring": false,
//   "slowMode": false,
//   "slowModeValue": 0.25,
//   "ledState": 2,
//   "debugEnabled": false,
//   "fireDisabled": false,
//   "forceFireActive": false,
//   "leftStickY": 0.0,
//   "rightStickY": 0.0,
//   "controllerConnected": true,
//   "cannonTilt": 0.0,
//   "cannonRotation": 0.0
// }

    // Handle 3: Accept incoming data/actions from your web browser (POST request)
    static class CommandPostHandler implements HttpHandler {
        private static final ObjectMapper mapper = new ObjectMapper();
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
                //skibidi balls 67 aura rizz farmer - bobert november 2026
                String receivedData = body.toString();
                System.out.println("Received command from web dashboard: " + receivedData);
                // JsonNode root = mapper.readTree(body);
                ObjectMapper mapper = new ObjectMapper();
        
                JsonNode node = mapper.readTree(receivedData);
                String parseAction = node.get("action").asText();
                Double parseValue = node.get("value").asDouble();

                // int age = node.get("age").asInt();
                if (parseAction != null) {
                    System.out.println("Start Parse " + parseAction);
                    if ("setLedState".equals(parseAction)) {
                        RobotContainer.castLEDvalue(parseValue);
                        System.out.println("Set Val: " + parseValue);
                    }
                }

                // TODO: Parse 'receivedData' and toggle things in your robot code here!

                sendResponse(t, 200, "text/plain", "Command processed successfully.");
            } else {
                // Reject GET requests trying to hit this command endpoint
                sendResponse(t, 405, "text/plain", "Method Not Allowed. Use POST.");
            }
        }
    }
    
}
