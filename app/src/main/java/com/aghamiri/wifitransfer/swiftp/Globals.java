

package com.aghamiri.wifitransfer.swiftp;

import android.content.Context;

import java.io.File;

public class Globals {
    private static Context context;
    private static String lastError;
    private static File chrootDir = new File(Defaults.chrootDir);
    private static ProxyConnector proxyConnector = null;
    private static String username = null;

    public static ProxyConnector getProxyConnector() {
        if(proxyConnector != null) {
            if(!proxyConnector.isAlive()) {
                return null;
            }
        }
        return proxyConnector;
    }

    public static void setProxyConnector(ProxyConnector proxyConnector) {
        Globals.proxyConnector = proxyConnector;
    }

    public static File getChrootDir() {
        return chrootDir;
    }

    public static void setChrootDir(File chrootDir) {
        if(chrootDir.isDirectory()) {
            Globals.chrootDir = chrootDir;
        }
    }

    public static String getLastError() {
        return lastError;
    }

    public static void setLastError(String lastError) {
        Globals.lastError = lastError;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        if(context != null) {
            Globals.context = context;
        }
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Globals.username = username;
    }

}
