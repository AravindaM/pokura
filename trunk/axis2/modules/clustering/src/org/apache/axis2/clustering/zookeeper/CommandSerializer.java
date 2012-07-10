package org.apache.axis2.clustering.zookeeper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.axis2.clustering.ClusteringCommand;

public class CommandSerializer implements ZkSerializer {

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
        	throw new ZkMarshallingError(e);
            //return null;
        }
	}

	public ClusteringCommand deserialize(byte[] bytes) throws ZkMarshallingError {
		ClusteringCommand command = null;
        try{
        ObjectInputStream ois = 
            new ObjectInputStream(new ByteArrayInputStream(bytes));
        ois.close();
        command = (ClusteringCommand)ois.readObject();
        }catch (Exception e) {
            // TODO: handle exception
        }
        return command;
	}

}
