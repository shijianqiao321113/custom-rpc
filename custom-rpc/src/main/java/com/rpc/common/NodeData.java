package com.rpc.common;

import java.io.Serializable;

public class NodeData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int serviceRequestNumber;

	private StrategyEnum strategyEnum; 

	private int weight;
	
	public NodeData setWeight(int weight){
		this.weight = weight;
		return self();
	}
	
	public int getWeight(){
		return this.weight;
	}
	
	public StrategyEnum getStrategyEnum() {
		return strategyEnum;
	}

	public NodeData setStrategyEnum(StrategyEnum strategyEnum) {
		this.strategyEnum = strategyEnum;
		return self();
	}

	public int getServiceRequestNumber() {
		return serviceRequestNumber;
	}

	public NodeData setServiceRequestNumber(int serviceRequestNumber) {
		this.serviceRequestNumber = serviceRequestNumber;
		return self();
	}

	private NodeData self(){
		return this;
	}
   	
}
