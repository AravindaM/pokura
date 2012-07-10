package org.apache.axis2.clustering.zookeeper;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;

public class Axis2MemberListener implements IZkChildListener {

	public void handleChildChange(String parentPath, List<String> currentChilds)
			throws Exception {
		// TODO This class should be able to handle member events that are generated for this listner
		
	}

}
