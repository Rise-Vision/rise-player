// Copyright © 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer.utils;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class WindowUtils {

	public static int[] getExtendedScreenSize() {
		GraphicsEnvironment ge = GraphicsEnvironment.
		getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		int height = 0;
        int width = 0;
		for (int j = 0; j < gs.length; j++) { 
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc =
	        gd.getConfigurations();
			for (int i=0; i < gc.length; i++) { 
				Rectangle gcBounds = gc[i].getBounds();
				height+= gcBounds.height;
				width+= gcBounds.width;
			}
		}
		int[] ret = new int[2];
		ret[0] = width;
		ret[1] = height;
		return ret;
	}
}
