package fr.oxilea.myhome;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TCPClient {

    // used to send messages
    private PrintStream mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    // reference to the created socket
    Socket socket = null;


    /**
     * Constructor of the class.
     * create a Socket
     */
    public TCPClient(String server, int port) {
        try {
            //create a socket to make the connection with the server
            Log.i("TCP Client", "New Socket...");
            socket = new Socket(server, port);
        }
        catch (Exception e) {
            Log.e("TCP", "S: Error", e);
        }
    }
    public String SendOverTCP(byte[] str2send, Boolean expectResponse) {
        String retStr= "";

        try {
            // set a read timeout (ms) only if wait for EOL on socket read
            // socket.setSoTimeout(500);

            Log.i("TCP Client", "BufferOut...");
            mBufferOut = new PrintStream(socket.getOutputStream(), true);

            Log.i("TCP Client", "BufferIn...");
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Log.i("TCP Client", "write...");
            mBufferOut.write(str2send);
            if(expectResponse) {
                Log.i("TCP Client read ","response");
                char cRead[]={'\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0'};
                // wait a little bit before read status
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBufferIn.read(cRead,0,10);

                // keep only valid char read
                int i=0;
                while(cRead[i]!='\0'){i++;}; // the last char is always \0 as only 10 chars are read
                Log.i("TCP Client read", String.valueOf(cRead,0,i));
                retStr = String.valueOf(cRead,0,i);
            }
        }
        catch (Exception e) {
            Log.e("TCP", "S: Error", e);
            retStr= "";
        }finally {
            return retStr;
        }

    }

    public Boolean CloseSocket() {

        Boolean retStatus= true;

        Log.i("TCP Client", "socket closed...");
        try {
            // close socket !!!
            socket.close();
        } catch (IOException e) {
            Log.e("TCP", "Socket close: Error", e);
            retStatus= false;
        } finally {
            return retStatus;
        }
    }
}
