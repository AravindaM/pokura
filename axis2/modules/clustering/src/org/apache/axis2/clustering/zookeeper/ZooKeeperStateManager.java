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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.state.StateClusteringCommand;
import org.apache.axis2.clustering.state.StateClusteringCommandFactory;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.clustering.state.commands.StateClusteringCommandCollection;
import org.apache.axis2.context.AbstractContext;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.description.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZooKeeperStateManager implements StateManager {

    private final Map<String, Parameter> parameters = new HashMap<String, Parameter>();

    @SuppressWarnings("rawtypes")
    private final Map<String, List> excludedReplicationPatterns = new HashMap<String, List>();

    private ZooKeeperSender sender;

    public ZooKeeperStateManager() {
    }

    public void setSender(ZooKeeperSender sender) {
        this.sender = sender;
    }


    public void addParameter(Parameter param) throws AxisFault {
        parameters.put(param.getName(), param);
    }

    public void removeParameter(Parameter param) throws AxisFault {
        parameters.remove(param.getName());
    }

    public void deserializeParameters(OMElement parameterElement)
            throws AxisFault {
        throw new UnsupportedOperationException();

    }

    public Parameter getParameter(String name) {
        return parameters.get(name);
    }

    public ArrayList<Parameter> getParameters() {
        ArrayList<Parameter> list = new ArrayList<Parameter>();
        for (String msg : parameters.keySet()) {
            list.add(parameters.get(msg));
        }
        return list;
    }

    public boolean isParameterLocked(String parameterName) {
        return getParameter(parameterName).isLocked();
    }
    
    public void updateContext(AbstractContext context) throws ClusteringFault {
        StateClusteringCommand cmd = StateClusteringCommandFactory
                .getUpdateCommand(context, excludedReplicationPatterns, false);
        if (cmd != null) {
            sender.sendToGroup(cmd);
        }

    }
    
    public void updateContext(AbstractContext context, String[] propertyNames)
            throws ClusteringFault {
        StateClusteringCommand cmd =
                StateClusteringCommandFactory.getUpdateCommand(context, propertyNames);
        if (cmd != null) {
            sender.sendToGroup(cmd);
        }
    }
    
    public void updateContexts(AbstractContext[] contexts)
            throws ClusteringFault {
        StateClusteringCommandCollection cmd =
                StateClusteringCommandFactory.getCommandCollection(contexts,
                        excludedReplicationPatterns);
        if (!cmd.isEmpty()) {
            sender.sendToGroup(cmd);
        }

    }

    public void replicateState(StateClusteringCommand command)
            throws ClusteringFault {
        sender.sendToGroup(command);
    }

    public void removeContext(AbstractContext context) throws ClusteringFault {
        StateClusteringCommand cmd = StateClusteringCommandFactory.getRemoveCommand(context);
        sender.sendToGroup(cmd);
    }

    public boolean isContextClusterable(AbstractContext context) {
        return (context instanceof ConfigurationContext) ||
                (context instanceof ServiceContext) ||
                (context instanceof ServiceGroupContext);
    }

    public void setConfigurationContext(
            ConfigurationContext configurationContext) {
        // nothing to do here

    }

    public void setReplicationExcludePatterns(String contextType, ArrayList<?> patterns) {
        excludedReplicationPatterns.put(contextType, patterns);
    }

    @SuppressWarnings("rawtypes")
    public Map<String, List> getReplicationExcludePatterns() {
        return excludedReplicationPatterns;
    }

    /* (non-Javadoc)
      * @see org.apache.axis2.clustering.state.StateManager#setReplicationExcludePatterns(java.lang.String, java.util.List)
      */
    @SuppressWarnings("rawtypes")
    public void setReplicationExcludePatterns(String contextType, List patterns) {
    	 excludedReplicationPatterns.put(contextType, patterns);
    }

}
