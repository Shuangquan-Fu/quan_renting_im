package com.quan.controller;

import com.quan.pojo.Message;
import com.quan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("message")
@CrossOrigin
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
