package org.pokura.jgroupstest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MethodLookup;
import org.jgroups.blocks.RpcDispatcher;

public class JServer extends ReceiverAdapter 
{

	static JChannel channel;
	RpcDispatcher      disp;
	private byte[] responseBytes;
	int index;

	public JServer(int index) {
		super();
		this.index = index;
		responseBytes = new byte[Constantz.MESSAGE_SIZE];
	}

	
	@Override
	public void viewAccepted(View view) {
		super.viewAccepted(view);
		System.out.println("** view: " + view);
	}
	

	@Override
	public void receive(Message msg) {
		super.receive(msg);
		//System.out.println(msg);
		System.out.println(msg.getSrc() + " " +msg.getLength());
	}
	

	private void start() throws Exception {
		channel = new JChannel();
		channel.setReceiver(this);
		channel.setName(Constantz.PREFIX_NAME_SERVER + String.valueOf(index));
		channel.connect(Constantz.CLUSTER_NAME);
		
		 disp=new RpcDispatcher(channel,this);
		
		eventLoop();
		channel.close();
	}
	
	public byte[] read(int clientIndex) throws Exception {
		System.out.println("Client " + clientIndex + " invoked read");
		return responseBytes;
    }

	public boolean write(int clientIndex, byte[] value) throws Exception {
		responseBytes = value;
		System.out.println("Client " + clientIndex + " invoked write with " + value.length + " bytes");
		return true;
	}

	private void eventLoop() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				System.out.print("Enter q or quit to exit: ");
				System.out.flush();
				String line = in.readLine().toLowerCase();
				if (line.startsWith("quit") || line.startsWith("q")) {
					break;
				}
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		
		JServer jserver;
		
		if(args != null && args.length > 0){
			jserver = new JServer(Integer.parseInt(args[0]));
		}else{
			jserver = new JServer(0);
		}
		
		
		try {
			jserver.start();
		} catch (Exception e) {
			System.out.println("Cannot start server");
			e.printStackTrace();
		}
	}

	

}
