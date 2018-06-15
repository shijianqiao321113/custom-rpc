package com.rpc.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import com.rpc.algorithm.LoadAlgorithm;
import com.rpc.client.RpcClient;
import com.rpc.tools.Constant;

public class PollingAlgorithmImpl implements LoadAlgorithm {
	
	private static final Object lock = new Object();
	
	private RpcClient rpcClient;
	
	public PollingAlgorithmImpl(RpcClient rpcClient){
		this.rpcClient = rpcClient;
	}

	@Override
	public String selector(Vector<String> serviceList,Map<String,Integer> serviceWeightConfigMap) {
		
		SortedMap<String,Integer> serviceWeightMinMax = new TreeMap<String,Integer>();
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
			
			serviceWeightMinMax.put(service,maxWeight + serviceWeightConfigMap.get(service));

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
			synchronized (PollingAlgorithmImpl.lock) {
				int historySelectServiceIndex = this.rpcClient.getCurrentSelectServiceIndex();
				if(historySelectServiceIndex >= (serviceList.size() - Constant._1)){
					this.rpcClient.setCurrentSelectServiceIndex(Constant._0);
					return serviceList.get(Constant._0);
				}
				this.rpcClient.setCurrentSelectServiceIndex(historySelectServiceIndex);
				return serviceList.get(historySelectServiceIndex + Constant._1);
			}
		}
		List<String> serviceArray = new ArrayList<String>();
		for(int i=Constant._0;i<maxWeight;i++){
			Iterator<Map.Entry<String,Integer>> it = serviceWeightMinMax.entrySet().iterator();  
	        while(it.hasNext()){
	            Map.Entry<String,Integer> entry = it.next();
	            serviceArray.add(entry.getKey());
	            if((entry.getValue() - Constant._1) <= Constant._0){
	            	it.remove();
	            }else{
	            	entry.setValue((entry.getValue() - Constant._1));
	            }
	        }
		}
		synchronized (PollingAlgorithmImpl.lock) {
			int historySelectServiceIndex = this.rpcClient.getCurrentSelectServiceIndex();
			if(historySelectServiceIndex >= (serviceArray.size() - Constant._1)){
				this.rpcClient.setCurrentSelectServiceIndex(Constant._0);
				return serviceArray.get(Constant._0);
			}
			this.rpcClient.setCurrentSelectServiceIndex(historySelectServiceIndex);
			return serviceArray.get(historySelectServiceIndex + Constant._1);
		}
	}
}
