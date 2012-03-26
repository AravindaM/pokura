package org.pokura.zookeepertest;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.data.Stat;

public class TestStatCallBack implements StatCallback{

	public void processResult(int rc, String path, Object ctx, Stat stat) {
		
	}

}
