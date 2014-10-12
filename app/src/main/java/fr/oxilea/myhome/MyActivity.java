package fr.oxilea.myhome;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MyActivity extends ListActivity {

    // global message Format
    // Packet head - Total length (2 bytes) - ID - Command - Parameter - Parity
    // 0x55 0xaa - 0x00 0x?? - 0x01 - 0x?? .. - 0x?? .. -0x??
    // TCP over network send full frame

    public static final byte[] STATUS_RELAY_MESSAGE={0x55, (byte)0xaa, 0x00, 0x02, 0x00, 0x0a, 0x0c };
    public static final byte[] CDE_ON_RELAY_MESSAGE={ 0x55, (byte)0xaa, 0x00, 0x03, 0x00, 0x02, 0x01, 0x06 };
    public static final byte[] CDE_OFF_RELAY_MESSAGE={0x55, (byte)0xaa, 0x00, 0x03, 0x00, 0x01, 0x01, 0x05 };

    // mdp hex => 62 37 65 62 38
    // mdp str =>  "b7eb8"
    // mdp command => mdp (bytes) + 0x0d, 0x0a
    public static final byte[] PASSWORD_RELAY_MESSAGE={0x62, 0x37, 0x65, 0x62, 0x38, 0x0d, 0x0a};


    // Temp default definition server port
    public static String SERVER_IP = "192.168.2.23"; //your computer IP address
    public static int SERVER_PORT = 8899;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add item in that array to add elment in the list
        // don't forget to add corresponding icon in tab_images_pour_la_liste
        String[] values = new String[] { "Portail", "Lampe Salon" };

        MonAdaptateurDeListe adaptateur = new MonAdaptateurDeListe(this, values);
        setListAdapter(adaptateur);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(this, "Position : " + position, Toast.LENGTH_LONG).show();

        // send relay command
        new SetRelayOnTask().execute();
    }

    // TCP connection should be started in a new thread
    class SetRelayOnTask extends AsyncTask<Void,Void,String> {

        public String doInBackground(Void... params) {
            // connect socket and send device password
            TCPClient sTcpClient = new TCPClient(SERVER_IP,SERVER_PORT );

            // send password over TCP connection
            String retStatus=sTcpClient.SendOverTCP(PASSWORD_RELAY_MESSAGE, true);

            // check if password is correct (return OK)
            if (retStatus.equals("OK")) {
                sTcpClient.SendOverTCP(CDE_ON_RELAY_MESSAGE, false);

                // check if pulse command
                if (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sTcpClient.SendOverTCP(CDE_OFF_RELAY_MESSAGE, false);
                }
                sTcpClient.CloseSocket();
            }
            String ret="ok";
            return ret;
        }
    }

}
