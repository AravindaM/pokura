package org.pokura.zookeepertest;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.data.Stat;

public class TestDataCallBack implements DataCallback{

	
	public void processResult(int rc, String path, Object ctx, byte[] data,
			Stat stat) {
			TestWatcher.readCount += 2 ;
	
		
	}

}
