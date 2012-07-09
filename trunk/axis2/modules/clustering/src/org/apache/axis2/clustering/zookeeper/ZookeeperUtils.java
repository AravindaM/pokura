package org.apache.axis2.clustering.zookeeper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.zookeeper.CreateMode;

public class ZookeeperUtils {

	public static ZkClient zookeeper;	

	public static  void setZookeeperConnection(ZkClient zkclient){
		zookeeper = zkclient;
	}
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

	public static List<ZkMember> getZkMembers(List<String> childlist){
		List<ZkMember> members = new ArrayList<ZkMember>();
		for (String childpath : childlist) {
			zookeeper.setZkSerializer(new ZkMemberSerializer());
			members.add((ZkMember)zookeeper.readData(childpath));
		}

		return members; 

	}

	public static List<ZkMember> getZkMembers(String parentPath){
		List<String> childlist = zookeeper.getChildren(parentPath);
		return getZkMembers(childlist);

	}
	
	public static List<ZkMember> getNewMembers(List<ZkMember> existingMembers,List<ZkMember> currentMembers){
		Collection<ZkMember> oldList = existingMembers;
		Collection<ZkMember> newList = currentMembers;
		
		newList.removeAll(oldList);
		
		
		return (List<ZkMember>)newList;
		
	}
	public static List<ZkMember> getNewMembers(Axis2MembershipManager membershipManager, List<ZkMember> currentMembers){
		return getNewMembers(membershipManager.getMembers(), currentMembers);
	}
	
	public static List<ZkMember> getNewMembers(Axis2MembershipManager membershipManager, String parentPath){
		return getNewMembers(membershipManager.getMembers(), getZkMembers(parentPath));
	}
	
	public static List<ClusteringCommand> getNewCommands(String path,String currentid){
		String id =currentid;
		List<ClusteringCommand> commands = new ArrayList<ClusteringCommand>();
		String commandpath = path+"/"+ZookeeperConstants.COMMAND_BASE_NAME;
		ClusteringCommand command;
		
		while ((command = (ClusteringCommand)zookeeper.readData(commandpath+id,true))!=null) {
			commands.add(command);
			id = getNextId(id);
		}
		
		return commands;
	}
	
	private static String getNextId(String id){
		Integer count = Integer.valueOf(id);
		count++;
		return String.format("%010d", count);
	}
}
