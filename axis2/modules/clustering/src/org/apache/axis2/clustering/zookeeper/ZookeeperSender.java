package org.apache.axis2.clustering.zookeeper;

import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.MessageSender;
import org.apache.axis2.clustering.tribes.MembershipManager;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperSender implements MessageSender{
	
	private MembershipManager membershipManager;
	private byte[] domain;
	
	public ZookeeperSender(MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}
	
	public void sendToGroup(ClusteringCommand msg) throws ClusteringFault {
		domain = membershipManager.getDomain();		
		String domainName = new String(domain);
		
		ZookeeperUtils.createCommandZNode(msg, domainName);
		
	}

	public void sendToSelf(ClusteringCommand msg) throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

}
