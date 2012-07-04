package org.apache.axis2.clustering.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperUtils {

public static ZooKeeper zookeeper;	
	
public static void createCommandZNode(ClusteringCommand command, ZkClient zkClient, String domain){
		zkClient.create("/"+domain+"/command/command", command,CreateMode.PERSISTENT_SEQUENTIAL);

	}

public static Object getAddedNodes(){
	//This method should able to find the nodes that were added and return them
	return null;
}

public static Object getChildNodes(String path){
	return null;
}

}
