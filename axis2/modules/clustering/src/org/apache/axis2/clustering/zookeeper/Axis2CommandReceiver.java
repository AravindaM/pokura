package org.apache.axis2.clustering.zookeeper;

import org.apache.axis2.clustering.tribes.MembershipManager;

public class Axis2CommandReceiver {

	private ZookeeperUtils zookeeperUtils;
	private MembershipManager membershipManager;

	public Axis2CommandReceiver(ZookeeperUtils zookeeperUtils, MembershipManager membershipManager) {
		this.zookeeperUtils = zookeeperUtils;
		this.membershipManager = membershipManager;
	}

	public void startRecieve() {
		String commandPath = "/" + membershipManager.getDomain() + ZookeeperConstants.COMMAND_BASE_NAME ;
		Integer initialId = generateCurrentId(commandPath);
		generateCurrentId(commandPath);
		zookeeperUtils.getZookeeper().subscribeChildChanges(
				commandPath,
				new Axis2CommandChildListener(initialId,zookeeperUtils));
	}
	
	private Integer generateCurrentId(String commandPath){
		return zookeeperUtils.getZookeeper().getChildren(commandPath).size();
	}
	

}
