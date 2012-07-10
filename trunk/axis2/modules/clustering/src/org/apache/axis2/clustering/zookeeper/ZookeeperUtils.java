package org.apache.axis2.clustering.zookeeper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.clustering.Member;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class ZookeeperUtils {

    public static ZkClient zookeeper;

    public ZookeeperUtils(ZkClient zk) {
		this.zookeeper = zk;
	}

	public static ZkClient getZookeeper() {
		return zookeeper;
	}

	public static void setZookeeperConnection(ZkClient zkclient) {
		zookeeper = zkclient;
	}

	public static void createCommandZNode(ClusteringCommand command,
			String domain) {
		zookeeper.create("/" + domain + ZookeeperConstants.COMMAND_BASE_NAME, command,
				CreateMode.PERSISTENT_SEQUENTIAL);
	}
	public static void setZkMemeber(String domain,ZkMember member){
		String id = "asdasasdasda1";
		ZkSerializer as = new SerializableSerializer();
		zookeeper.setZkSerializer(as);
		
		System.out.print(false);
		zookeeper.createPersistent("/domain1/members/"+id, member);
	}
	
    public static Object getAddedNodes() {
        //This method should able to find the nodes that were added and return them
        return null;
    }

    public static Object getChildNodes(String path) {
        return null;
    }

    public static List<ZkMember> getZkMembers(List<String> childlist) {
        List<ZkMember> members = new ArrayList<ZkMember>();
        for (String childpath : childlist) {
            zookeeper.setZkSerializer(new ZkMemberSerializer());
            members.add((ZkMember) zookeeper.readData(childpath));
        }

        return members;

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
	
		// TODO Later members should not execute previous commands 
		// When a new member is initialized it should be assigned a currentId 
		
		String id =id = getNextId(currentid);
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
    public static List<ZkMember> getZkMembers(String parentPath) {
        List<String> childlist = zookeeper.getChildren(parentPath);
        return getZkMembers(childlist);

    }

    public static boolean isInDomain(ZkMember member, byte[] domain) {
        return false;
    }

    public static boolean areInSameDomain(ZkMember member, byte[] domain) {
        if (member != null) {
            return java.util.Arrays.equals(member.getDomain(), domain);
        } else return false;
    }

    //
    public static String getName(ZkMember member) {
        return getHost(member) + ":" + member.getPort() + "(" + new String(member.getDomain()) + ")";
    }

    //TODO Implementation of this method
    //Using String Buffer ?
    public static String getHost(ZkMember member) {
        return null;
    }

    public static Member toAxis2Member(ZkMember member) {
        Member axis2Member = new Member(ZookeeperUtils.getHost(member), member.getPort());

        Properties props = ZookeeperUtils.getPayload(member.getPayLoad());

        String httpPort = props.getProperty("httpPort");
        if (httpPort != null && httpPort.trim().length() != 0) {
            axis2Member.setHttpPort(Integer.parseInt(httpPort));
        }

        String httpsPort = props.getProperty("httpsPort");
        if (httpsPort != null && httpsPort.trim().length() != 0) {
            axis2Member.setHttpsPort(Integer.parseInt(httpsPort));
        }

        String isActive = props.getProperty(ClusteringConstants.Parameters.IS_ACTIVE);
        if (isActive != null && isActive.trim().length() != 0) {
            axis2Member.setActive(Boolean.valueOf(isActive));
        }

        axis2Member.setDomain(new String(member.getDomain()));
        axis2Member.setProperties(props);

        return axis2Member;
    }

    private static Properties getPayload(byte[] payLoad) {
        Properties properties = null;

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(payLoad);
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
           //???
        }
        return properties;
    }

}
