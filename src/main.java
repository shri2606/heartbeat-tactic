import Server.HeartbeatMonitor;
import Client.WearableHealthTracker;

public class Main {

    public static void main(String[] args) {
        // Start the server in a separate thread
        Thread serverThread = new Thread(() -> HeartbeatMonitor.startServer());
        serverThread.start();

        // Start the client in a separate thread after a brief delay to ensure server starts first
        try {
            Thread.sleep(1000); // Wait for server to start
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread clientThread = new Thread(() -> WearableHealthTracker.startClient());
        clientThread.start();
    }
}
