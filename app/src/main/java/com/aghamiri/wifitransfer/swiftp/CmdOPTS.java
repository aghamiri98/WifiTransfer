

package com.aghamiri.wifitransfer.swiftp;


public class CmdOPTS extends FtpCmd implements Runnable {
	public static final String message = "TEMPLATE!!";
	private String input;
	
	public CmdOPTS(SessionThread sessionThread, String input) {
		super(sessionThread, CmdOPTS.class.toString());
		this.input = input;
	}
	
	public void run() {
		String param = getParameter(input);
		String errString = null;
		
		mainBlock: {
			if(param == null) {
				errString = "550 Need argument to OPTS\r\n";
				myLog.w("Couldn't understand empty OPTS command");
				break mainBlock;
			}
			String[] splits = param.split(" ");
			if(splits.length != 2) {
				errString = "550 Malformed OPTS command\r\n";
				myLog.w("Couldn't parse OPTS command");
				break mainBlock;
			}
			String optName = splits[0].toUpperCase();
			String optVal = splits[1].toUpperCase();
			if(optName.equals("UTF8")) {
				// OK, whatever. Don't really know what to do here. We
				// always operate in UTF8 mode.
				if(optVal.equals("ON")) {
					myLog.d("Got OPTS UTF8 ON");
					sessionThread.setEncoding("UTF-8");
				} else {
					myLog.i("Ignoring OPTS UTF8 for something besides ON");
				}
				break mainBlock;
			} else {
				myLog.d("Unrecognized OPTS option: " + optName);
				errString = "502 Unrecognized option\r\n";
				break mainBlock;
			}
		}
		if(errString != null) {
			sessionThread.writeString(errString);
			myLog.i("Template log message");
		} else {
			sessionThread.writeString("200 OPTS accepted\r\n");
			myLog.d("Handled OPTS ok");
		}
	}

}
