

/*
 * Since the FTP verbs LIST and NLST do very similar things related to listing
 * directory contents, the common tasks that they share have been factored
 * out into this abstract class. Both CmdLIST and CmdNLST inherit from this
 * class. 
 */

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

import java.io.File;

public abstract class CmdAbstractListing extends FtpCmd {
	protected static MyLog staticLog = new MyLog(CmdLIST.class.toString());
	
	public CmdAbstractListing(SessionThread sessionThread, String input) {
		super(sessionThread, CmdAbstractListing.class.toString());
	}
	
	abstract String makeLsString(File file);
	
	// Creates a directory listing by finding the contents of the directory,
	// calling makeLsString on each file, and concatenating the results.
	// Returns an error string if failure, returns null on success. May be
	// called by CmdLIST or CmdNLST, since they each override makeLsString
	// in a different way.
	public String listDirectory(StringBuilder response, File dir) {
		if(!dir.isDirectory()) {
			return "500 Internal error, listDirectory on non-directory\r\n";
		}
		myLog.l(Log.DEBUG, "Listing directory: " + dir.toString());
		
		// Get a listing of all files and directories in the path
		File[] entries = dir.listFiles();
		if(entries == null) {
			return "500 Couldn't list directory. Check config and mount status.\r\n";
		}
		myLog.l(Log.DEBUG, "Dir len " + entries.length);
		for(File entry : entries) {
			String curLine = makeLsString(entry);
			if(curLine != null) {
				response.append(curLine);
			}
		}
		return null;
	}

	// Send the directory listing over the data socket. Used by CmdLIST and
	// CmdNLST.
	// Returns an error string on failure, or returns null if successful.
	protected String sendListing(String listing) {
		if(sessionThread.startUsingDataSocket()) {
			myLog.l(Log.DEBUG, "LIST/NLST done making socket");
		} else {
			sessionThread.closeDataSocket();
			return "425 Error opening data socket\r\n";
		}
		String mode = sessionThread.isBinaryMode() ? "BINARY" : "ASCII";
		sessionThread.writeString(
				"150 Opening "+mode+" mode data connection for file list\r\n");
		myLog.l(Log.DEBUG, "Sent code 150, sending listing string now");
		if(!sessionThread.sendViaDataSocket(listing)) {
			myLog.l(Log.DEBUG, "sendViaDataSocket failure");
			sessionThread.closeDataSocket();
			return "426 Data socket or network error\r\n";
		}
		sessionThread.closeDataSocket();
		myLog.l(Log.DEBUG, "Listing sendViaDataSocket success");
		sessionThread.writeString("226 Data transmission OK\r\n");
		return null;
	}
}
