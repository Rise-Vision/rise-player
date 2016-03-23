// Copyright 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.risevision.riseplayer.Config;

public class SystemInfo {
    private static final String NETWORK_CHECK_URL = "www.google.com";

    private static boolean isInitialized = false;
    private static String os_name = null;
    private static String player_name = null;
    private static String installer_version = null;
    private static String java_version = null;
    private static String risePlayer_version = null;
    private static String riseCache_version = null;
    private static String main_ip_address = null;

    public static String asUrlParam(boolean encode) {
        init();

        StringBuilder sb = new StringBuilder();
        sb.append("os=" + os_name);
        sb.append("&iv=" + installer_version);
        if (java_version != null && !java_version.isEmpty()) {
            sb.append("&jv=" + java_version);
        }
        sb.append("&pn=" + player_name);
        sb.append("&pv=" + risePlayer_version);
        sb.append("&ev=" + riseCache_version);

        if (main_ip_address != null && !main_ip_address.isEmpty()) {
            sb.append("&ip=" + main_ip_address);
        }

        String res = sb.toString();

        if (encode) {
            try {
                res = URLEncoder.encode(sb.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                res = "";
            }
        }

        return res;
    }

    public static void init() {
        if (!isInitialized) {
            isInitialized = true;
            os_name = Config.playerOS;
            player_name = Config.getPlayerName();
            installer_version = readVersion("installer.ver");
            java_version = readVersion("java.ver");
            risePlayer_version = readVersion("RisePlayer.ver");
            riseCache_version = readVersion("RiseCache.ver");
            main_ip_address = getMainNetworkInterfaceIP();
        }
    }

    private static String readVersion(String fileName) {
        String res = "";
        File file = new File(Config.currVersionPath, fileName);
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                if (lines != null && lines.size() > 0)
                    res = lines.get(0);
            } catch (IOException e) {
            }
        }
        return res;
    }

    public static String getMainNetworkInterfaceIP() {
        try {
            Socket socket = new Socket(NETWORK_CHECK_URL, 80);
            InetAddress boundAddress;

            socket.setSoTimeout(3000);
            boundAddress = socket.getLocalAddress();

            socket.close();

            return boundAddress.getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }
}
