package com.quan.controller;

import com.quan.pojo.Message;
import com.quan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    //get message list
    @GetMapping
    public List<Message> queryMessageList(@RequestParam("fromId") Long fromId, @RequestParam("toId") Long toId, @RequestParam(value = "page",
                                                  defaultValue = "1") Integer page,
                                          @RequestParam(value = "rows",
                                                  defaultValue = "10") Integer rows){
        return messageService.queryMessageList(toId,fromId,page,rows);
    }
}
