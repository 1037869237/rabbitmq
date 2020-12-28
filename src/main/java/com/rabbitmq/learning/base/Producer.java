package com.rabbitmq.learning.base;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.util.HashMap;
import java.util.Map;

public class Producer {
    public static void main(String[] args) throws  Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(RabbitMQ.USER);
        factory.setPassword(RabbitMQ.PASSWORD);
       // factory.setVirtualHost();
        factory.setHost(RabbitMQ.IP_ADDR);
        factory.setPort(RabbitMQ.PORT);
        Connection connection = factory.newConnection();
        //一个连接可以创建多个channel实例，但是channel实例不能在线程间共享，应用程序应该为每一个线程开辟一个channel
        //多线程共享channel实例是非线程安全的
        Channel channel = connection.createChannel();

        //声明交换机，这是一个多重载方法
        /**
         * 1.
         * exchange:交换机名称
         * type：交换机类型：
         * durable：是否持久化
         * autoDelete:是否设置自动删除，自动删除的前提：至少有一个队列或者交换机与这个交换机绑定，之后所有与这个交换机绑定的队列或者交换机都与此解绑
         * internal:是否为内置的，为ture则：这是内置的交换机，客户端程序无法直接发送消息到这个交换机中，只能通过交换机路由到交换机这种方式
         * augument：结构化参数：详细笔记稍后写
         * channel.exchangeDeclare(String exchange,String Type,boolean durable,boolean autoDelete,Map<String,Object> arguments)
         *
         * 2.
         * 用来检测交换机是否存在，如果存在则正常返回。如果不存在则抛出异常，channel关闭
         * channel.exchangeDeclarePassive(String name);
         *
         * 3.交换机删除
         * ifUnused:删除时设置，是否在交换机没有被使用的情况下删除。 true，则只有在此交换器没有被使用的情况下才会被删除，false,无论如何都要删除。
         * exchangeDelete(String exchange,boolean ifUnused)
         */
        channel.exchangeDeclare("secondexchange","direct",false,false,null);

        /**
         * 1.
         *  queue:队列的名称
         *  durable:设置持久化
         *  exclusive: 设置是否排他。如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除。
         *         1. 排他队列是基于connection的，同一个connection的不同channel是可以同时访问同一连接创建的exclusive queue
         *         2. 如果一个连接已经声明了一个排他队列，其他连接是不允许建立同名的exclusive queue，这个和普通队列不同
         *         3. 即使exclusive持久化，一旦连接关闭和客户端退出，该排他队列都会被自动删除，这种队列适用一个库护短同时发送和读取消息的场景
         *   autoDelete: 自动删除的前提是：至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，才会自动删除
         *   argument：设置队列的其他参数
         * channel:.queueDeclare(String queue,boolean durable,boolean exclusive,boolean autoDelete,Map<String,Objedct> arguments)
         *
         * 生产者和消费者都能够适用queueDeclare来声明一个队列，但是如果消费者在同一个channel上订阅了另一个队列，就无法再声明队列了。必须取消订阅，然后将信道置为 传输模式，之后才能声明队列
         *
         * 2.
         * channel.queueDeclarePassive(String name);
         * 这个方法检测相应的队列是否存在。如果存在则正常返回，如果不存在则抛出异常
         *
         * 3。
         * queueDelete(String queue,boolean ifUnused,boolean ifEmpty)
         * 4.
         * queuePurge(String queue)；清空队列中的内容，而不删除队列本身
         *
         */
        channel.queueDeclare("secondqueue",false,false,true,null);

        /**
         * queue:队列名称
         * exchange：交换机名称
         * routingKey:用来绑定队列和交换机的路由键
         * argument：定义绑定的一些参数
         *  channel.queueBind(String queue,String source,String routingKey,Map)
         */
        channel.queueBind("secondqueue","secondexchange","secondbind",null);


        /**
         * 发送消息： 发送消息的交换机并没有绑定任何队列，那么消息将会丢失，或者绑定了某个队列，但是发送消息时，路邮件不匹配，消息也会丢失。之后可以配合mandatory参数或者备份交换机来解决
         * props:消息的基本属性：常用的如下：
         * 设置传递模式为2，即消息会被持久化在服务器中。  同时这条消息的优先级为0，
         * new AMQP.BasicProperties.Builder()
         *                                   .contentType("text/plain")
         *                                   .deliveryMode(2)
         *                                   .priority(1)
         *                                   .userId("hidden")
         *                                   .build()
         *
         *  发送一条带有headers的消息：
         *        Map<String,Object> headers = new HashMap<String, Object>();
         *         headers.put("location","here");
         *         headers.put("time","today");
         *  new AMQP.BasicProperties.Builder()
         *                                    .headers(headers)
         *                                    .build()
         *
         * 带有过期时间的消息：
         *
         *  body:消息内容
         *
         *  mandatory  immediate之后的章节再学
         *
         * channel.basicPublish(String exchange,String routingKey,boolean mandatory,boolean immediate,BasicProperties props,byte[] body)
         *
         */

        Map<String,Object> headers = new HashMap<String, Object>();
        headers.put("location","here");
        headers.put("time","today");
        String message = "hello world";
        channel.basicPublish("secondexchange","secondbind",false,false,new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                .deliveryMode(2)
                .priority(1)
                .userId("root")
                .headers(headers)
                .build(),message.getBytes());

        channel.close();
        connection.close();

    }
}
