package com.example.service;

import com.example.model.ExampleObject;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class ExampleObjectEcho{

    @SqsListener("${com.sqsexample.queue.name}")
    public void recvSqsMessage(ExampleObject exampleObject) {
        System.out.println(exampleObject);
    }

}
