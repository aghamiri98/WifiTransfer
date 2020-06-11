

package com.aghamiri.wifitransfer.swiftp;

import android.util.Log;

import com.aghamiri.wifitransfer.service.FTPServerService;


public class MyLog {
	protected String tag;
	
	public MyLog(String tag) {
		this.tag = tag;
	}
	
	public void l(int level, String str, boolean sysOnly) {
		synchronized (MyLog.class) {
			str = str.trim();
			// Messages of this severity are handled specially
			if(level == Log.ERROR || level == Log.WARN) {
				Globals.setLastError(str);
			}
			if(level >= Defaults.getConsoleLogLevel()) {
				Log.println(level,tag, str);
			}
			if(!sysOnly) { // some messages only go to the Android log
				if(level >= Defaults.getUiLogLevel()) {
					FTPServerService.log(level, str);
				}
			}
		}
	}
	
	public void l(int level, String str) {
		l(level, str, false);
	}
	
	public void e(String s) {
		l(Log.ERROR, s, false);
	}
	public void w(String s) {
		l(Log.WARN, s, false);
	}
	public void i(String s) {
		l(Log.INFO, s, false);
	}
	public void d(String s) {
		l(Log.DEBUG, s, false);
	}
}
