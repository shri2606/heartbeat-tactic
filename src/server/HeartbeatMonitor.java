package Server;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Logger;
import Utils.HeartbeatConstants;

public class HeartbeatMonitor {

    private static final Logger logger = Logger.getLogger(HeartbeatMonitor.class.getName());

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(HeartbeatConstants.SERVER_PORT)) {
            System.out.println("Monitoring system waiting for connection on port " + HeartbeatConstants.SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept connection from wearable
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                long lastHeartbeatTime = System.currentTimeMillis();

                // Start monitoring the connected wearable device
                while (true) {
                    if (in.ready()) {
                        String heartbeat = in.readLine();
                        System.out.println("Received: " + heartbeat);
                        lastHeartbeatTime = System.currentTimeMillis(); // Reset the heartbeat time
                    }

                    // Check if heartbeat timeout is exceeded
                    if ((System.currentTimeMillis() - lastHeartbeatTime) > HeartbeatConstants.HEARTBEAT_TIMEOUT) {
                        logger.warning("No heartbeat received within " + HeartbeatConstants.HEARTBEAT_TIMEOUT + " ms. Device failure detected.");
                        logFailure();
                        break; // Stop monitoring this device after failure
                    }

                    Thread.sleep(100); // Sleep for a short time before checking again
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Log the device failure to the log file
    private static void logFailure() {
        try (FileWriter fw = new FileWriter(HeartbeatConstants.LOG_FILE, true)) {  // true to append to file
            fw.write("Device failure detected at: " + new Date() + "\n");
            System.out.println("Failure logged to: " + HeartbeatConstants.LOG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
