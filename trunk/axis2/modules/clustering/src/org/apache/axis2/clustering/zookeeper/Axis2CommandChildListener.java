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
