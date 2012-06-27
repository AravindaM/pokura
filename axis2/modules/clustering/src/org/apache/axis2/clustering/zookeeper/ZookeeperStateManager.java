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
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.state.StateClusteringCommand;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.context.AbstractContext;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;

public class ZookeeperStateManager implements StateManager{

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

	public void updateContext(AbstractContext context) throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void updateContext(AbstractContext context, String[] propertyNames)
			throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void updateContexts(AbstractContext[] contexts)
			throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void replicateState(StateClusteringCommand command)
			throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void removeContext(AbstractContext context) throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public boolean isContextClusterable(AbstractContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setConfigurationContext(
			ConfigurationContext configurationContext) {
		// TODO Auto-generated method stub
		
	}

	public void setReplicationExcludePatterns(String contextType, List patterns) {
		// TODO Auto-generated method stub
		
	}

	public Map getReplicationExcludePatterns() {
		// TODO Auto-generated method stub
		return null;
	}

}
