package com.rpc.algorithm;

import java.util.Map;
import java.util.Vector;

public interface LoadAlgorithm {
	
	public String selector(Vector<String> serviceList,Map<String,Integer> serviceWeightConfigMap);
	
}
