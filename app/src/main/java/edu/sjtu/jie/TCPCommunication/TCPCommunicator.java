package edu.sjtu.jie.TCPCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class TCPCommunicator {
    private static TCPCommunicator uniqInstance;
    private static String serverHost;
    private static int serverPort;
    private static List<TCPListener> allListeners;
    private static DataOutputStream out;
    private static DataInputStream in;
    private static Socket s;
    private static Handler UIHandler;
    private static Context appContext;
    private static final String  TAG = "MainActivity";

    private TCPCommunicator() {
        allListeners = new ArrayList<TCPListener>();
    }

    public static TCPCommunicator getInstance() {
        if (uniqInstance == null) {
            uniqInstance = new TCPCommunicator();
        }
        return uniqInstance;
    }

    public TCPWriterErrors init(String host, int port) {
        setServerHost(host);
        setServerPort(port);
        InitTCPClientTask task = new InitTCPClientTask();
        task.execute(new Void[0]);
        return TCPWriterErrors.OK;
    }

    public static TCPWriterErrors writeToSocket(final JSONObject msgObj, Handler handle, Context context) {
        UIHandler = handle;
        appContext = context;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String outMsg = msgObj.toString();
                    int megLen=outMsg.getBytes().length;
                    out.write((String.valueOf(megLen)+"\n").getBytes());
                    out.flush();
                    out.write(outMsg.getBytes());
                    out.flush();
                    Log.i("TcpClient", "sent: " + outMsg);
                } catch (Exception e) {
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(appContext, "a problem has occurred, the app might not be able to reach the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return TCPWriterErrors.OK;
    }

    public static void addListener(TCPListener listener) {
//		allListeners.clear();
        allListeners.add(listener);
    }

    public static void removeAllListeners() {
        allListeners.clear();
    }

    public static void closeStreams() {
        try {
            s.close();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getServerHost() {
        return serverHost;
    }

    public static void setServerHost(String serverHost) {
        TCPCommunicator.serverHost = serverHost;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        TCPCommunicator.serverPort = serverPort;
    }


    public class InitTCPClientTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                s = new Socket(getServerHost(), getServerPort());
                Log.i(TAG,"Init socket: ip:"+getServerHost()+", port: "+getServerPort()+", socket: "+s+"------------------");
                in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());
                for (TCPListener listener : allListeners)
                    listener.onTCPConnectionStatusChanged(true);
                while (true) {
                    byte[] bufLen = new byte[1024];
                    in.read(bufLen);
                    String lenMsg=new String(bufLen);
                    Log.i(TAG,"lenMsg~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+lenMsg);
                    int byteLen=Integer.parseInt(lenMsg.substring(0,lenMsg.indexOf("\n")));
                    Log.i(TAG,"byteLen------------------"+String.valueOf(byteLen));
                    byte[] buf = new byte[byteLen];

                    int lenRead = 0;
                    while(lenRead < byteLen) {
                        lenRead += in.read(buf, lenRead, byteLen - lenRead);
                    }
                    Log.i(TAG, "received: " + new String(buf));
                    for (TCPListener listener : allListeners)
                            listener.onTCPMessageReceived(new String(buf));

//                    int len = in.read(buf);
//                    if (len != -1 && len == byteLen) {
//                        Log.i(TAG, "received: " + new String(buf));
//                        for (TCPListener listener : allListeners)
//                            listener.onTCPMessageReceived(new String(buf));
//                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public enum TCPWriterErrors {UnknownHostException, IOException, otherProblem, OK}
}

