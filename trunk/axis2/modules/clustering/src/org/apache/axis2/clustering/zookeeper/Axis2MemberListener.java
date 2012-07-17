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

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Axis2MemberListener implements IZkChildListener {
	
	 private static Log log = LogFactory.getLog(Axis2MemberListener.class);
	 private final Axis2MembershipManager membershipManager;
	    
	public Axis2MemberListener(Axis2MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public void handleChildChange(String parentPath, List<String> currentChilds)
			throws Exception {
		//TODO improve performance by making sure unwanted member objects are not retrieved 
		List<ZkMember> oldmembers = membershipManager.getMembers();
		List<ZkMember> newmembers = ZookeeperUtils.getZkMembers(currentChilds);
		
		List<ZkMember> addedmembers = ZookeeperUtils.getNewMembers(oldmembers,newmembers);
		 for (ZkMember zkMember : addedmembers) {
			 if (membershipManager.addMember(zkMember)) {
		            log.info("New member " + ZookeeperUtils.getName(zkMember) + " joined cluster.");
		            System.out.println("New member " + ZookeeperUtils.getName(zkMember) + " joined cluster.");
			  }
		}
		 
		
	}

}
