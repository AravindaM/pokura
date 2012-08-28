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

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.clustering.Member;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;

public class ZooKeeperUtils {

	private static final Log log = LogFactory.getLog(ZooKeeperUtils.class);
	private static ZkClient zookeeper;
	private static ZooKeeper directZookeeper;

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
	 * @param zkclient the ZooKeeper client
	 */
	public static void setZookeeperConnection(ZkClient zkclient,String serverList) {
		setZookeeper(zkclient,serverList);
	}

	/**
	 * This method is used to serialize and save a command object into the group
	 * command node
	 *
	 * @param command the command object to be saved
	 * @param domain  the domain that the command should be sent to
	 */
	public static void createCommandZNode(ClusteringCommand command,
			String domain) {

		try{

			getZookeeper().setZkSerializer(new SerializableSerializer());
			byte data[] = new SerializableSerializer().serialize(command);
			directZookeeper.create("/" + domain + ZooKeeperConstants.COMMAND_BASE_NAME, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL, null, null);

		}catch (ZkMarshallingError e) {
			log.error("Error occurred while serialization "+e.getMessage());
		}

	}
	/**
	 * Returns the ZooKeeper client object for asynchronous communications
	 * @return ZooKeeper client object
	 */
	public static ZooKeeper getDirectZookeeper() {
		return directZookeeper;
	}
	/**
	 * Sets the ZooKeeper client object for asynchronous communications
	 * @param directZookeeper ZooKeeper client object
	 */
	public static void setDirectZookeeper(ZooKeeper directZookeeper) {
		ZooKeeperUtils.directZookeeper = directZookeeper;
	}
	/**
	 * Creates the Last command entry in the ZooKeeper Quorum
	 * @param lastCommand the lastcommand name
	 * @param domain the domain related to the command
	 */
	public static void createLastCommandEntry(String lastCommand, String domain) {
		String path = "/" + domain + ZooKeeperConstants.LAST_COMMAND_BASE_NAME
		+ "/" + lastCommand;

		ZkClient zkClient = getZookeeper();

		synchronized (zkClient) {
			if (!zkClient.exists(path)){
				try{
					zkClient.create(path, null, CreateMode.PERSISTENT);
				}catch (ZkInterruptedException e) {
					log.error(e.getMessage());
				}catch (IllegalArgumentException e) {
					log.error(e.getMessage());
				}catch (ZkException e) {
					log.error(e.getMessage());
				}catch (RuntimeException e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * Saves the given ZooKeeper member in the member node under the domain that
	 * the member belongs to
	 *
	 * @param member the member object to be saved
	 */
	public static void setZkMember(ZkMember member) {
		String domain = new String(member.getDomain());
		String id = member.getZkNodeID().toString();
		ZkSerializer serializer = new SerializableSerializer();
		getZookeeper().setZkSerializer(serializer);

		try{
			getZookeeper().createEphemeral("/" + domain + "/members/" + id, member);
		}catch (ZkInterruptedException e) {
			log.error(e.getMessage());
		}catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		}catch (ZkException e) {
			log.error(e.getMessage());
		}catch (RuntimeException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Retrieves the member objects according to the given path list
	 *
	 * @param childList the list of paths to the members
	 * @return a list ZooKeeper members
	 */
	public static List<ZkMember> getZkMembers(List<String> childList,
			String parentPath) {
		List<ZkMember> members = new ArrayList<ZkMember>();
		for (String childPath : childList) {
			getZookeeper().setZkSerializer(new SerializableSerializer());

			Object m = getZookeeper().readData(parentPath + "/" + childPath);
			members.add((ZkMember) m);
		}

		return members;

	}

	/**
	 * calculates the new members in the list when old and new list of members
	 * are given
	 *
	 * @param existingMembers - the old set of members
	 * @param currentMembers  - new set of members
	 * @return a list of members
	 */
	public static List<ZkMember> getNewMembers(List<ZkMember> existingMembers,
			List<ZkMember> currentMembers) {
		
		Collection<ZkMember> oldList = new ArrayList<ZkMember>(existingMembers);
		Collection<ZkMember> newList = new ArrayList<ZkMember>(currentMembers);
		newList.removeAll(oldList);

		return (List<ZkMember>) newList;

	}

	/**
	 * calculates the new members using the membership manager
	 *
	 * @param membershipManager - the membership manger of the system
	 * @param currentMembers    - new set of members
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
	 * @param membershipManager - the membership manger of the system
	 * @param parentPath        -new set of members
	 * @return a list of members
	 */
	public static List<ZkMember> getNewMembers(
			ZooKeeperMembershipManager membershipManager, String parentPath) {
		return getNewMembers(membershipManager.getMembers(),
				getZkMembers(parentPath));
	}

	/**
	 * This method returns the list of new commands form the specified current id 
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
	 * Return the last executed command with the minimum sequence number
	 * @param domain  domain name
	 * @return       last executed command name as a string
	 */
	public static String getLastCommand(String domain) {
		List<String> lastCommandList;
		String path = "/" + domain + ZooKeeperConstants.LAST_COMMAND_BASE_NAME;
		lastCommandList = getZookeeper().getChildren(path);
		Collections.sort(lastCommandList);
		if (lastCommandList.size() > 0) {
			return lastCommandList.get(0);
		} else {
			return null;
		}

	}

	public static int getCommandID(String commandName) {
		return Integer.parseInt(String.valueOf(commandName.toCharArray(), 7, 10));
	}


	/**
	 * Generates the next id of the command node sequence.
	 *
	 * @param id the current id
	 * @return returns the next id
	 */
	private static String getNextId(String id) {
		Integer count = Integer.valueOf(id);
		count++;
		return String.format("%010d", count);
	}

	public static String commandNameofIndex(int index)
	{
		return "command"+ String.format("%010d", index);
	}


	/**
	 * This method gets the zookeeper members under the given parent path
	 *
	 * @param parentPath the path of the parent node
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
	 * @param member the member to be checked
	 * @param domain the domain
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

	/**
	 * Returns the host name of the given member
	 * @param member the member who's host needs to be retrieved 
	 * @return the host name of the member
	 */
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
	 * @param member The zookeeper member to be converted
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
			log.error(e.getMessage());
		}
		return properties;
	}

	/**
	 * @param zookeeper the zookeeper to set
	 */
	public static void setZookeeper(ZkClient zookeeper,String serverList) {
		ZooKeeperUtils.zookeeper = zookeeper;
		try {
			directZookeeper = new ZooKeeper(serverList, 5000, zookeeper);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public static String getLocalHost(Parameter tcpListenHost) {
		String host = null;
		if (tcpListenHost != null) {
			host = ((String) tcpListenHost.getValue()).trim();
		} else {
			try {
				host = Utils.getIpAddress();
			} catch (SocketException e) {
				String msg = "Could not get local IP address";
				log.error(msg, e);
			}
		}
		if (System.getProperty(ClusteringConstants.LOCAL_IP_ADDRESS) != null) {
			host = System.getProperty(ClusteringConstants.LOCAL_IP_ADDRESS);
		}
		return host;
	}


}
