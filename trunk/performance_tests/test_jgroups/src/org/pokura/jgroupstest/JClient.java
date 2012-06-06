package org.pokura.jgroupstest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.FutureListener;
import org.jgroups.util.NotifyingFuture;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

public class JClient {

	private int msgCount = 0;
	JChannel channel;
	RpcDispatcher disp;
	// MessageDispatcher disp1;
	RspList rsp_list;
	int index;
	int totalReads = 0;
	int totalWrites = 0;

	// Create file
	FileWriter fstream;
	BufferedWriter out;

	public JClient(int index) {
		super();
		this.index = index;

		FileWriter fstream;
		try {
			fstream = new FileWriter("Client_"+ String.valueOf(index) +"_result.txt");
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void start() throws Exception {
		channel = new JChannel();
		channel.setName(Constantz.PREFIX_NAME_CLIENT + String.valueOf(index));
		channel.connect(Constantz.CLUSTER_NAME);
		eventLoop();
		channel.close();
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
				} catch (Exception e) {// Catch exception if any
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
				// System.out.println("READ");
				NotifyingFuture<T> future;
				try {
					future = disp.callRemoteMethodWithFuture(selectServerAddress(index), readMethodCall,
							readRequestOptions);
					future.setListener(new FutureListener() {

						public void futureDone(Future future) {
							++totalReads;
						}

					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// System.out.println("WRITE");
				NotifyingFuture<T> future;
				try {
					future = (NotifyingFuture<T>) disp.callRemoteMethodWithFuture(selectServerAddress(index),
							writeMethodCall, writeRequestOptions);
					future.setListener(new FutureListener() {

						public void futureDone(Future future) {
							// System.out.println("writes=" +
							// String.valueOf(++totalWrites));
							++totalWrites;
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	private <T> void doRead() {

		MethodCall call = getReadMethod();
		RequestOptions opts = getReadRequestOptions();
		disp = new RpcDispatcher(channel, this);
		call.setArgs(index);

		try {
			while (true) {
				System.out.println("Helllooooooo");
				rsp_list = disp.callRemoteMethod(selectServerAddress(index), call, opts);
				++totalReads;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Address> getServerList(List<Address> allMembers) {
		List<Address> serverList = new ArrayList<Address>();

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
			return -1;
		}
	}

	private Address selectServerAddress(int serverIndex) {
		List<Address> address = getServerList(channel.getView().getMembers());

		int selectedIndex = serverIndex % Constantz.SERVER_COUNT;
		for (int i = 0; i < address.size(); i++) {
			if (selectedIndex == getServerIndex(address.get(i))) {
				return address.get(i);
			}
		}
		return address.get(0);
	}

	private MethodCall getReadMethod() {
		try {
			MethodCall call = new MethodCall(JServer.class.getMethod("read", int.class));
			return call;
		} catch (SecurityException e) {
			System.out.println("Cannot access method");
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			System.out.println("Cannot find method");
			e.printStackTrace();
			return null;
		}

	}

	private MethodCall getWriteMethod() {
		try {
			MethodCall call = new MethodCall(JServer.class.getMethod("write", long.class, byte[].class));
			return call;
		} catch (SecurityException e) {
			System.out.println("Cannot access method");
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			System.out.println("Cannot find method");
			e.printStackTrace();
			return null;
		}
	}

	private RequestOptions getReadRequestOptions() {
		RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_NONE, 1000);
		requestOptions.setFlags(Message.OOB);
		requestOptions.setFlags(Message.DONT_BUNDLE, Message.NO_FC);
		return requestOptions;
	}

	private RequestOptions getWriteRequestOptions() {
		RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_NONE, 1000);
		requestOptions.setFlags(Message.OOB);
		requestOptions.setFlags(Message.DONT_BUNDLE, Message.NO_FC);
		return requestOptions;
	}

	public static void main(String[] args) {
		JClient jclient;

		if (args != null && args.length > 0) {
			jclient = new JClient(Integer.parseInt(args[0]));
		} else {
			jclient = new JClient(0);
		}
		
		try {
			jclient.start();
		} catch (Exception e) {
			System.out.println("Cannot start client");
			e.printStackTrace();
		}

		
	}

}
