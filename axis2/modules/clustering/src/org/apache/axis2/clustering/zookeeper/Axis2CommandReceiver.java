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

import org.apache.axis2.clustering.tribes.MembershipManager;

public class Axis2CommandReceiver {

	private Axis2MembershipManager membershipManager;

	public Axis2CommandReceiver(Axis2MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	/**
	 * Set Zookeeper command listener
	 */
	public void startRecieve() {		
		String domainName = new String(membershipManager.getDomain());
		String commandPath = "/" + domainName + ZookeeperConstants.COMMANDS_BASE_NAME ;
		Integer initialId = generateCurrentId(commandPath);
		generateCurrentId(commandPath);
		ZookeeperUtils.getZookeeper().subscribeChildChanges(
				commandPath,
				new Axis2CommandChildListener(initialId));
	}
	public void stopRecive(){
		// TODO this method should be able to remove the chlidlistners from the given path
	}
	/**
	 * Generated the sequence number of the command
	 * @param commandPath - pass the path of the command objects to process
	 * @return return the number of command objects in the given path
	 */
	private Integer generateCurrentId(String commandPath){
		// TODO size cannot do because later old commands have to delete
		return ZookeeperUtils.getZookeeper().getChildren(commandPath).size();
	}
	

}
