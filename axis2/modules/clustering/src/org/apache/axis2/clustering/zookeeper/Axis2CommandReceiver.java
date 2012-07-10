package org.apache.axis2.clustering.zookeeper;

import org.apache.axis2.clustering.tribes.MembershipManager;

public class Axis2CommandReceiver {

	private MembershipManager membershipManager;

	public Axis2CommandReceiver(MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public void startRecieve() {
		String commandPath = "/" + membershipManager.getDomain() + ZookeeperConstants.COMMAND_BASE_NAME ;
		Integer initialId = generateCurrentId(commandPath);
		generateCurrentId(commandPath);
		ZookeeperUtils.getZookeeper().subscribeChildChanges(
				commandPath,
				new Axis2CommandChildListener(initialId));
	}
	
	private Integer generateCurrentId(String commandPath){
		// TODO size cannot do because later old commands have to delete
		return ZookeeperUtils.getZookeeper().getChildren(commandPath).size();
	}
	

}
