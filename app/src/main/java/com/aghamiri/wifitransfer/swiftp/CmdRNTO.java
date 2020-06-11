

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

import java.io.File;

public class CmdRNTO extends FtpCmd implements Runnable {
	protected String input;

	public CmdRNTO(SessionThread sessionThread, String input) {
		super(sessionThread, CmdRNTO.class.toString());
		this.input = input;
	}
	
	public void run() {
		String param = getParameter(input);
		String errString = null;
		File toFile = null;
		myLog.l(Log.DEBUG, "RNTO executing\r\n");
		mainblock: {
			myLog.l(Log.INFO, "param: " + param); 
			toFile = inputPathToChrootedFile(sessionThread.getWorkingDir(), param);
			myLog.l(Log.INFO, "RNTO parsed: " + toFile.getPath());
			if(violatesChroot(toFile)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			}
			File fromFile = sessionThread.getRenameFrom();
			if(fromFile == null) {
				errString = "550 Rename error, maybe RNFR not sent\r\n";
				break mainblock;
			}
			if(!fromFile.renameTo(toFile)) {
				errString = "550 Error during rename operation\r\n";
				break mainblock;
			}
		}
		if(errString != null) {
			sessionThread.writeString(errString);
			myLog.l(Log.INFO, "RNFR failed: " + errString.trim());
		} else {
			sessionThread.writeString("250 rename successful\r\n");
		}
		sessionThread.setRenameFrom(null);
		myLog.l(Log.DEBUG, "RNTO finished");
	}
}
