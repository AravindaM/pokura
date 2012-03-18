package org.pokura.zookeepertest;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ClientReader implements Runnable, Watcher, DataCallback,StatCallback{

	ZooKeeper client;
	TestDataCallBack tDataCallBack;
	TestStatCallBack tStatCallBack;
	String path;
	int resettercount = 0;
	

	public void run() {
		shortRunTest();
		
	}
	
	public void shortRunTest(){
		
		
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
		
			readData(client,true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public void readData(ZooKeeper client,boolean isFirst){
		int count = 0;
		if(isFirst){
			while(count <= 40000){
				int c = count%1000;
				client.exists(path+c, false, this, null);
				client.getData(path+c, false, this, null);
				count++;
			
			}
		}else{
			while(count <= 30000){
				int c = count%1000;
				client.exists(path+c, false, this, null);
				client.getData(path+c, false, this, null);
				count++;
			
			}
			
		}
	}

	private void longRunTest(){
		
	}
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void processResult(int rc, String path, Object ctx, Stat stat) {
		TestWatcher.readCount++;
		this.resettercount++;
		if(resettercount==30000){
			readData(client,false);
			resettercount = 0;
		}
		
	}

	public void processResult(int rc, String path, Object ctx, byte[] data,
			Stat stat) {
		TestWatcher.readCount++;
		
	}
}
