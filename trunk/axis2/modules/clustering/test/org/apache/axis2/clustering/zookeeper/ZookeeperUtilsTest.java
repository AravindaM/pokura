package org.apache.axis2.clustering.zookeeper;


import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ZookeeperUtilsTest extends TestCase{
	ZkServer zks;
	
 @Override
	protected void setUp() throws Exception {
	 zks = new ZkServer("/tmp/zookeepertest/data", "/tmp/zookeepertest/log",new IDefaultNameSpace() {
			
			public void createDefaultNameSpace(ZkClient zkClient) {
				// TODO Auto-generated method stub
				
			}
		},4599);
		 zks.start();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		zks.shutdown();
		super.tearDown();
	}


 public void testGetNewMembers(){
	ZkClient zkcli = new ZkClient("localhost:4599");
	ZookeeperUtils.setZookeeperConnection(zkcli);
	ZkMemberImpl member =  new  ZkMemberImpl();
	ZookeeperUtils.setZkMemeber(member);
	zks.shutdown();
 }
}
