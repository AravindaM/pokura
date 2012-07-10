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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class ZkMemberSerializer implements ZkSerializer {

	public byte[] serialize(Object data) throws ZkMarshallingError {
		if (data == null) {
            return null;
          }
        try{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(data);
        return baos.toByteArray();
        }catch (Exception e) {
            return null;
        }
        
	}

	public ZkMember deserialize(byte[] bytes) throws ZkMarshallingError {
		ZkMember zkmember = null;
	        try{
	        ObjectInputStream ois = 
	            new ObjectInputStream(new ByteArrayInputStream(bytes));
	        ois.close();
	        zkmember = (ZkMember)ois.readObject();
	        }catch (Exception e) {
	            // TODO: handle exception
	        }
	        return null;
	}

}
