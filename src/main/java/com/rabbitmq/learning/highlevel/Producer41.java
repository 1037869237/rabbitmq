package com.rabbitmq.learning.highlevel;

import com.rabbitmq.client.*;
import com.rabbitmq.learning.entity.RabbitMQ;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Producer41 {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(RabbitMQ.PORT);
        factory.setHost(RabbitMQ.IP_ADDR);
        factory.setUsername(RabbitMQ.USER);
        factory.setPassword(RabbitMQ.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        /**
         * 备份交换机（AE）：
         * 如果不设置mandatory，若消息在未被路由的情况下会丢失，如果设置了，生产者的代码将变得复杂。
         * 所以引出了备份交换机：将未被路由的消息存储在mq中，再需要的时候去处理这些消息。
         * 在声明交换机的时候，加上alternate-exchange参数来实现,
         * 建议把AE声明成fanout类型
         */
        //下面一段代码就是给一个普通的交换机发送消息，如果路由键不匹配则发送到到备份交换机，最后路由到广播队列中
        Map<String,Object> map = new HashMap<>();
        map.put("alternate-exchange","myAe");

        channel.exchangeDeclare("normalExchange","direct",true,false,map);  //声明一个普通交换机
        channel.exchangeDeclare("myAe","fanout",true,false,null);  //声明一个备份交换机


        channel.queueDeclare("normalQueue",true,false,false,null);  //声明一个队列与普通交换机进行绑定
        channel.queueBind("normalQueue","normalExchange","normalKey");


        channel.queueDeclare("unroutedQueue",true,false,false,null); //声明一个队列与AE进行绑定，fanout类型所以不用指定路由键
        channel.queueBind("unroutedQueue","myAe","");

        /**
         * mandatory是basicPublish方法的参数，若为true，如果交换器无法根据自身的类型和路由键找到一个符合条件的队列，那么会将消息返回给生产者。为false则将消息直接丢弃
         * 可以调用channel.addReturnListener来添加ReturnListener监听器实现
         */
        channel.basicPublish("exchange","",true, MessageProperties.PERSISTENT_TEXT_PLAIN,"mandatory test".getBytes());
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                System.out.println(new String(bytes));
            }
        });

        channel.close();
        connection.close();
        /**
         * 对mandatory和备份交换机的总结：
         * 1。 如果设置的备份交换机不存在，客户端和mq服务端都不会有异常出现，此时消息会丢失
         * 2。 如果备份交换机没有绑定任何队列，客户端和mq服务端不会有异常出现，消息丢失
         * 3。 如果备份交换机没有任何匹配的队列，客户端和mq服务端都不会有异常出现，消息丢失
         * 4。 AE和mandatory一起使用，mandatory失效
         */
    }
}
