package org.apache.axis2.clustering.zookeeper;

import org.apache.axis2.clustering.tribes.MembershipManager;

public class Axis2CommandReceiver {

	private MembershipManager membershipManager;

	public Axis2CommandReceiver(MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public void startRecieve() {		
		String domainName = new String(membershipManager.getDomain());
		String commandPath = "/" + domainName + ZookeeperConstants.COMMANDS_BASE_NAME ;
		Integer initialId = generateCurrentId(commandPath);
		generateCurrentId(commandPath);
		ZookeeperUtils.getZookeeper().subscribeChildChanges(
				commandPath,
				new Axis2CommandChildListener(initialId));
	}
	public void stopRecive(){
		// TODO this method should be able to remove the chlidlistners from the given path
	}
	private Integer generateCurrentId(String commandPath){
		// TODO size cannot do because later old commands have to delete
		return ZookeeperUtils.getZookeeper().getChildren(commandPath).size();
	}
	

}
