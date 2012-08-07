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

import org.I0Itec.zkclient.ZkClient;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.*;
import org.apache.axis2.clustering.management.GroupManagementAgent;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.clustering.tribes.TribesConstants;
import org.apache.axis2.clustering.tribes.TribesUtil;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.PhaseRule;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.DispatchPhase;
import org.apache.axis2.engine.Phase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class ZooKeeperClusteringAgent implements ClusteringAgent {

    private static final Log log = LogFactory.getLog(ZooKeeperClusteringAgent.class);

    private ZooKeeperNodeManager configurationManager;
    private ZooKeeperStateManager contextManager;
    private final Map<String, GroupManagementAgent> groupManagementAgents =
            new HashMap<String, GroupManagementAgent>();
    private ZooKeeperMembershipManager primaryMembershipManager;

    private ZooKeeperCommandListener axis2CommandChildListener;
    private ZooKeeperCommandSubscriber axis2CommandReceiver;
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

        ZooKeeperCommandSubscriber zooKeeperCommandSubscriber = new ZooKeeperCommandSubscriber(
                contextManager, configurationContext, configurationManager, primaryMembershipManager);
        zooKeeperCommandSubscriber.startRecieve();
        final ZooKeeperSender sender = new ZooKeeperSender(primaryMembershipManager);

        contextManager.setSender(sender);
        axis2CommandReceiver = new ZooKeeperCommandSubscriber(primaryMembershipManager);
        axis2MemberReceiver = new ZooKeeperMemberSubscriber(primaryMembershipManager);

		axis2MemberReceiver =  new ZooKeeperMemberSubscriber(primaryMembershipManager);
        axis2MemberReceiver.startRecieve();

        MembershipScheme memberScheme = new ZooKeeperMembershipScheme(primaryMembershipManager, parameters, domain);
        setMemberInfo();
        memberScheme.init();
        //setMaximumRetries();
        //configureMode(domain);
        // configureMembershipScheme(domain, mode.getMembershipManagers());


    }

    public StateManager getStateManager() {
        return contextManager;
    }

    public NodeManager getNodeManager() {
        return configurationManager;
    }

    public void setStateManager(StateManager stateManager) {
        this.contextManager = (ZooKeeperStateManager) stateManager;
    }

    public void setNodeManager(NodeManager nodeManager) {
        this.configurationManager = (ZooKeeperNodeManager) nodeManager;
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

    private void setMemberInfo() throws ClusteringFault {
        Properties memberInfo = new Properties();
        AxisConfiguration axisConfig = configurationContext.getAxisConfiguration();
        TransportInDescription httpTransport = axisConfig.getTransportIn("http");
        int portOffset = 0;
        if (System.getProperty("portOffset") != null) {
            portOffset = Integer.parseInt(System.getProperty("portOffset"));
        }
        if (httpTransport != null) {
            Parameter port = httpTransport.getParameter("port");
            if (port != null) {
                memberInfo.put("httpPort",
                        String.valueOf(Integer.valueOf((String) port.getValue()) + portOffset));
            }
        }
        TransportInDescription httpsTransport = axisConfig.getTransportIn("https");
        if (httpsTransport != null) {
            Parameter port = httpsTransport.getParameter("port");
            if (port != null) {
                memberInfo.put("httpsPort",
                        String.valueOf(Integer.valueOf((String) port.getValue()) + portOffset));
            }
        }

        memberInfo.setProperty("hostName",
                TribesUtil.getLocalHost(getParameter(TribesConstants.LOCAL_MEMBER_HOST)));

        Parameter propsParam = getParameter("properties");
        if (propsParam != null) {
            OMElement paramEle = propsParam.getParameterElement();
            for (Iterator<?> iter = paramEle.getChildrenWithLocalName("property"); iter.hasNext(); ) {
                OMElement propEle = (OMElement) iter.next();
                OMAttribute nameAttrib = propEle.getAttribute(new QName("name"));
                if (nameAttrib != null) {
                    String attribName = nameAttrib.getAttributeValue();
                    attribName = replaceProperty(attribName, memberInfo);

                    OMAttribute valueAttrib = propEle.getAttribute(new QName("value"));
                    if (valueAttrib != null) {
                        String attribVal = valueAttrib.getAttributeValue();
                        attribVal = replaceProperty(attribVal, memberInfo);
                        memberInfo.setProperty(attribName, attribVal);
                    }
                }
            }
        }

        memberInfo.remove("hostName"); // this was needed only to populate other properties. No need to send it.

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            memberInfo.store(bout, "");
        } catch (IOException e) {
            String msg = "Cannot store member transport properties in the ByteArrayOutputStream";
            log.error(msg, e);
            throw new ClusteringFault(msg, e);
        }
        ZkMember member = primaryMembershipManager.getLocalMember();
        member.setPayLoad(bout.toByteArray());
        primaryMembershipManager.setLocalMember(member);
        // channel.getMembershipService().setPayload(bout.toByteArray());
    }

    private static String replaceProperty(String text, Properties props) {
        int indexOfStartingChars = -1;
        int indexOfClosingBrace;

        // The following condition deals with properties.
        // Properties are specified as ${system.property},
        // and are assumed to be System properties
        while (indexOfStartingChars < text.indexOf("${") &&
                (indexOfStartingChars = text.indexOf("${")) != -1 &&
                (indexOfClosingBrace = text.indexOf("}")) != -1) { // Is a property used?
            String sysProp = text.substring(indexOfStartingChars + 2,
                    indexOfClosingBrace);
            String propValue = props.getProperty(sysProp);
            if (propValue == null) {
                propValue = System.getProperty(sysProp);
            }
            if (propValue != null) {
                text = text.substring(0, indexOfStartingChars) + propValue +
                        text.substring(indexOfClosingBrace + 1);
            }
        }
        return text;
    }

    public Set<String> getDomains() {
        return groupManagementAgents.keySet();
    }

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

    public void setZkInstancesInfo() {
        Parameter paramZkServers = parameters.get("zookeeperServers");

        StringBuilder serveListbuilder = new StringBuilder();
        if (paramZkServers != null) {
            OMElement serversElement = paramZkServers.getParameterElement();
            String serverString = null;

            for (Iterator<?> itr = serversElement.getChildrenWithLocalName("zkServer"); itr.hasNext(); ) {
                OMElement serverChild = (OMElement) itr.next();
                OMAttribute serverAttrib = serverChild.getAttribute(new QName("serverString"));

                if (serverAttrib != null) {
                    serverString = serverAttrib.getAttributeValue();
                }
                serveListbuilder.append(serverString);
                if(itr.hasNext())
                serveListbuilder.append(",");
            }
            serveListbuilder.deleteCharAt(serveListbuilder.length() - 1);
        }
        connectToServer(serveListbuilder.toString());
    }

    public boolean connectToServer(String severList) {
        ZooKeeperUtils.setZookeeperConnection(new ZkClient(severList));
        return true;
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

    public void finalize() {

    }

	public void addGroupManagementAgent(GroupManagementAgent arg0, String arg1,
			String arg2) {
		// TODO Auto-generated method stub
		
	}

	public int getAliveMemberCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public GroupManagementAgent getGroupManagementAgent(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
