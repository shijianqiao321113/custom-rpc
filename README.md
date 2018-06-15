# custom-rpc

基于tcp实现的RPC框架

支持点对点调用

支持集群调用

netty、负责网络通讯，数据传输，解码编码等

protostuff、序列化，用于序列化网络传输中的数据

zookeeper、用于服务发现，服务注册，分布式锁等

负载算法、支持随机算法，轮询算法，ip_hash算法，权重

支持spring boot 集成

普通 maven 项目引入
``` xml
<dependency>
    <groupId>com.rpc</groupId>
    <artifactId>custom-rpc</artifactId>
    <version>1.0.0</version>
</dependency>
```

spring boot maven 项目引入
``` xml
<dependency>
	<groupId>com.custom.rpc.spring.boot.autoconfigure</groupId>
	<artifactId>custom-rpc-spring-boot-starter</artifactId>
	<version>1.0.0</version>
	<exclusions>
		<exclusion>
			<artifactId>slf4j-log4j12</artifactId>
			<groupId>org.slf4j</groupId>
		</exclusion>
		<exclusion>
			<artifactId>log4j</artifactId>
			<groupId>log4j</groupId>
		</exclusion>
	</exclusions>
</dependency>
```

spring boot 服务端启动标记注解
@CustomRpcServer

spring boot 服务端接口实现类注解
@ApiService

spring boot 客户端启动标记注解
@CustomRpcClient

![image](https://github.com/shijianqiao321113/custom-rpc/blob/master/custom-rpc/20180615112622.png)

![image](https://github.com/shijianqiao321113/custom-rpc/blob/master/custom-rpc/zookeeperFlowNode.png)