// Copyright 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.risevision.riseplayer.Config;
import com.risevision.riseplayer.Globals;
import com.risevision.riseplayer.Log;
import com.risevision.riseplayer.externallogger.ExternalLogger;
import com.risevision.riseplayer.externallogger.InsertSchema;

public class WebServer {

    /* static class data/methods */
    protected enum RequestType {
        UNKNOWN, CONTENT, CONFIGURATION
    }

    /* print to stdout */
    protected static void p(String s) {
        System.out.println(s);
    }

    /* print to the log file */
    protected static void log(String s) {
        if (log == null) {
            p("logging to stdout");
            log = System.out;
        }

        synchronized (log) {
            log.println(s);
            log.flush();
            Log.info(s); // write to log file
        }
    }

    static PrintStream log = null;
    /*
     * our server's configuration information is stored in these properties
     */
    protected static Properties props = new Properties();

    /* Where worker threads stand idle */
    static Vector<Worker> threads = new Vector<Worker>();

    /* timeout on client connections */
    static int timeout = 0;

    /* timeout on client connections */
    static Vector<Integer> ports = new Vector<>();

    /* max # worker threads */
    static int workers = Globals.MAX_WORKERS;

    static void printProps() {
        p("app folder=" + Config.appPath);
        p("timeout=" + timeout);
        p("workers=" + workers);
    }

    public static void main(String[] a) throws Exception {

        //TODO: move this to Main.main()
        printProps();

		/* start worker threads */
        for (int i = 0; i < workers; ++i) {
            Worker w = new Worker();
            (new Thread(w, "worker #" + i)).start();
            threads.addElement(w);
        }

        ServerSocket ss = createServerSocket();//new ServerSocket(Config.basePort, -1,  InetAddress.getByName(null));

        log("Server started");
        ExternalLogger.logExternal(InsertSchema.withEvent("server started"));

        try {
            while (true) {

                Socket s = ss.accept();

                Worker w = null;
                synchronized (threads) {
                    if (threads.isEmpty()) {
                        Worker ws = new Worker();
                        ws.setSocket(s);
                        (new Thread(ws, "additional worker")).start();
                    } else {
                        w = (Worker) threads.elementAt(0);
                        threads.removeElementAt(0);
                        w.setSocket(s);
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            if (ss != null)
                ss.close();
        }
    }


    public static ServerSocket createServerSocket() throws Exception {
        //IMPORTANT! Specify local network address (InetAddress.getByName(null) is better option than just 127.0.0.1).
        //           Otherwise 0.0.0.0 IP will be used which triggers Windows Firewall popup.
        //use default backlog value
        return new ServerSocket(Config.basePort, -1, InetAddress.getByName(null));
    }

}
