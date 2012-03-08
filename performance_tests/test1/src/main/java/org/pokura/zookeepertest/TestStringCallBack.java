package org.pokura.zookeepertest;

import org.apache.zookeeper.AsyncCallback.StringCallback;

public class TestStringCallBack implements StringCallback{
	
	public void processResult(int rc, String path, Object ctx, String name) {
		TestWatcher.writeCount++;
	}

}
