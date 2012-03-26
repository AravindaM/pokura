package org.pokura.zookeepertest;

import java.util.Timer;
import java.util.TimerTask;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class TestWatcher implements Runnable {
	
	public static int readCount = 0;
	public static int writeCount = 0;
	public static boolean istestActive = true;
	public long testTime = 1000*60*1;
	
	public void run() {
		startWatcher();

	}
	
	private void startWatcher(){
		Timer tick1 = new Timer();
		
		TimerTask checkStats = new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("TestWrites :"+TestWatcher.writeCount);
				System.out.println("TestReads :"+TestWatcher.readCount);
				TestWatcher.readCount = 0;
				TestWatcher.writeCount = 0;
				
			}
		};
		
		
		
	//	tick1.scheduleAtFixedRate(checkStats, 0, 1000);
		
	}

	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}


}
