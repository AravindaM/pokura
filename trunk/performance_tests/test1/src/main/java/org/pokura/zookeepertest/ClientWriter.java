package org.pokura.zookeepertest;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.ZooDefs.Ids;

public class ClientWriter implements Runnable, Watcher, StringCallback{
	
	ZooKeeper client;
	TestVoidCallBack tVoidCallBack;
	String path;
	int resetterCount = 0;
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
	byte [] databytes;
	long timePeriod = 1000*60*1;
	long startTime ;
	long currentTime;

	public void run() {
	
		startTime = System.currentTimeMillis();		
		shortRunTest();
				
	}

	private void shortRunTest(){
		
		tVoidCallBack = new TestVoidCallBack();
		
		try {
			
			CreateMode cm =CreateMode.EPHEMERAL_SEQUENTIAL;
		    databytes = oneKbString.getBytes("UTF-16LE");
			
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
			
			writeData(client, true, cm);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void longRunTest(){
		
	}
	
	public void writeData(ZooKeeper client,boolean isFirst, CreateMode cm){
		int count = 0;
		boolean isShown = false;
		int c = 0;
		startTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
		boolean create = true;
			while((currentTime-startTime) < timePeriod){
				
				if(((currentTime - startTime)%5000 ) < 1000 && !isShown){
                    System.out.println((currentTime-startTime)+"TestReads :"+count);
                    count = 0;
                    isShown = true;

	            }
	            
	            if(((currentTime - startTime)%5000 ) > 1000){
	                    isShown = false;
	            }
				if (create){
					client.create("/testwrite/data"+c,databytes, Ids.OPEN_ACL_UNSAFE, cm,null,null);
					if(c == 30000){
						create = false;
						c--;
					}
					c++;
				}else{
					client.delete("/testwrite/data"+c, 0, null, null);
					if ( c == 100){
						create = true;
						c++;
					}
					c--;
				}
				
				count++;
				c++; 
				currentTime = System.currentTimeMillis();
				
			
			}
	}
	public void deleteData(ZooKeeper client,String path, int version){
		
		client.delete(path, version, tVoidCallBack, null);
		
	}
	public void dataWriter(){
		
		
	}
	public void process(WatchedEvent event) {
		
		
	}

	public void processResult(int rc, String path, Object ctx, String name) {
		
		CreateMode cm =CreateMode.EPHEMERAL_SEQUENTIAL;
		this.resetterCount++;
		TestWatcher.writeCount++;
		deleteData(client,name,0);
		if(this.resetterCount == 15000){
			writeData(client, false, cm);
			this.resetterCount = 0;
		}
		
	}

	
}
