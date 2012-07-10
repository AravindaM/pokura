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


import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ZookeeperUtilsTest extends TestCase{
	ZkServer zks;
	
 @Override
	protected void setUp() throws Exception {
	 zks = new ZkServer("/tmp/zookeepertest/data", "/tmp/zookeepertest/log",new IDefaultNameSpace() {
			
			public void createDefaultNameSpace(ZkClient zkClient) {
				// TODO Auto-generated method stub
				
			}
		},4599);
		 zks.start();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		zks.shutdown();
		super.tearDown();
	}


 public void testGetNewMembers(){
	ZkClient zkcli = new ZkClient("localhost:4599");
	ZookeeperUtils.setZookeeperConnection(zkcli);
	ZkMemberImpl member =  new  ZkMemberImpl();
	ZookeeperUtils.setZkMemeber(member);
	zks.shutdown();
 }
}
