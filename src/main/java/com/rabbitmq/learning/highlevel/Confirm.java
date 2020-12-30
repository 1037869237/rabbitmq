package com.rabbitmq.learning.highlevel;

public class Confirm {
    public static void main(String[] args) {
        /**
         * 消息确认机制（消息的可靠传输问题）：
         * 如何能保证消息可以不丢失的被消费，这得需要我们来进行一些额外的处理：
         *
         * 1。 消息生产者需要开启事务机制或者publisher confirm机制，以确保消息可以可靠的传输到mq中
         * 2。 消息生产者需要配合适用mandatory参数或者备份交换机来确保消息能够从交换机路由到队列，今儿能够保存下来而不被丢弃
         * 3。 消息和队列都需要进行持久化处理，以确保mq服务器在遇到异常情况时不会造成消息丢失（再加上集群配置，高可用，防止单点故障）
         * 4。 消费者在消费消息的同时需要将autoAck设置为false，然后手动确认的方式去确认，以免在消费端引起不必要的丢失
         */
    }
}
