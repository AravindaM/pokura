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

public class ZooKeeperMemberSubscriber {
    private ZooKeeperMembershipManager membershipManager;

    public ZooKeeperMemberSubscriber(ZooKeeperMembershipManager membershipManager) {
        this.membershipManager = membershipManager;
    }

    /**
     * Set Zookeeper command listener
     */
    public void startRecieve() {

        //TODO handle null pointers and instances where the zookeeper is down
        String domainName = new String(membershipManager.getDomain());
        String memberPath = "/" + domainName + ZooKeeperConstants.MEMEBER_BASE_NAME;
        //TODO handle the state when the parent nodes are not created
        if (!ZooKeeperUtils.getZookeeper().exists("/" + domainName)) {
            ZooKeeperUtils.getZookeeper().createPersistent("/" + domainName);
        }
        if (!ZooKeeperUtils.getZookeeper().exists("/" + domainName + ZooKeeperConstants.MEMEBER_BASE_NAME)) {
            ZooKeeperUtils.getZookeeper().createPersistent("/" + domainName + ZooKeeperConstants.MEMEBER_BASE_NAME);
        }

        ZooKeeperUtils.getZookeeper().subscribeChildChanges(
                memberPath,
                new ZooKeeperMemberListener(membershipManager));
    }

    public void stopRecieve() {
        // TODO this method should be able to remove the chlidlistners from the given path

    }
}
