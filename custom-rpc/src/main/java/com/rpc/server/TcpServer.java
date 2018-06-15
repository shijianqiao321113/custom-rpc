package com.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rpc.codec.RpcDecode;
import com.rpc.codec.RpcEncode;
import com.rpc.tools.Constant;

class TcpServer extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
	
	private RpcServer rpcServer;
	
	public TcpServer(RpcServer rpcServer){
		this.rpcServer = rpcServer;
	}
	@Override
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(this.rpcServer.getConfig().getBossGroupThreadCount());
        NioEventLoopGroup workGroup = new NioEventLoopGroup(this.rpcServer.getConfig().getWorkGroupThreadCount());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            for(Map.Entry<ChannelOption<?>, Object> op : this.rpcServer.getConfig().getOptions().entrySet()){
            	bootstrap.option(ChannelOption.valueOf(op.getKey().name()),op.getValue());
            }
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(new IdleStateHandler(rpcServer.getConfig().getReaderIdleTimeSeconds(),
                    		rpcServer.getConfig().getWriterIdleTimeSeconds(),
                    		rpcServer.getConfig().getAllIdleTimeSeconds()));
                    p.addLast(new LengthFieldBasedFrameDecoder(
                    		rpcServer.getConfig().getMaxFrameLength(),Constant._2,Constant._4,Constant._8,Constant._0));
                    p.addLast(new RpcDecode());
                    p.addLast(new RpcEncode());
                    p.addLast(new RpcServerHandler(rpcServer));
                }
            });
            ChannelFuture future = bootstrap.bind(this.rpcServer.getPort()).sync();
            logger.info("service start success port "+this.rpcServer.getPort());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
	}
}