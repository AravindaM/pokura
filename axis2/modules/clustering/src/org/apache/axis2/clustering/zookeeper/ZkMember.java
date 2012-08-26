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

import java.io.Serializable;
import java.util.UUID;

public interface ZkMember extends Serializable {

	/**
	 * Returns the Host associated with the member
	 * @return the byte array that represents the host
	 */
    public byte[] getZkHost();

    /**
     * Returns the host name of the member
     * @return host name as String 
     */
    public String getZkHostName();

    /**
     * Returns the Unique id used to identify this member in the 
     * ZooKeeper Quorum
     * @return The UUID value of the identifier 
     */
    public UUID getZkNodeID();

    /**
     * Returns the port number of this member
     * @return the int value of the port
     */
    public int getPort();

    /**
     * Returns the domain that the member is associated to 
     * @return the byte array value of the domain
     */
    public byte[] getDomain();

    /**
     * Returns whether the member is alive
     * @return true if the member is active and alive return false if not
     */
    public boolean isAlive();
    /**
     * Returns the payload of the member, the payload contains the Axis2 Member
     * @return the byte array of the payload
     */
    public byte[] getPayLoad();

    /**
     * Sets  the Host associated with the member
     * @param zkHost - the byte array that represents the host
     */
    public void setZkHost(byte[] zkHost);

    /**
     * Sets  the host name of the member
     * @param zkHostName - the name of the host in string
     */
    public void setZkHostName(String zkHostName);

    /**
     * Sets the Unique id used to identify this member in the 
     * ZooKeeper Quorum
     * @param zkNodeId - UUID value that represents the node id
     */
    public void setZkNodeId(UUID zkNodeId);

    /**
     * Sets the port number of this member
     * @param port - the port number of the member
     */
    public void setPort(int port);

    /**
     * Sets the domain of the member
     * @param domain -  the byte array value of the domain
     */
    public void setDomain(byte[] domain);

    /**
     * Sets the payload of the member
     * @param payLoad the byte array of the payload
     */
    public void setPayLoad(byte[] payLoad);

    public boolean equals(Object obj);

    public int hashCode();

}
