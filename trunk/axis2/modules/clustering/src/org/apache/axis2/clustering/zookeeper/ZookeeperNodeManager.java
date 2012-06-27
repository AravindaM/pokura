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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.management.NodeManagementCommand;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;

public class ZookeeperNodeManager implements NodeManager {

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

	public void prepare() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void rollback() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void commit() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void exceptionOccurred(Throwable throwable) throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void setConfigurationContext(
			ConfigurationContext configurationContext) {
		// TODO Auto-generated method stub
		
	}

	public void sendMessage(NodeManagementCommand command)
			throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

}
