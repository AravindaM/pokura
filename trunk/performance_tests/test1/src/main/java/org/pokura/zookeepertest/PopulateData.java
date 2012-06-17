package org.pokura.zookeepertest;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class PopulateData implements Watcher {

	public static void main(String[] args ){
	
		PopulateData popd = new PopulateData();
		popd.populateData();
	    
	}

	public void populateData(){
		
		CreateMode cm =CreateMode.PERSISTENT;
	    byte [] databytes = new byte[1024];
	    int count = 0;
	    try {
			ZooKeeper client = new ZooKeeper("localhost:2181", 5000, this);
			
				client.create("/testdata", databytes, Ids.OPEN_ACL_UNSAFE, cm);
				client.create("/testwrite", databytes, Ids.OPEN_ACL_UNSAFE, cm);

			while(count <= 10000){
				
				client.create("/testdata/data"+count,databytes, Ids.OPEN_ACL_UNSAFE, cm);
				count++;
			
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	}
	public void process(WatchedEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}