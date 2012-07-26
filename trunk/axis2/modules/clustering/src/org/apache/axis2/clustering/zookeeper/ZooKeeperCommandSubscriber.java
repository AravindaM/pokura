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

import org.apache.axis2.context.ConfigurationContext;

public class ZooKeeperCommandSubscriber {
	private ZooKeeperStateManager stateManager;
	private ConfigurationContext configurationContext;
	private ZooKeeperNodeManager nodeManager;
	private ZooKeeperMembershipManager membershipManager;
	private Integer initialId;
	public static long startTime;
	public static long eventCount;

	/**
	 * @param stateManager
	 * @param configurationContext
	 * @param nodeManager
	 * @param membershipManager
	 */
	public ZooKeeperCommandSubscriber(ZooKeeperStateManager stateManager,
			ConfigurationContext configurationContext,
			ZooKeeperNodeManager nodeManager,
			ZooKeeperMembershipManager membershipManager) {
		this.stateManager = stateManager;
		this.configurationContext = configurationContext;
		this.nodeManager = nodeManager;
		this.membershipManager = membershipManager;
	}

	/**
	 * @param membershipManager
	 */
	public ZooKeeperCommandSubscriber(
			ZooKeeperMembershipManager membershipManager) {
		super();
		this.membershipManager = membershipManager;
	}

	/**
	 * Set Zookeeper command listener
	 */
	public void startRecieve() {

		String domainName = new String(membershipManager.getDomain());
		String commandPath = "/" + domainName
				+ ZooKeeperConstants.COMMANDS_BASE_NAME;
		initialId = generateCurrentId(commandPath);

		ZooKeeperUtils.getZookeeper().subscribeChildChanges(
				commandPath,
				new ZooKeeperCommandListener(initialId, stateManager,
						configurationContext, nodeManager, membershipManager));
		System.out.println(commandPath);

	}

	public void stopRecive() {
		// TODO this method should be able to remove the chlidlistners from the
		// given path
	}

	/**
	 * Generated the sequence number of the command
	 * 
	 * @param commandPath
	 *            - pass the path of the command objects to process
	 * @return return the number of command objects in the given path
	 */
	private Integer generateCurrentId(String commandPath) {
		// TODO size cannot do because later old commands have to delete
		System.out.println(commandPath);
		if (ZooKeeperUtils.getZookeeper().exists(commandPath)) {
			return ZooKeeperUtils.getZookeeper().getChildren(commandPath)
					.size();
		} else {
			return 0;
		}
	}

}
