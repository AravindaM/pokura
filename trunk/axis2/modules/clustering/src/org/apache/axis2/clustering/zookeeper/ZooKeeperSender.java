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

import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.MessageSender;

/**
 * This class is used to send messages to Zookeeper cluster
 */

public class ZooKeeperSender implements MessageSender {

    private ZooKeeperMembershipManager membershipManager;
    private byte[] domain;

    public ZooKeeperSender(ZooKeeperMembershipManager membershipManager) {
        this.membershipManager = membershipManager;
    }

    /**
     * send command objects to the group
     *
     * @param msg - command object
     */
    public void sendToGroup(ClusteringCommand msg) throws ClusteringFault {
        domain = membershipManager.getDomain();
        String domainName = new String(domain);

        if (!ZooKeeperUtils.getZookeeper().exists("/" + domainName
                + ZooKeeperConstants.COMMANDS_BASE_NAME)) {

            ZooKeeperUtils.getZookeeper().createPersistent("/" + domainName
                    + ZooKeeperConstants.COMMANDS_BASE_NAME);

        }
        ZooKeeperUtils.createCommandZNode(msg, domainName);
    }

    public void sendToSelf(ClusteringCommand msg) throws ClusteringFault {
        // TODO Auto-generated method stub

    }

}
