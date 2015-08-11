// Copyright © 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.riseplayer;

public class DisplayError {

	public int code;
	public int occourances;
	
	public DisplayError(int code, int occourances) {
		this.code = code;
		this.occourances = occourances;
	}
	public boolean compareTo(DisplayError e) { return (this.code == e.code ?  true: false); }

}
