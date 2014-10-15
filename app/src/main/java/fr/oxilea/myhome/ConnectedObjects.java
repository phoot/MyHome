package fr.oxilea.myhome;

/**
 * Created by philippe on 10/10/2014.
 */
public class ConnectedObjects {



    // data structure: ObjectName | index | type (pulse 0 or On/off 1 | networkIpAddress | networkPort | devicePswd
    String connectedObjectList [][]={   {"Portail", "0", "0","your.ip.add","8899", "xxx"},
                                        {"Prise", "1", "1","your.ip.add","8899","xxx"}};

    /**
     * Constructor of the class.
     */
    public ConnectedObjects(){
        // currently objects are declared in a array
        // to be modified as a read from local database

    }

    String[] GetObjectDetails(int index){
        return connectedObjectList[index];
    }
}
