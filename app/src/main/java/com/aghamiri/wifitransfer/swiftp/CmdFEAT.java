

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

public class CmdFEAT extends FtpCmd implements Runnable {
	public static final String message = "TEMPLATE!!"; 
	
	public CmdFEAT(SessionThread sessionThread, String input) {
		super(sessionThread, CmdFEAT.class.toString());
	}
	
	public void run() {
		//sessionThread.writeString("211 No extended features\r\n");
		sessionThread.writeString("211-Features supported\r\n");
		sessionThread.writeString(" UTF8\r\n"); // advertise UTF8 support (fixes bug 14)
		sessionThread.writeString("211 End\r\n");
		myLog.l(Log.DEBUG, "Gave FEAT response");
	}

}
