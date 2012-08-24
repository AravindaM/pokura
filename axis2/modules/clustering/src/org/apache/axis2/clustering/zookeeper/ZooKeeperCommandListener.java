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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZooKeeperCommandListener implements IZkChildListener {

    private static Log log = LogFactory.getLog(ZooKeeperCommandListener.class);
    private ZooKeeperStateManager stateManager;
    private ConfigurationContext configurationContext;
    private ZooKeeperNodeManager nodeManager;
    private ZooKeeperMembershipManager zooKeeperMembershipManager;
    private Object syncObject = new Object();
    private static Object deleteUpdateSync = new Object();
    private int commandDeleteThreshold;
    private int commandUpdateThreshold;

    String lastCommandName;

    static long startTimeStatic;

    /**
     * @param lastCommandName      Last command in the command list
     * @param stateManager         ZooKeeperStateManager instance of the member
     * @param configurationContext ConfigurationContext instance of the member
     * @param nodeManager          ZooKeeperNodeManager instance of the member
     * @param membershipManager    ZooKeeperMembershipManager instance of the member
     */
    public ZooKeeperCommandListener(String lastCommandName,
                                    ZooKeeperStateManager stateManager,
                                    ConfigurationContext configurationContext,
                                    ZooKeeperNodeManager nodeManager,
                                    ZooKeeperMembershipManager membershipManager,
                                    int commandDeleteThreshold,
                                    int commandUpdateThreshold) {
        this.lastCommandName = lastCommandName;
        this.stateManager = stateManager;
        this.configurationContext = configurationContext;
        this.nodeManager = nodeManager;
        this.zooKeeperMembershipManager = membershipManager;
        this.commandDeleteThreshold = commandDeleteThreshold;
        this.commandUpdateThreshold = commandUpdateThreshold;

    }

    public void handleChildChange(String parentPath, List<String> currentChilds) {
        // each event is handled by separate threads
        new ZooKeeperCommandHandler(stateManager, configurationContext, nodeManager, zooKeeperMembershipManager,
                parentPath, currentChilds, commandDeleteThreshold, commandUpdateThreshold).start();
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
        private int commandDeleteThreshold;
        private int commandUpdateThreshold;

        public ZooKeeperCommandHandler(ZooKeeperStateManager stateManager,
                                       ConfigurationContext configurationContext,
                                       ZooKeeperNodeManager nodeManager,
                                       ZooKeeperMembershipManager membershipManager,
                                       String parentPath, List<String> currentChilds,
                                       int commandDeleteThreshold,
                                       int commandUpdateThreshold) {
            this.stateManager = stateManager;
            this.configurationContext = configurationContext;
            this.nodeManager = nodeManager;
            this.zooKeeperMembershipManager = membershipManager;
            this.parentPath = parentPath;
            this.currentChilds = currentChilds;
            this.commandDeleteThreshold = commandDeleteThreshold;
            this.commandUpdateThreshold = commandUpdateThreshold;
        }

        @Override
        public void run() {

            // call command processing method for each new command
            long startTime = System.nanoTime();
            startTimeStatic = startTime;

            synchronized (syncObject) {
                Collections.sort(currentChilds);

                int id;
                if (lastCommandName == null) {
                    id = 0;
                } else {
                    id = currentChilds.indexOf(ZooKeeperUtils.commandNameofIndex(ZooKeeperUtils.getCommandID(lastCommandName))) + 1;

                    if (id < currentChilds.size() && id > -1) {

                        if (ZooKeeperUtils.getCommandID(currentChilds.get(id)) <= ZooKeeperUtils.getCommandID(lastCommandName)) {
                            id = -1;
                        }
                    }
                }

              //delete processed commands to reduce the size of the command list
                if (currentChilds.size() > commandDeleteThreshold && id > -1) {

                		
                        String lastCommandPath = "/" + zooKeeperMembershipManager.getDomainName() + ZooKeeperConstants.LAST_COMMAND_BASE_NAME;
                        String commandPath = "/" + zooKeeperMembershipManager.getDomainName() + ZooKeeperConstants.COMMANDS_BASE_NAME;
                        if (ZooKeeperUtils.getZookeeper().exists(lastCommandPath)) {

                            ArrayList<String> lastCommandList = (ArrayList) ZooKeeperUtils.getZookeeper().getChildren(lastCommandPath);
                           if(lastCommandList.size()>zooKeeperMembershipManager.getMembers().size()){
                            	
                           
                            String deleteUpto = ZooKeeperUtils.getLastCommand(zooKeeperMembershipManager.getDomainName());

                            if (deleteUpto != null) {
                                if (ZooKeeperUtils.getZookeeper().exists(commandPath + "/" + deleteUpto)) {
                                    ArrayList<String> commandList = (ArrayList) ZooKeeperUtils.getZookeeper().getChildren(commandPath);

                                    Collections.sort(lastCommandList);
                                    Collections.sort(commandList);

                                    for (int i = 0; i <= commandList.indexOf(deleteUpto); i++) {
                                        try {
                                            ZooKeeperUtils.getDirectZookeeper().delete(commandPath + "/" + commandList.get(i), -1, null, null);
                                        } catch (Exception e) {
                                            log.error(e.getMessage());
                                        }
                                    }

                                    try {
                                        ZooKeeperUtils.getZookeeper().delete(lastCommandPath + "/" + lastCommandList.get(0));
                                    } catch (Exception e) {
                                        log.error(e.getMessage());
                                    }
                                    log.info("Commands deleted upto : " + deleteUpto);
                                }
                            }
                        }
                        }
                    }
                
                if (id < currentChilds.size() && id > -1) {
                	
                    for (int i = id; i < currentChilds.size(); i++) {

                        String cmName = currentChilds.get(i);

                        if (ZooKeeperUtils.getZookeeper().exists(parentPath + "/" + cmName)) {
                            try {
                                ClusteringCommand cm = (ClusteringCommand) ZooKeeperUtils.getZookeeper()
                                        .readData(parentPath + "/" + cmName);

                                try {
                                    processMessage(cm);
                                    lastCommandName = cmName;
                                    log.info(cmName + " " + cm.toString() + " processed successfully by member : "
                                            + zooKeeperMembershipManager.getLocalMember());
                                } catch (ClusteringFault e) {
                                    log.error(cmName + " " + cm.toString() + " processing failed : " + e.toString());

                                }
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }

                        }

                    }
                    //update the lastCommand entry

                    if (currentChilds.size() > commandUpdateThreshold) {
                            String parentPath = "/" + zooKeeperMembershipManager.getDomainName() + ZooKeeperConstants.COMMANDS_BASE_NAME;
                            String lastCommandParentPath = "/" + zooKeeperMembershipManager.getDomainName() + ZooKeeperConstants.LAST_COMMAND_BASE_NAME;

                            if (ZooKeeperUtils.getZookeeper().exists(parentPath + "/" + lastCommandName) && !(ZooKeeperUtils.getZookeeper().exists(lastCommandParentPath+ "/" + lastCommandName))) {
                                try{
                            	ZooKeeperUtils.createLastCommandEntry(lastCommandName, zooKeeperMembershipManager.getDomainName());
                                log.info("lastcommand entry updated with " + lastCommandName);
                                }catch(Exception e){
                                	log.info("lastcommand entry enrty " + lastCommandName +" exists");
                                }
                            }
                        

                    }
                }

                

                }
            


            // wait specified time and check whether another command executed during that time
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }


            synchronized (syncObject) {
                if (startTime == startTimeStatic) {
                    try {
                        timeoutCommandProcess();
                    } catch (Exception e) {
                        log.error("Zkserver offline");
                    }
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

            int id;
            if (lastCommandName == null) {
                id = 0;
            } else {
                id = currentChilds.indexOf(ZooKeeperUtils.commandNameofIndex(ZooKeeperUtils.getCommandID(lastCommandName))) + 1;

                if (id < currentChilds.size() && id > -1) {

                    if (ZooKeeperUtils.getCommandID(currentChilds.get(id)) <= ZooKeeperUtils.getCommandID(lastCommandName)) {
                        id = -1;
                    }
                }
            }

            if (id < currentChilds.size() && id > -1) {

                for (int i = id; i < currentChilds.size(); i++) {

                    ZkClient zk = ZooKeeperUtils.getZookeeper();
                    String cmName = currentChilds.get(i);

                    if (zk.exists(commandPath + "/" + cmName)) {
                        try {
                            ClusteringCommand cm = (ClusteringCommand) zk
                                    .readData(commandPath + "/" + cmName);
                            try {
                                processMessage(cm);
                                lastCommandName = cmName;
                                log.info(cmName + " " + cm.toString() + " processed successfully by member : "
                                        + zooKeeperMembershipManager.getLocalMember());
                            } catch (ClusteringFault e) {
                                log.error(cmName + " " + cm.toString() + " processing failed : " + e.toString());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }


                }
            }
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
        }

    }
}
