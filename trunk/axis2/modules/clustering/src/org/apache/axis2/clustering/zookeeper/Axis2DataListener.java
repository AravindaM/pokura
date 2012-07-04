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

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;



public class Axis2DataListener implements Watcher {
	
	private WatchedEvent event;
	private String path;
	private EventType eventType;
	
	public void process(WatchedEvent event) {
		this.event = event; 
		path = event.getPath();
		eventType = event.getType();

		switch (event.getType()) {
		case None:

			break;
		case NodeCreated:

			break;
		case NodeDataChanged:

			break;
		case NodeDeleted:

			break;
		case NodeChildrenChanged:

			break;
		default:
			break;
		}
	}

	public void handleKeeperStateEvent(){
		switch (event.getState()) {
		case AuthFailed:

			break;
		case Disconnected:

			break;
		case Expired:

			break;
		case SyncConnected:

			break;


		default:
			break;
		}

	}

	public void handleNodeCreateEvent(){
		
	}

	public void handleNodeDataChangeEvent(){

	}

	public void handleNodeDeletedEvent(){

	}

	public void handleNodeChildrenChangedEvent(){
		 
	}
}
