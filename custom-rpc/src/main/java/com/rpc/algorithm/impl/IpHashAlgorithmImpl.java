package com.rpc.algorithm.impl;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import com.rpc.algorithm.LoadAlgorithm;
import com.rpc.tools.Constant;

public class IpHashAlgorithmImpl implements LoadAlgorithm {
	
	private String networkIp;
	
	public IpHashAlgorithmImpl(String networkIp){
		this.networkIp = networkIp;
	}

	@Override
	public String selector(Vector<String> serviceList,
			Map<String, Integer> serviceWeightConfigMap) {

		SortedMap<Integer, String> virtualNodes = new TreeMap<Integer, String>();
		for (int k=Constant._0;k<serviceList.size();k++) {
			String service = serviceList.get(k);
			for(int i=Constant._0;i<Constant._10;i++){
				virtualNodes.put(getHash(Constant.join(service,Constant.UNDERLINE,i)), service);
			}
		}
		SortedMap<Integer, String> subMap = virtualNodes.tailMap(getHash(this.networkIp));
		if (subMap.size() <= Constant._0) {
			return virtualNodes.get(virtualNodes.firstKey());
		}
		return subMap.get(subMap.firstKey());
	}

	/*使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别*/
	private int getHash(String str) {
		final int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < str.length(); i++)
			hash = (hash ^ str.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;

		// 如果算出来的值为负数则取其绝对值
		if (hash < 0)
			hash = Math.abs(hash);
		return hash;
	}
}

