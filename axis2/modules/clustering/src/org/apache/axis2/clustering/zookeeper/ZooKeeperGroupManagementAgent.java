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

import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.management.GroupManagementAgent;
import org.apache.axis2.clustering.management.GroupManagementCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * This class used to initialize Group of different members and send commands
 * initiated members
 * 
 */
public class ZooKeeperGroupManagementAgent {

	private static final Log log = LogFactory.getLog(ZooKeeperGroupManagementAgent.class);
	private final List<ZkMember> members = new ArrayList<ZkMember>();
	private ZooKeeperMembershipManager membershipManager;
	private ZooKeeperSender zookeeperSender;
	private String description;

	public ZooKeeperGroupManagementAgent(ZooKeeperMembershipManager membershipManager, String description) {
		this.membershipManager = membershipManager;
		this.description = description;
		initZookeeperSender();
	}

	public ZooKeeperMembershipManager getMembershipManager() {
		return membershipManager;
	}

	public void setMembershipManager(ZooKeeperMembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ZkMember> getMembers() {
		return members;
	}

	public void setZookeeperSender(ZooKeeperSender zookeeperSender) {
		this.zookeeperSender = zookeeperSender;
	}

	public void applicationMemberAdded(ZkMember member) {
		Thread th = new Thread(new MemberAdder(member));
		th.setPriority(Thread.MAX_PRIORITY);
		th.start();
	}

	public void applicationMemberRemoved(ZkMember member) {
		log.info("Application member " + member + " left cluster.");
		members.remove(member);
	}

	public void send(GroupManagementCommand command) throws ClusteringFault {
		
		for(int i=0; i< members.size() ; i++){
			membershipManager.addMember(members.get(i));
		}
		zookeeperSender.sendToGroup(command);
	}

	private void initZookeeperSender() {
		if (membershipManager != null) {
			zookeeperSender = new ZooKeeperSender(membershipManager);
		}
	}

	private class MemberAdder implements Runnable {

		private final ZkMember member;

		private MemberAdder(ZkMember member) {
			this.member = member;
		}

		public void run() {
			if (members.contains(member)) {
				return;
			}
			if (canConnect(member)) {
				try {
					Thread.sleep(10000); // Sleep for sometime to allow complete
											// initialization of the node
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!members.contains(member)) {
					members.add(member);
				}
				log.info("Application member " + member + " joined application cluster");
			} else {
				log.error("Could not add application member " + member);
			}
		}

		/**
		 * Before adding a member, we will try to verify whether we can connect
		 * to it
		 * 
		 * @param member
		 *            The member whose connectvity needs to be verified
		 * @return true, if the member can be contacted; false, otherwise.
		 */
		private boolean canConnect(ZkMember member) {
			// TODO implement check for connectivity of ZkMember
			return false;
		}
	}

}
