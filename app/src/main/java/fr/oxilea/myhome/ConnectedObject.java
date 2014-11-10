package fr.oxilea.myhome;


public class ConnectedObject {

    // data structure: ObjectName | index | cdeType (pulse 0 or On/off 1 | networkIpAddress | networkPort | devicePswd | iconType

    private int id;
    private String objectName;
    private String objectIndex;
    private String cdeType;
    private String ipAddress;
    private String ipPort;
    private String password;
    private String iconType;


    /**
     * Constructor of the class.
     */
    public ConnectedObject(){
        // default, create only pointer to ConnectedObject

    }

    public ConnectedObject(String objName, String objIndex, String objCdeType, String objIpAddress, String objIpPort, String objPassword, String objIconType ){
        objectName = objName;
        objectIndex = objIndex;
        cdeType = objCdeType;
        ipAddress = objIpAddress;
        ipPort = objIpPort;
        password = objPassword;
        iconType = objIconType;
    }


    int GetId(){
        return id;
    }

    void SetId(int objId){
        id = objId;
    }

    String GetObjectName(){
        return objectName;
    }

    void SetObjectName(String objName){
        objectName = objName;
    }

    String GetObjectIndex(){
        return objectIndex;
    }

    void SetObjectIndex(String objIndex){
        objectIndex = objIndex;
    }

    String GetObjectCdeType(){
        return cdeType;
    }

    void SetObjectCdeType(String objCdeType){
        cdeType = objCdeType;
    }

    String GetObjectIpAddress(){
        return ipAddress;
    }

    void SetObjectIpAddress(String objIpAddress){
        ipAddress = objIpAddress;
    }

    String GetObjectIpPort(){
        return ipPort;
    }

    void SetObjectIpPort(String objIpPort){
        ipPort = objIpPort;
    }

    String GetObjectPassword(){
        return password;
    }

    void SetObjectPassword(String objPassword){
        password = objPassword;
    }

    String GetObjectIconType(){
        return iconType;
    }

    void SetObjectIconType(String objIconType){
        iconType = objIconType;
    }

}
