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

import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZooKeeperMemberSubscriber {
	private static final Log log = LogFactory.getLog(ZooKeeperUtils.class);

	private ZooKeeperMembershipManager membershipManager;

	public ZooKeeperMemberSubscriber(ZooKeeperMembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	/**
	 * Subscribe for the changes in child nodes of "/domain/members" node in ZooKeeper server.
	 */
	public void startReceive() {

		String domainName = new String(membershipManager.getDomain());
		String memberPath = "/" + domainName + ZooKeeperConstants.MEMBER_BASE_NAME;

		try{
			if (!ZooKeeperUtils.getZookeeper().exists("/" + domainName)) {

				ZooKeeperUtils.getZookeeper().createPersistent("/" + domainName);

			}
			if (!ZooKeeperUtils.getZookeeper().exists("/" + domainName + ZooKeeperConstants.MEMBER_BASE_NAME)) {
				ZooKeeperUtils.getZookeeper().createPersistent("/" + domainName + ZooKeeperConstants.MEMBER_BASE_NAME);
			}
		}catch (ZkInterruptedException e) {
			log.error(e.getMessage());
		}catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		}catch (ZkException e) {
			log.error(e.getMessage());
		}catch (RuntimeException e) {
			log.error(e.getMessage());
		}

		ZooKeeperUtils.getZookeeper().subscribeChildChanges(
				memberPath,
				new ZooKeeperMemberListener(membershipManager));
	}

	public void stopReceive() {
		// TODO this method should be able to remove the childListeners from the given path

	}
}
