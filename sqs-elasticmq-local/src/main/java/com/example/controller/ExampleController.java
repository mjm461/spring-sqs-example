package com.example.controller;

import com.example.service.SqsSendService;
import com.example.model.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class ExampleController {

    private String queueName;
    private SqsSendService sqsService;

    @Autowired
    public ExampleController(
            @Value("${com.sqsexample.queue.name}") String queueName,
            SqsSendService sqsService) {
        this.queueName = queueName;
        this.sqsService = sqsService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postexampleObject(
            @RequestBody ExampleObject exampleObject) {
        sqsService.sendSqsMessage(queueName, exampleObject);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
