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

public class ZooKeeperMembershipManager {

    private static Log log = LogFactory.getLog(ZooKeeperMembershipManager.class);

    private byte[] domain;
    private ConfigurationContext configContext;
    private GroupManagementAgent groupManagementAgent;

    private final List<ZkMember> members = new ArrayList<ZkMember>();
    //Member represents this node
    private ZkMember localMember;

    public ZooKeeperMembershipManager() {
    }

    public ZooKeeperMembershipManager(ConfigurationContext configCtxt) {
        this.configContext = configCtxt;
    }

    /**
     * Returns the domain as a byte array
     * @return domain as a byte array
     */
    public byte[] getDomain() {
        return domain;
    }

    /**
     * Returns the domain as a String
     * @return domain as String
     */
    public String getDomainName(){
        return new String(domain);
    }

    /**
     * Sets the domain for this membership manager
     * @param domain the byte array value of the domain
     */
    public void setDomain(byte[] domain) {
        this.domain = domain;
    }

    /**
     * Returns the Configuration Context for this manager
     * @return the configuration context
     */
    public ConfigurationContext getConfigContext() {
        return configContext;
    }
    
    /**
     * Sets the Configuration context of the manager
     * @param configContext the configuration context 
     */
    public void setConfigContext(ConfigurationContext configContext) {
        this.configContext = configContext;
    }

    /**
     * Gets the Group management agent related to this manager
     * @return the Group management agent
     */
    public GroupManagementAgent getGroupManagementAgent() {
        return groupManagementAgent;
    }
    
    /**
     * Sets the Group management agent
     * @param groupManagementAgent Group management agent
     */
    public void setGroupManagementAgent(GroupManagementAgent groupManagementAgent) {
        this.groupManagementAgent = groupManagementAgent;
    }
    
    /**
     * Returns the list of current members in this manager
     * @return list of members
     */
    public List<ZkMember> getMembers() {
        return members;
    }

    /** 
     * Sets a collection of member to the manager
     * @param members the list of members to be added
     */
    public void setMembers(List<ZkMember> members) {
        members.addAll(members);
    }

    /**
     * @param member The New member added to the cluster
     * @return true If the member was added to the <code>members</code> list
     */

    public boolean addMember(ZkMember member) {

        boolean memberExists = members.contains(member);
        boolean belongsToSameDomain = ZooKeeperUtils.areInSameDomain(member, domain);

        if (log.isDebugEnabled()) {
            log.debug("Members List contains " + memberExists);
            log.debug("Memeber belongs to my domain " + belongsToSameDomain);
        }

        //If member already exists or the member is belongs to another domain, no need to add it
        //to the cluster

        if (memberExists || !(belongsToSameDomain)) {
            return false;
        }

        boolean shouldAddMember = (localMember == null) || ZooKeeperUtils.areInSameDomain(member,
                localMember.getDomain());

        //If Member handles the service requests, i.e. Memeber is an application member
        if (groupManagementAgent != null) {
            log.info("Application member " + ZooKeeperUtils.getName(member) + " joined the group"
                    + new String(member.getDomain()));
            groupManagementAgent.applicationMemberAdded(ZooKeeperUtils.toAxis2Member(member));
        }

        if (shouldAddMember) {
            members.add(member);
            if (log.isDebugEnabled()) {
                log.debug("Added member" + ZooKeeperUtils.getName(member) + "to domain" + new String(member.getDomain()));
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
    public boolean memberRemoved(ZkMember member) {
        if (log.isDebugEnabled()) {
            log.debug("Member disappeared" + ZooKeeperUtils.getName(member) + "from domain" + new String(member.getDomain()));
           
        }
        // If this an application domain member
        if (groupManagementAgent != null) {
            groupManagementAgent.applicationMemberRemoved(ZooKeeperUtils.toAxis2Member(member));
        }
        return members.remove(member);
    }
    
    /**
     * Returns the local member of the manager
     * @return local member
     */
    public ZkMember getLocalMember() {
        return localMember;
    }
    
    /**
     * Sets the local member of the manager
     * @param localMember local member
     */
    public void setLocalMember(ZkMember localMember) {
        this.localMember = localMember;
    }
}
