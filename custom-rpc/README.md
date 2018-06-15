# custom-rpc

基于tcp实现的RPC框架

支持点对点调用

支持集群调用

netty、负责网络通讯，数据传输，解码编码等

protostuff、序列化，用于序列化网络传输中的数据

zookeeper、用于服务发现，服务注册，分布式锁等

负载算法、支持随机算法，轮询算法，ip_hash算法，权重

![image](https://github.com/shijianqiao321113/custom-rpc/blob/master/custom-rpc/TIM%E6%88%AA%E5%9B%BE20180523170643.png)

![image](https://github.com/shijianqiao321113/custom-rpc/blob/master/custom-rpc/zookeeperFlowNode.png)