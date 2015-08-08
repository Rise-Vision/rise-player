// Copyright © 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer.timers;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.risevision.riseplayer.Config;
import com.risevision.riseplayer.utils.Utils;

public class RestartTimer {

	private static Timer timer = null;
	private static Date restartTime = null;
	
	static class OnTimerTask extends TimerTask {
		@Override
		public void run() {
			Utils.setFlag_ClearCacheAfterReboot();
			Utils.reboot();
		}
	}

	public static void restartIfTimeChanged() {
		if (restartTime==null || !restartTime.equals(Config.getRestartTime())) {
			stop();
			start();
		}
	}

	public static void start() {
		if (Config.getRestartTime() != null && Config.getRestartTime().after(new Date())) {
			timer = new Timer();
			timer.schedule(new OnTimerTask(), Config.getRestartTime());
		}
	}

	public static void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
