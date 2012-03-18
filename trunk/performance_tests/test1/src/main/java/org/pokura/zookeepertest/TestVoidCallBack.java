package org.pokura.zookeepertest;

import org.apache.zookeeper.AsyncCallback.VoidCallback;

public class TestVoidCallBack implements VoidCallback{

	public void processResult(int rc, String path, Object ctx) {
		TestWatcher.writeCount++;
		
	}

}
