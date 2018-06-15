package com.rpc.tools;


public class Constant {
	
	public static final String EMPTY_STRING = "";
	
	public static final String UNDERLINE = "_";
	
	public static final String BACKSLASH = "/";
	
	public static final String COLON = ":";
	
	public static final byte _0XAB = (byte)0xAB;
	
	public static final byte _0XBC = (byte)0xBC;
	
	public static final int __1 = -1 ;
	
	public static final int _0 = 0 ;
	
	public static final int _1 = 1 ;
	
	public static final int _2 = 2;
	
	public static final int _3 = 3 ;
	
	public static final int _4 = 4;
	
	public static final int _5 = 5;
	
	public static final int _8  = 8;
	
	public static final int _10  = 10;
	
	public static final int _12  = 12;
	
	public static final int _1000  = 1000;
	
	public static final int _10000  = 10000;
	
	public static final long _10000L  = 10000L;
	
	public static final int _50000 = 50000;
	
	public static final String PRODUCER = "producer";
	
	public static final String CONSUMER = "consumer";
	
	public static final String API = "api";
	
	public static final String LOCK = "lock";
	
	public static final String SUB = "sub_";
	
	public static final String CONFIG = "config";
	
	public static final String STRATEGY = "strategy";
	
	public static final String WEIGHT = "weight";
	
	
	public static String join(Object... strs){
		if(strs == null){
			return null;
		}
		StringBuffer buf = new StringBuffer();
		for(Object str:strs){
			buf.append(String.valueOf(str));
		}
		return buf.toString();
	}
	
	public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
