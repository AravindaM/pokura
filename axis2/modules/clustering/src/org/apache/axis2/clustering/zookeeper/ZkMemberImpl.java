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

import java.util.UUID;

public class ZkMemberImpl implements ZkMember {

    private static final long serialVersionUID = 3453754554654424234L;
    private byte[] zkHost;
    private String zkHostName;
    private UUID zkNodeId;
    private int port;
    private byte[] domain;
    private byte[] payLoad;

    public String getZkHostName() {
        return zkHostName;
    }

    public UUID getZkNodeID() {
        return zkNodeId;
    }

    public int getPort() {
        return port;
    }

    public byte[] getDomain() {
        return domain;
    }

    public byte[] getPayLoad() {
        return payLoad;
    }

    public boolean isAlive() {
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ZkMember)) {
            return false;
        }
        return this.zkNodeId.equals(((ZkMember) obj).getZkNodeID());
    }

    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + zkNodeId.hashCode();
        hash = hash * 31 + (zkHostName == null ? 0 : zkHostName.hashCode());
        return hash;
    }

    public void setZkHostName(String zkHostName) {
        this.zkHostName = zkHostName;
    }

    public void setZkNodeId(UUID zkNodeId) {
        this.zkNodeId = zkNodeId;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDomain(byte[] domain) {
        this.domain = domain;
    }

    public void setPayLoad(byte[] payLoad) {
        this.payLoad = payLoad;
    }

    public String toString() {
        return this.getClass().getName();
    }

    public byte[] getZkHost() {
        return zkHost;
    }

    public void setZkHost(byte[] zkHost) {
        this.zkHost = zkHost;
    }
}

