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

import java.util.ArrayList;
import java.util.Collections;

public class ZooKeeperCommandSubscriber {
	private ZooKeeperStateManager stateManager;
	private ConfigurationContext configurationContext;
	private ZooKeeperNodeManager nodeManager;
	private ZooKeeperMembershipManager membershipManager;

	/**
	 * Initializes the ZooKeeperCommandSubscriber
	 * @param stateManager  ZooKeeperStateManager instance of the member
	 * @param configurationContext  ConfigurationContext instance of the member
	 * @param nodeManager  ZooKeeperNodeManager instance of the member
	 * @param membershipManager ZooKeeperMembershipManager instance of the member
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
	 * Sets the Command listeners
	 * @param cmdDelThreshold the command delete threshold
	 * @param cmdUpdateThreshold the command update threshold
	 */
	public void startRecieve(int cmdDelThreshold,int cmdUpdateThreshold) {
		String domainName = new String(membershipManager.getDomain());
		String commandPath = "/" + domainName
		+ ZooKeeperConstants.COMMANDS_BASE_NAME;

		String lastCommandName = getLastCommandName(commandPath);
		ZooKeeperUtils.getZookeeper().subscribeChildChanges(
				commandPath,
				new ZooKeeperCommandListener(lastCommandName, stateManager,
						configurationContext, nodeManager, membershipManager,cmdDelThreshold,cmdUpdateThreshold));

		if (!ZooKeeperUtils.getZookeeper().exists("/" + domainName
				+ ZooKeeperConstants.LAST_COMMAND_BASE_NAME)) {

			ZooKeeperUtils.getZookeeper().createPersistent("/" + domainName
					+ ZooKeeperConstants.LAST_COMMAND_BASE_NAME);

		}
	}

	/**
	 * Generated the sequence number of the command
	 *
	 * @param commandPath - pass the path of the command objects to process
	 * @return return the number of command objects in the given path
	 */
	private Integer generateCurrentId(String commandPath) {
		if (ZooKeeperUtils.getZookeeper().exists(commandPath)) {
			return ZooKeeperUtils.getZookeeper().getChildren(commandPath)
			.size();
		} else {
			return 0;
		}
	}

	/**
	 * Return the last executed command
	 * @param commandPath    command path
	 * @return   last command name as a String
	 */
	private String getLastCommandName(String commandPath)
	{
		ArrayList<String> commandList = (ArrayList) ZooKeeperUtils.getZookeeper().getChildren(commandPath);
		Collections.sort(commandList);
		try
		{
			return commandList.get(commandList.size()-1);
		}
		catch (Exception e)
		{
			return null;
		}

	}

}

