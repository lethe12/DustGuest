package com.grean.dustguest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.grean.dustguest.model.ScanDeviceState;
import com.grean.dustguest.protocol.GeneralClientProtocol;
import com.grean.dustguest.protocol.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by weifeng on 2018/1/23.
 */

public class SocketTask {
    private static final String tag = "SocketTask";
    private static SocketTask instance = new SocketTask();
    private String serverIp;
    private int serverPort;
    private Context context;
    private GeneralClientProtocol clientProtocol;
    private static boolean heartRun = false,connected = false;
    private HeartThread heartThread;
    private ReceiverThread receiverThread;
    private Socket socketClient;
    private InputStream receive;
    private OutputStream send;
    private ConcurrentLinkedQueue<byte[]> sendBuffer = new ConcurrentLinkedQueue<byte[]>();

    public static SocketTask getInstance() {
        return instance;
    }

    private SocketTask(){

    }

    public static boolean isConnected() {
        return connected;
    }

    public void startSocketHeart(String ip, int port, Context context, GeneralClientProtocol clientProtocol){
        this.clientProtocol = clientProtocol;
        this.context = context;
        if(ip.equals("RouterIP")){
            this.serverIp = getWifiRouteIPAddress(context);
        }else {
            this.serverIp = ip;
        }
        Log.d(tag,"server ip = "+serverIp);
        this.serverPort = port;
        if (!heartRun){
            heartThread = new HeartThread();
            heartThread.start();
        }
    }

    public void restartSocketHeart(){
        if (!heartRun){
            heartThread = new HeartThread();
            heartThread.start();
        }
    }

    public void stopSocketHeart(){
        heartRun = false;
        connected = false;
        try {
            if(socketClient!=null){
                socketClient.shutdownInput();
                socketClient.shutdownOutput();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ReceiverThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                /*if(notifyProcessDialogInfo!=null){
                    notifyProcessDialogInfo.showInfo("新建链接");
                }*/
                Log.d(tag,"IP:"+serverIp+" Port:"+String.valueOf(serverPort)+"router"+getWifiRouteIPAddress(context));
                //getAllIp();
                socketClient.connect(new InetSocketAddress(serverIp,serverPort),5000);
                socketClient.setTcpNoDelay(true);
                socketClient.setSoLinger(true,30);
                socketClient.setSendBufferSize(10240);
                socketClient.setReceiveBufferSize(10240);
                socketClient.setKeepAlive(true);
                receive = socketClient.getInputStream();
                send = socketClient.getOutputStream();
                socketClient.setOOBInline(true);
                connected = true;
                int count;
                byte[] readBuff = new byte[20480];
                /*if(notifyProcessDialogInfo!=null){
                    notifyProcessDialogInfo.showInfo("已链接");
                }
                if(notifyOperateInfo!=null){
                    notifyOperateInfo.cancelDialog();
                }
                if(show!=null){
                    show.showLocal(true);
                }*/
                while (connected){
                    if (socketClient.isConnected()){
                        String content=null;
                        int index=0;
                        while ((count = receive.read(readBuff))!=-1 && connected){
                            if((readBuff[count-2]=='\r')&&(readBuff[count-1]=='\n')) {//正常结尾

                                if(content==null) {
                                    //Log.d(tag,"处理一");
                                    content = new String(readBuff, 0, count);
                                }else{
                                    Log.d(tag,"处理二");
                                    content=content + new String(readBuff, 0, count);
                                }
                                index = 0;
                                //Log.d(tag,"TCP Content:"+content);
                                if(JSON.isFrameRight(content)) {
                                    clientProtocol.handleReceiveData(content.substring(content.indexOf("$$")+2,content.indexOf("\r\n")));
                                }
                                content = null;
                            }else{
                                Log.d(tag,"异常");
                                if((readBuff[0]=='#')&&(readBuff[1]=='#')){//正常包头，异常包尾
                                    content = new String(readBuff, 0, count);
                                    index = count;
                                }else{//包头、包尾皆异常
                                    if(content!=null){
                                        content = content+new String(readBuff,0,count);
                                        index+=count;
                                        if(index>10240){//超长包舍弃
                                            index = 0;
                                            content = null;
                                        }
                                    }else{//如未由正常包头，舍弃
                                        content = null;
                                        index = 0;
                                    }
                                }
                            }
                        }
                        connected = false;
                        break;
                    }else {
                        connected = false;
                    }
                    Log.d(tag,"one turn");
                }
            } catch (IOException e) {
                connected = false;
                /*if(show!=null){
                    show.showLocal(false);
                }
                Log.d(tag,"找不到服务器");
                if(notifyProcessDialogInfo!=null){
                    notifyProcessDialogInfo.showInfo("服务器未开启");
                }
                if(notifyOperateInfo!=null){
                    notifyOperateInfo.cancelDialog();
                }*/
                e.printStackTrace();
            }
            Log.d(tag,"断开连接");
            ScanDeviceState.getInstance().stopRun();
            /*if(show!=null){
                show.showLocal(false);
            }*/
        }
    }

    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * wifi获取 路由ip地址
     *
     * @param context
     * @return
     */
    private static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
//        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
//        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
//        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
//        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.d(tag, "wifi route ip：" + routeIp);

        return routeIp;
    }

    /*作者：A_客
    链接：http://www.jianshu.com/p/be244fb85a4e
    來源：简书
    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。*/

    /**
     * 得到无线网关的IP地址
     *
     * @return
     */
    private void getAllIp() {

        try {
            // 获取本地设备的所有网络接口
            Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                    .getNetworkInterfaces();
            while (enumerationNi.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNi.nextElement();
                String interfaceName = networkInterface.getDisplayName();
                Log.i("tag", "网络名字" + interfaceName);

                Enumeration<InetAddress> enumIpAddr = networkInterface
                        .getInetAddresses();

                while (enumIpAddr.hasMoreElements()) {
                    // 返回枚举集合中的下一个IP地址信息
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.i("tag", inetAddress.getHostAddress() + "哪个类型的   "+inetAddress.getClass().toString());

                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    private class HeartThread extends Thread{
        @Override
        public void run() {
            heartRun = true;
            super.run();
            int times=0;
            /*if(notifyProcessDialogInfo!=null){
                notifyProcessDialogInfo.showInfo("正在联网");
            }*/
            while ((heartRun)&&(!interrupted())){
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo info = manager.getActiveNetworkInfo();
                if(info!=null && info.isAvailable()){
                    Log.d(tag,"is online");
                    /*if(notifyProcessDialogInfo!=null){
                        notifyProcessDialogInfo.showInfo("已联网");
                    }*/
                    break;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(times == 6){
                    /*if(notifyOperateInfo!=null){
                        notifyOperateInfo.cancelDialog();
                    }*/
                }else{
                    times++;
                }
            }
           // Log.d(tag,"IP=V"+getIpAddressString()+"router ip ="+getWifiRouteIPAddress(context));
            //getAllIp();
            while ((!interrupted())&&(heartRun)){
                //Log.d(tag,"heartRun");
                if (connected){//已连接服务器

                    try {
                        if(!sendBuffer.isEmpty()){
                            byte[] buf = sendBuffer.poll();
                            send.write(buf);
                            send.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    if(heartRun) {
                        socketClient = new Socket();
                        receiverThread = new ReceiverThread();
                        receiverThread.start();
                        try {
                            Thread.sleep(9900);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public boolean send(byte[] buff){
        if(connected){
            sendBuffer.add(buff);
            return true;
        }else{
            return false;
        }
    }

}
