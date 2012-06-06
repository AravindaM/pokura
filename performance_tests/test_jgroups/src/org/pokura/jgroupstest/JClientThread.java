package org.pokura.jgroupstest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.FutureListener;
import org.jgroups.util.NotifyingFuture;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

public class JClientThread extends Thread {

	private int msgCount = 0;
	JChannel channel;
	RpcDispatcher disp;

	RspList rsp_list;
	int index;
	int totalReads = 0;
	int totalWrites = 0;

	// Create file
	FileWriter fstream;
	BufferedWriter out;

	public JClientThread(int index) {
		super();
		this.index = index;

		try {
			fstream = new FileWriter("Client_" + String.valueOf(index) + "_result.txt");
			out = new BufferedWriter(fstream);

			channel = new JChannel();
			channel.setName(Constantz.PREFIX_NAME_CLIENT + String.valueOf(index));
			channel.connect(Constantz.CLUSTER_NAME);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		super.run();

		try {
			eventLoop();
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <T> void eventLoop() throws Exception {

		RequestOptions readRequestOptions = getReadRequestOptions();
		RequestOptions writeRequestOptions = getWriteRequestOptions();

		MethodCall readMethodCall = getReadMethod();
		MethodCall writeMethodCall = getWriteMethod();

		Timer tick = new Timer();

		TimerTask checkStats = new TimerTask() {

			@Override
			public void run() {

				try {
					out.append("\n\nTestWrites :" + totalWrites);
					out.append("\nTestReads :" + totalReads);

					out.flush();
					fstream.flush();
				} catch (Exception e) {
					// System.err.println("Error: " + e.getMessage());
				}

				System.out.println("\nTestWrites :" + totalWrites);
				System.out.println("TestReads :" + totalReads);
				totalReads = 0;
				totalWrites = 0;
			}
		};

		tick.scheduleAtFixedRate(checkStats, 0, 1000);

		disp = new RpcDispatcher(channel, this);

		while (true) {

			boolean read = Util.tossWeightedCoin(Constantz.READ_PERCENTAGE);

			// Util.sleep(1);

			if (read) {
				NotifyingFuture<RspList<Object>> nf;
				nf = disp.callRemoteMethodWithFuture(selectServerAddress(index), readMethodCall,
						RequestOptions.SYNC());
				nf.setListener(new FutureListener<RspList<Object>>() {
					@Override
					public void futureDone(Future<RspList<Object>> future) {
						try {
							//System.out.println("result r :" + future.get());
							if(future.get() != null){
								++totalReads;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				NotifyingFuture<RspList<Object>> nf;
				nf = disp.callRemoteMethodsWithFuture(getServerList(), writeMethodCall, RequestOptions.SYNC());
				nf.setListener(new FutureListener<RspList<Object>>() {
					@Override
					public void futureDone(Future<RspList<Object>> future) {
						try {
							//System.out.println("result w :" + future.get());
							
							if(future.get().getFirst() !=null){
								++totalWrites;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				});
			}

		}

	}

	public ArrayList<Address> getServerList() {
		List<Address> allMembers = channel.getView().getMembers();
		ArrayList<Address> serverList = new ArrayList<Address>();

		for (int i = 0; i < allMembers.size(); i++) {
			String name = channel.getName(allMembers.get(i));
			if (name.contains(Constantz.PREFIX_NAME_SERVER)) {
				serverList.add(allMembers.get(i));
			}
		}

		return serverList;
	}

	public int getServerIndex(Address serverAddress) {
		String name = channel.getName(serverAddress);
		if (name.contains(Constantz.PREFIX_NAME_SERVER)) {
			return Integer.parseInt(name.split(",")[1]);
		} else {
			return 0;
		}
	}

	private Address selectServerAddress(int serverIndex) {
		List<Address> address = getServerList();

		int selectedIndex = serverIndex % Constantz.SERVER_COUNT;
		for (int i = 0; i < address.size(); i++) {
			if (selectedIndex == getServerIndex(address.get(i))) {
				return address.get(i);
			}
		}
		return address.get(0);
	}

	private MethodCall getReadMethod() {
		Object[] readArgs = { index };
		Class[] classes = { int.class };
		MethodCall call = new MethodCall("read", readArgs, classes);

		return call;
	}

	private MethodCall getWriteMethod() {
		Object[] readArgs = { index, new byte[Constantz.MESSAGE_SIZE] };
		Class[] classes = { int.class, byte[].class };
		MethodCall call = new MethodCall("write", readArgs, classes);

		return call;
	}

	private RequestOptions getReadRequestOptions() {
		RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_NONE, 1000);
		requestOptions.setFlags(Message.OOB);
		//requestOptions.setFlags(Message.DONT_BUNDLE, Message.NO_FC);
		return requestOptions;
	}

	private RequestOptions getWriteRequestOptions() {
		RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_NONE, 1000);
		requestOptions.setFlags(Message.OOB);
		requestOptions.setFlags(Message.DONT_BUNDLE, Message.NO_FC);
		return requestOptions;
	}

	public static void main(String[] args) {

		JClientThread[] ts;
		// first arg is starting no
		// second arg is no. of threads
		if (args != null && args.length >= 2) {
			ts = new JClientThread[Integer.parseInt(args[1])];
		} else {
			ts = new JClientThread[1];
		}

		int i;
		if (args != null && args.length >= 2) {
			i = Integer.parseInt(args[0]);
		} else {
			i = 0;
		}

		for (; i < ts.length; i++) {
			ts[i] = new JClientThread(i);
			ts[i].start();
		}

	}

}
