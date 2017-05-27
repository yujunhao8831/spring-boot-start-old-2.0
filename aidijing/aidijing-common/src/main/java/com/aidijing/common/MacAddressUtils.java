package com.aidijing.common;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author : 披荆斩棘
 * @date : 2016/12/10
 */
public class MacAddressUtils {

    public static String getLocalIP () {
        String ip = "";
        try {
            Enumeration< NetworkInterface > networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while ( networkInterfaces.hasMoreElements() ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if ( ! networkInterface.getName().equals( "eth0" ) ) {
                    continue;
                } else {
                    Enumeration< InetAddress > inetAddresses = networkInterface.getInetAddresses();
                    while ( inetAddresses.hasMoreElements() ) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if ( inetAddress instanceof Inet6Address )
                            continue;
                        ip = inetAddress.getHostAddress();
                    }
                    break;
                }
            }
        } catch ( SocketException e ) {
            LogUtils.getLogger().catching( e );
            if ( LogUtils.getLogger().isDebugEnabled() ) {
                LogUtils.getLogger().debug( MacAddressUtils.class.getSimpleName(), e );
            }
        }
        return ip;
    }

}





