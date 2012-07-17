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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.clustering.ClusteringCommand;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.ClusteringMessage;
import org.apache.axis2.clustering.Member;
import org.apache.axis2.clustering.MembershipScheme;
import org.apache.axis2.clustering.RequestBlockingHandler;
import org.apache.axis2.clustering.management.GroupManagementAgent;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.clustering.tribes.MembershipManager;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.PhaseRule;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.DispatchPhase;
import org.apache.axis2.engine.Phase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZooKeeperClusteringAgent implements ClusteringAgent{

	private static final Log log = LogFactory.getLog(ZooKeeperClusteringAgent.class);
	
	private ZooKeeperNodeManager configurationManager;
    private ZooKeeperStateManager contextManager;
    private final Map<String, GroupManagementAgent> groupManagementAgents =
        new HashMap<String, GroupManagementAgent>();
    private ZooKeeperMembershipManager primaryMembershipManager;

    private ZooKeeperCommandListener axis2CommandChildListener;
    private	ZooKeeperCommandSubscriber axis2CommandReceiver;
    private ZooKeeperMemberListener axis2MemberListener;
    private ZooKeeperMemberSubscriber axis2MemberReceiver;
    private final HashMap<String, Parameter> parameters;
    
    private ConfigurationContext configurationContext;
    
    
    
    
    public ZooKeeperCommandListener getAxis2CommandChildListener() {
		return axis2CommandChildListener;
	}

	public void setAxis2CommandChildListener(
			ZooKeeperCommandListener axis2CommandChildListener) {
		this.axis2CommandChildListener = axis2CommandChildListener;
	}

	public ZooKeeperCommandSubscriber getAxis2CommandReceiver() {
		return axis2CommandReceiver;
	}

	public void setAxis2CommandReceiver(ZooKeeperCommandSubscriber axis2CommandReceiver) {
		this.axis2CommandReceiver = axis2CommandReceiver;
	}

	public ZooKeeperMemberListener getAxis2MemberListener() {
		return axis2MemberListener;
	}

	public void setAxis2MemberListener(ZooKeeperMemberListener axis2MemberListener) {
		this.axis2MemberListener = axis2MemberListener;
	}

	public ZooKeeperMemberSubscriber getAxis2MemberReceiver() {
		return axis2MemberReceiver;
	}

	public void setAxis2MemberReceiver(ZooKeeperMemberSubscriber axis2MemberReceiver) {
		this.axis2MemberReceiver = axis2MemberReceiver;
	}

	
    /**
     * Static members
     */
    private List<org.apache.axis2.clustering.Member> members;
    
	
	public ZooKeeperClusteringAgent() {
		parameters = new HashMap<String, Parameter>();
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
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Parameter> getParameters() {
		ArrayList<Parameter> list = new ArrayList<Parameter>();
		for (String msg : parameters.keySet()) {
            list.add(parameters.get(msg));
        }
		return list;
	}

	public boolean isParameterLocked(String parameterName) {
		Parameter parameter = parameters.get(parameterName);
        return parameter != null && parameter.isLocked();
	}
	
	public void init() throws ClusteringFault {
        log.info("Initializing cluster...");
        addRequestBlockingHandlerToInFlows();
        primaryMembershipManager = new ZooKeeperMembershipManager(configurationContext);
        byte[] domain = getClusterDomain();
        log.info("Cluster domain: " + new String(domain));
        primaryMembershipManager.setDomain(domain);
        ZkMember zkm = new ZkMemberImpl();
        zkm.setDomain(domain);
        primaryMembershipManager.setLocalMember(zkm);
        
      //  axis2CommandReceiver = new Axis2CommandReceiver(primaryMembershipManager);
        axis2MemberReceiver =  new ZooKeeperMemberSubscriber(primaryMembershipManager);
        
       // axis2CommandReceiver.startRecieve();
        axis2MemberReceiver.startRecieve();
        
        MembershipScheme mm = new ZooKeeperMembershipScheme(primaryMembershipManager);
        mm.joinGroup();
        
        //setMaximumRetries();
        //configureMode(domain);
       // configureMembershipScheme(domain, mode.getMembershipManagers());
       // setMemberInfo();
        
		
	}

	public StateManager getStateManager() {
		return contextManager;
	}

	public NodeManager getNodeManager() {
		return configurationManager;
	}

	public void setStateManager(StateManager stateManager) {
		this.contextManager = (ZooKeeperStateManager)stateManager;
	}

	public void setNodeManager(NodeManager nodeManager) {
		this.configurationManager = (ZooKeeperNodeManager)nodeManager;
	}

	public void shutdown() throws ClusteringFault {
		// TODO Auto-generated method stub
		
	}

	public void setConfigurationContext(
			ConfigurationContext configurationContext) {
		this.configurationContext = configurationContext;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public List<Member> getMembers() {
		return this.members;
	}

	public void addGroupManagementAgent(GroupManagementAgent agent,
			String applicationDomain) {
		log.info("Managing group application domain " + applicationDomain +
                " using agent " + agent.getClass());
		groupManagementAgents.put(applicationDomain, agent);
		
	}

	public GroupManagementAgent getGroupManagementAgent(String applicationDomain) {
		return groupManagementAgents.get(applicationDomain);
	}

	 /**
     * Get the clustering domain to which this node belongs to
     *
     * @return The clustering domain to which this node belongs to
     */
    private byte[] getClusterDomain() {
        Parameter domainParam = getParameter(ClusteringConstants.Parameters.DOMAIN);
        byte[] domain;
        if (domainParam != null) {
            domain = ((String) domainParam.getValue()).getBytes();
        } else {
            domain = ClusteringConstants.DEFAULT_DOMAIN.getBytes();
        }
        return domain;
    }
    
	public Set<String> getDomains() {
		return groupManagementAgents.keySet();
	}
	 /**
     * Set the maximum number of retries, if message sending to a particular node fails
     */
    private void setMaximumRetries() {
       //TODO create set max Retries if applicable for ZooKeeper 
    }
    /*private void configureMode(byte[] domain) {
//        if (clusterManagementMode) {
//            mode = new ClusterManagementMode(domain, groupManagementAgents, primaryMembershipManager);
//            for (GroupManagementAgent agent : groupManagementAgents.values()) {
//
//                if (agent instanceof DefaultGroupManagementAgent) {
//                    ((DefaultGroupManagementAgent) agent).setSender(channelSender);
//                }
//            }
//        } else {
//            mode = new ApplicationMode(domain, primaryMembershipManager);
//        }
//        mode.init(channel);
    }*/

	/**
     * A RequestBlockingHandler, which is an implementation of
     * {@link org.apache.axis2.engine.Handler} is added to the InFlow & InFaultFlow. This handler
     * is used for rejecting Web service requests until this node has been initialized. This handler
     * can also be used for rejecting requests when this node is reinitializing or is in an
     * inconsistent state (which can happen when a configuration change is taking place).
     */
    private void addRequestBlockingHandlerToInFlows() {
        AxisConfiguration axisConfig = configurationContext.getAxisConfiguration();
        for (Object o : axisConfig.getInFlowPhases()) {
            Phase phase = (Phase) o;
            if (phase instanceof DispatchPhase) {
                RequestBlockingHandler requestBlockingHandler = new RequestBlockingHandler();
                if (!phase.getHandlers().contains(requestBlockingHandler)) {
                    PhaseRule rule = new PhaseRule("Dispatch");
                    rule.setAfter("SOAPMessageBodyBasedDispatcher");
                    rule.setBefore("InstanceDispatcher");
                    HandlerDescription handlerDesc = requestBlockingHandler.getHandlerDesc();
                    handlerDesc.setHandler(requestBlockingHandler);
                    handlerDesc.setName(ClusteringConstants.REQUEST_BLOCKING_HANDLER);
                    handlerDesc.setRules(rule);
                    phase.addHandler(requestBlockingHandler);

                    log.debug("Added " + ClusteringConstants.REQUEST_BLOCKING_HANDLER +
                              " between SOAPMessageBodyBasedDispatcher & InstanceDispatcher to InFlow");
                    break;
                }
            }
        }
        for (Object o : axisConfig.getInFaultFlowPhases()) {
            Phase phase = (Phase) o;
            if (phase instanceof DispatchPhase) {
                RequestBlockingHandler requestBlockingHandler = new RequestBlockingHandler();
                if (!phase.getHandlers().contains(requestBlockingHandler)) {
                    PhaseRule rule = new PhaseRule("Dispatch");
                    rule.setAfter("SOAPMessageBodyBasedDispatcher");
                    rule.setBefore("InstanceDispatcher");
                    HandlerDescription handlerDesc = requestBlockingHandler.getHandlerDesc();
                    handlerDesc.setHandler(requestBlockingHandler);
                    handlerDesc.setName(ClusteringConstants.REQUEST_BLOCKING_HANDLER);
                    handlerDesc.setRules(rule);
                    phase.addHandler(requestBlockingHandler);

                    log.debug("Added " + ClusteringConstants.REQUEST_BLOCKING_HANDLER +
                              " between SOAPMessageBodyBasedDispatcher & InstanceDispatcher to InFaultFlow");
                    break;
                }
            }
        }
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
