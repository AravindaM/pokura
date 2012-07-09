package org.apache.axis2.clustering.zookeeper;

import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.MessageSender;
import org.apache.axis2.clustering.tribes.MembershipManager;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperSender implements MessageSender{
	
	private ZookeeperUtils zookeeperUtils;
	
	private MembershipManager membershipManager;
	private byte[] domain;
	
	public ZookeeperSender(ZookeeperUtils zkUtils, MembershipManager membershipManager) {
		this.zookeeperUtils = zkUtils;
		this.membershipManager = membershipManager;
	}
	
	public void sendToGroup(ClusteringCommand msg) throws ClusteringFault {
		domain = membershipManager.getDomain();		
		String domainName = new String(domain);
		
		zookeeperUtils.createCommandZNode(msg, domainName);
		
	}

	public void sendToSelf(ClusteringCommand msg) throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

}
