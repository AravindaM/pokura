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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.clustering.Member;
import org.apache.zookeeper.CreateMode;

public class ZooKeeperUtils {

	private static ZkClient zookeeper;

	/**
	 * This method allows the user to get the zookeeper client instance
	 * 
	 * @return the ZooKeeper Object
	 */
	public static ZkClient getZookeeper() {
		return zookeeper;
	}

	/**
	 * This method is used to set the ZooKeeper client
	 * 
	 * @param zkclient
	 *            the ZooKeeper client
	 */
	public static void setZookeeperConnection(ZkClient zkclient) {
		setZookeeper(zkclient);
	}

	/**
	 * This method is used to serialize and save a command object into the group
	 * command node
	 * 
	 * @param command
	 *            the command object to be saved
	 * @param domain
	 *            the domain that the command should be sent to
	 */
	public static void createCommandZNode(ClusteringCommand command,
			String domain) {
	
		

		getZookeeper().setZkSerializer(new SerializableSerializer());
//		zookeeper.setZkSerializer(new CommandSerializer());
	
		getZookeeper().create("/" + domain + ZooKeeperConstants.COMMAND_BASE_NAME,
				command, CreateMode.PERSISTENT_SEQUENTIAL);
	
	}

	/**
	 * Saves the given ZooKeeper member in the member node under the domain that
	 * the member belongs to
	 * 
	 * @param member
	 *            the member object to be saved
	 */
	public static void setZkMemeber(ZkMember member) {
		String domain = new String(member.getDomain());
		String id = UUID.randomUUID().toString();
		ZkSerializer as = new SerializableSerializer();
		getZookeeper().setZkSerializer(as);

		// System.out.print(false);
		getZookeeper().createEphemeral("/" + domain + "/members/" + id, member);
	}

	public static Object getAddedNodes() {
		// This method should able to find the nodes that were added and return
		// them
		return null;
	}

	public static Object getChildNodes(String path) {
		return null;
	}

	/**
	 * Retrieves the member objects according to the given path list
	 * 
	 * @param childlist
	 *            the list of paths to the members
	 * @return a list ZooKeeper members
	 */
	public static List<ZkMember> getZkMembers(List<String> childlist,
			String parentPath) {
		List<ZkMember> members = new ArrayList<ZkMember>();
		for (String childpath : childlist) {
			getZookeeper().setZkSerializer(new SerializableSerializer());

			Object m = getZookeeper().readData(parentPath + "/" + childpath);
			members.add((ZkMember) m);
		}

		return members;

	}

	/**
	 * calculates the new members in the list when old and new list of members
	 * are given
	 * 
	 * @param existingMembers
	 *            - the old set of members
	 * @param currentMembers
	 *            - new set of members
	 * @return a list of members
	 */
	public static List<ZkMember> getNewMembers(List<ZkMember> existingMembers,
			List<ZkMember> currentMembers) {
		Collection<ZkMember> oldList = existingMembers;
		Collection<ZkMember> newList = currentMembers;

		newList.removeAll(oldList);

		return (List<ZkMember>) newList;

	}

	/**
	 * calculates the new members using the membership manager
	 * 
	 * @param membershipManager
	 *            - the membership manger of the system
	 * @param currentMembers
	 *            - new set of members
	 * @return a list of members
	 */
	public static List<ZkMember> getNewMembers(
			ZooKeeperMembershipManager membershipManager,
			List<ZkMember> currentMembers) {
		return getNewMembers(membershipManager.getMembers(), currentMembers);
	}

	/**
	 * calculates the new members using the membership manager and parent node
	 * path
	 * 
	 * @param membershipManager
	 *            - the membership manger of the system
	 * @param parentPath
	 *            -new set of members
	 * @return a list of members
	 */
	public static List<ZkMember> getNewMembers(
			ZooKeeperMembershipManager membershipManager, String parentPath) {
		return getNewMembers(membershipManager.getMembers(),
				getZkMembers(parentPath));
	}

	/**
	 * This method returns the
	 * 
	 * @param path
	 * @param currentid
	 * @return a list of commands
	 */
	public static List<ClusteringCommand> getNewCommands(String path,
			String currentid) {

		// TODO Later members should not execute previous commands
		// When a new member is initialized it should be assigned a currentId

		String id = getNextId(currentid);
		List<ClusteringCommand> commands = new ArrayList<ClusteringCommand>();
		String commandpath = path + "/" + ZooKeeperConstants.COMMAND_BASE_NAME;
		ClusteringCommand command;

		while ((command = (ClusteringCommand) getZookeeper().readData(commandpath
				+ id, true)) != null) {
			commands.add(command);
			id = getNextId(id);
		}

		return commands;
	}

	/**
	 * Generates the next id of the command node sequence.
	 * 
	 * @param id
	 *            the current id
	 * @return returns the next id
	 */
	private static String getNextId(String id) {
		Integer count = Integer.valueOf(id);
		count++;
		return String.format("%010d", count);
	}

	/**
	 * This method gets the zookeeper members under the given parent path
	 * 
	 * @param parentPath
	 *            the path of the parent node
	 * @return a list od zookeeper members
	 */
	public static List<ZkMember> getZkMembers(String parentPath) {
		List<String> childlist = getZookeeper().getChildren(parentPath);
		return getZkMembers(childlist, parentPath);

	}

	// /**
	// * Checks whether the given member is in the specified domain
	// * @param member the member to be checked
	// * @param domain the domain
	// * @return true if the member is i this domain
	// */
	// public static boolean isInDomain(ZkMember member, byte[] domain) {
	// return false;
	// }
	/**
	 * Checks whether the given member is in the specified domain
	 * 
	 * @param member
	 *            the member to be checked
	 * @param domain
	 *            the domain
	 * @return true if the member is i this domain
	 */
	public static boolean areInSameDomain(ZkMember member, byte[] domain) {
		if (member != null) {
			return java.util.Arrays.equals(member.getDomain(), domain);
		} else
			return false;
	}

	/**
	 * Gets the name of the given member
	 * 
	 * @param member
	 * @return the name of the member
	 */
	public static String getName(ZkMember member) {
		return getHost(member) + ":" + member.getPort() + "("
				+ new String(member.getDomain()) + ")";
	}

	public static String getHost(ZkMember member) {
		byte[] hostBytes = member.getZkHost();
        StringBuffer host = new StringBuffer();
        if (hostBytes != null) {
            for (int i = 0; i < hostBytes.length; i++) {
                int hostByte = hostBytes[i] >= 0 ? (int) hostBytes[i] : (int) hostBytes[i] + 256;
                host.append(hostByte);
                if (i < hostBytes.length - 1) {
                    host.append(".");
                }
            }
        }
        return host.toString();
	}

	/**
	 * Converts a given zookeeper member into a Axis2 member
	 * 
	 * @param member
	 *            The zookeeper member to be converted
	 * @return returns the axis2 member
	 */
	public static Member toAxis2Member(ZkMember member) {
		Member axis2Member = new Member(ZooKeeperUtils.getHost(member),
				member.getPort());

		Properties props = ZooKeeperUtils.getPayload(member.getPayLoad());

		String httpPort = props.getProperty("httpPort");
		if (httpPort != null && httpPort.trim().length() != 0) {
			axis2Member.setHttpPort(Integer.parseInt(httpPort));
		}

		String httpsPort = props.getProperty("httpsPort");
		if (httpsPort != null && httpsPort.trim().length() != 0) {
			axis2Member.setHttpsPort(Integer.parseInt(httpsPort));
		}

		String isActive = props
				.getProperty(ClusteringConstants.Parameters.IS_ACTIVE);
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
			// ???
		}
		return properties;
	}

	/**
	 * @param zookeeper the zookeeper to set
	 */
	public static void setZookeeper(ZkClient zookeeper) {
		ZooKeeperUtils.zookeeper = zookeeper;
	}

}
