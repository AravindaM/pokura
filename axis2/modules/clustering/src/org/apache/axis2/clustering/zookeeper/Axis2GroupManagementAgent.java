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

import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.Member;
import org.apache.axis2.clustering.management.GroupManagementAgent;
import org.apache.axis2.clustering.management.GroupManagementCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class Axis2GroupManagementAgent implements GroupManagementAgent{

    private static final Log log = LogFactory.getLog(Axis2GroupManagementAgent.class);
    private final List<Member> members = new ArrayList<Member>();
    private Axis2MembershipManager membershipManager;
    private String description;

    public void setDescription(String description) {
        this.description=description;
    }

    public String getDescription() {
        return description;
    }

    public void applicationMemberAdded(Member member) {

    }

    public void applicationMemberRemoved(Member member) {

    }

    public List<Member> getMembers() {
        return members;
    }

    public void send(GroupManagementCommand command) throws ClusteringFault {

    }

    public void setMembershipManager(Axis2MembershipManager membershipManager) {
        this.membershipManager = membershipManager;
    }
}
