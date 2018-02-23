package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SqsSendService<T>{

    protected QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public SqsSendService(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendSqsMessage(String queueName, T object) {
        queueMessagingTemplate.convertAndSend(queueName, object);
    }

}
