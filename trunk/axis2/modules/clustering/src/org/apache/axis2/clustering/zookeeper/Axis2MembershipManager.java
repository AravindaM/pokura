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
    //Member represents this node
    private ZkMember localMember;

    public Axis2MembershipManager() {
    }

    public Axis2MembershipManager(ConfigurationContext configCtxt) {
        this.configContext = configCtxt;
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

    public void setMembers(List<ZkMember> members) {
        members.addAll(members);
    }

    /**
     * @param member The New member added to the cluster
     * @return true If the member was added to the <code>members</code> list
     */

    public boolean addMember(ZkMember member) {

        boolean memberExists = members.contains(member);
        boolean belongsToSameDomain = ZookeeperUtils.areInSameDomain(member, domain);

        if (log.isDebugEnabled()) {
            log.debug("Members List contains " + memberExists);
            log.debug("Memeber belongs to my domain " + belongsToSameDomain);
        }

        //If member already exists or the member is belongs to another domain, no need to add it
        //to the cluster

        if (memberExists || !(belongsToSameDomain)) {
            return false;
        }
        // TODO What are the other checks to be carried out before adding a member

        boolean shouldAddMember = (localMember == null) || ZookeeperUtils.areInSameDomain(member,
                localMember.getDomain());

        //If Member handles the service requests, i.e. Memeber is an application member
        if (groupManagementAgent != null) {
            log.info("Application member " + ZookeeperUtils.getName(member) + " joined the group"
                    + new String(member.getDomain()));
            groupManagementAgent.applicationMemberAdded(ZookeeperUtils.toAxis2Member(member));
        }

        if (shouldAddMember) {
            members.add(member);
            if (log.isDebugEnabled()) {
                log.debug("Added member" + ZookeeperUtils.getName(member) + "to domain" + new String(member.getDomain()));
            }
            return true;
        }
        // TODO rest of the method impl.
        return false;
    }

    /**
     * When member left the cluster
     *
     * @param member The member that left the cluster
     */
    public void memberRemoved(ZkMember member) {
        if (log.isDebugEnabled()) {
            log.debug("Member disappeared" + ZookeeperUtils.getName(member) + "from domain" + new String(member.getDomain()));
            members.remove(member);
        }
        // If this an application domain member
        if (groupManagementAgent != null) {
            groupManagementAgent.applicationMemberRemoved(ZookeeperUtils.toAxis2Member(member));
        }
    }

    public ZkMember getLocalMember() {
        return localMember;
    }

    public void setLocalMember(ZkMember localMember) {
        this.localMember = localMember;
    }
}
