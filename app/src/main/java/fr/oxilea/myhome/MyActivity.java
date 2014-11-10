package fr.oxilea.myhome;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.regex.Pattern;


public class MyActivity extends ListActivity {

    // global message Format
    // Packet head - Total length (2 bytes) - ID - Command - Parameter - Parity
    // 0x55 0xaa - 0x00 0x?? - 0x01 - 0x?? .. - 0x?? .. -0x??
    // TCP over network send full frame

    public static final byte[] STATUS_RELAY_MESSAGE = {0x55, (byte) 0xaa, 0x00, 0x02, 0x00, 0x0a, 0x0c};
    public static final byte[] CDE_ON_RELAY_MESSAGE = {0x55, (byte) 0xaa, 0x00, 0x03, 0x00, 0x02, 0x01, 0x06};
    public static final byte[] CDE_OFF_RELAY_MESSAGE = {0x55, (byte) 0xaa, 0x00, 0x03, 0x00, 0x01, 0x01, 0x05};
    public static final byte[] CDE_SWITCH_RELAY_MESSAGE = {0x55, (byte) 0xaa, 0x00, 0x03, 0x00, 0x03, 0x01, 0x07};

    // psw hex => 62 37 65 62 38
    // psw str =>  "xxxxx"
    // psw command => psw (bytes) + 0x0d, 0x0a
    public byte[] PASSWORD_RELAY_MESSAGE = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0d, 0x0a};

    // no id in connectedObject type, id only present in database (index is same as bdd minus 1
    public static final int ObjectName = 0;
    public static final int index = 1;
    public static final int type = 2;
    public static final int networkIpAddress = 3;
    public static final int networkPort = 4;
    public static final int devicePswd = 5;

    // maximum possible object managed through this software
    public static final int MAX_MANAGED_OBJECT = 25;

    private Boolean editMode=false;
    private MonAdaptateurDeListe adaptateur;
    private ConnectedObject myCurrentObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // update the device list for display
        UpdateObjectListNameForAdaptateur();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // update the device list for display if another activity (setting new or change) modify it
        UpdateObjectListNameForAdaptateur();

    }
    // allow to construct the list of object managed
    // should be called on activity creation and when the list is updated
    private void UpdateObjectListNameForAdaptateur()
    {
        // add item in that array to add element in the list
        // icon are retrieved when view is displayed
        // retrieve the object definition

        // get the name from bdd
        DeviceBdd mySettingBdd = new DeviceBdd(this);
        mySettingBdd.open();
        ConnectedObject currentDevice= new ConnectedObject();

        String[] valuesTmp = new String[MAX_MANAGED_OBJECT];
        int i=0;
        currentDevice = mySettingBdd.getObjectWithId(i);
        while (currentDevice != null)
        {
            valuesTmp[i] = currentDevice.GetObjectName();
            i++;
            currentDevice = mySettingBdd.getObjectWithId(i);
        }
        mySettingBdd.close();

        // create the array with only the real number<of object currently defined
        String[] values = new String[i];
        int j=0;
        while (j <i)
        {
            values[j]= valuesTmp[j];
            j++;
        }

        //String[] values = new String[]{currentDevice.GetObjectDetails(0)[ObjectName], currentDevice.GetObjectDetails(1)[ObjectName]};

        adaptateur = new MonAdaptateurDeListe(this, values);
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
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_settings:
                // create setting activity
                intent = new Intent(MyActivity.this, ActivitySetting.class);

                // id = 0 new device creation
                intent.putExtra("Id",-1);
                startActivity(intent);
                return true;

            case R.id.action_about:
                // create about activity
                intent = new Intent(MyActivity.this, ActivityAbout.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onOptionSwitchMode(MenuItem item) {
        ActionBar bar;
        bar = this.getActionBar();

        switch (item.getItemId()) {
            case R.id.action_edit:

                if (editMode) {
                    // exit edit mode
                    editMode = false;
                    // change action bar color
                    bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));
                } else{
                    // enter edit mode
                    editMode = true;
                    // change action bar color
                    bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_red_bg));
                }

            return;

            case R.id.action_settings:
            case R.id.action_about:
                //nothing to do right now
            return;
        }
    }

        @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        if (editMode)
        {
            //enter edit mode for the item selected
            // create setting activity
            Intent intent;
            intent = new Intent(MyActivity.this, ActivitySetting.class);

            // transmit the id
            intent.putExtra("Id", position);
            startActivity(intent);


        }else {

            // retrieve the object definition, (corresponding to the list Item click (line position +1 in BDD)
            DeviceBdd mySettingBdd = new DeviceBdd(this);
            mySettingBdd.open();
            myCurrentObject= new ConnectedObject();
            myCurrentObject = mySettingBdd.getObjectWithId(position);
            mySettingBdd.close();

            // Display feedback to the user, ie the object name
            Toast.makeText(this, myCurrentObject.GetObjectName(), Toast.LENGTH_SHORT).show();


            // send relay command
            new SetRelayOnTask(position).execute();
        }
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


            // object definition retrieved from myCurrentObject
            // connect socket on device IP address and Port
            String ipPortStr = myCurrentObject.GetObjectIpPort();
            int ipPort;

            // check if string has only digits
            if (Pattern.matches("[0-9]+", ipPortStr))
                ipPort = Integer.parseInt(ipPortStr);
            else
            ipPort =0;

            TCPClient sTcpClient = new TCPClient(myCurrentObject.GetObjectIpAddress(), ipPort);

            // send password over TCP socket just connected
            // get the password and format the command for the device
            String pwd = myCurrentObject.GetObjectPassword();
            int pwdLength = pwd.length();
            int i=0;
            while (i < pwdLength) {
                PASSWORD_RELAY_MESSAGE[i]=(byte)pwd.charAt(i);
                i++;
            }
            // psw end with 0x0d, 0x0a
            PASSWORD_RELAY_MESSAGE[i++]=(byte)0x0d;
            PASSWORD_RELAY_MESSAGE[i]=(byte)0x0a;

            String retStatus=sTcpClient.SendOverTCP(PASSWORD_RELAY_MESSAGE, true);

            // check if password is correct (return OK)
            if (retStatus.equals("OK")) {
                // check if pulse command
                if (myCurrentObject.GetObjectCdeType().equals("1")) {
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
            return "ok";
        }
    }

}
