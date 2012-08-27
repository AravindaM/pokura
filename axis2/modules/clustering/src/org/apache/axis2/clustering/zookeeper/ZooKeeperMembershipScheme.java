package org.apache.axis2.clustering.zookeeper;

import org.apache.axis2.clustering.ClusteringFault;
import org.apache.axis2.clustering.MembershipScheme;
import org.apache.axis2.clustering.tribes.TribesConstants;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.util.Utils;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZooKeeperMembershipScheme implements MembershipScheme {

	private static final org.apache.commons.logging.Log log = LogFactory.getLog(ZooKeeperMembershipScheme.class);

	private final ZooKeeperMembershipManager membershipManager;
	private final Map<String, Parameter> parameters;
	private final byte[] localDomain;

	public ZooKeeperMembershipScheme(ZooKeeperMembershipManager membershipManager,
			Map<String, Parameter> parameters,
			byte[] domain) {
		this.membershipManager = membershipManager;
		this.parameters = parameters;
		this.localDomain = domain;
	}

	public void init() throws ClusteringFault {
		configureMembership();
		joinGroup();
	}

	public void joinGroup() throws ClusteringFault {
		String domainName = new String(membershipManager.getDomain());
		String memberPath = "/" + domainName + ZooKeeperConstants.MEMEBER_BASE_NAME;

		List<ZkMember> members = ZooKeeperUtils.getZkMembers(memberPath);

		if (!members.isEmpty()) {
			membershipManager.setMembers(members);
		} else {
			log.info("No members found. Local member is the 1st member ");
		}
		//create a node for this member
		ZooKeeperUtils.setZkMemeber(membershipManager.getLocalMember());
	}

	private void configureMembership() throws ClusteringFault {

		ZkMember localMember = new ZkMemberImpl();

		// ------------ START: Configure and add the local member ---------------------
		localMember.setZkNodeId(UUID.randomUUID());

		Parameter localHost = getParameter(ZooKeeperConstants.LOCAL_MEMBER_HOST);
		String host;
		if (localHost != null) {
			host = ((String) localHost.getValue()).trim();
		} else { // In cases where the localhost needs to be automatically figured out
			try {
				try {
					host = Utils.getIpAddress();
				} catch (SocketException e) {
					String msg = "Could not get local IP address";
					log.error(msg, e);
					throw new ClusteringFault(msg, e);
				}
			} catch (Exception e) {
				String msg = "Could not get the localhost name";
				log.error(msg, e);
				throw new ClusteringFault(msg, e);
			}
		}
		localMember.setZkHostName(host);


		Parameter localPort = getParameter(TribesConstants.LOCAL_MEMBER_PORT);

		int port;
		try {
			if (localPort != null) {
				port = Integer.parseInt(((String) localPort.getValue()).trim());
				port = getLocalPort(new ServerSocket(), localMember.getZkHostName(), port, 4000, 1000);
			} else { // In cases where the localport needs to be automatically figured out
				port = getLocalPort(new ServerSocket(), localMember.getZkHostName(), -1, 4000, 1000);
			}
		} catch (IOException e) {
			String msg =
				"Could not allocate the specified port or a port in the range 4000-5000 " +
				"for local host " + localMember.getZkHostName() +
				". Check whether the IP address specified or inferred for the local " +
				"member is correct.";
			log.error(msg, e);
			throw new ClusteringFault(msg, e);
		}

		byte[] payload = "ping".getBytes();
		localMember.setPayLoad(payload);

		localMember.setPort(port);
		localMember.setDomain(localDomain);
		membershipManager.setLocalMember(localMember);

		// ------------ END: Configure and add the local member ---------------------
	}

	protected int getLocalPort(ServerSocket socket, String hostname,
			int preferredPort, int portstart, int retries) throws IOException {
		if (preferredPort != -1) {
			try {
				return getLocalPort(socket, hostname, preferredPort);
			} catch (IOException ignored) {
				// Fall through and try a default port
			}
		}
		InetSocketAddress addr = null;
		if (retries > 0) {
			try {
				return getLocalPort(socket, hostname, portstart);
			} catch (IOException x) {
				retries--;
				if (retries <= 0) {
					log.error("Unable to bind server socket to:" + addr + " throwing error.");
					throw x;
				}
				portstart++;
				try {
					Thread.sleep(50);
				} catch (InterruptedException ignored) {
					ignored.printStackTrace();
				}
				portstart = getLocalPort(socket, hostname, portstart, retries, -1);
			}
		}
		return portstart;
	}

	private int getLocalPort(ServerSocket socket, String hostname, int port) throws IOException {
		InetSocketAddress addr;
		addr = new InetSocketAddress(hostname, port);
		socket.bind(addr);
		log.info("Receiver Server Socket bound to:" + addr);
		socket.setSoTimeout(5);
		socket.close();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {
			ignored.printStackTrace();
		}
		return port;
	}

	public Parameter getParameter(String name) {
		return parameters.get(name);
	}

}
