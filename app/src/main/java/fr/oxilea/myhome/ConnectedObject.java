package fr.oxilea.myhome;

/**
 * Created by philippe on 10/10/2014.
 */
public class ConnectedObject {

    // data structure: ObjectName | index | type (pulse 0 or On/off 1 | localIpAddress | local port | networkIpAddress | networkPort | localWifiName | devicePswd
    String connectedObjectList [][]={   {"Portail", "0", "0","phoot.hd.free.fr","8899","b7eb8"},
                                        {"Prise", "1", "1","phoot.hd.free.fr","8899","b7eb8"}};

    /**
     * Constructor of the class.
     */
    public ConnectedObject(){
        // currently objects are declared in a array
        // to be modified as a read from local database

    }

    String[] GetObjectDetails(int index){
        return connectedObjectList[index];
    }
}
