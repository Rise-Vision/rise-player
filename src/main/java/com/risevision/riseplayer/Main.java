// Copyright 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer;

import java.net.BindException;

import com.risevision.riseplayer.server.WebServer;
import com.risevision.riseplayer.timers.DisplayErrorsTimer;
import com.risevision.riseplayer.timers.HeartbeatTimer;
import com.risevision.riseplayer.utils.Utils;
import com.risevision.riseplayer.externallogger.*;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Config.init(Main.class);
        Log.init(Config.appPath, Globals.APPLICATION_NAME);
        Log.info("***** " + Globals.APPLICATION_NAME + " version " + Globals.APPLICATION_VERSION + " *****");
        Config.loadApplicationProperties();
        Config.loadDisplayProperties();
        DisplayErrors.getInstance().loadErrorsFromFile();

        try {
            ExternalLogger.logExternal(InsertSchema.withEvent("startup"));

            //use socket to test if another instance is running
            java.net.ServerSocket ss = WebServer.createServerSocket();// new java.net.ServerSocket(Config.basePort);

            ss.close();

            if (!Config.viewerHeartBeatOverride.equals("true")) {
                HeartbeatTimer.start();
            }

            DisplayErrorsTimer.start();

            Utils.startViewer();

            WebServer.main(args);

            //kill all "chrome.exe" processes when player shuts down?
            Utils.stopViewer();

            DisplayErrors.getInstance().writeErrorsToFile();

        } catch (BindException e) {
            Log.error("Cannot start application. Cannot open port " + Config.basePort + ". You can only run one instance of " + Globals.APPLICATION_NAME + ".");
            ExternalLogger.logExternal(InsertSchema.withEvent("startup failed bind"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.error(e.getMessage());
            ExternalLogger.logExternal(InsertSchema.withEvent("application crash", e.getMessage()));
        }

    }

}
