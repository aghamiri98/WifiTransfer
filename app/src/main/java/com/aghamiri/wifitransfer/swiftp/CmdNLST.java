

/* The code that is common to LIST and NLST is implemented in the abstract
 * class CmdAbstractListing, which is inherited here. 
 * CmdLIST and CmdNLST just override the
 * makeLsString() function in different ways to provide the different forms
 * of output.
 */

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

import java.io.File;

public class CmdNLST extends CmdAbstractListing implements Runnable {
	// The approximate number of milliseconds in 6 months
	public final static long MS_IN_SIX_MONTHS = 6 * 30 * 24 * 60 * 60 * 1000; 
	private String input;
	
	
	public CmdNLST(SessionThread sessionThread, String input) {
		super(sessionThread, input);
		this.input = input;
	}
	
	public void run() {
		String errString = null;
		
		mainblock: {
			String param = getParameter(input);
			if(param.startsWith("-")) {
				// Ignore options to list, which start with a dash
				param = "";
			}
			File fileToList = null;
			if(param.equals("")) {
				fileToList = sessionThread.getWorkingDir();
			} else {
				if(param.contains("*")) {
					errString = "550 NLST does not support wildcards\r\n";
					break mainblock;
				}
				fileToList = new File(sessionThread.getWorkingDir(), param);
				if(violatesChroot(fileToList)) {
					errString = "450 Listing target violates chroot\r\n";
					break mainblock;
				} else if(fileToList.isFile()) {
					// Bernstein suggests that NLST should fail when a 
					// parameter is given and the parameter names a regular 
					// file (not a directory).
					errString = "550 NLST for regular files is unsupported\r\n";
					break mainblock;
				}				
			}
			String listing;
			if(fileToList.isDirectory()) {
				StringBuilder response = new StringBuilder();
				errString = listDirectory(response, fileToList);
				if(errString != null) {
					break mainblock;
				}
				listing = response.toString();
			} else {
				listing = makeLsString(fileToList);
				if(listing == null) {
					errString = "450 Couldn't list that file\r\n";
					break mainblock;
				}
			}
			errString = sendListing(listing);
			if(errString != null) {
				break mainblock;
			}
		}	
		
		if(errString != null) {
			sessionThread.writeString(errString);
			myLog.l(Log.DEBUG, "NLST failed with: " + errString);
		} else {
			myLog.l(Log.DEBUG, "NLST completed OK");
		}
		// The success or error response over the control connection will
		// have already been handled by sendListing, so we can just quit now.
	}
	
	protected String makeLsString(File file) {
		if(!file.exists()) {
			staticLog.l(Log.INFO, "makeLsString had nonexistent file");
			return null;
		}

		// See Daniel Bernstein's explanation of NLST format at:
		// http://cr.yp.to/ftp/list/binls.html
		// This stuff is almost entirely based on his recommendations.
		
		String lastNamePart = file.getName();
		// Many clients can't handle files containing these symbols
		if(lastNamePart.contains("*") || 
		   lastNamePart.contains("/"))
		{
			staticLog.l(Log.INFO, "Filename omitted due to disallowed character");
			return null;
		} else {
			staticLog.l(Log.DEBUG, "Filename: " + lastNamePart );
			return lastNamePart + "\r\n";
		}
	}
}
