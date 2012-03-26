package org.pokura.zookeepertest;

import java.util.Timer;
import java.util.TimerTask;

public class ZookeeperPerfoemanceTest {

	public static void main(String[] args) {
		ClientWriter clientw = new ClientWriter();
		ClientReader clientr = new ClientReader();
		TestWatcher watcher = new TestWatcher();
		Thread clientThreadw1 = new Thread(clientw,"1");
		Thread clientThreadw2 = new Thread(clientw,"2");
		Thread clientThreadr1 = new Thread(clientr,"1");
		Thread clientThreadr2 = new Thread(clientr,"2");
		Thread watcherThread = new Thread(watcher, "watcher1");
		clientThreadw1.start();
		//clientThreadw2.start();
		//clientThreadr1.start();
		//clientThreadr2.start();
		//watcherThread.start();
		

	}

	

}
