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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.ClusteringMessage;
import org.apache.axis2.clustering.Member;
import org.apache.axis2.clustering.management.GroupManagementAgent;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;

public class ZookeeperClusteringAgent implements ClusteringAgent{

	public void addParameter(Parameter param) throws AxisFault {
		// TODO Auto-generated method stub
		
	}

	public void removeParameter(Parameter param) throws AxisFault {
		// TODO Auto-generated method stub
		
	}

	public void deserializeParameters(OMElement parameterElement)
			throws AxisFault {
		// TODO Auto-generated method stub
		
	}

	public Parameter getParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Parameter> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isParameterLocked(String parameterName) {
		// TODO Auto-generated method stub
		return false;
	}

	public void init() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public StateManager getStateManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeManager getNodeManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStateManager(StateManager stateManager) {
		// TODO Auto-generated method stub
		
	}

	public void setNodeManager(NodeManager nodeManager) {
		// TODO Auto-generated method stub
		
	}

	public void shutdown() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void setConfigurationContext(
			ConfigurationContext configurationContext) {
		// TODO Auto-generated method stub
		
	}

	public void setMembers(List<Member> members) {
		// TODO Auto-generated method stub
		
	}

	public List<Member> getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addGroupManagementAgent(GroupManagementAgent agent,
			String applicationDomain) {
		// TODO Auto-generated method stub
		
	}

	public GroupManagementAgent getGroupManagementAgent(String applicationDomain) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getDomains() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCoordinator() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<ClusteringCommand> sendMessage(ClusteringMessage msg,
			boolean isRpcMessage) throws ClusteringFault {
		// TODO Auto-generated method stub
		return null;
	}

	 public void finalize(){
	      
	    }
}
