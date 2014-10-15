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
    public static final byte[] CDE_SWITCH_RELAY_MESSAGE={0x55, (byte)0xaa, 0x00, 0x03, 0x00, 0x03, 0x01, 0x07 };

    // psw hex => 62 37 65 62 38
    // psw str =>  "b7eb8"
    // psw command => psw (bytes) + 0x0d, 0x0a
    public byte[] PASSWORD_RELAY_MESSAGE={0x62, 0x37, 0x65, 0x62, 0x38, 0x0d, 0x0a};

    public static final int ObjectName=0;
    public static final int index=1;
    public static final int type=2;
    public static final int networkIpAddress=3;
    public static final int networkPort=4;
    public static final int devicePswd=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add item in that array to add element in the list
        // don't forget to add corresponding icon in tab_images_pour_la_liste
        // retrieve the object definition
        ConnectedObject currentDevice=new ConnectedObject();

        String[] values = new String[] { currentDevice.GetObjectDetails(0)[ObjectName], currentDevice.GetObjectDetails(1)[ObjectName]};

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

        // retrieve the object definition, (corresponding to the list Item click (line position)
        ConnectedObject currentDevice=new ConnectedObject();
        String currentDeviceDefinition[] = currentDevice.GetObjectDetails(position);

        // Display feedback to the user, ie the object name
        Toast.makeText(this, currentDeviceDefinition[ObjectName], Toast.LENGTH_SHORT).show();

        // send relay command
        new SetRelayOnTask(position).execute();
    }

    // TCP connection should be started in a new thread
    class SetRelayOnTask extends AsyncTask<Void,Void,String> {

        int selectedIndex;

        // add new constructor with param
        public SetRelayOnTask(int index) {
            super();
            // set the index of the request
            selectedIndex=index;
        }

        public String doInBackground(Void... params) {


            // retrieve the object definition
            ConnectedObject currentDevice=new ConnectedObject();
            String currentDeviceDefinition[] = currentDevice.GetObjectDetails(selectedIndex);

            // connect socket and send device password
            //TCPClient sTcpClient = new TCPClient(SERVER_IP,SERVER_PORT );
            TCPClient sTcpClient = new TCPClient(currentDeviceDefinition[networkIpAddress],Integer.parseInt(currentDeviceDefinition[networkPort]) );

            // send password over TCP connection
            String retStatus=sTcpClient.SendOverTCP(PASSWORD_RELAY_MESSAGE, true);

            // check if password is correct (return OK)
            if (retStatus.equals("OK")) {
                // check if pulse command
                if (currentDeviceDefinition[type].equals("0")) {
                    sTcpClient.SendOverTCP(CDE_ON_RELAY_MESSAGE, true);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sTcpClient.SendOverTCP(CDE_OFF_RELAY_MESSAGE, true);
                }else{
                    sTcpClient.SendOverTCP(CDE_SWITCH_RELAY_MESSAGE, true);
                }
                sTcpClient.CloseSocket();
            }
            String ret="ok";
            return ret;
        }
    }

}
