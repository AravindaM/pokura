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
import org.I0Itec.zkclient.ZkClient;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.management.GroupManagementCommand;
import org.apache.axis2.clustering.management.NodeManagementCommand;
import org.apache.axis2.clustering.state.StateClusteringCommand;
import org.apache.axis2.context.ConfigurationContext;

import java.util.Collections;
import java.util.List;

public class ZooKeeperCommandListener implements IZkChildListener {

    private ZooKeeperStateManager stateManager;
    private ConfigurationContext configurationContext;
    private ZooKeeperNodeManager nodeManager;
    private ZooKeeperCommandSubscriber zooKeeperCommandSubscriber;
    private ZooKeeperMembershipManager zooKeeperMembershipManager;

    Integer currentId;
    static long startTimeStatic;

    /**
     * @param stateManager
     * @param configurationContext
     * @param nodeManager
     */
    public ZooKeeperCommandListener(Integer initialId,
                                    ZooKeeperStateManager stateManager,
                                    ConfigurationContext configurationContext,
                                    ZooKeeperNodeManager nodeManager,
                                    ZooKeeperMembershipManager membershipManager,
                                    ZooKeeperCommandSubscriber zooKeeperCommandSubscriber) {
        currentId = initialId;
        this.stateManager = stateManager;
        this.configurationContext = configurationContext;
        this.nodeManager = nodeManager;
        this.zooKeeperCommandSubscriber = zooKeeperCommandSubscriber;
        this.zooKeeperMembershipManager = membershipManager;
    }

    public void handleChildChange(String parentPath, List<String> currentChilds)
            throws Exception {

        new ZooKeeperCommandHandler(stateManager, configurationContext, nodeManager, zooKeeperMembershipManager,
                zooKeeperCommandSubscriber, parentPath, currentChilds).start();

    }

    class ZooKeeperCommandHandler extends Thread {

        private ZooKeeperStateManager stateManager;
        private ConfigurationContext configurationContext;
        private ZooKeeperNodeManager nodeManager;
        private ZooKeeperCommandSubscriber zooKeeperCommandSubscriber;
        private ZooKeeperMembershipManager zooKeeperMembershipManager;
        private String parentPath;
        private List<String> currentChilds;


        public ZooKeeperCommandHandler(ZooKeeperStateManager stateManager,
                                       ConfigurationContext configurationContext,
                                       ZooKeeperNodeManager nodeManager,
                                       ZooKeeperMembershipManager membershipManager,
                                       ZooKeeperCommandSubscriber zooKeeperCommandSubscriber,
                                       String parentPath, List<String> currentChilds) {
            this.stateManager = stateManager;
            this.configurationContext = configurationContext;
            this.nodeManager = nodeManager;
            this.zooKeeperCommandSubscriber = zooKeeperCommandSubscriber;
            this.zooKeeperMembershipManager = membershipManager;
            this.parentPath = parentPath;
            this.currentChilds = currentChilds;
        }

        @Override
        public void run() {
            // call command processing method for each new command
            long startTime = System.nanoTime();
            startTimeStatic = startTime;

            Collections.sort(currentChilds);

            System.out.println("\nStart loop" + this.toString() + zooKeeperMembershipManager.getLocalMember());
            for (int i = currentId; i < currentChilds.size(); i++) {
                System.out.println(currentChilds.get(i) + " processing... ");

                ZkClient zk = ZooKeeperUtils.getZookeeper();
                String cmName = currentChilds.get(i);

                if (zk.exists(parentPath + "/" + cmName)) {
                    ClusteringCommand cm = (ClusteringCommand) zk
                            .readData(parentPath + "/" + cmName);
                    try {
                        processMessage(cm);
                    } catch (Exception e) {
                    }

                }

                currentId++;
            }
            System.out.println("end loop\n" + this.toString() + zooKeeperMembershipManager.getLocalMember());

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {

            }

        }

        public void timeoutCommandProcess() throws Exception {
            String domainName = new String(zooKeeperMembershipManager.getDomain());
            String commandPath = "/" + domainName
                    + ZooKeeperConstants.COMMANDS_BASE_NAME;

            List<String> currentChilds = ZooKeeperUtils.getZookeeper().getChildren(
                    commandPath);

            System.out.println("/nTimeout Start Loop" + this.toString() + zooKeeperMembershipManager.getLocalMember());
            for (int i = currentId; i < currentChilds.size(); i++) {
                System.out.println(currentChilds.get(i)
                        + " after timeout processing...");

                ZkClient zk = ZooKeeperUtils.getZookeeper();
                String cmName = currentChilds.get(i);

                if (zk.exists(commandPath + "/" + cmName)) {
                    ClusteringCommand cm = (ClusteringCommand) zk
                            .readData(commandPath + "/" + cmName);
                    processMessage(cm);
                }

                currentId++;
            }
            System.out.println("Timeout End Loop\n" + this.toString() + zooKeeperMembershipManager.getLocalMember());
        }

        private void processMessage(ClusteringCommand command)
                throws ClusteringFault {
            // process the command object

            if (command instanceof StateClusteringCommand && stateManager != null) {
                StateClusteringCommand ctxCmd = (StateClusteringCommand) command;
                ctxCmd.execute(configurationContext);
            } else if (command instanceof NodeManagementCommand
                    && nodeManager != null) {
                ((NodeManagementCommand) command).execute(configurationContext);
            } else if (command instanceof GroupManagementCommand) {
                ((GroupManagementCommand) command).execute(configurationContext);
            }
            System.out.println("processed");
        }
    }
}