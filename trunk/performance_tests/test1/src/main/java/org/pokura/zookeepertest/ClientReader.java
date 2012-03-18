package org.pokura.zookeepertest;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ClientReader implements Runnable, Watcher{

	ZooKeeper client;
	TestDataCallBack tDataCallBack;
	TestStatCallBack tStatCallBack;
	String path;
	

	public void run() {
		shortRunTest();
		
	}
	
	public void shortRunTest(){
		int count = 0;
		String path = "";
		tDataCallBack = new TestDataCallBack();
		tStatCallBack = new  TestStatCallBack();
		try {
			
			String threadName = Thread.currentThread().getName();
			int threadId = Integer.valueOf(threadName);
			
			switch (threadId) {
			case 1:{ client = new ZooKeeper("localhost:2181", 5000, this); 
				path = "/supun1/thread1";
			}
				break;
			case 2:{ client = new ZooKeeper("localhost:2182", 5000, this);
				path = "/supun/thread1";
			}
				break;
			case 3:client = new ZooKeeper("localhost:2183", 5000, this);
				break;
			case 4:client = new ZooKeeper("localhost:2184", 5000, this);
				break;
			case 5:client = new ZooKeeper("localhost:2185", 5000, this);
				break;
			case 6:client = new ZooKeeper("localhost:2186", 5000, this);
				break;
			case 7:client = new ZooKeeper("localhost:2187", 5000, this);
				break;

			default:
				break;
			}
		   long s = 40000;
			while(count <= 40000){
				int c = count%1000;
				client.exists(path+c, false, tStatCallBack, null);
				client.getData(path+c, false, tDataCallBack, null);
				count++;
			
			}
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	private void longRunTest(){
		
	}
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
}
