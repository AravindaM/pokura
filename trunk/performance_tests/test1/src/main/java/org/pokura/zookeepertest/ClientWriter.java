package org.pokura.zookeepertest;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.ZooDefs.Ids;

public class ClientWriter implements Runnable, Watcher{
	
	ZooKeeper client;
	TestStringCallBack tStringCallBack;
	String path;
	String oneKbString = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

	public void run() {
	
				
		shortRunTest();
				
	}

	private void shortRunTest(){
		
		int count = 0;
		tStringCallBack = new TestStringCallBack();
		
		try {
			
			CreateMode cm =CreateMode.EPHEMERAL_SEQUENTIAL;
			byte [] databytes = oneKbString.getBytes("UTF-16LE");
			
			String threadName = Thread.currentThread().getName();
			int threadId = Integer.valueOf(threadName);
			
			switch (threadId) {
			case 1: client = new ZooKeeper("localhost:2181", 5000, this); 
				break;
			case 2:client = new ZooKeeper("localhost:2182", 5000, this);
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
		
			while(count <= 40000){
				
				client.create("/supun2/data",databytes, Ids.OPEN_ACL_UNSAFE, cm,tStringCallBack,null);
			
				count++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void longRunTest(){
		
	}

	public void process(WatchedEvent event) {
		
		
	}

	public void processResult(int rc, String path, Object ctx, String name) {
		
		
	}

	
}
