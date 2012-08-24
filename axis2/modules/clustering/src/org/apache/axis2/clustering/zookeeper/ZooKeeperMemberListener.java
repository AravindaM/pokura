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

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ZooKeeperMemberListener implements IZkChildListener {

	private static Log log = LogFactory.getLog(ZooKeeperMemberListener.class);
	private final ZooKeeperMembershipManager membershipManager;

	public ZooKeeperMemberListener(ZooKeeperMembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public void handleChildChange(String parentPath, List<String> currentChilds)
	throws Exception {
		//TODO improve performance by making sure unwanted member objects are not retrieved
		List<ZkMember> oldmembers = membershipManager.getMembers();
		List<ZkMember> newmembers = ZooKeeperUtils.getZkMembers(currentChilds, parentPath);
		if (newmembers.size()>oldmembers.size() ){
			List<ZkMember> addedmembers = ZooKeeperUtils.getNewMembers(oldmembers, newmembers);
			for (ZkMember zkMember : addedmembers) {
				if (membershipManager.addMember(zkMember)) {
					log.info("New member " + ZooKeeperUtils.getName(zkMember) + " joined cluster.");
				}
			}
		}else if(newmembers.size()<oldmembers.size() ){
			List<ZkMember> removemembers = ZooKeeperUtils.getNewMembers(newmembers,oldmembers);
			for (ZkMember zkMember : removemembers) {
				if (membershipManager.memberRemoved(zkMember)) {
					log.info("Old member " + ZooKeeperUtils.getName(zkMember) + " left cluster.");
				}
			}
		}


	}

}
