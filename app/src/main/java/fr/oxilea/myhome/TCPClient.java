package fr.oxilea.myhome;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TCPClient {

    public static final byte[] PASSWORD_RELAY_MESSAGE={0x62, 0x37, 0x65, 0x62, 0x38, 0x0d, 0x0a};

    public static String SERVER_IP = "192.168.2.23"; //your computer IP address
    public static int SERVER_PORT = 8899;

    // used to send messages
    private PrintStream mBufferOutPwd;
    private PrintStream mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferInPwd;
    private BufferedReader mBufferIn;

    // reference to the created socket
    Socket socket = null;


    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(byte[] str2send) {
        try {
            //create a socket to make the connection with the server
            Log.i("TCP Client", "New Socket...");
            socket = new Socket(SERVER_IP, SERVER_PORT);

            // set a read timeout (ms)
            socket.setSoTimeout(500);

            Log.i("TCP Client", "BufferOut...");
            mBufferOut = new PrintStream(socket.getOutputStream(), true);

            Log.i("TCP Client", "BufferIn...");
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Log.i("TCP Client", "write...");
            mBufferOut.write(str2send);
            String strRead = mBufferIn.readLine();
            Log.i("TCP Client read", strRead);

        }
        catch (Exception e) {
            Log.e("TCP", "S: Error", e);
        }
        // close socket !!!
        Log.i("TCP Client", "socket closed...");
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("TCP", "Socket close: Error", e);
        }
    }
}
