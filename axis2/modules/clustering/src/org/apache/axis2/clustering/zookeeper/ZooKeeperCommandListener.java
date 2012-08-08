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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;

public class ZooKeeperCommandListener implements IZkChildListener {

    private static Log log = LogFactory.getLog(ZooKeeperCommandListener.class);
    private ZooKeeperStateManager stateManager;
    private ConfigurationContext configurationContext;
    private ZooKeeperNodeManager nodeManager;
    private ZooKeeperMembershipManager zooKeeperMembershipManager;

    Integer currentId;
    static long startTimeStatic;

    /**
     * @param initialId            Start position of the commands to process by the member
     * @param stateManager         ZooKeeperStateManager instance of the member
     * @param configurationContext ConfigurationContext instance of the member
     * @param nodeManager          ZooKeeperNodeManager instance of the member
     * @param membershipManager    ZooKeeperMembershipManager instance of the member
     */
    public ZooKeeperCommandListener(Integer initialId,
                                    ZooKeeperStateManager stateManager,
                                    ConfigurationContext configurationContext,
                                    ZooKeeperNodeManager nodeManager,
                                    ZooKeeperMembershipManager membershipManager) {
        this.currentId = initialId;
        this.stateManager = stateManager;
        this.configurationContext = configurationContext;
        this.nodeManager = nodeManager;
        this.zooKeeperMembershipManager = membershipManager;
    }

    public void handleChildChange(String parentPath, List<String> currentChilds) {
        // each event is handled by separate threads
        new ZooKeeperCommandHandler(stateManager, configurationContext, nodeManager, zooKeeperMembershipManager,
                parentPath, currentChilds).start();

    }

    /**
     * Use a new thread to process each command
     */
    class ZooKeeperCommandHandler extends Thread {

        private ZooKeeperStateManager stateManager;
        private ConfigurationContext configurationContext;
        private ZooKeeperNodeManager nodeManager;
        private ZooKeeperMembershipManager zooKeeperMembershipManager;
        private String parentPath;
        private List<String> currentChilds;


        public ZooKeeperCommandHandler(ZooKeeperStateManager stateManager,
                                       ConfigurationContext configurationContext,
                                       ZooKeeperNodeManager nodeManager,
                                       ZooKeeperMembershipManager membershipManager,
                                       String parentPath, List<String> currentChilds) {
            this.stateManager = stateManager;
            this.configurationContext = configurationContext;
            this.nodeManager = nodeManager;
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

//            System.out.println("\nStart loop" + this.toString() + zooKeeperMembershipManager.getLocalMember());
            for (int i = currentId; i < currentChilds.size(); i++) {
//                System.out.println(currentChilds.get(i) + " processing... ");

                ZkClient zk = ZooKeeperUtils.getZookeeper();
                String cmName = currentChilds.get(i);

                if (zk.exists(parentPath + "/" + cmName)) {
                    ClusteringCommand cm = (ClusteringCommand) zk
                            .readData(parentPath + "/" + cmName);
                    try {
                        processMessage(cm);
                        log.info("Command " + cm.toString() + " processed successfully by member : "
                                + zooKeeperMembershipManager.getLocalMember());
                    } catch (ClusteringFault e) {
                        log.error("Command " + cm.toString() + " processing failed : " + e.toString());

                    }

                }

                currentId++;
            }
//            System.out.println("end loop\n" + this.toString() + zooKeeperMembershipManager.getLocalMember());

            // wait specified time and check whether another command executed during that time
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.info(e.toString());
            }

            if (startTime == startTimeStatic) {
//                System.out.println("timeout reached");
                try {
                    timeoutCommandProcess();
                } catch (Exception e) {
                    log.error("Zkserver offline");
                }
            }
        }

        /**
         * After the timeout if no other command events triggered executed the remaining commands
         */
        public void timeoutCommandProcess() {
            String domainName = new String(zooKeeperMembershipManager.getDomain());
            String commandPath = "/" + domainName
                    + ZooKeeperConstants.COMMANDS_BASE_NAME;

            List<String> currentChilds = ZooKeeperUtils.getZookeeper().getChildren(
                    commandPath);
            Collections.sort(currentChilds);

//            System.out.println("\nTimeout Start Loop" + this.toString()
//                                      + zooKeeperMembershipManager.getLocalMember());
            for (int i = currentId; i < currentChilds.size(); i++) {
//                System.out.println("currentid : " + currentId);
//                System.out.println(currentChilds.get(i)
//                        + " after timeout processing...");

                ZkClient zk = ZooKeeperUtils.getZookeeper();
                String cmName = currentChilds.get(i);

                if (zk.exists(commandPath + "/" + cmName)) {
                    ClusteringCommand cm = (ClusteringCommand) zk
                            .readData(commandPath + "/" + cmName);
                    try {
                        processMessage(cm);
                        log.info("Command " + cm.toString() + " processed successfully by member : "
                                + zooKeeperMembershipManager.getLocalMember());
                    } catch (ClusteringFault e) {
                        log.error("Command " + cm.toString() + " processing failed : " + e.toString());
                    }
                }

                currentId++;
            }
//            System.out.println("Timeout End Loop " + this.toString()
//                                      + zooKeeperMembershipManager.getLocalMember());
        }

        /**
         * process the command object
         *
         * @param command pass the command object to process
         * @throws ClusteringFault throws ClusteringFault if processing failed
         */
        private void processMessage(ClusteringCommand command)
                throws ClusteringFault {
            if (command instanceof StateClusteringCommand && stateManager != null) {
                command.execute(configurationContext);
            } else if (command instanceof NodeManagementCommand
                    && nodeManager != null) {
                command.execute(configurationContext);
            } else if (command instanceof GroupManagementCommand) {
                command.execute(configurationContext);
            }
//          System.out.println("processed");
        }
    }
}