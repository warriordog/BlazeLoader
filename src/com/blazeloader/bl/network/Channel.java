package com.blazeloader.bl.network;

import com.blazeloader.bl.main.BLMain;

public enum Channel {
	PARTICLES;
	
	public String getChannelIdentifier() {
		return BLMain.instance().getPluginChannelName() + ":" + toString();
	}
}
