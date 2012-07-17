package org.apache.axis2.clustering.zookeeper;

import java.util.List;

import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.MembershipScheme;
import org.apache.commons.logging.LogFactory;

public class ZooKeeperMembershipScheme implements MembershipScheme{

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(ZooKeeperMembershipScheme.class);

    private final ZooKeeperMembershipManager membershipManager;

    public ZooKeeperMembershipScheme(ZooKeeperMembershipManager membershipManager) {
        this.membershipManager = membershipManager;
    }

    public void init() throws ClusteringFault {
        // TODO Auto-generated method stub

    }

    public void joinGroup() throws ClusteringFault {
        String domainName = new String(membershipManager.getDomain());
        String memberPath = "/" + domainName + ZooKeeperConstants.MEMEBER_BASE_NAME;

        List<ZkMember> members = ZooKeeperUtils.getZkMembers(memberPath);

        if (!members.isEmpty()) {
            membershipManager.setMembers(members);
        } else {
            log.info("No members found. Local member is the 1st member ");
        }
        //create a node for this member
        ZooKeeperUtils.setZkMemeber(membershipManager.getLocalMember());
    }

}
