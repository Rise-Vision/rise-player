// Copyright © 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer.timers;

import java.util.Timer;
import java.util.TimerTask;

import com.risevision.riseplayer.DisplayErrors;

public class DisplayErrorsTimer {

	public static final long DISPLAYERROR_TIMER_INTERVAL_MS = 60 * 60 * 1000;
	
	static class OnTimerTask extends TimerTask {

		@Override
		public void run() {
			DisplayErrors.getInstance().reportDisplayErrorstoCore();
		}

	}

	private static Timer timer;

	public static void start() {
		timer = new Timer();
		
		timer.schedule(new OnTimerTask(), 0, DISPLAYERROR_TIMER_INTERVAL_MS);
	}
	
}
