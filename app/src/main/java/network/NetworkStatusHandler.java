package network;

public interface NetworkStatusHandler {

    void checkConnection();
    void handleConnection(boolean isConnected);
}
