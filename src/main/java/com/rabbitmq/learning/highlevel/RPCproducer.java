package com.rabbitmq.learning.highlevel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.learning.entity.RabbitMQ;

public class RPCproducer {
    public static void main(String[] args) throws  Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(RabbitMQ.PORT);
        factory.setHost(RabbitMQ.IP_ADDR);
        factory.setUsername(RabbitMQ.USER);
        factory.setPassword(RabbitMQ.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**
         * mq实现rpc，
         * rpc即远程调用。比入有两台服务器A 和B ,若A想调用B的函数方法，由于没有在一个内存空间，不能直接调用，需要通过网络来表达调用的语义和传达的数据。
         * 流程：
         * 1. 当客户端启动时，创建一个匿名的回调队列（由mq自动创建）
         * 2. 客户端为rpc请求设置2个属性，replyTo用来告知rpc服务端回复请求时的目的队列，即回调队列；correlationId用来标记一个请求
         * 3. 请求被发送到rpc_queue队列中
         * 4. rpc服务端监听rpc_queue队列中的请求，当请求到来时，服务端会处理并且把带有结果的消息发送给客户端。接收的队列就是replyTo设定的回调队列
         * 5. 客户端监听回调队列，当有消息时，检查correlationId属性，如果与请求匹配，那就是结果了。
         */

        //rpc远程调用例子详细见书，这次累了 先不打了 日后更新。
    }
}
