package org.apache.axis2.clustering.zookeeper;

import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.clustering.ClusteringConstants;
import org.apache.axis2.clustering.management.NodeManager;
import org.apache.axis2.clustering.state.StateManager;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.util.Utils;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZooKeeperStateReplicationTest extends TestCase {
	private static Log log = LogFactory
			.getLog(ZooKeeperStateReplicationTest.class);
	private static final String TEST_SERVICE_NAME = "testService";
	ZkServer zks;
	private static final Parameter domainParam = new Parameter(
			ClusteringConstants.Parameters.DOMAIN, "axis2.domain."
					+ UIDGenerator.generateUID());
	
	private static Parameter serverString;
	// --------------- Cluster-1
	// ------------------------------------------------------
	private ClusteringAgent clusterManager1;
	private StateManager ctxMan1;
	private NodeManager configMan1;
	private ConfigurationContext configurationContext1;
	private AxisServiceGroup serviceGroup1;
	@SuppressWarnings("unused")
	private AxisService service1;
	// ---------------------------------------------------------------------------------

	// --------------- Cluster-2
	// ------------------------------------------------------
	private ClusteringAgent clusterManager2;
	private StateManager ctxMan2;
	private NodeManager configMan2;
	private ConfigurationContext configurationContext2;
	private AxisServiceGroup serviceGroup2;
	@SuppressWarnings("unused")
	private AxisService service2;

	// ---------------------------------------------------------------------------------

	protected void setUp() throws Exception {
		// canRunTests();
		// if (!canRunTests) {
		// System.out.println("[WARNING] Aborting clustering tests");
		// return;
		// }
		try{
		zks = new ZkServer("/tmp/zookeepertest/data", "/tmp/zookeepertest/log",
				new IDefaultNameSpace() {

					public void createDefaultNameSpace(ZkClient zkClient) {
						// TODO Auto-generated method stub

					}
				}, 4599);
		zks.start();
		}catch(IllegalStateException e){
			
		}catch (Exception e) {
			// TODO: handle exception
		}

		
		OMElement serversElement = AXIOMUtil.stringToOM("<parameter name='zookeeperServers'><zkServer serverString='localhost:4599'/></parameter>");
		serverString = new Parameter(
					"zookeeperServers", "<parameter name='zookeeperServers'><zkServer serverString='localhost:4599'/></parameter>");
		serverString.setParameterElement(serversElement);
		
		
		// First cluster
	
		configurationContext1 = ConfigurationContextFactory
				.createDefaultConfigurationContext();
		serviceGroup1 = createAxisServiceGroup(configurationContext1);
		service1 = createAxisService(serviceGroup1);
		ctxMan1 = getContextManager();
		configMan1 = getConfigurationManager();
		clusterManager1 = getClusterManager(configurationContext1, ctxMan1,
				configMan1);
		clusterManager1.addParameter(domainParam);
		clusterManager1.addParameter(serverString);
		
		clusterManager1.init();
		System.out
				.println("---------- ClusteringAgent-1 successfully initialized -----------");

		// Second cluster
		configurationContext2 = ConfigurationContextFactory
				.createDefaultConfigurationContext();
		serviceGroup2 = createAxisServiceGroup(configurationContext2);
		service2 = createAxisService(serviceGroup2);
		ctxMan2 = getContextManager();
		configMan2 = getConfigurationManager();
		clusterManager2 = getClusterManager(configurationContext2, ctxMan2,
				configMan2);
		clusterManager2.addParameter(domainParam);
		clusterManager2.addParameter(serverString);

		clusterManager2.init();
		System.out
				.println("---------- ClusteringAgent-2 successfully initialized -----------");

	}

	protected ClusteringAgent getClusterManager(ConfigurationContext configCtx,
			StateManager stateManager, NodeManager configManager)
			throws AxisFault {
		ClusteringAgent clusteringAgent = new ZooKeeperClusteringAgent();
		configCtx.getAxisConfiguration().setClusteringAgent(clusteringAgent);
		clusteringAgent.setNodeManager(configManager);
		clusteringAgent.setStateManager(stateManager);
		clusteringAgent.setConfigurationContext(configCtx);

		return clusteringAgent;
	}

	protected AxisServiceGroup createAxisServiceGroup(
			ConfigurationContext configCtx) throws AxisFault {
		AxisConfiguration axisConfig = configCtx.getAxisConfiguration();
		AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
		axisConfig.addServiceGroup(serviceGroup);
		return serviceGroup;
	}

	protected AxisService createAxisService(AxisServiceGroup serviceGroup)
			throws AxisFault {
		AxisService service = new AxisService(TEST_SERVICE_NAME);
		serviceGroup.addService(service);
		return service;
	}

	protected StateManager getContextManager() throws AxisFault {
		StateManager stateManager = new ZooKeeperStateManager();
		return stateManager;
	}

	protected NodeManager getConfigurationManager() throws AxisFault {
		NodeManager contextManager = new ZooKeeperNodeManager();
		return contextManager;
	}

	public void testSetPropertyInConfigurationContext() throws Exception {
		// if (!canRunTests) {
		// return;
		// }

		{
			String key1 = "configCtxKey";
			String val1 = "configCtxVal1";
			configurationContext1.setProperty(key1, val1);
			ctxMan1.updateContext(configurationContext1);
			Thread.sleep(2000);
			String value = (String) configurationContext2.getProperty(key1);

			assertEquals(val1, value);

//			System.out.println(val1+value);
//			log.info("Reached assertEqual-"+val1+":"+value);

		}

		{
			String key2 = "configCtxKey2";
			String val2 = "configCtxVal1";
			configurationContext2.setProperty(key2, val2);
			ctxMan2.updateContext(configurationContext2);
			Thread.sleep(2000);
			String value = (String) configurationContext1.getProperty(key2);
			assertEquals(val2, value);
		}

	}

	public void testRemovePropertyFromConfigurationContext() throws Exception {

		String key1 = "configCtxKey";
		String val1 = "configCtxVal1";

		// First set the property on a cluster 1 and replicate it
		{
			configurationContext1.setProperty(key1, val1);
			ctxMan1.updateContext(configurationContext1);
			Thread.sleep(2000);
			String value = (String) configurationContext2.getProperty(key1);
			assertEquals(val1, value);
		}

		// Next remove this property from cluster 2, replicate it, and check
		// that it is unavailable in cluster 1
		configurationContext2.removeProperty(key1);
		ctxMan2.updateContext(configurationContext2);
		Thread.sleep(2000);
		String value = (String) configurationContext1.getProperty(key1);
		assertNull(configurationContext2.getProperty(key1));
		assertNull(value);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ZooKeeperUtils.getZookeeper().close();
		if (clusterManager1 != null) {
			clusterManager1.shutdown();
			System.out.println("------ CLuster-1 shutdown complete ------");
		}
		if (clusterManager2 != null) {
			clusterManager2.shutdown();
			System.out.println("------ CLuster-2 shutdown complete ------");
		}
		// MembershipManager.removeAllMembers();
		Thread.sleep(500);

		zks.shutdown();
	}

}
