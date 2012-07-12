/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
			
			if(!zkcli.exists("/TestDomain/command"))
			{
				zkcli.createPersistent("/TestDomain");
				zkcli.createPersistent("/TestDomain/command");
			}
	
			ZookeeperUtils.setZookeeperConnection(zkcli);
			
			MembershipManager membershipManager = new MembershipManager();
			membershipManager.setDomain(new String("TestDomain").getBytes());
			
			Axis2CommandReceiver axis2CommandReceiver = new Axis2CommandReceiver(membershipManager);
			axis2CommandReceiver.startRecieve();
			ZookeeperSender sender = new ZookeeperSender(membershipManager);
			
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
