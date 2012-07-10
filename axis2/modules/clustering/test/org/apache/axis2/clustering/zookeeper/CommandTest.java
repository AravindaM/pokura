package org.apache.axis2.clustering.zookeeper;

import java.io.Serializable;

import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.state.commands.DeleteServiceStateCommand;
import org.apache.axis2.clustering.state.commands.UpdateStateCommand;
import org.apache.axis2.clustering.tribes.MembershipManager;
import org.apache.axis2.context.ConfigurationContext;

import junit.framework.TestCase;

public class CommandTest extends TestCase implements Serializable{

	ZkServer zks;

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

	public void testSendCommand() throws ClusteringFault{
			ZkClient zkcli = new ZkClient("localhost:4599");
			ZookeeperUtils.setZookeeperConnection(zkcli);
			
			MembershipManager membershipManager = new MembershipManager();
			membershipManager.setDomain(new String("TestDomain").getBytes());
			
			ZookeeperSender sender = new ZookeeperSender(membershipManager);
			
			
//			UpdateStateCommand command = new UpdateStateCommand() {
//				
//				@Override
//				public void execute(ConfigurationContext configContext)
//						throws ClusteringFault {
//					// TODO Auto-generated method stub
//					System.out.println("State Updated");
//				}
//			};
			
			DeleteServiceStateCommand command = new DeleteServiceStateCommand();
			
			sender.sendToGroup(command);
		 }

	/**
	 * Ends initialized data
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		zks.shutdown();
	}

}
