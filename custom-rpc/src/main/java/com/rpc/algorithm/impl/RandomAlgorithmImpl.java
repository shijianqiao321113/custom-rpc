package com.rpc.algorithm.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.rpc.algorithm.LoadAlgorithm;
import com.rpc.tools.Constant;

public class RandomAlgorithmImpl implements LoadAlgorithm {

	@Override
	public String selector(Vector<String> serviceLists,Map<String,Integer> serviceWeightConfigMap) {
		
		Vector<String> serviceList = new Vector<String>(serviceLists.size());
		serviceList.addAll(serviceLists);
		
		Map<String,int[]> serviceWeightMinMax = new HashMap<String,int[]>();
		Map<String,Integer> serviceWeightMap = new HashMap<String, Integer>();
		int maxWeight = Constant._0;
		for(int i=Constant._0;i<serviceList.size();i++){
			String service = serviceList.get(i);
			if(!serviceWeightConfigMap.containsKey(service)){
				serviceWeightMap.put(service, Constant._1);
			}else{
				Integer weight = serviceWeightConfigMap.get(service);
				serviceWeightMap.put(service, weight == null ? Constant._1 : weight);
			}
			
			serviceWeightMinMax.put(service, new int[]{maxWeight,maxWeight + serviceWeightConfigMap.get(service)});

			maxWeight = maxWeight + serviceWeightConfigMap.get(service);
		}
		
		Boolean isEqual = Boolean.TRUE;
		Integer weight = null;
		for(Map.Entry<String, Integer> m:serviceWeightMap.entrySet()){
			if(weight == null){
				weight = m.getValue();
			}else{
				if(m.getValue() != weight){
					isEqual = Boolean.FALSE;
					break;
				}
			}
		}
		
		if(isEqual){
			Collections.sort(serviceList);
			return serviceList.get(new Random().nextInt(serviceList.size()));
		}
		
		int random = new Random().nextInt(maxWeight)+Constant._1;
		for(Map.Entry<String, int[]> m:serviceWeightMinMax.entrySet()){
			if(random > m.getValue()[Constant._0] && random <= m.getValue()[Constant._1]){
				return m.getKey();
			}
		}
		return null;
	}

}
