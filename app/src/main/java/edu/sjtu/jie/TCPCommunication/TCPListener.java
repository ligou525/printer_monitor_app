package edu.sjtu.jie.TCPCommunication;

public interface TCPListener {
    public void onTCPMessageReceived(String message);

    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}
