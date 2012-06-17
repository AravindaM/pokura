package org.pokura.zkclient.async;

import java.util.List;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public interface IZkAsyncConnection {
	   
	    public void connect(Watcher watcher);

	    void close() throws InterruptedException;

	    public void create(String path, byte[] data, CreateMode mode,StringCallback cb,Object ctx) throws KeeperException, InterruptedException;

	    public void delete(String path,VoidCallback cb,Object ctx) throws InterruptedException, KeeperException;

	    public void exists(String path, boolean watch,StatCallback cb,Object ctx) throws KeeperException, InterruptedException;

	    public void getChildren(String path, boolean watch,ChildrenCallback cb,Object ctx) throws KeeperException, InterruptedException;

	    public void readData(String path, Stat stat, boolean watch,DataCallback cb, Object ctx) throws KeeperException, InterruptedException;

	    public void writeData(String path, byte[] data, int expectedVersion,StatCallback cb, Object ctx) throws KeeperException, InterruptedException;

	    public String getServers();
}
