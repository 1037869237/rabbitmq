package com.rabbitmq.learning.entity;

import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import lombok.Data;

@Data
public class Person {
    private String name;
    private int age;
}
