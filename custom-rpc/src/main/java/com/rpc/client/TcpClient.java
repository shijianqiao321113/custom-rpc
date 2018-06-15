package com.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.Map;

import com.rpc.codec.RpcDecode;
import com.rpc.codec.RpcEncode;
import com.rpc.common.ResponseHander;
import com.rpc.exception.LinkToServerErrorException;
import com.rpc.message.MessageHead;
import com.rpc.message.RpcRequest;
import com.rpc.message.RpcResponse;
import com.rpc.tools.Constant;
import com.rpc.tools.Counter;

public class TcpClient{
	
	public Channel channel;
	
	private EventLoopGroup worker ;
	
	private Bootstrap bootstrap;
	
	private RpcClient rpcClient;
	
	private Thread connectionMonitorThread;
	
	private Thread sendHeartbeatMessageThread;
	
	private InetSocketAddress address;
	
	private Boolean isLoad;
	
	private Boolean isKeep = Boolean.TRUE;
	
	public TcpClient(RpcClient rpcClient,InetSocketAddress address,Boolean isLoad){
		this.rpcClient = rpcClient;
		this.address = address;
		this.isLoad = isLoad;
	}
	
	public void start(){
		connect();
		
		this.connectionMonitorThread = new Thread() {
			@Override
			public void run() {
				isConnnection();
			}
		};
		this.rpcClient.getExec().execute(this.connectionMonitorThread);
		this.sendHeartbeatMessageThread =  new Thread() {
			@Override
			public void run() {
				sendHeartbeatData();
			}
		};
		this.rpcClient.getExec().execute(this.sendHeartbeatMessageThread);
	}
	
	private void connect() {
        bootstrap = new Bootstrap();
        worker = new NioEventLoopGroup(this.rpcClient.getConfig().getWorkGroupThreadCount());
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        for(Map.Entry<ChannelOption<?>, Object> op : this.rpcClient.getConfig().getOptions().entrySet()){
        	bootstrap.option(ChannelOption.valueOf(op.getKey().name()),op.getValue());
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new IdleStateHandler(rpcClient.getConfig().getReaderIdleTimeSeconds(),
                		rpcClient.getConfig().getWriterIdleTimeSeconds(),
                		rpcClient.getConfig().getAllIdleTimeSeconds()));
                p.addLast(new LengthFieldBasedFrameDecoder(rpcClient.getConfig().getMaxFrameLength(),Constant._2,Constant._4,Constant._8,Constant._0));
                p.addLast(new RpcDecode());
                p.addLast(new RpcEncode());
                p.addLast(new RpcClientHander(rpcClient));
            }
        });
        doConnection();
    }
	
	private void doConnection(){
        try {
        	channel = bootstrap.connect(this.address).sync().channel();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private Boolean isActive(){
    	if(this.channel != null && this.channel.isActive()){
    		return Boolean.TRUE;
    	}
    	return Boolean.FALSE;
    }
    
    private void sendHeartbeatData() {
    	while(this.isKeep){
    		try {
    			if (isActive()) {
    	        	MessageHead messageHead = new MessageHead(Constant._0XAB,Constant._0XAB,null,Counter.addOne());
    	            RpcRequest request = new RpcRequest();
    	            request.setMessageHead(messageHead);
    	            this.channel.writeAndFlush(request);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
            try {Thread.sleep(this.rpcClient.getConfig().getHeartbeatTntervalTime());} catch (Exception e) {}
    	}
    }
    
    private void isConnnection(){
    	while(this.isKeep){
    		if(!isActive()){
    			if(!isLoad){
    				/*单点链接*/
    				doConnection();
    			}else{
    				/*集群链接*/
    				isSignOut();
    			}
			}
    		try {Thread.sleep(this.rpcClient.getConfig().getValidationLinkValidIntervalTime());} catch (Exception e) {}
		}
    }
    
    @SuppressWarnings({ "deprecation" })
	private void isSignOut(){
    	try {
    		this.rpcClient.removeServer(Constant.join(this.address.getHostName(),Constant.COLON,this.address.getPort()));
        	this.worker.shutdownGracefully();
        	this.isKeep = Boolean.FALSE;
        	this.connectionMonitorThread.stop();
        	this.sendHeartbeatMessageThread.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public RpcResponse invoke(RpcRequest request) throws Exception {
    	if(!isActive()){
    		throw new LinkToServerErrorException("unconnected to the server");
    	}
    	ResponseHander responseHander = new ResponseHander();
    	this.rpcClient.setResponseHanderMapping(String.valueOf(request.getMessageHead().getMessageId()),responseHander);
    	this.channel.writeAndFlush(request);
    	return responseHander.getRpcResponse(String.valueOf(request.getMessageHead().getMessageId()),this.rpcClient);
    }
}