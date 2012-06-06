package org.pokura.jgroupstest;

import java.util.Timer;
import java.util.TimerTask;

public class Counter {
	private final Timer timer = new Timer();
	
	public static int readsCount;
	public static int writesCount;

	public Counter() {
		
	}

	public void start() {		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println("reads=" + readsCount );
				System.out.println("writes=" + writesCount );
				readsCount ++;
			}
		}, 0, 1000);
	}

	public static void main(String[] args) {
		Counter counter = new Counter();
		counter.start();
		
		
	}

}
