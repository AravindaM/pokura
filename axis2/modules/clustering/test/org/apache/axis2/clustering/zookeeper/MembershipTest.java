package org.apache.axis2.clustering.zookeeper;

import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;

import junit.framework.TestCase;

public class MembershipTest extends TestCase{
	ZkServer zks;

	  // --------------- Cluster-1 ------------------------------------------------------
    private ClusteringAgent clusterManager1;
    private StateManager ctxMan1;
    private NodeManager configMan1;
    private ConfigurationContext configurationContext1;
    private AxisServiceGroup serviceGroup1;
    private AxisService service1;
    //---------------------------------------------------------------------------------

    // --------------- Cluster-2 ------------------------------------------------------
    private ClusteringAgent clusterManager2;
    private StateManager ctxMan2;
    private NodeManager configMan2;
    private ConfigurationContext configurationContext2;
    private AxisServiceGroup serviceGroup2;
    private AxisService service2;
    //---------------------------------------------------------------------------------
    
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		zks = new ZkServer("/tmp/zookeepertest/data", "/tmp/zookeepertest/log",
				new IDefaultNameSpace() {

					public void createDefaultNameSpace(ZkClient zkClient) {
						// TODO Auto-generated method stub

					}
				}, 4599);
		zks.start();	
		
		
	}
	
	public void membershipManagmentTest(){
		
	}
}
