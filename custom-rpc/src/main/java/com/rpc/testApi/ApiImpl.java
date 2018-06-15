package com.rpc.testApi;

import com.rpc.tools.Constant;

public class ApiImpl implements Api {

	@Override
	public String getRandom(String name) {
		return Constant.join(name,Constant.UNDERLINE,Math.random());
	}

}
