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

import java.util.Collections;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.management.GroupManagementCommand;
import org.apache.axis2.clustering.management.NodeManagementCommand;
import org.apache.axis2.clustering.state.StateClusteringCommand;
import org.apache.axis2.context.ConfigurationContext;

public class Axis2CommandChildListener implements IZkChildListener {

	private ZookeeperStateManager stateManager;
	private ConfigurationContext configurationContext;
	private ZookeeperNodeManager nodeManager;
	private Integer currentId;

	public Axis2CommandChildListener(Integer initialId) {
		currentId = initialId;
	}

	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
		Collections.sort(currentChilds);
		
		for (int i = currentId; i < currentChilds.size(); i++) {
			System.out.println(currentChilds.get(i) + " processing...");
			processMessage((ClusteringCommand) ZookeeperUtils.getZookeeper().readData(currentChilds.get(i)));
			currentId++;
		}
		
	}

	private void processMessage(ClusteringCommand command) throws ClusteringFault {
		if (command instanceof StateClusteringCommand && stateManager != null) {
			StateClusteringCommand ctxCmd = (StateClusteringCommand) command;
			ctxCmd.execute(configurationContext);
		} else if (command instanceof NodeManagementCommand && nodeManager != null) {
			((NodeManagementCommand) command).execute(configurationContext);
		} else if (command instanceof GroupManagementCommand) {
			((GroupManagementCommand) command).execute(configurationContext);
		}
	}

}
