package org.apache.axis2.clustering.zookeeper;

import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.clustering.management.DefaultNodeManager;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.clustering.state.DefaultStateManager;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.clustering.tribes.TribesClusteringAgent;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.util.Utils;

import junit.framework.TestCase;

public class ZooKeeperStateReplicationTest extends TestCase {
	private static final String TEST_SERVICE_NAME = "testService";

	private static final Parameter domainParam =
		new Parameter(ClusteringConstants.Parameters.DOMAIN,
				"axis2.domain." + UIDGenerator.generateUID());
	// --------------- Cluster-1 ------------------------------------------------------
	private ClusteringAgent clusterManager1;
	private StateManager ctxMan1;
	private NodeManager configMan1;
	private ConfigurationContext configurationContext1;
	private AxisServiceGroup serviceGroup1;
	private AxisService service1;
	//---------------------------------------------------------------------------------

	// --------------- Cluster-2 ------------------------------------------------------
	private ClusteringAgent clusterManager2;
	private StateManager ctxMan2;
	private NodeManager configMan2;
	private ConfigurationContext configurationContext2;
	private AxisServiceGroup serviceGroup2;
	private AxisService service2;
	//---------------------------------------------------------------------------------


	protected void setUp() throws Exception {
		//      canRunTests();
		//      if (!canRunTests) {
		//          System.out.println("[WARNING] Aborting clustering tests");
		//          return;
		//      }

		System.setProperty(ClusteringConstants.LOCAL_IP_ADDRESS, Utils.getIpAddress());

		// First cluster
		configurationContext1 =
			ConfigurationContextFactory.createDefaultConfigurationContext();
		serviceGroup1 = createAxisServiceGroup(configurationContext1);
		service1 = createAxisService(serviceGroup1);
		ctxMan1 = getContextManager();
		configMan1 = getConfigurationManager();
		clusterManager1 = getClusterManager(configurationContext1, ctxMan1, configMan1);
		clusterManager1.addParameter(domainParam);
		clusterManager1.init();
		System.out.println("---------- ClusteringAgent-1 successfully initialized -----------");

		// Second cluster
		configurationContext2 =
			ConfigurationContextFactory.createDefaultConfigurationContext();
		serviceGroup2 = createAxisServiceGroup(configurationContext2);
		service2 = createAxisService(serviceGroup2);
		ctxMan2 = getContextManager();
		configMan2 = getConfigurationManager();
		clusterManager2 = getClusterManager(configurationContext2, ctxMan2, configMan2);
		clusterManager2.addParameter(domainParam);
		clusterManager2.init();
		System.out.println("---------- ClusteringAgent-2 successfully initialized -----------");
	}

	protected ClusteringAgent getClusterManager(ConfigurationContext configCtx,
			StateManager stateManager,
			NodeManager configManager)
	throws AxisFault {
		ClusteringAgent clusteringAgent = new ZookeeperClusteringAgent();
		configCtx.getAxisConfiguration().setClusteringAgent(clusteringAgent);
		clusteringAgent.setNodeManager(configManager);
		clusteringAgent.setStateManager(stateManager);
		clusteringAgent.setConfigurationContext(configCtx);

		return clusteringAgent;
	}
	protected AxisServiceGroup createAxisServiceGroup(ConfigurationContext configCtx)
	throws AxisFault {
		AxisConfiguration axisConfig = configCtx.getAxisConfiguration();
		AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
		axisConfig.addServiceGroup(serviceGroup);
		return serviceGroup;
	}

	protected AxisService createAxisService(AxisServiceGroup serviceGroup) throws AxisFault {
		AxisService service = new AxisService(TEST_SERVICE_NAME);
		serviceGroup.addService(service);
		return service;
	}

	protected StateManager getContextManager() throws AxisFault {
		StateManager stateManager = new ZookeeperStateManager();
		return stateManager;
	}

	protected NodeManager getConfigurationManager() throws AxisFault {
		NodeManager contextManager = new ZookeeperNodeManager();
		return contextManager;
	}
}