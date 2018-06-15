package com.rpc.config;

import io.netty.channel.ChannelOption;

import java.util.LinkedHashMap;
import java.util.Map;

import com.rpc.tools.Constant;

public class RpcClientConfig {
	
	private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();

	private int readerIdleTimeSeconds = Constant._12;

	private int writerIdleTimeSeconds = Constant._0;

	private int allIdleTimeSeconds = Constant._0;

	private int workGroupThreadCount = Constant._4;
	
	private int heartbeatTntervalTime = Constant._50000;
	
	private int validationLinkValidIntervalTime = Constant._1000;
	
	private Long connectionTimeOut = Constant._10000L;

	private int maxFrameLength = Integer.MAX_VALUE;

	public <T> RpcClientConfig setOptions(ChannelOption<T> option, T value) {
		if (option == null) {
			throw new NullPointerException("option");
		}
		if (value == null) {
			synchronized (options) {
				options.remove(option);
			}
		} else {
			synchronized (options) {
				options.put(option, value);
			}
		}
		return self();
	}
	
	public Map<ChannelOption<?>, Object> getOptions() {
		return options;
	}

	public int getReaderIdleTimeSeconds() {
		return readerIdleTimeSeconds;
	}

	public RpcClientConfig setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
		this.readerIdleTimeSeconds = readerIdleTimeSeconds;
		return self();
	}

	public int getWriterIdleTimeSeconds() {
		return writerIdleTimeSeconds;
	}

	public RpcClientConfig setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
		this.writerIdleTimeSeconds = writerIdleTimeSeconds;
		return self();
	}

	public int getAllIdleTimeSeconds() {
		return allIdleTimeSeconds;
	}

	public RpcClientConfig setAllIdleTimeSeconds(int allIdleTimeSeconds) {
		this.allIdleTimeSeconds = allIdleTimeSeconds;
		return self();
	}

	public int getMaxFrameLength() {
		return maxFrameLength;
	}

	public RpcClientConfig setMaxFrameLength(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
		return self();
	}

	public int getWorkGroupThreadCount() {
		return workGroupThreadCount;
	}

	public RpcClientConfig setWorkGroupThreadCount(int workGroupThreadCount) {
		this.workGroupThreadCount = workGroupThreadCount;
		return self();
	}

	public int getHeartbeatTntervalTime() {
		return heartbeatTntervalTime;
	}

	public RpcClientConfig setHeartbeatTntervalTime(int heartbeatTntervalTime) {
		this.heartbeatTntervalTime = heartbeatTntervalTime;
		return self();
	}

	public int getValidationLinkValidIntervalTime() {
		return validationLinkValidIntervalTime;
	}

	public RpcClientConfig setValidationLinkValidIntervalTime(
			int validationLinkValidIntervalTime) {
		this.validationLinkValidIntervalTime = validationLinkValidIntervalTime;
		return self();
	}

	public Long getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public RpcClientConfig setConnectionTimeOut(Long connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
		return self();
	}

	private RpcClientConfig self(){
		return this;
	}
}
