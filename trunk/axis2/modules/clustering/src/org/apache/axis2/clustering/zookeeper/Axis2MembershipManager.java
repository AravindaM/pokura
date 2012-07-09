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

import org.apache.axis2.clustering.management.GroupManagementAgent;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class Axis2MembershipManager {

    private static Log log = LogFactory.getLog(Axis2MembershipManager.class);

    private byte[] domain;
    private ConfigurationContext configContext;
    private GroupManagementAgent groupManagementAgent;

    private final List<ZkMember> members = new ArrayList<ZkMember>();

    public Axis2MembershipManager(){
    }

    public Axis2MembershipManager(ConfigurationContext configCtxt) {
        this.configContext=configCtxt;
    }

    public byte[] getDomain() {
        return domain;
    }

    public void setDomain(byte[] domain) {
        this.domain = domain;
    }

    public ConfigurationContext getConfigContext() {
        return configContext;
    }

    public void setConfigContext(ConfigurationContext configContext) {
        this.configContext = configContext;
    }

    public GroupManagementAgent getGroupManagementAgent() {
        return groupManagementAgent;
    }

    public void setGroupManagementAgent(GroupManagementAgent groupManagementAgent) {
        this.groupManagementAgent = groupManagementAgent;
    }

/*
    public ZkMember[] getMembers() {
        return members.toArray(new ZkMember[members.size()]);

    }
*/

    public List<ZkMember> getMembers() {
        return members;
    }

    public boolean addMember(ZkMember member){
        if(log.isDebugEnabled()){
            log.debug("Members List contains "+ members.contains(member));
            log.debug("Memeber belongs to my domain "+ZookeeperUtils.isInDomain(member,domain));
        }

        return false;
    }
}
