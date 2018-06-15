package com.rpc.config;

import io.netty.channel.ChannelOption;

import java.util.LinkedHashMap;
import java.util.Map;

import com.rpc.tools.Constant;

public abstract class RpcServerConfig{

	public abstract LinkedHashMap<String, Class<?>> initServiceMappingConfig();

	private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();

	private int readerIdleTimeSeconds = Constant._12;

	private int writerIdleTimeSeconds = Constant._0;

	private int allIdleTimeSeconds = Constant._0;

	private int bossGroupThreadCount = Constant._2;

	private int workGroupThreadCount = Constant._4;

	private int maxFrameLength = Integer.MAX_VALUE;
	
	public <T> RpcServerConfig setOptions(ChannelOption<T> option, T value) {
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

	public RpcServerConfig setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
		this.readerIdleTimeSeconds = readerIdleTimeSeconds;
		return self();
	}

	public int getWriterIdleTimeSeconds() {
		return writerIdleTimeSeconds;
	}

	public RpcServerConfig setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
		this.writerIdleTimeSeconds = writerIdleTimeSeconds;
		return self();
	}

	public int getAllIdleTimeSeconds() {
		return allIdleTimeSeconds;
	}

	public RpcServerConfig setAllIdleTimeSeconds(int allIdleTimeSeconds) {
		this.allIdleTimeSeconds = allIdleTimeSeconds;
		return self();
	}

	public int getMaxFrameLength() {
		return maxFrameLength;
	}

	public RpcServerConfig setMaxFrameLength(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
		return self();
	}

	public int getBossGroupThreadCount() {
		return bossGroupThreadCount;
	}

	public RpcServerConfig setBossGroupThreadCount(int bossGroupThreadCount) {
		this.bossGroupThreadCount = bossGroupThreadCount;
		return self();
	}

	public int getWorkGroupThreadCount() {
		return workGroupThreadCount;
	}

	public RpcServerConfig setWorkGroupThreadCount(int workGroupThreadCount) {
		this.workGroupThreadCount = workGroupThreadCount;
		return self();
	}

	private RpcServerConfig self(){
		return this;
	}
}
