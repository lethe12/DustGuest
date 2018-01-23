package com.grean.dustguest.protocol;

/**
 * Created by weifeng on 2018/1/23.
 */

public class ProtocolLib {
    private GeneralClientProtocol clientProtocol;
    private static ProtocolLib instance = new ProtocolLib();

    private ProtocolLib(){

    }

    public static ProtocolLib getInstance() {
        return instance;
    }

    public GeneralClientProtocol getClientProtocol(){
        if(clientProtocol==null){
            clientProtocol = new ClientProtocol();
        }
        return clientProtocol;
    }
}
