package edu.sjtu.jie.TCPCommunication;

public interface TCPListener {
    public void onTCPMessageRecieved(String message);

    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}
