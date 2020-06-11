

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

public class CmdUSER extends FtpCmd implements Runnable {
	protected String input;
	
	public CmdUSER(SessionThread sessionThread, String input) {
		super(sessionThread, CmdUSER.class.toString());
		this.input = input;
		
	}
	
	public void run() {
		myLog.l(Log.DEBUG, "USER executing");
		String username = getParameter(input);
		if(!username.matches("[A-Za-z0-9]+")) { 
			sessionThread.writeString("530 Invalid username\r\n");
			return;
		}
		sessionThread.writeString("331 Send password\r\n");
		sessionThread.account.setUsername(username);
	}

}
