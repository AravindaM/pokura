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