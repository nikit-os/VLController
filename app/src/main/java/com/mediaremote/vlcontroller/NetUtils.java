package com.mediaremote.vlcontroller;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 23/06/15.
 */
public class NetUtils {
    public static final String TAG = NetUtils.class.toString();

    public static List<InetAddress> getIpRangeFromMask(int ipAddress, int mask) {
        List<InetAddress> inetAddressList = new ArrayList<>();

        int startIpAddress = ipAddress & mask;
        byte[] startIpAddressBytes = { (byte)(0xff & startIpAddress),
                (byte)(0xff & (startIpAddress >> 8)),
                (byte)(0xff & (startIpAddress >> 16)),
                (byte)(0xff & (startIpAddress >> 24)) };

        try {
            for (; startIpAddressBytes[3] != -1; startIpAddressBytes[3]++) {
                InetAddress address = InetAddress.getByAddress(startIpAddressBytes);
                inetAddressList.add(address);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        return inetAddressList;
    }
}

