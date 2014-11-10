package fr.oxilea.myhome;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


public class ActivitySetting extends Activity {

    int currentEditedId=-1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the current edited id from the value set on intent
        Bundle b = getIntent().getExtras();
        currentEditedId = b.getInt("Id");

        setContentView(R.layout.settings_scr);

        // manage spinner for icon type
        Spinner spinner = (Spinner) findViewById(R.id.spinnerIcon);

        if (currentEditedId != -1){
            // this is an update of an already defined setting, should retrieve data from BDD
            DeviceBdd mySettingBdd = new DeviceBdd(this);
            mySettingBdd.open();
            ConnectedObject myObject= new ConnectedObject();
            myObject = mySettingBdd.getObjectWithId(currentEditedId);
            mySettingBdd.close();

            // set the already defined Device Name
            TextView myTextView = (TextView) findViewById(R.id.editTextDevice);
            myTextView.setText(myObject.GetObjectName());

            // set current command type value (pulse OFF/ON)
            Switch s = (Switch) findViewById(R.id.switchPulse);
            if (s != null) {
                Boolean valSwitch = !(myObject.GetObjectCdeType().equals("0"));
                s.setChecked(valSwitch);
            }

            myTextView = (TextView) findViewById(R.id.editTextDeviceAdd);
            myTextView.setText(myObject.GetObjectIpAddress());

            myTextView = (TextView) findViewById(R.id.editTextDevicePort);
            myTextView.setText(myObject.GetObjectIpPort());

            myTextView = (TextView) findViewById(R.id.editSettingPsw);
            myTextView.setText(myObject.GetObjectPassword());

            // set the current spinner value
            String iconType = myObject.GetObjectIconType();
            int iconIndex = Integer.parseInt(iconType);
            spinner.setSelection(iconIndex);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.icons_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    // save or update device from BDD
    public void saveDeviceSetting(View v)
    {
            // get all inputs
            TextView myTextView = (TextView) findViewById(R.id.editTextDevice);
            String deviceName = myTextView.getText().toString();

            Switch s = (Switch)findViewById(R.id.switchPulse);
            String deviceCdeType = "0";
            if (s.isChecked())
                deviceCdeType="1";

            myTextView = (TextView) findViewById(R.id.editTextDeviceAdd);
            String deviceDeviceAdd = myTextView.getText().toString();

            myTextView = (TextView) findViewById(R.id.editTextDevicePort);
            String deviceDevicePort = myTextView.getText().toString();

            myTextView = (TextView) findViewById(R.id.editSettingPsw);
            String deviceDevicePsw = myTextView.getText().toString();

            // manage spinner for icon type
            Spinner spinner = (Spinner) findViewById(R.id.spinnerIcon);
            String deviceDeviceIcon = String.valueOf(spinner.getSelectedItemPosition());

            DeviceBdd mySettingBdd = new DeviceBdd(this);
            mySettingBdd.open();
            ConnectedObject myObject= new ConnectedObject(deviceName, deviceCdeType, deviceCdeType, deviceDeviceAdd, deviceDevicePort, deviceDevicePsw, deviceDeviceIcon);
        if (currentEditedId == -1) {
            // this is a new object creation}
            mySettingBdd.insertObject(myObject);
        }
            else
        {
            // this is an update of an already defined setting
            mySettingBdd.updateObject(currentEditedId, myObject);
        }

        // close BDD
        mySettingBdd.close();

        // exit setting activity
        finish();
    }


    // remove device from BDD
    public void deleteDeviceSetting(View v)
    {
        if (currentEditedId != -1) {
            DeviceBdd mySettingBdd = new DeviceBdd(this);
            mySettingBdd.open();

            mySettingBdd.removeObjectWithID(currentEditedId);

            // close BDD
            mySettingBdd.close();
        }

        // exit setting activity
        finish();
    }
}

