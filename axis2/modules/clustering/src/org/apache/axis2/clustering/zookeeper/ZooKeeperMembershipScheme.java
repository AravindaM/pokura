package org.apache.axis2.clustering.zookeeper;

import java.util.List;

import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.MembershipScheme;

public class ZooKeeperMembershipScheme implements MembershipScheme{
	
	private final Axis2MembershipManager membershipManager;

	public ZooKeeperMembershipScheme(Axis2MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public void init() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void joinGroup() throws ClusteringFault {
		String domainName = new String(membershipManager.getDomain());
		String memberPath = "/" + domainName + ZookeeperConstants.MEMEBER_BASE_NAME ;
		
		List<ZkMember> members = ZookeeperUtils.getZkMembers(memberPath);
		membershipManager.setMembers(members);
		
		//create a node for this member
		ZookeeperUtils.setZkMemeber(membershipManager.getLocalMember());
	}

}
