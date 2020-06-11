

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class CmdCWD extends FtpCmd implements Runnable {
	protected String input;
	
	public CmdCWD(SessionThread sessionThread, String input) {
		super(sessionThread, CmdCWD.class.toString());
		this.input = input;
	}
	
	public void run() {
		myLog.l(Log.DEBUG, "CWD executing");
		String param = getParameter(input);
		File newDir;
		String errString = null;
		mainblock: {
			newDir = inputPathToChrootedFile(sessionThread.getWorkingDir(), param);

			// Ensure the new path does not violate the chroot restriction
			if(violatesChroot(newDir)) {
				errString = "550 Invalid name or chroot violation\r\n";
				sessionThread.writeString(errString);
				myLog.l(Log.INFO, errString);
				break mainblock;
			}

			try {
				newDir = newDir.getCanonicalFile();
				if(!newDir.isDirectory()) {
					sessionThread.writeString("550 Can't CWD to invalid directory\r\n");
				} else if(newDir.canRead()) {
					sessionThread.setWorkingDir(newDir);
					sessionThread.writeString("250 CWD successful\r\n");
				} else {
					sessionThread.writeString("550 That path is inaccessible\r\n");
				}
			} catch(IOException e) {
				sessionThread.writeString("550 Invalid path\r\n");
				break mainblock;
			}
		}
		myLog.l(Log.DEBUG, "CWD complete");
	}
}
