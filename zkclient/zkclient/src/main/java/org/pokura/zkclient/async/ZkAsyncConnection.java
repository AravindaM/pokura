package org.pokura.zkclient.async;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.I0Itec.zkclient.ZkConnection;
import org.apache.log4j.Logger;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class ZkAsyncConnection implements IZkAsyncConnection{

    private static final Logger LOG = Logger.getLogger(ZkConnection.class);

    /** It is recommended to use quite large sessions timeouts for ZooKeeper. */
    private static final int DEFAULT_SESSION_TIMEOUT = 30000;

    private ZooKeeper _zk = null;
    private Lock _zookeeperLock = new ReentrantLock();

    private final String _servers;
    private final int _sessionTimeOut;

    public ZkAsyncConnection(String zkServers) {
        this(zkServers, DEFAULT_SESSION_TIMEOUT);
    }

    public ZkAsyncConnection(String zkServers, int sessionTimeOut) {
        _servers = zkServers;
        _sessionTimeOut = sessionTimeOut;
    }
    
	@Override
	public void connect(Watcher watcher) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(String path, byte[] data, CreateMode mode,StringCallback cb,Object ctx)
			throws KeeperException, InterruptedException {
		_zk.create(path, data, Ids.OPEN_ACL_UNSAFE, mode, cb, ctx);
	}

	@Override
	public void delete(String path,VoidCallback cb,Object ctx) throws InterruptedException,
			KeeperException {
		_zk.delete(path, -1, cb, ctx);
	}

	@Override
	public void exists(String path, boolean watch,StatCallback cb,Object ctx) throws KeeperException,
			InterruptedException {
		_zk.exists(path, watch, cb, ctx);
	}

	@Override
	public void getChildren(String path, boolean watch,ChildrenCallback cb,Object ctx)
			throws KeeperException, InterruptedException {
		 _zk.getChildren(path, watch, cb, ctx);
	}

	@Override
	public void readData(String path, Stat stat, boolean watch,DataCallback cb, Object ctx)
			throws KeeperException, InterruptedException {
			_zk.getData(path, watch, cb, ctx);
	}

	@Override
	public void writeData(String path, byte[] data, int expectedVersion,StatCallback cb, Object ctx)
			throws KeeperException, InterruptedException {
			_zk.setData(path, data, expectedVersion, cb, ctx);
	}

	@Override
	public String getServers() {
		return _servers;
	}

}
