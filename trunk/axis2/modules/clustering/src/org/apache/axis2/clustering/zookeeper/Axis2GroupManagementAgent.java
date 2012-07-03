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

import java.util.List;

public class Axis2GroupManagementAgent implements GroupManagementAgent{
    public void setDescription(String description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void applicationMemberAdded(Member member) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void applicationMemberRemoved(Member member) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Member> getMembers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(GroupManagementCommand command) throws ClusteringFault {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
